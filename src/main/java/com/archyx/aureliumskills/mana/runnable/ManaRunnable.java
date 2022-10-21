package com.archyx.aureliumskills.mana.runnable;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.event.ManaRegenerateEvent;
import com.archyx.aureliumskills.data.PluginPlayer;
import com.archyx.aureliumskills.mana.MAbility;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ManaRunnable extends BukkitRunnable {

    private final AureliumSkills plugin;

    public ManaRunnable(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    public synchronized void silencedCancelled() {
        try {
            super.cancel();
        } catch (Exception ignored) {}
    }

    @Override
    public void run() {
        for (PluginPlayer pluginPlayer : plugin.getPlayerManager().getPlayerDataMap().values()) {
            double originalMana = pluginPlayer.getMana();
            double maxMana = pluginPlayer.getMaxMana();
            if (originalMana < maxMana) {
                if (!pluginPlayer.getAbilityData(MAbility.ABSORPTION).getBoolean("activated")) {
                    double regen = pluginPlayer.getManaRegen();
                    double finalRegen = Math.min(originalMana + regen, maxMana) - originalMana;
                    ManaRegenerateEvent event = new ManaRegenerateEvent(pluginPlayer, finalRegen);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        pluginPlayer.setMana(originalMana + event.getAmount());
                    }
                }
            }
        }
    }
}
