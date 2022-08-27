package com.egl.hadeslabyrinth;

import java.util.ArrayList;
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

public class SlaveSoldierGUI implements InventoryProvider {

    Slave slave;

    boolean doingCombat = false;

    public SlaveSoldierGUI(Slave slave) {
	super();
	this.slave = slave;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
	ItemStack dialog = new ItemStack(Material.PAPER, 1);
	ItemMeta im = dialog.getItemMeta();
	im.setDisplayName(slave.getName() + ":");
	List<String> lore = new ArrayList<String>();
	lore.add(((SlaveSoldierTrait) slave.getTrait()).randomLine());
	im.setLore(lore);
	dialog.setItemMeta(im);

	contents.fillBorders(ClickableItem.empty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));

	contents.set(2, 2, ClickableItem.of(new ItemStack(dialog), e -> {
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

	contents.set(4, 1, ClickableItem.of(follow, e -> slave.follow((Entity) player)));

	ItemStack inventory = new ItemStack(Material.CHEST);
	ItemMeta im3 = inventory.getItemMeta();
	im3.setDisplayName("Inventory");
	inventory.setItemMeta(im3);

	contents.set(4, 2, ClickableItem.of(inventory,
		e -> openSlaveInventory(player, (InventoryProvider) new SlaveInventoryGUI(slave))));

	ItemStack combat = new ItemStack(Material.IRON_SWORD);
	ItemMeta im4 = combat.getItemMeta();
	if (!doingCombat) {
	    im4.setDisplayName("Start Combat");
	} else {
	    im4.setDisplayName("End Combat");
	}
	combat.setItemMeta(im4);

	contents.set(4, 3, ClickableItem.of(combat, e -> {
	    if (!doingCombat) {
		((SlaveSoldierTrait) slave.getTrait()).startCombat();
		doingCombat = true;
	    } else {
		((SlaveSoldierTrait) slave.getTrait()).endCombat();
		doingCombat = false;
	    }
	    init(player, contents);
	    player.sendMessage(
		    ChatColor.BLUE + "[" + slave.getName() + "] " + ChatColor.GREEN + "I will do my best, my lord!");
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
}
