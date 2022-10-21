package com.archyx.aureliumskills.modifier;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PluginPlayer;
import com.archyx.aureliumskills.requirement.Requirements;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.stats.StatLeveler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ModifierManager {

    private final AureliumSkills plugin;
    private final StatLeveler statLeveler;

    public ModifierManager(AureliumSkills plugin) {
        this.plugin = plugin;
        this.statLeveler = new StatLeveler(plugin);
    }

    public void reloadPlayer(Player player) {
        PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
        Requirements requirements = new Requirements(plugin);
        Modifiers modifiers = new Modifiers(plugin);
        Multipliers multipliers = new Multipliers(plugin);
        if (pluginPlayer != null) {
            Set<Stat> statsToReload = new HashSet<>();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (!(item.getType() == Material.AIR)) {
                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, item)) {
                    pluginPlayer.removeStatModifier(modifier.getName());
                    statsToReload.add(modifier.getStat());
                }
                for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, item)) {
                    pluginPlayer.removeMultiplier(multiplier.getName());
                }
                if (requirements.meetsRequirements(ModifierType.ITEM, item, player)) {
                    for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, item)) {
                        pluginPlayer.addStatModifier(modifier, false);
                        statsToReload.add(modifier.getStat());
                    }
                    for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, item)) {
                        pluginPlayer.addMultiplier(multiplier);
                    }
                }
            }
            ItemStack itemOffHand = player.getInventory().getItemInOffHand();
            if (!(itemOffHand.getType() == Material.AIR)) {
                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, itemOffHand)) {
                    pluginPlayer.removeStatModifier(modifier.getName() + ".Offhand");
                    statsToReload.add(modifier.getStat());
                }
                for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, itemOffHand)) {
                    pluginPlayer.removeMultiplier(multiplier.getName() + ".Offhand");
                }
                if (requirements.meetsRequirements(ModifierType.ITEM, itemOffHand, player)) {
                    for (StatModifier modifier : modifiers.getModifiers(ModifierType.ITEM, itemOffHand)) {
                        StatModifier offHandModifier = new StatModifier(modifier.getName() + ".Offhand", modifier.getStat(), modifier.getValue());
                        pluginPlayer.addStatModifier(offHandModifier, false);
                        statsToReload.add(modifier.getStat());
                    }
                    for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ITEM, itemOffHand)) {
                        Multiplier offHandMultiplier = new Multiplier(multiplier.getName() + ".Offhand", multiplier.getSkill(), multiplier.getValue());
                        pluginPlayer.addMultiplier(offHandMultiplier);
                    }
                }
            }
            EntityEquipment equipment = player.getEquipment();
            if (equipment != null) {
                for (ItemStack armor : equipment.getArmorContents()) {
                    if (armor != null) {
                        if (!(armor.getType() == Material.AIR)) {
                            for (StatModifier modifier : modifiers.getModifiers(ModifierType.ARMOR, armor)) {
                                pluginPlayer.removeStatModifier(modifier.getName());
                                statsToReload.add(modifier.getStat());
                            }
                            for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ARMOR, armor)) {
                                pluginPlayer.removeMultiplier(multiplier.getName());
                            }
                            if (requirements.meetsRequirements(ModifierType.ARMOR, armor, player)) {
                                for (StatModifier modifier : modifiers.getModifiers(ModifierType.ARMOR, armor)) {
                                    pluginPlayer.addStatModifier(modifier, false);
                                    statsToReload.add(modifier.getStat());
                                }
                                for (Multiplier multiplier : multipliers.getMultipliers(ModifierType.ARMOR, armor)) {
                                    pluginPlayer.addMultiplier(multiplier);
                                }
                            }
                        }
                    }
                }
            }
            for (Stat stat : statsToReload) {
                statLeveler.reloadStat(player, stat);
            }
        }
    }
}
