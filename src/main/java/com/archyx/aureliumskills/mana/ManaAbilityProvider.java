package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.AbilityProvider;
import com.archyx.aureliumskills.api.event.ManaAbilityActivateEvent;
import com.archyx.aureliumskills.data.PluginPlayer;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.sorcery.SorceryLeveler;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public abstract class ManaAbilityProvider extends AbilityProvider implements Listener {

    protected final AureliumSkills plugin;
    protected final ManaAbilityManager manager;
    protected final MAbility mAbility;
    protected final Skill skill;
    protected final SorceryLeveler sorceryLeveler;
    protected final ManaAbilityMessage activateMessage;
    protected final ManaAbilityMessage stopMessage;

    public ManaAbilityProvider(AureliumSkills plugin, MAbility mAbility, ManaAbilityMessage activateMessage, @Nullable ManaAbilityMessage stopMessage) {
        super(plugin, mAbility.getSkill());
        this.plugin = plugin;
        this.manager = plugin.getManaAbilityManager();
        this.mAbility = mAbility;
        this.skill = mAbility.getSkill();
        this.sorceryLeveler = plugin.getSorceryLeveler();
        this.activateMessage = activateMessage;
        this.stopMessage = stopMessage;
    }

    public void activate(Player player) {
        PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
        if (pluginPlayer == null) return;

        int duration = getDuration(pluginPlayer);
        ManaAbilityActivateEvent event = new ManaAbilityActivateEvent(player, mAbility, duration);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        manager.setActivated(player, mAbility, true);

        onActivate(player, pluginPlayer); // Mana ability specific behavior is run
        consumeMana(player, pluginPlayer);

        if (duration != 0) {
            //Schedules stop
            new BukkitRunnable() {
                @Override
                public void run() {
                    stop(player);
                    manager.setActivated(player, mAbility, false);
                    manager.setReady(player.getUniqueId(), mAbility, false);
                }
            }.runTaskLater(plugin, duration);
        } else {
            stop(player);
            manager.setActivated(player, mAbility, false);
            manager.setReady(player.getUniqueId(), mAbility, false);
        }
    }

    public abstract void onActivate(Player player, PluginPlayer pluginPlayer);

    public void stop(Player player) {
        PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
        if (pluginPlayer == null) return;
        onStop(player, pluginPlayer); // Mana ability specific stop behavior is run
        manager.setPlayerCooldown(player, mAbility); // Apply cooldown
        // Send stop message if applicable
        if (stopMessage != null) {
            plugin.getAbilityManager().sendMessage(player, Lang.getMessage(stopMessage, plugin.getLang().getLocale(player)));
        }
    }

    public abstract void onStop(Player player, PluginPlayer pluginPlayer);

    protected int getDuration(PluginPlayer pluginPlayer) {
        return (int) Math.round(getValue(mAbility, pluginPlayer) * 20);
    }

    protected void consumeMana(Player player, PluginPlayer pluginPlayer) {
        double manaConsumed = manager.getManaCost(mAbility, pluginPlayer);
        pluginPlayer.setMana(pluginPlayer.getMana() - manaConsumed);
        sorceryLeveler.level(player, manaConsumed);
        plugin.getAbilityManager().sendMessage(player, TextUtil.replace(Lang.getMessage(activateMessage, pluginPlayer.getLocale())
                ,"{mana}", NumberUtil.format0(manaConsumed)));
    }

    // Returns true if player has enough mana
    protected boolean hasEnoughMana(Player player) {
        PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
        if (pluginPlayer == null) return false;
        Locale locale = pluginPlayer.getLocale();
        if (pluginPlayer.getMana() >= plugin.getManaAbilityManager().getManaCost(mAbility, pluginPlayer)) {
            return true;
        }
        else {
            plugin.getAbilityManager().sendMessage(player, TextUtil.replace(Lang.getMessage(ManaAbilityMessage.NOT_ENOUGH_MANA, locale)
                    ,"{mana}", NumberUtil.format0(plugin.getManaAbilityManager().getManaCost(mAbility, pluginPlayer))
                    , "{current_mana}", String.valueOf(Math.round(pluginPlayer.getMana()))
                    , "{max_mana}", String.valueOf(Math.round(pluginPlayer.getMaxMana()))));
            return false;
        }
    }

}
