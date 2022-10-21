package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PluginPlayer;
import com.archyx.aureliumskills.util.mechanics.DamageType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Strength {

	public void strength(EntityDamageByEntityEvent event, PluginPlayer pluginPlayer, DamageType damageType) {
		if (damageType == DamageType.HAND) {
			if (OptionL.getBoolean(Option.STRENGTH_HAND_DAMAGE)) {
				applyStrength(event, pluginPlayer);
			}
		}
		else if (damageType == DamageType.BOW) {
			if (OptionL.getBoolean(Option.STRENGTH_BOW_DAMAGE)) {
				applyStrength(event, pluginPlayer);
			}
		}
		else {
			applyStrength(event, pluginPlayer);
		}
	}

	private void applyStrength(EntityDamageByEntityEvent event, PluginPlayer pluginPlayer) {
		double strength = pluginPlayer.getStatLevel(Stats.STRENGTH);
		if (OptionL.getBoolean(Option.STRENGTH_USE_PERCENT)) {
			event.setDamage(event.getDamage() * (1 + (strength * OptionL.getDouble(Option.STRENGTH_MODIFIER)) / 100));
		} else {
			event.setDamage(event.getDamage() + strength * OptionL.getDouble(Option.STRENGTH_MODIFIER));
		}
	}

}
