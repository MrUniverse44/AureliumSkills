package com.archyx.aureliumskills.item;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PluginPlayer;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.util.item.ItemUtils;
import com.archyx.aureliumskills.util.misc.KeyIntPair;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class UnclaimedItemsMenu implements InventoryProvider {

    private final AureliumSkills plugin;
    private final PluginPlayer pluginPlayer;

    public UnclaimedItemsMenu(AureliumSkills plugin, PluginPlayer pluginPlayer) {
        this.plugin = plugin;
        this.pluginPlayer = pluginPlayer;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        for (int slot = 0; slot < 54; slot++) {
            int row = slot / 9;
            int column = slot % 9;
            if (pluginPlayer.getUnclaimedItems().size() <= slot) { // Empty slot
                contents.set(row, column, ClickableItem.empty(new ItemStack(Material.AIR)));
            } else { // Slot with item
                KeyIntPair keyIntPair = pluginPlayer.getUnclaimedItems().get(slot);

                String itemKey = keyIntPair.getKey();
                int amount = keyIntPair.getValue();
                ItemStack item = plugin.getItemRegistry().getItem(itemKey);
                if (item == null) {
                    plugin.getLogger().warning("Could not find a registered item with key " + itemKey + " when claiming unclaimed item rewards");
                    continue;
                }
                item.setAmount(amount);
                contents.set(row, column, ClickableItem.of(getDisplayItem(item), event -> {
                    // Give item on click
                    ItemStack leftoverItem = ItemUtils.addItemToInventory(player, item);
                    if (leftoverItem == null) { // All items were added
                        pluginPlayer.getUnclaimedItems().remove(keyIntPair);
                        if (pluginPlayer.getUnclaimedItems().size() > 0) {
                            init(player, contents);
                        } else {
                            player.closeInventory();
                        }
                    } else if (leftoverItem.getAmount() != item.getAmount()) { // Some items could not fit
                        keyIntPair.setValue(leftoverItem.getAmount());
                        init(player, contents);
                    } else { // All items could not fit
                        player.sendMessage(Lang.getMessage(MenuMessage.INVENTORY_FULL, pluginPlayer.getLocale()));
                        player.closeInventory();
                    }
                }));
            }
        }
    }

    public static SmartInventory getInventory(AureliumSkills plugin, PluginPlayer pluginPlayer) {
        return SmartInventory.builder()
                .manager(plugin.getInventoryManager())
                .provider(new UnclaimedItemsMenu(plugin, pluginPlayer))
                .size(6, 9)
                .title(Lang.getMessage(MenuMessage.UNCLAIMED_ITEMS_TITLE, pluginPlayer.getLocale()))
                .build();
    }

    private ItemStack getDisplayItem(ItemStack baseItem) {
        ItemStack displayItem = baseItem.clone();
        ItemMeta meta = displayItem.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            } else {
                lore.add(" ");
            }
            lore.add(Lang.getMessage(MenuMessage.CLICK_TO_CLAIM, pluginPlayer.getLocale()));
            meta.setLore(lore);
        }
        displayItem.setItemMeta(meta);
        return displayItem;
    }

}
