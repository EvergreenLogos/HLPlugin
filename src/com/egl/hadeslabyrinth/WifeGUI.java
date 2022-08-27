package com.egl.hadeslabyrinth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;

public class WifeGUI implements InventoryProvider {

    Slave slave;

    WifeTrait trait;

    public WifeGUI(Slave slave) {
	super();
	this.slave = slave;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
	ItemStack dialog = new ItemStack(Material.PAPER, 1);
	ItemMeta im = dialog.getItemMeta();
	im.setDisplayName(ChatColor.LIGHT_PURPLE + slave.getName() + ":");
	List<String> lore = new ArrayList<String>();
	lore.add(((WifeTrait) slave.getTrait()).randomLine());
	im.setLore(lore);
	dialog.setItemMeta(im);

	contents.fillBorders(ClickableItem.empty(new ItemStack(Material.RED_STAINED_GLASS_PANE)));

	contents.set(2, 2, ClickableItem.of(new ItemStack(dialog), e -> {
	}));

	ItemStack stats = new ItemStack(Material.EMERALD, 1);
	ItemMeta im4 = stats.getItemMeta();
	im4.setDisplayName(ChatColor.LIGHT_PURPLE + "Stats");
	List<String> lore2 = new ArrayList<String>();
	lore2.add("Affection: " + ((WifeTrait) slave.getTrait()).getAffection());
	im4.setLore(lore2);
	stats.setItemMeta(im4);

	contents.set(2, 4, ClickableItem.of(new ItemStack(stats), e -> {
	}));

	contents.set(2, 6, ClickableItem.of(new ItemStack(Material.BARRIER), e -> player.closeInventory()));

	ItemStack follow = new ItemStack(Material.COMPASS);
	ItemMeta im2 = follow.getItemMeta();
	if (!slave.isFollowing()) {
	    im2.setDisplayName("Follow");
	} else {
	    im2.setDisplayName("Stop following");
	}
	follow.setItemMeta(im2);

	contents.set(4, 1, ClickableItem.of(follow, e -> {
	    slave.follow((Entity) player);
	    init(player, contents);
	}));

	ItemStack gift = new ItemStack(Material.CHEST);
	ItemMeta im3 = gift.getItemMeta();
	im3.setDisplayName("Give gift");
	im3.setLore(Arrays.asList("Drag & drop an item here to gift."));
	gift.setItemMeta(im3);

	contents.set(4, 2, ClickableItem.of(gift, e -> {
	    // ((WifeTrait) slave.getTrait()).giveGift(item)
	    String response = trait.giveGift(player.getItemOnCursor().getType());
	    player.sendMessage(ChatColor.LIGHT_PURPLE + "[" + slave.getName() + "] " + ChatColor.GREEN + response);
	    player.setItemOnCursor(null);
	    player.closeInventory();
	}));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }

    private void openSlaveInventory(Player player, InventoryProvider inventory) {
	SmartInventory slaveInventory;
	slaveInventory = SmartInventory.builder().id("gui_main").provider((InventoryProvider) inventory).size(6, 9)
		.title(ChatColor.AQUA + slave.getName()).closeable(true).build();

	slaveInventory.open(player);
    }

    public void setTrait(WifeTrait trait) {
	this.trait = trait;
    }
}
