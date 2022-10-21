package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.AbilityData;
import com.archyx.aureliumskills.data.PluginPlayer;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;

public class ChargedShot extends ManaAbilityProvider {

    public ChargedShot(AureliumSkills plugin) {
        super(plugin, MAbility.CHARGED_SHOT, ManaAbilityMessage.CHARGED_SHOT_SHOOT, null);
        tickChargedShotCooldown();
    }

    @EventHandler
    public void onToggle(PlayerInteractEvent event) {
        if (blockDisabled(MAbility.CHARGED_SHOT)) return;
        Player player = event.getPlayer();
        if (blockAbility(player)) return;
        ItemStack item = event.getItem();
        if (item == null) return;
        if (item.getType() != Material.BOW) return;
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer == null) return;
            if (pluginPlayer.getManaAbilityLevel(MAbility.CHARGED_SHOT) == 0) return;
            Locale locale = pluginPlayer.getLocale();
            AbilityData abilityData = pluginPlayer.getAbilityData(MAbility.CHARGED_SHOT);
            if (abilityData.getInt("cooldown") == 0) {
                if (!abilityData.getBoolean("enabled")) { // Toggle on
                    abilityData.setData("enabled", true);
                    plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.CHARGED_SHOT_ENABLE, locale));
                } else { // Toggle off
                    abilityData.setData("enabled", false);
                    plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.CHARGED_SHOT_DISABLE, locale));
                }
                abilityData.setData("cooldown", 8);
            }
        }
    }

    private void tickChargedShotCooldown() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
                    if (pluginPlayer != null) {
                        if (pluginPlayer.containsAbilityData(MAbility.CHARGED_SHOT)) {
                            AbilityData abilityData = pluginPlayer.getAbilityData(MAbility.CHARGED_SHOT);
                            int cooldown = abilityData.getInt("cooldown");
                            if (cooldown != 0) {
                                abilityData.setData("cooldown", cooldown - 1);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 3L, 5L);
    }

    @EventHandler
    public void activationListener(EntityShootBowEvent event) {
        if (blockDisabled(MAbility.CHARGED_SHOT)) return;
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (blockAbility(player)) return;
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer == null) return;
            if (pluginPlayer.getAbilityData(MAbility.CHARGED_SHOT).getBoolean("enabled")) {
                if (pluginPlayer.getManaAbilityLevel(MAbility.CHARGED_SHOT) == 0) return;
                ManaAbilityManager manager = plugin.getManaAbilityManager();
                int cooldown = manager.getPlayerCooldown(player.getUniqueId(), MAbility.SHARP_HOOK);
                if (cooldown == 0) {
                    pluginPlayer.getMetadata().put("charged_shot_projectile", event.getProjectile());
                    pluginPlayer.getMetadata().put("charged_shot_force", event.getForce());
                    activate(player);
                } else {
                    if (manager.getErrorTimer(player.getUniqueId(), MAbility.SHARP_HOOK) == 0) {
                        Locale locale = pluginPlayer.getLocale();
                        plugin.getAbilityManager().sendMessage(player, TextUtil.replace(Lang.getMessage(ManaAbilityMessage.NOT_READY, locale), "{cooldown}", NumberUtil.format1((double) (cooldown) / 20)));
                        manager.setErrorTimer(player.getUniqueId(), MAbility.SHARP_HOOK, 2);
                    }
                }
            }
        }
    }

    public void applyChargedShot(EntityDamageByEntityEvent event) {
        if (event.getDamager().hasMetadata("ChargedShotMultiplier")) {
            double multiplier = event.getDamager().getMetadata("ChargedShotMultiplier").get(0).asDouble();
            event.setDamage(event.getDamage() * multiplier);
        }
    }

    @Override
    public void onActivate(Player player, PluginPlayer pluginPlayer) {
        // Calculate damage increase
        double manaConsumed = getManaConsumed(pluginPlayer);
        if (manaConsumed <= 0) return;
        double damagePercent = manaConsumed * plugin.getManaAbilityManager().getValue(MAbility.CHARGED_SHOT, pluginPlayer);
        // Add meta to entity
        Object obj = pluginPlayer.getMetadata().get("charged_shot_projectile");
        if (!(obj instanceof Entity)) return;
        Entity projectile = (Entity) obj;
        projectile.setMetadata("ChargedShotMultiplier", new FixedMetadataValue(plugin, 1 + damagePercent / 100));
        // Play sound
        if (plugin.getManaAbilityManager().getOptionAsBooleanElseTrue(MAbility.CHARGED_SHOT, "enable_sound")) {
            if (XMaterial.isNewVersion()) {
                player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 0.5f, 1);
            } else {
                player.playSound(player.getLocation(), "entity.evocation_illager.cast_spell", 0.5f, 1);
            }
        }
    }

    @Override
    public void onStop(Player player, PluginPlayer pluginPlayer) {
        pluginPlayer.getMetadata().remove("charged_shot_projectile");
        pluginPlayer.getMetadata().remove("charged_shot_force");
    }

    @Override
    protected void consumeMana(Player player, PluginPlayer pluginPlayer) {
        double manaConsumed = getManaConsumed(pluginPlayer);
        if (manaConsumed <= 0) return;
        double damagePercent = manaConsumed * plugin.getManaAbilityManager().getValue(MAbility.CHARGED_SHOT, pluginPlayer);
        pluginPlayer.setMana(pluginPlayer.getMana() - manaConsumed);
        sorceryLeveler.level(player, manaConsumed);
        if (plugin.getManaAbilityManager().getOptionAsBooleanElseTrue(MAbility.CHARGED_SHOT, "enable_message")) {
            plugin.getAbilityManager().sendMessage(player, TextUtil.replace(Lang.getMessage(activateMessage, pluginPlayer.getLocale())
                    , "{mana}", NumberUtil.format0(manaConsumed)
                    , "{percent}", NumberUtil.format0(damagePercent)));
        }
    }

    private double getManaConsumed(PluginPlayer pluginPlayer) {
        Object obj = pluginPlayer.getMetadata().get("charged_shot_force");
        float force = 0;
        if (obj instanceof Float) {
            force = (float) obj;
        }
        return Math.min(manager.getManaCost(MAbility.CHARGED_SHOT, pluginPlayer) * force, pluginPlayer.getMana());
    }

    @Override
    protected int getDuration(PluginPlayer pluginPlayer) {
        return 0;
    }

}
