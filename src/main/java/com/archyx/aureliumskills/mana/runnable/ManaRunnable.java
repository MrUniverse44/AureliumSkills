package com.archyx.aureliumskills.mana.runnable;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.event.ManaRegenerateEvent;
import com.archyx.aureliumskills.data.PlayerData;
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
        for (PlayerData playerData : plugin.getPlayerManager().getPlayerDataMap().values()) {
            double originalMana = playerData.getMana();
            double maxMana = playerData.getMaxMana();
            if (originalMana < maxMana) {
                if (!playerData.getAbilityData(MAbility.ABSORPTION).getBoolean("activated")) {
                    double regen = playerData.getManaRegen();
                    double finalRegen = Math.min(originalMana + regen, maxMana) - originalMana;
                    ManaRegenerateEvent event = new ManaRegenerateEvent(playerData, finalRegen);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        playerData.setMana(originalMana + event.getAmount());
                    }
                }
            }
        }
    }
}
