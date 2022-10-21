package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PluginPlayer;
import org.bukkit.entity.Player;

public class StatLeveler {

	private final AureliumSkills plugin;

	public StatLeveler(AureliumSkills plugin) {
		this.plugin = plugin;
	}

	public void reloadStat(Player player, Stat stat) {
		if (stat.equals(Stats.HEALTH)) {
			plugin.getHealth().reload(player);
		}
		else if (stat.equals(Stats.LUCK)) {
			new Luck(plugin).reload(player);
		}
		else if (stat.equals(Stats.WISDOM)) {
			if (!OptionL.getBoolean(Option.WISDOM_ALLOW_OVER_MAX_MANA)) {
				PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
				if (pluginPlayer == null) return;
				if (pluginPlayer.getMana() > pluginPlayer.getMaxMana()) {
					pluginPlayer.setMana(pluginPlayer.getMaxMana());
				}
			}
		}
	}
	
}
