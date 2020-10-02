package com.archyx.aureliumskills.menu;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.Message;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.stats.Stat;
import com.cryptomorin.xseries.XMaterial;
import dev.dbassett.skullcreator.SkullCreator;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StatsMenu implements InventoryProvider{

	private final Player player;
	
	public StatsMenu(Player player) {
		this.player = player;
	}
	
	@Override
	public void init(Player player, InventoryContents contents) {
		contents.fill(ClickableItem.empty(MenuItems.getEmptyPane()));
		contents.set(SlotPos.of(1, 4), ClickableItem.empty(getPlayerHead(SkillLoader.playerStats.get(player.getUniqueId()))));
		contents.set(SlotPos.of(1, 1), ClickableItem.empty(getStatItem(
			Stat.STRENGTH, 14, new Message[] {Message.FORAGING_NAME, Message.FIGHTING_NAME, Message.SORCERY_NAME}, 
			new Message[] {Message.FARMING_NAME, Message.ARCHERY_NAME}, ChatColor.DARK_RED)));
		contents.set(SlotPos.of(1, 2), ClickableItem.empty(getStatItem(
			Stat.HEALTH, 1, new Message[] {Message.FARMING_NAME, Message.ALCHEMY_NAME}, 
			new Message[] {Message.FISHING_NAME, Message.DEFENSE_NAME, Message.HEALING_NAME}, ChatColor.RED)));
		contents.set(SlotPos.of(1, 3), ClickableItem.empty(getStatItem(
			Stat.REGENERATION, 4, new Message[] {Message.EXCAVATION_NAME, Message.ENDURANCE_NAME, Message.HEALING_NAME},
			new Message[] {Message.FIGHTING_NAME, Message.AGILITY_NAME}, ChatColor.GOLD)));
		contents.set(SlotPos.of(1, 5), ClickableItem.empty(getStatItem(
			Stat.LUCK, 13, new Message[] {Message.FISHING_NAME, Message.ARCHERY_NAME}, 
			new Message[] {Message.MINING_NAME, Message.EXCAVATION_NAME, Message.ENCHANTING_NAME}, ChatColor.DARK_GREEN)));
		contents.set(SlotPos.of(1, 6), ClickableItem.empty(getStatItem(
			Stat.WISDOM, 11, new Message[] {Message.AGILITY_NAME, Message.ENCHANTING_NAME}, 
			new Message[] {Message.ALCHEMY_NAME, Message.SORCERY_NAME, Message.FORAGING_NAME}, ChatColor.BLUE)));
		contents.set(SlotPos.of(1, 7), ClickableItem.empty(getStatItem(
			Stat.TOUGHNESS, 10, new Message[] {Message.MINING_NAME, Message.DEFENSE_NAME, Message.FORAGING_NAME}, 
			new Message[] {Message.FORAGING_NAME, Message.ENDURANCE_NAME}, ChatColor.DARK_PURPLE)));
	}

	@Override
	public void update(Player player, InventoryContents contents) {
		
		
	}

	private ItemStack getStatItem(Stat stat, int color, Message[] primarySkills, Message[] secondarySkills, ChatColor chatColor) {
		//Creates item and sets it to correct color
		ItemStack item = XMaterial.WHITE_STAINED_GLASS_PANE.parseItem();
		if (color == 14) {
			item = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
		}
		else if (color == 1) {
			item = XMaterial.ORANGE_STAINED_GLASS_PANE.parseItem();
		}
		else if (color == 4) {
			item = XMaterial.YELLOW_STAINED_GLASS_PANE.parseItem();
		}
		else if (color == 13) {
			item = XMaterial.GREEN_STAINED_GLASS_PANE.parseItem();
		}
		else if (color == 11) {
			item = XMaterial.BLUE_STAINED_GLASS_PANE.parseItem();
		}
		else if (color == 10) {
			item = XMaterial.PURPLE_STAINED_GLASS_PANE.parseItem();
		}
		//Sets item name
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(chatColor + Lang.getMessage(Message.valueOf(ChatColor.stripColor(stat.getDisplayName()).toUpperCase() + "_NAME")));
		List<String> lore = new LinkedList<String>();
		//Parses description and separates long descriptions
		String fullDesc = Lang.getMessage(Message.valueOf(ChatColor.stripColor(stat.getDisplayName()).toUpperCase() + "_DESCRIPTION"));
		String[] splitDesc = fullDesc.replaceAll("(?:\\s*)(.{1,"+ 38 +"})(?:\\s+|\\s*$)", "$1\n").split("\n");
		for (String s : splitDesc) {
			lore.add(ChatColor.GRAY + s);
		}
		lore.add(" ");
		//Formats primary skills array into comma separated string
		String primarySkillsMessage = "";
		for (Message m : primarySkills) {
			if (primarySkills[0] == m) {
				primarySkillsMessage += Lang.getMessage(m);
			}
			else {
				primarySkillsMessage += ", " + Lang.getMessage(m);
			}
		}
		//Formats secondary skills array into comma separated string
		String secondarySkillsMessage = "";
		for (Message m : secondarySkills) {
			if (secondarySkills[0] == m) {
				secondarySkillsMessage += Lang.getMessage(m);
			}
			else {
				secondarySkillsMessage += ", " + Lang.getMessage(m);
			}
		}
		//Add primary and secondary skill lists
		lore.add(ChatColor.GRAY + Lang.getMessage(Message.PRIMARY_SKILLS) + ": " + ChatColor.RESET + primarySkillsMessage);
		lore.add(ChatColor.GRAY + Lang.getMessage(Message.SECONDARY_SKILLS) + ": " + ChatColor.RESET + secondarySkillsMessage);
		lore.add(" ");
		//Add player stat levels and values
		if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
			PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
			lore.add(ChatColor.GRAY + Lang.getMessage(Message.YOUR_LEVEL) + ": " + stat.getColor() + playerStat.getStatLevel(stat));
			lore.add(" ");
			lore.addAll(Arrays.asList(getStatValue(stat, playerStat).split("\n")));
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack getPlayerHead(PlayerStat stat) {
		ItemStack item = SkullCreator.itemFromUuid(player.getUniqueId());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + player.getName());
		List<String> lore = new LinkedList<>();
		lore.add(ChatColor.DARK_RED + "  ➽ " + Lang.getMessage(Message.STRENGTH_NAME) + " " + ChatColor.WHITE + stat.getStatLevel(Stat.STRENGTH));
		lore.add(ChatColor.RED + "  ❤ " + Lang.getMessage(Message.HEALTH_NAME) + " " + ChatColor.WHITE + stat.getStatLevel(Stat.HEALTH));
		lore.add(ChatColor.GOLD + "  ❥ " + Lang.getMessage(Message.REGENERATION_NAME) + " " + ChatColor.WHITE + stat.getStatLevel(Stat.REGENERATION));
		lore.add(ChatColor.DARK_GREEN + "  ☘ " + Lang.getMessage(Message.LUCK_NAME) + " " + ChatColor.WHITE + stat.getStatLevel(Stat.LUCK));
		lore.add(ChatColor.BLUE + "  ✿ " + Lang.getMessage(Message.WISDOM_NAME) + " " + ChatColor.WHITE + stat.getStatLevel(Stat.WISDOM));
		lore.add(ChatColor.DARK_PURPLE + "  ✦ " + Lang.getMessage(Message.TOUGHNESS_NAME) + " " + ChatColor.WHITE + stat.getStatLevel(Stat.TOUGHNESS));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private String getStatValue(Stat stat, PlayerStat ps) {
		NumberFormat nf = new DecimalFormat("##.##");
		switch(stat) {
			case STRENGTH:
				double strengthLevel = ps.getStatLevel(Stat.STRENGTH);
				double attackDamage = strengthLevel * OptionL.getDouble(Option.STRENGTH_MODIFIER);
				if (OptionL.getBoolean(Option.STRENGTH_DISPLAY_DAMAGE_WITH_HEALTH_SCALING)) {
					attackDamage *= OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
				}
				return ChatColor.DARK_RED + "+"  +  nf.format(attackDamage) + " " + Lang.getMessage(Message.ATTACK_DAMAGE);
			case HEALTH:
				double modifier = ((double) ps.getStatLevel(Stat.HEALTH)) * OptionL.getDouble(Option.HEALTH_MODIFIER);
				double scaledHealth = modifier * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
				return ChatColor.RED + "+" + nf.format(scaledHealth) + " " + Lang.getMessage(Message.HP);
			case LUCK:
				double luck = ps.getStatLevel(Stat.LUCK) * OptionL.getDouble(Option.LUCK_MODIFIER);
				double doubleDropChance = (double) ps.getStatLevel(Stat.LUCK) * OptionL.getDouble(Option.LUCK_DOUBLE_DROP_MODIFIER) * 100;
				if (doubleDropChance > OptionL.getDouble(Option.LUCK_DOUBLE_DROP_PERCENT_MAX)) {
					doubleDropChance = OptionL.getDouble(Option.LUCK_DOUBLE_DROP_PERCENT_MAX);
				}
				return ChatColor.DARK_GREEN + "+" + nf.format(luck) + " " + Lang.getMessage(Message.LUCK_NAME) + "\n" + ChatColor.DARK_GREEN + Lang.getMessage(Message.DOUBLE_DROP_CHANCE) + ": " + nf.format(doubleDropChance) + "%";
			case REGENERATION:
				double saturatedRegen = ps.getStatLevel(Stat.REGENERATION) * OptionL.getDouble(Option.REGENERATION_SATURATED_MODIFIER) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
				double hungerFullRegen = ps.getStatLevel(Stat.REGENERATION) *  OptionL.getDouble(Option.REGENERATION_HUNGER_FULL_MODIFIER) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
				double almostFullRegen = ps.getStatLevel(Stat.REGENERATION) *  OptionL.getDouble(Option.REGENERATION_HUNGER_ALMOST_FULL_MODIFIER) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
				double manaRegen = ps.getStatLevel(Stat.REGENERATION) * OptionL.getDouble(Option.REGENERATION_MANA_MODIFIER);
				return ChatColor.GOLD + "+" + nf.format(saturatedRegen) + " " + Lang.getMessage(Message.SATURATED_REGEN) + "\n" + ChatColor.GOLD + "+" + nf.format(hungerFullRegen)
						+ " " + Lang.getMessage(Message.FULL_HUNGER_REGEN) + "\n" + ChatColor.GOLD + "+" + nf.format(almostFullRegen) + " " + Lang.getMessage(Message.ALMOST_FULL_HUNGER_REGEN)
						+ "\n" + ChatColor.AQUA + "+" + nf.format(manaRegen) + " " + Lang.getMessage(Message.MANA_REGEN);
			case TOUGHNESS:
				double toughness = ps.getStatLevel(Stat.TOUGHNESS) * OptionL.getDouble(Option.TOUGHNESS_NEW_MODIFIER);
				double damageReduction = (-1.0 * Math.pow(1.01, -1.0 * toughness) + 1) * 100;
				return ChatColor.DARK_PURPLE + "-" + nf.format(damageReduction) + "% " + Lang.getMessage(Message.INCOMING_DAMAGE);
			case WISDOM:
				double xpModifier = ps.getStatLevel(Stat.WISDOM) * OptionL.getDouble(Option.WISDOM_EXPERIENCE_MODIFIER) * 100;
				int anvilCostReduction = (int) (ps.getStatLevel(Stat.WISDOM) * OptionL.getDouble(Option.WISDOM_ANVIL_COST_MODIFIER));
				return ChatColor.BLUE + "+" + nf.format(xpModifier) + "% " + Lang.getMessage(Message.XP_GAIN) + "\n" + ChatColor.BLUE + Lang.getMessage(Message.ANVIL_COST_REDUCTION) + ": " + anvilCostReduction;
			default:
				return "";
		}
	}
	
	public static SmartInventory getInventory(Player player) {
		return SmartInventory.builder()
				.provider(new StatsMenu(player))
				.size(3, 9)
				.title(Lang.getMessage(Message.YOUR_STATS))
				.manager(AureliumSkills.invManager)
				.build();
	}
	
}
