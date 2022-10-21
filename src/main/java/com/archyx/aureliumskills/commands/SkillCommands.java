package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PluginPlayer;
import com.archyx.aureliumskills.menus.levelprogression.LevelProgressionOpener;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import org.bukkit.entity.Player;

public class SkillCommands {

    public static class SkillCommand extends BaseCommand {
        
        protected final AureliumSkills plugin;
        protected final Skill skill;
        
        public SkillCommand(AureliumSkills plugin, Skill skill) {
            this.plugin = plugin;
            this.skill = skill;
        }
        
        protected void openMenu(Player player, PluginPlayer pluginPlayer) {
            if (OptionL.isEnabled(skill)) {
                new LevelProgressionOpener(plugin).open(player, pluginPlayer, skill);
            } else {
                plugin.getCommandManager().formatMessage(plugin.getCommandManager().getCommandIssuer(player), MessageType.ERROR, MessageKeys.UNKNOWN_COMMAND);
            }
        }
    }

    @CommandAlias("farming")
    public static class FarmingCommand extends SkillCommand {
        
        public FarmingCommand(AureliumSkills plugin) {
            super(plugin, Skills.FARMING);
        }

        @Default
        public void onCommand(Player player) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer != null) {
                openMenu(player, pluginPlayer);
            }
        }
    }
    @CommandAlias("foraging")
    public static class ForagingCommand extends SkillCommand {

        public ForagingCommand(AureliumSkills plugin) {
            super(plugin, Skills.FORAGING);
        }

        @Default
        public void onCommand(Player player) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer != null) {
                openMenu(player, pluginPlayer);
            }
        }
    }
    @CommandAlias("mining")
    public static class MiningCommand extends SkillCommand {

        public MiningCommand(AureliumSkills plugin) {
            super(plugin, Skills.MINING);
        }

        @Default
        public void onCommand(Player player) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer != null) {
                openMenu(player, pluginPlayer);
            }
        }
    }
    @CommandAlias("fishing")
    public static class FishingCommand extends SkillCommand {

        public FishingCommand(AureliumSkills plugin) {
            super(plugin, Skills.FISHING);
        }

        @Default
        public void onCommand(Player player) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer != null) {
                openMenu(player, pluginPlayer);
            }
        }
    }
    @CommandAlias("excavation")
    public static class ExcavationCommand extends SkillCommand {

        public ExcavationCommand(AureliumSkills plugin) {
            super(plugin, Skills.EXCAVATION);
        }

        @Default
        public void onCommand(Player player) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer != null) {
                openMenu(player, pluginPlayer);
            }
        }
    }
    @CommandAlias("archery")
    public static class ArcheryCommand extends SkillCommand {

        public ArcheryCommand(AureliumSkills plugin) {
            super(plugin, Skills.ARCHERY);
        }
        
        @Default
        public void onCommand(Player player) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer != null) {
                openMenu(player, pluginPlayer);
            }
        }
    }
    @CommandAlias("defense")
    public static class DefenseCommand extends SkillCommand {

        public DefenseCommand(AureliumSkills plugin) {
            super(plugin, Skills.DEFENSE);
        }
        
        @Default
        public void onCommand(Player player) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer != null) {
                openMenu(player, pluginPlayer);
            }
        }
    }
    @CommandAlias("fighting")
    public static class FightingCommand extends SkillCommand {

        public FightingCommand(AureliumSkills plugin) {
            super(plugin, Skills.FIGHTING);
        }
        
        @Default
        public void onCommand(Player player) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer != null) {
                openMenu(player, pluginPlayer);
            }
        }
    }
    @CommandAlias("endurance")
    public static class EnduranceCommand extends SkillCommand {

        public EnduranceCommand(AureliumSkills plugin) {
            super(plugin, Skills.ENDURANCE);
        }
        
        @Default
        public void onCommand(Player player) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer != null) {
                openMenu(player, pluginPlayer);
            }
        }
    }
    @CommandAlias("agility")
    public static class AgilityCommand extends SkillCommand {

        public AgilityCommand(AureliumSkills plugin) {
            super(plugin, Skills.AGILITY);
        }
        
        @Default
        public void onCommand(Player player) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer != null) {
                openMenu(player, pluginPlayer);
            }
        }
    }
    @CommandAlias("alchemy")
    public static class AlchemyCommand extends SkillCommand {

        public AlchemyCommand(AureliumSkills plugin) {
            super(plugin, Skills.ALCHEMY);
        }

        @Default
        public void onCommand(Player player) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer != null) {
                openMenu(player, pluginPlayer);
            }
        }
    }
    @CommandAlias("enchanting")
    public static class EnchantingCommand extends SkillCommand {

        public EnchantingCommand(AureliumSkills plugin) {
            super(plugin, Skills.ENCHANTING);
        }

        @Default
        public void onCommand(Player player) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer != null) {
                openMenu(player, pluginPlayer);
            }
        }
    }
    @CommandAlias("sorcery")
    public static class SorceryCommand extends SkillCommand {

        public SorceryCommand(AureliumSkills plugin) {
            super(plugin, Skills.SORCERY);
        }

        @Default
        public void onCommand(Player player) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer != null) {
                openMenu(player, pluginPlayer);
            }
        }
    }
    @CommandAlias("healing")
    public static class HealingCommand extends SkillCommand {

        public HealingCommand(AureliumSkills plugin) {
            super(plugin, Skills.HEALING);
        }

        @Default
        public void onCommand(Player player) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer != null) {
                openMenu(player, pluginPlayer);
            }
        }
    }
    @CommandAlias("forging")
    public static class ForgingCommand extends SkillCommand {

        public ForgingCommand(AureliumSkills plugin) {
            super(plugin, Skills.FORGING);
        }

        @Default
        public void onCommand(Player player) {
            PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
            if (pluginPlayer != null) {
                openMenu(player, pluginPlayer);
            }
        }
    }
}
