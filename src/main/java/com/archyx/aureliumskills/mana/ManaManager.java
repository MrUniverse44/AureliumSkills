package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.mana.runnable.ManaRunnable;
import org.bukkit.event.Listener;

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
