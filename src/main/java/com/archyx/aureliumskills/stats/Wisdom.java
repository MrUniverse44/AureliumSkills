package com.archyx.aureliumskills.stats;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PluginPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.AnvilInventory;

public class Wisdom implements Listener {

	private final AureliumSkills plugin;

	public Wisdom(AureliumSkills plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerExpChange(PlayerExpChangeEvent event) {
		Player player = event.getPlayer();
		//Check for disabled world
		if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
			return;
		}
		PluginPlayer pluginPlayer = plugin.getPlayerManager().getPlayerData(player);
		if (pluginPlayer == null) return;
		event.setAmount((int) (event.getAmount() * (1 + (pluginPlayer.getStatLevel(Stats.WISDOM) * OptionL.getDouble(Option.WISDOM_EXPERIENCE_MODIFIER)))));
	}
	
	@EventHandler
	public void onAnvilPrepare(PrepareAnvilEvent event) {
		PluginPlayer pluginPlayer = null;
		//Finds the viewer with the highest wisdom level
		for (HumanEntity entity : event.getViewers()) {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				//Check for disabled world
				if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
					return;
				}
				PluginPlayer checkedPluginPlayer = plugin.getPlayerManager().getPlayerData(player);
				if (checkedPluginPlayer != null) {
					if (pluginPlayer == null) {
						pluginPlayer = checkedPluginPlayer;
					} else if (pluginPlayer.getStatLevel(Stats.WISDOM) < checkedPluginPlayer.getStatLevel(Stats.WISDOM)) {
						pluginPlayer = checkedPluginPlayer;
					}
				}
			}
		}
		if (pluginPlayer != null) {
			AnvilInventory anvil = event.getInventory();
			double wisdom = pluginPlayer.getStatLevel(Stats.WISDOM) * OptionL.getDouble(Option.WISDOM_ANVIL_COST_MODIFIER);
			int cost = (int) Math.round(anvil.getRepairCost() * (1 - (-1.0 * Math.pow(1.025, -1.0 * wisdom) + 1)));
			if (cost > 0) {
				anvil.setRepairCost(cost);
			} else {
				anvil.setRepairCost(1);
			}
		}
	}
}
