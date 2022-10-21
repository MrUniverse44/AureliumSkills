package com.archyx.aureliumskills.data.storage;


import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PluginPlayer;
import com.archyx.aureliumskills.data.PlayerDataLoadEvent;
import com.archyx.aureliumskills.data.PlayerDataState;
import com.archyx.aureliumskills.data.PlayerManager;
import com.archyx.aureliumskills.leaderboard.AverageSorter;
import com.archyx.aureliumskills.leaderboard.LeaderboardManager;
import com.archyx.aureliumskills.leaderboard.LeaderboardSorter;
import com.archyx.aureliumskills.leaderboard.SkillValue;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.stats.StatLeveler;
import com.archyx.aureliumskills.stats.Stats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

public abstract class StorageProvider {

    public final AureliumSkills plugin;
    public final PlayerManager playerManager;

    public StorageProvider(AureliumSkills plugin) {
        this.playerManager = plugin.getPlayerManager();
        this.plugin = plugin;
    }

    public PluginPlayer createNewPlayer(Player player) {
        PluginPlayer pluginPlayer = new PluginPlayer(player, plugin);
        // Set all skills to level 1 for new players
        for (Skill skill : plugin.getSkillRegistry().getSkills()) {
            pluginPlayer.setSkillLevel(skill, 1);
            pluginPlayer.setSkillXp(skill, 0.0);
        }
        playerManager.addPlayerData(pluginPlayer);
        plugin.getLeveler().updatePermissions(player);
        PlayerDataLoadEvent event = new PlayerDataLoadEvent(pluginPlayer);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(event);
            }
        }.runTask(plugin);
        return pluginPlayer;
    }

    protected void sendErrorMessageToPlayer(Player player, Exception e) {
        player.sendMessage(ChatColor.RED + "There was an error loading your skill data: " + e.getMessage() +
                ". Please report the error to your server administrator. To prevent your data from resetting permanently" +
                ", your skill data will not be saved. Try relogging to attempt loading again.");
    }

    protected void applyData(PluginPlayer pluginPlayer, Map<Skill, Integer> levels, Map<Skill, Double> xpLevels) {
        for (Stat stat : plugin.getStatRegistry().getStats()) {
            pluginPlayer.setStatLevel(stat, 0);
        }
        // Apply to object if in memory
        for (Skill skill : Skills.values()) {
            int level = levels.get(skill);
            pluginPlayer.setSkillLevel(skill, level);
            pluginPlayer.setSkillXp(skill, xpLevels.get(skill));
            // Add stat levels
            plugin.getRewardManager().getRewardTable(skill).applyStats(pluginPlayer, level);
        }
        // Reload stats
        new StatLeveler(plugin).reloadStat(pluginPlayer.getBukkitPlayer(), Stats.HEALTH);
        new StatLeveler(plugin).reloadStat(pluginPlayer.getBukkitPlayer(), Stats.LUCK);
        new StatLeveler(plugin).reloadStat(pluginPlayer.getBukkitPlayer(), Stats.WISDOM);
        // Immediately save to file
        save(pluginPlayer.getBukkitPlayer(), false);
    }

    protected Map<Skill, Integer> getLevelsFromBackup(ConfigurationSection playerDataSection, String stringId) {
        Map<Skill, Integer> levels = new HashMap<>();
        for (Skill skill : Skills.values()) {
            int level = playerDataSection.getInt(stringId + "." + skill.toString().toLowerCase(Locale.ROOT) + ".level", 1);
            levels.put(skill, level);
        }
        return levels;
    }

    protected Map<Skill, Double> getXpLevelsFromBackup(ConfigurationSection playerDataSection, String stringId) {
        Map<Skill, Double> xpLevels = new HashMap<>();
        for (Skill skill : Skills.values()) {
            double xp = playerDataSection.getDouble(stringId + "." + skill.toString().toLowerCase(Locale.ROOT) + ".xp");
            xpLevels.put(skill, xp);
        }
        return xpLevels;
    }

    protected Set<UUID> addLoadedPlayersToLeaderboards(Map<Skill, List<SkillValue>> leaderboards, List<SkillValue> powerLeaderboard, List<SkillValue> averageLeaderboard) {
        Set<UUID> loadedFromMemory = new HashSet<>();
        for (PluginPlayer pluginPlayer : playerManager.getPlayerDataMap().values()) {
            UUID id = pluginPlayer.getBukkitPlayer().getUniqueId();
            int powerLevel = 0;
            double powerXp = 0;
            int numEnabled = 0;
            for (Skill skill : Skills.values()) {
                int level = pluginPlayer.getSkillLevel(skill);
                double xp = pluginPlayer.getSkillXp(skill);
                // Add to lists
                SkillValue skillLevel = new SkillValue(id, level, xp);
                leaderboards.get(skill).add(skillLevel);

                if (OptionL.isEnabled(skill)) {
                    powerLevel += level;
                    powerXp += xp;
                    numEnabled++;
                }
            }
            // Add power and average
            SkillValue powerValue = new SkillValue(id, powerLevel, powerXp);
            powerLeaderboard.add(powerValue);
            double averageLevel = (double) powerLevel / numEnabled;
            SkillValue averageValue = new SkillValue(id, 0, averageLevel);
            averageLeaderboard.add(averageValue);

            loadedFromMemory.add(pluginPlayer.getBukkitPlayer().getUniqueId());
        }
        return loadedFromMemory;
    }

    protected void sortLeaderboards(Map<Skill, List<SkillValue>> leaderboards, List<SkillValue> powerLeaderboard, List<SkillValue> averageLeaderboard) {
        LeaderboardManager manager = plugin.getLeaderboardManager();
        LeaderboardSorter sorter = new LeaderboardSorter();
        for (Skill skill : Skills.values()) {
            leaderboards.get(skill).sort(sorter);
        }
        powerLeaderboard.sort(sorter);
        AverageSorter averageSorter = new AverageSorter();
        averageLeaderboard.sort(averageSorter);

        // Add skill leaderboards to map
        for (Skill skill : Skills.values()) {
            manager.setLeaderboard(skill, leaderboards.get(skill));
        }
        manager.setPowerLeaderboard(powerLeaderboard);
        manager.setAverageLeaderboard(averageLeaderboard);
        manager.setSorting(false);
    }

    public abstract void load(Player player);

    /**
     * Loads a snapshot of player data for an offline player
     *
     * @param uuid The uuid of the player
     * @return A PlayerDataState containing a snapshot of player data
     */
    @Nullable
    public abstract PlayerDataState loadState(UUID uuid);

    public void save(Player player) {
        save(player, true);
    }

    public abstract void save(Player player, boolean removeFromMemory);

    public abstract void loadBackup(FileConfiguration file, CommandSender sender);

    public abstract void updateLeaderboards();

    public abstract void delete(UUID uuid) throws IOException;

}
