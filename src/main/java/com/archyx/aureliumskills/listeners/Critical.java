package com.archyx.aureliumskills.listeners;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PluginPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class Critical {

    private final Random r = new Random();
    private final AureliumSkills plugin;

    public Critical(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    public void applyCrit(EntityDamageByEntityEvent event, Player player, PluginPlayer pluginPlayer) {
        if (plugin.getAbilityManager().isEnabled(Ability.CRIT_CHANCE)) {
            if (isCrit(pluginPlayer)) {
                event.setDamage(event.getDamage() * getCritMultiplier(pluginPlayer));
                player.setMetadata("skillsCritical", new FixedMetadataValue(plugin, true));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.removeMetadata("skillsCritical", plugin);
                    }
                }.runTaskLater(plugin, 1L);
            }
        }
    }

    private boolean isCrit(PluginPlayer pluginPlayer) {
        return r.nextDouble() < (plugin.getAbilityManager().getValue(Ability.CRIT_CHANCE, pluginPlayer.getAbilityLevel(Ability.CRIT_CHANCE)) / 100);
    }

    private double getCritMultiplier(PluginPlayer pluginPlayer) {
        if (plugin.getAbilityManager().isEnabled(Ability.CRIT_DAMAGE)) {
            double multiplier = plugin.getAbilityManager().getValue(Ability.CRIT_DAMAGE, pluginPlayer.getAbilityLevel(Ability.CRIT_DAMAGE)) / 100;
            return OptionL.getDouble(Option.CRITICAL_BASE_MULTIPLIER) * (1 + multiplier);
        }
        return OptionL.getDouble(Option.CRITICAL_BASE_MULTIPLIER);
    }

}
