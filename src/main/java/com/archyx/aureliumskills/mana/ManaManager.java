package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.event.ManaRegenerateEvent;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.mana.runnable.ManaRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class ManaManager implements Listener {

    private final ManaRunnable runnable;
    private final AureliumSkills plugin;

    public ManaManager(AureliumSkills plugin) {
        this.runnable = new ManaRunnable(plugin);
        this.plugin   = plugin;
    }

    /**
     * Start regenerating Mana
     */
    public void startRegen() {
        runnable.silencedCancelled();

        runnable.runTaskTimer(
                plugin,
                20L,
                20L
        );
    }

}
