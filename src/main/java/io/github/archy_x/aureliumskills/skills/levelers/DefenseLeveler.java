package io.github.archy_x.aureliumskills.skills.levelers;

import io.github.archy_x.aureliumskills.AureliumSkills;
import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.Setting;
import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.Source;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DefenseLeveler implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (Options.isEnabled(Skill.DEFENSE)) {
			if (!event.isCancelled()) {
				if (event.getEntity() instanceof Player) {
					Player p = (Player) event.getEntity();
					//Checks if in blocked world
					if (AureliumSkills.worldManager.isInBlockedWorld(p.getLocation())) {
						return;
					}
					//Checks if in blocked region
					if (AureliumSkills.worldGuardEnabled) {
						if (AureliumSkills.worldGuardSupport.isInBlockedRegion(p.getLocation())) {
							return;
						}
					}
					if (p.isBlocking()) {
						return;
					}
					Skill s = Skill.DEFENSE;
					double d = event.getDamage();
					if (event.getDamager() instanceof Player) {
						if (d * Options.getXpAmount(Source.PLAYER_DAMAGE) <= Options.getDoubleOption(Setting.DEFENSE_MAX)) {
							if (d * Options.getXpAmount(Source.PLAYER_DAMAGE) >= Options.getDoubleOption(Setting.DEFENSE_MIN)) {
								Leveler.addXp(p, s, d * Options.getXpAmount(Source.PLAYER_DAMAGE));
							}
						}
						else {
							Leveler.addXp(p, s, Options.getDoubleOption(Setting.DEFENSE_MAX));
						}
					} else {
						if (d * Options.getXpAmount(Source.MOB_DAMAGE) <= Options.getDoubleOption(Setting.DEFENSE_MAX)) {
							if (d * Options.getXpAmount(Source.MOB_DAMAGE) >= Options.getDoubleOption(Setting.DEFENSE_MIN)) {
								Leveler.addXp(p, s, d * Options.getXpAmount(Source.MOB_DAMAGE));
							}
						}
						else {
							Leveler.addXp(p, s, Options.getDoubleOption(Setting.DEFENSE_MAX));
						}
					}
				}
			}
		}
	}
}