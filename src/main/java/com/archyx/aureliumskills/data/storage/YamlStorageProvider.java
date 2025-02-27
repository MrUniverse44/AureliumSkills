package com.archyx.aureliumskills.data.storage;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.AbstractAbility;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.AbilityData;
import com.archyx.aureliumskills.data.PluginPlayer;
import com.archyx.aureliumskills.data.PlayerDataLoadEvent;
import com.archyx.aureliumskills.data.PlayerDataState;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.leaderboard.LeaderboardManager;
import com.archyx.aureliumskills.leaderboard.SkillValue;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.misc.KeyIntPair;
import com.archyx.aureliumskills.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YamlStorageProvider extends StorageProvider {

    public YamlStorageProvider(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public void load(Player player) {
        File file = new File(plugin.getDataFolder() + "/playerdata/" + player.getUniqueId() + ".yml");
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            PluginPlayer pluginPlayer = new PluginPlayer(player, plugin);
            try {
                // Make sure file name and uuid match
                UUID id = UUID.fromString(config.getString("uuid", player.getUniqueId().toString()));
                if (!player.getUniqueId().equals(id)) {
                    throw new IllegalArgumentException("File name and uuid field do not match!");
                }
                // Load skill data
                for (Skill skill : Skills.values()) {
                    String path = "skills." + skill.name().toLowerCase(Locale.ROOT) + ".";
                    int level = config.getInt(path + "level", 1);
                    double xp = config.getDouble(path + "xp", 0.0);
                    pluginPlayer.setSkillLevel(skill, level);
                    pluginPlayer.setSkillXp(skill, xp);
                    // Add stat levels
                    plugin.getRewardManager().getRewardTable(skill).applyStats(pluginPlayer, level);
                }
                // Load stat modifiers
                ConfigurationSection modifiersSection = config.getConfigurationSection("stat_modifiers");
                if (modifiersSection != null) {
                    for (String entry : modifiersSection.getKeys(false)) {
                        ConfigurationSection modifierEntry = modifiersSection.getConfigurationSection(entry);
                        if (modifierEntry != null) {
                            String name = modifierEntry.getString("name");
                            String statName = modifierEntry.getString("stat");
                            double value = modifierEntry.getDouble("value");
                            if (name != null && statName != null) {
                                Stat stat = plugin.getStatRegistry().getStat(statName);
                                StatModifier modifier = new StatModifier(name, stat, value);
                                pluginPlayer.addStatModifier(modifier);
                            }
                        }
                    }
                }
                pluginPlayer.setMana(config.getDouble("mana")); // Load mana
                // Load locale
                String locale = config.getString("locale");
                if (locale != null) {
                    pluginPlayer.setLocale(new Locale(locale));
                }
                // Load ability data
                ConfigurationSection abilitySection = config.getConfigurationSection("ability_data");
                if (abilitySection != null) {
                    for (String abilityName : abilitySection.getKeys(false)) {
                        ConfigurationSection abilityEntry = abilitySection.getConfigurationSection(abilityName);
                        if (abilityEntry != null) {
                            AbstractAbility ability = AbstractAbility.valueOf(abilityName.toUpperCase(Locale.ROOT));
                            if (ability != null) {
                                AbilityData abilityData = pluginPlayer.getAbilityData(ability);
                                for (String key : abilityEntry.getKeys(false)) {
                                    Object value = abilityEntry.get(key);
                                    abilityData.setData(key, value);
                                }
                            }
                        }
                    }
                }
                // Unclaimed item rewards
                List<String> unclaimedItemsList = config.getStringList("unclaimed_items");
                if (unclaimedItemsList.size() > 0) {
                    List<KeyIntPair> unclaimedItems = new ArrayList<>();
                    for (String entry : unclaimedItemsList) {
                        String[] splitEntry = entry.split(" ");
                        String itemKey = splitEntry[0];
                        int amount = 1;
                        if (splitEntry.length >= 2) {
                            amount = NumberUtil.toInt(splitEntry[1], 1);
                        }
                        unclaimedItems.add(new KeyIntPair(itemKey, amount));
                    }
                    pluginPlayer.setUnclaimedItems(unclaimedItems);
                    pluginPlayer.clearInvalidItems();
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
            } catch (Exception e) {
                Bukkit.getLogger().warning("There was an error loading player data for player " + player.getName() + " with UUID " + player.getUniqueId() + ", see below for details.");
                e.printStackTrace();
                PluginPlayer data = createNewPlayer(player);
                data.setShouldSave(false);
                sendErrorMessageToPlayer(player, e);
            }
        } else {
            createNewPlayer(player);
        }
    }

    @Override
    @Nullable
    public PlayerDataState loadState(UUID uuid) {
        File file = new File(plugin.getDataFolder() + "/playerdata/" + uuid + ".yml");
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            try {
                // Load skill data
                Map<Skill, Integer> skillLevels = new HashMap<>();
                Map<Skill, Double> skillXp = new HashMap<>();
                for (Skill skill : Skills.values()) {
                    String path = "skills." + skill.name().toLowerCase(Locale.ROOT) + ".";
                    int level = config.getInt(path + "level", 1);
                    double xp = config.getDouble(path + "xp", 0.0);
                    skillLevels.put(skill, level);
                    skillXp.put(skill, xp);
                }
                Map<String, StatModifier> statModifiers = new HashMap<>();
                // Load stat modifiers
                ConfigurationSection modifiersSection = config.getConfigurationSection("stat_modifiers");
                if (modifiersSection != null) {
                    for (String entry : modifiersSection.getKeys(false)) {
                        ConfigurationSection modifierEntry = modifiersSection.getConfigurationSection(entry);
                        if (modifierEntry != null) {
                            String name = modifierEntry.getString("name");
                            String statName = modifierEntry.getString("stat");
                            double value = modifierEntry.getDouble("value");
                            if (name != null && statName != null) {
                                Stat stat = plugin.getStatRegistry().getStat(statName);
                                StatModifier modifier = new StatModifier(name, stat, value);
                                statModifiers.put(name, modifier);
                            }
                        }
                    }
                }
                double mana = config.getDouble("mana"); // Load mana
                return new PlayerDataState(uuid, skillLevels, skillXp, statModifiers, mana);
            } catch (Exception e) {
                Bukkit.getLogger().warning("There was an error loading player data state for player with UUID " + uuid + ", see below for details.");
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void save(Player player, boolean removeFromMemory) {
        PluginPlayer pluginPlayer = playerManager.getPlayerData(player);
        if (pluginPlayer == null) return;
        if (pluginPlayer.shouldNotSave()) return;
        // Don't save if blank profile
        if (!OptionL.getBoolean(Option.SAVE_BLANK_PROFILES) && pluginPlayer.isBlankProfile()) {
            return;
        }
        // Save lock
        if (pluginPlayer.isSaving()) return;
        pluginPlayer.setSaving(true);
        // Load file
        File file = new File(plugin.getDataFolder() + "/playerdata/" + player.getUniqueId() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        try {
            config.set("uuid", player.getUniqueId().toString());
            // Save skill data
            for (Skill skill : Skills.values()) {
                String path = "skills." + skill.toString().toLowerCase(Locale.ROOT) + ".";
                config.set(path + "level", pluginPlayer.getSkillLevel(skill));
                config.set(path + "xp", pluginPlayer.getSkillXp(skill));
            }
            config.set("stat_modifiers", null); // Clear existing modifiers
            // Save stat modifiers
            int count = 0;
            for (StatModifier modifier : pluginPlayer.getStatModifiers().values()) {
                String path = "stat_modifiers." + count + ".";
                config.set(path + "name", modifier.getName());
                config.set(path + "stat", modifier.getStat().toString().toLowerCase(Locale.ROOT));
                config.set(path + "value", modifier.getValue());
                count++;
            }
            config.set("mana", pluginPlayer.getMana()); // Save mana
            // Save locale
            Locale locale = pluginPlayer.getLocale();
            if (locale != null) {
                config.set("locale", locale.toString());
            }
            // Save ability data
            for (AbilityData abilityData : pluginPlayer.getAbilityDataMap().values()) {
                String path = "ability_data." + abilityData.getAbility().toString().toLowerCase(Locale.ROOT) + ".";
                for (Map.Entry<String, Object> entry : abilityData.getDataMap().entrySet()) {
                    config.set(path + entry.getKey(), entry.getValue());
                }
            }
            // Save unclaimed items
            List<KeyIntPair> unclaimedItems = pluginPlayer.getUnclaimedItems();
            config.set("unclaimed_items", null);
            if (unclaimedItems != null && unclaimedItems.size() > 0) {
                List<String> stringList = new ArrayList<>();
                for (KeyIntPair unclaimedItem : unclaimedItems) {
                    stringList.add(unclaimedItem.getKey() + " " + unclaimedItem.getValue());
                }
                config.set("unclaimed_items", stringList);
            }
            config.save(file);
            if (removeFromMemory) {
                playerManager.removePlayerData(player.getUniqueId()); // Remove from memory
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("There was an error saving player data for player " + player.getName() + " with UUID " + player.getUniqueId() + ", see below for details.");
            e.printStackTrace();
        }
        pluginPlayer.setSaving(false); // Unlock
    }

    @Override
    public void loadBackup(FileConfiguration config, CommandSender sender) {
        ConfigurationSection playerDataSection = config.getConfigurationSection("player_data");
        Locale locale = plugin.getLang().getLocale(sender);
        if (playerDataSection != null) {
            try {
                for (String stringId : playerDataSection.getKeys(false)) {
                    UUID id = UUID.fromString(stringId);
                    // Load levels and xp from backup
                    Map<Skill, Integer> levels = getLevelsFromBackup(playerDataSection, stringId);
                    Map<Skill, Double> xpLevels = getXpLevelsFromBackup(playerDataSection, stringId);
                    PluginPlayer pluginPlayer = playerManager.getPlayerData(id);
                    if (pluginPlayer != null) {
                        applyData(pluginPlayer, levels, xpLevels);
                    } else {
                        // Load file for offline players
                        File file = new File(plugin.getDataFolder() + "/playerdata/" + id + ".yml");
                        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(file);
                        playerConfig.set("uuid", id.toString());
                        // Save skill data
                        for (Skill skill : Skills.values()) {
                            String path = "skills." + skill.toString().toLowerCase(Locale.ROOT) + ".";
                            playerConfig.set(path + "level", levels.get(skill));
                            playerConfig.set(path + "xp", xpLevels.get(skill));
                        }
                        // Save file
                        playerConfig.save(file);
                    }
                }
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.BACKUP_LOAD_LOADED, locale));
            } catch (Exception e) {
                sender.sendMessage(AureliumSkills.getPrefix(locale) + TextUtil.replace(Lang.getMessage(CommandMessage.BACKUP_LOAD_ERROR, locale), "{error}", e.getMessage()));
            }
        }
    }

    @Override
    public void updateLeaderboards() {
        LeaderboardManager manager = plugin.getLeaderboardManager();
        manager.setSorting(true);
        // Initialize lists
        Map<Skill, List<SkillValue>> leaderboards = new HashMap<>();
        for (Skill skill : Skills.values()) {
            leaderboards.put(skill, new ArrayList<>());
        }
        List<SkillValue> powerLeaderboard = new ArrayList<>();
        List<SkillValue> averageLeaderboard = new ArrayList<>();
        // Add players already in memory
        Set<UUID> loadedFromMemory = addLoadedPlayersToLeaderboards(leaderboards, powerLeaderboard, averageLeaderboard);

        File playerDataFolder = new File(plugin.getDataFolder() + "/playerdata");
        // Load data from files
        if (playerDataFolder.exists() && playerDataFolder.isDirectory()) {
            File[] files = playerDataFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".yml")) {
                        UUID id = UUID.fromString(file.getName().substring(0, file.getName().lastIndexOf('.')));
                        if (!loadedFromMemory.contains(id)) {
                            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                            try {
                                int powerLevel = 0;
                                double powerXp = 0;
                                int numEnabled = 0;
                                for (Skill skill : Skills.values()) {
                                    // Load from config
                                    String path = "skills." + skill.toString().toLowerCase(Locale.ROOT) + ".";
                                    int level = config.getInt(path + "level", 1);
                                    double xp = config.getDouble(path + "xp");
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
                            } catch (Exception e) {
                                Bukkit.getLogger().warning("[AureliumSkills] Error reading playerdata file " + file.getName() + ", see error below for details:");
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        sortLeaderboards(leaderboards, powerLeaderboard, averageLeaderboard);
    }

    @Override
    public void delete(UUID uuid) throws IOException {
        File file = new File(plugin.getDataFolder() + "/playerdata/" + uuid.toString() + ".yml");
        if (file.exists()) {
            boolean success = file.delete();
            if (!success) {
                throw new IOException("Unable to delete file");
            }
        } else {
            throw new IOException("File not found in playerdata folder");
        }
    }

}
