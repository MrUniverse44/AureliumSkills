package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PluginPlayer;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Toughness {

	public static void onDamage(EntityDamageByEntityEvent event, PluginPlayer pluginPlayer) {
		double toughness = pluginPlayer.getStatLevel(Stats.TOUGHNESS) * OptionL.getDouble(Option.TOUGHNESS_NEW_MODIFIER);
		event.setDamage(event.getDamage() * (1 - (-1.0 * Math.pow(1.01, -1.0 * toughness) + 1)));
	}
}
