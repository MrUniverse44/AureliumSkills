package com.archyx.aureliumskills.skills.excavation;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.api.event.LootDropCause;
import com.archyx.aureliumskills.data.PluginPlayer;
import com.archyx.aureliumskills.loot.handler.BlockLootHandler;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;
import com.archyx.lootmanager.loot.LootPool;
import org.bukkit.block.Block;

public class ExcavationLootHandler extends BlockLootHandler {

    public ExcavationLootHandler(AureliumSkills plugin) {
        super(plugin, Skills.EXCAVATION, Ability.EXCAVATOR);
    }

    @Override
    public Source getSource(Block block) {
        return ExcavationSource.getSource(block);
    }

    @Override
    public double getChance(LootPool pool, PluginPlayer pluginPlayer) {
        double chance = getCommonChance(pool, pluginPlayer);
        if (pool.getName().equals("rare") && plugin.getAbilityManager().isEnabled(Ability.METAL_DETECTOR)) {
            chance += (getValue(Ability.METAL_DETECTOR, pluginPlayer) / 100);
        } else if (pool.getName().equals("epic") && plugin.getAbilityManager().isEnabled(Ability.LUCKY_SPADES)) {
            chance += (getValue(Ability.LUCKY_SPADES, pluginPlayer) / 100);
        }
        return chance;
    }

    @Override
    public LootDropCause getCause(LootPool pool) {
        LootDropCause cause;
        if (pool.getName().equals("rare") && plugin.getAbilityManager().isEnabled(Ability.METAL_DETECTOR)) {
            cause = LootDropCause.METAL_DETECTOR;
        } else if (pool.getName().equals("epic") && plugin.getAbilityManager().isEnabled(Ability.LUCKY_SPADES)) {
            cause = LootDropCause.LUCKY_SPADES;
        } else {
            cause = LootDropCause.EXCAVATION_OTHER_LOOT;
        }
        return cause;
    }

}
