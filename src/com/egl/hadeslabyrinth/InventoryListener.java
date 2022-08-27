package com.egl.hadeslabyrinth;

import java.util.HashMap;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryListener implements Listener {

    HLPlugin plugin;

    public InventoryListener(HLPlugin plugin) {
	this.plugin = plugin;
    }

    ItemStack upgradeItem(int toLevel, ItemStack item) {
	ItemMeta meta = item.getItemMeta();
	meta.setCustomModelData(toLevel);

	HashMap<Enchantment, Integer> enchantments = (HashMap<Enchantment, Integer>) item.getEnchantments();

	if (enchantments.containsKey(Enchantment.DURABILITY)) {
	    enchantments.replace(Enchantment.DURABILITY, toLevel);
	} else if (enchantments.containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
	    enchantments.replace(Enchantment.LOOT_BONUS_BLOCKS, toLevel);
	} else if (enchantments.containsKey(Enchantment.DIG_SPEED)) {
	    enchantments.replace(Enchantment.DIG_SPEED, toLevel);
	}

	item.addUnsafeEnchantments(enchantments);

	return item;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClickEvent(InventoryClickEvent event) {
	if (event.getClickedInventory() instanceof MerchantInventory) {
	    if (event.getAction() == InventoryAction.DROP_ONE_CURSOR) {
		MerchantInventory inv = (MerchantInventory) event.getInventory();
		ItemStack i = event.getCurrentItem();

		if (i.getItemMeta().hasCustomModelData()) {
		    int nextLevel = i.getItemMeta().getCustomModelData() + 1;
		    if (nextLevel < 1000) {
			ItemStack newItem = upgradeItem(nextLevel, i);
			Merchant merchant = inv.getMerchant();

			MerchantRecipe recipe = new MerchantRecipe(newItem, 1);
			ItemStack crystals = plugin.itemf.create(1500);
			crystals.setAmount((nextLevel ^ 2) + 20);

			recipe.addIngredient(i);
			recipe.addIngredient(crystals);

			merchant.setRecipe(0, recipe);
		    }
		}
	    }
	}
    }
}
