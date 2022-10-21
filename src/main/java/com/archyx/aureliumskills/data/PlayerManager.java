package com.archyx.aureliumskills.data;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private final AureliumSkills plugin;
    private final ConcurrentHashMap<UUID, PluginPlayer> playerData;

    public PlayerManager(AureliumSkills plugin) {
        this.plugin = plugin;
        this.playerData = new ConcurrentHashMap<>();
        if (OptionL.getBoolean(Option.AUTO_SAVE_ENABLED)) {
            startAutoSave();
        }
    }

    @Nullable
    public PluginPlayer getPlayerData(Player player) {
        return playerData.get(player.getUniqueId());
    }

    @Nullable
    public PluginPlayer getPlayerData(UUID id) {
        return this.playerData.get(id);
    }

    public void addPlayerData(@NotNull PluginPlayer pluginPlayer) {
        this.playerData.put(pluginPlayer.getUniqueId(), pluginPlayer);
    }

    public void removePlayerData(UUID id) {
        this.playerData.remove(id);
    }

    public boolean hasPlayerData(Player player) {
        return playerData.containsKey(player.getUniqueId());
    }

    public ConcurrentHashMap<UUID, PluginPlayer> getPlayerDataMap() {
        return playerData;
    }

    public void startAutoSave() {
        long interval = OptionL.getInt(Option.AUTO_SAVE_INTERVAL_TICKS);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
                    if (pluginPlayer != null && !pluginPlayer.isSaving()) {
                        plugin.getStorageProvider().save(player, false);
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, interval, interval);
    }

}
