package com.archyx.aureliumskills.skills.foraging;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.api.event.LootDropCause;
import com.archyx.aureliumskills.data.PluginPlayer;
import com.archyx.aureliumskills.loot.handler.BlockLootHandler;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;
import com.archyx.lootmanager.loot.LootPool;
import org.bukkit.block.Block;

public class ForagingLootHandler extends BlockLootHandler {

    public ForagingLootHandler(AureliumSkills plugin) {
        super(plugin, Skills.FORAGING, Ability.FORAGER);
    }

    @Override
    public Source getSource(Block block) {
        return ForagingSource.getSource(block);
    }

    @Override
    public double getChance(LootPool pool, PluginPlayer pluginPlayer) {
        return getCommonChance(pool, pluginPlayer);
    }

    @Override
    public LootDropCause getCause(LootPool pool) {
        return LootDropCause.FORAGING_OTHER_LOOT;
    }
}
