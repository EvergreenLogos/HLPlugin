package com.egl.hadeslabyrinth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class UpgradeMerchant implements Listener, InventoryHolder {
    HLPlugin plugin;
    private Inventory inv;
    private Merchant merch;
    HumanEntity player;

    public UpgradeMerchant(HLPlugin plugin, Player player) {
	this.plugin = plugin;
	this.player = (HumanEntity) player;

	// inv = (MerchantInventory) Bukkit.createMerchant("Upgrade pickaxe");
	merch = Bukkit.createMerchant("Upgrade pickaxe");
    }

    @Override
    public Inventory getInventory() {
	return inv;
    }

    public void openInventory(final HumanEntity ent) {
	// ent.openInventory(inv);
	ent.openMerchant(merch, true);
    }

    ItemStack upgradeItem(int toLevel, ItemStack item) {
	ItemMeta meta = item.getItemMeta();
	meta.setCustomModelData(toLevel);
	item.setItemMeta(meta);

	Map<Enchantment, Integer> enchantments = item.getEnchantments();

	Map<Enchantment, Integer> copy = new HashMap<>();

	for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
	    copy.put(entry.getKey(), entry.getValue());
	}

	if (copy.containsKey(Enchantment.DURABILITY)) {
	    copy.replace(Enchantment.DURABILITY, toLevel);
	} else if (copy.containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
	    copy.replace(Enchantment.LOOT_BONUS_BLOCKS, toLevel);
	} else if (copy.containsKey(Enchantment.DIG_SPEED)) {
	    copy.replace(Enchantment.DIG_SPEED, toLevel);
	}

	item.addUnsafeEnchantments(copy);

	return item;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClickEvent(InventoryClickEvent event) {
	if (event.getClickedInventory() instanceof InventoryClickEvent)
	    return;

	plugin.getLogger().info("0" + event.getClickedInventory().toString());
	if ((MerchantInventory) event.getClickedInventory() instanceof MerchantInventory) {
	    plugin.getLogger().info("1 " + event.getWhoClicked().getName() + " " + player.getName());
	    if (event.getWhoClicked().getName().equals(player.getName())) {
		plugin.getLogger().info("2");

		if (event.getAction() == InventoryAction.PLACE_ONE || event.getAction() == InventoryAction.PLACE_ALL) {
		    MerchantInventory inv = (MerchantInventory) event.getInventory();
		    ItemStack i = event.getCursor();

		    plugin.getLogger().info("4");
		    if (!i.getItemMeta().hasCustomModelData()) {
			plugin.getLogger().info("He doesn't have any meta");
			ItemMeta im = i.getItemMeta();
			im.setCustomModelData(1);
			i.setItemMeta(im);
		    }

		    if (i.getItemMeta().hasCustomModelData()) {
			int nextLevel = i.getItemMeta().getCustomModelData() + 1;
			if (nextLevel < 1000) {
			    ItemStack newItem = upgradeItem(nextLevel, i);
			    Merchant merchant = inv.getMerchant();

			    if (inv.getItem(0) != null && inv.getItem(0).getType() != Material.DIAMOND_PICKAXE) {
				plugin.getLogger().info("not a pickaxe");
				event.setCancelled(true);
				return;
			    }

			    inv.addItem(newItem);

			    MerchantRecipe recipe = new MerchantRecipe(newItem, 1);
			    ItemStack crystals = plugin.itemf.create(1500);
			    crystals.setAmount((nextLevel ^ 2) + 20);

			    recipe.addIngredient(i);
			    recipe.addIngredient(crystals);

			    List<MerchantRecipe> merchantRecipes = new ArrayList<MerchantRecipe>();
			    merchantRecipes.add(recipe);

			    merchant.setRecipes(merchantRecipes);
			    event.getWhoClicked().openMerchant(merchant, true);
			    plugin.getLogger().info("5");
			}
		    }
		}
	    }
	}
    }
}
