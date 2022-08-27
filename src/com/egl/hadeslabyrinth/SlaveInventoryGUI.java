package com.egl.hadeslabyrinth;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;

public class SlaveInventoryGUI implements InventoryProvider {

    Slave slave;

    public SlaveInventoryGUI(Slave slave) {
	super();
	this.slave = slave;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
	contents.fillBorders(ClickableItem.empty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));

	String[] column1 = { "<- Helmet", "<- Chestplate", "<- Leggings", "<- Boots" };

	for (int i = 0; i < column1.length; i++) {
	    ItemStack label = new ItemStack(Material.PAPER);
	    ItemMeta im = label.getItemMeta();
	    im.setDisplayName(column1[i]);
	    label.setItemMeta(im);

	    contents.set(1 + i, 2, ClickableItem.of(label, e -> {
	    }));
	}

	for (int i = 0; i < 4; i++) {
	    ItemStack slot = slave.getItemFromInventory(i, false);

	    final Integer innerI = new Integer(i);

	    if (slot == null) {
		contents.set(1 + i, 1, ClickableItem.of(null, e -> {
		    addItem(player.getItemOnCursor(), innerI);
		    player.setItemOnCursor(null);
		    init(player, contents);
		}));
	    } else {
		contents.set(1 + i, 1, ClickableItem.of(slot, e -> {
		    if (player.getItemOnCursor() != null) {
			player.getInventory().addItem(player.getItemOnCursor());
			player.setItemOnCursor(null);
		    }

		    player.setItemOnCursor(takeItem(innerI));
		    init(player, contents);
		}));
	    }
	}

	String[] column2 = { "<- Hand", "<- Pendant", "<- Misc", "<- Misc" };

	for (int i = 0; i < column2.length; i++) {
	    ItemStack label = new ItemStack(Material.PAPER);
	    ItemMeta im = label.getItemMeta();
	    im.setDisplayName(column2[i]);
	    label.setItemMeta(im);

	    contents.set(1 + i, 4, ClickableItem.of(label, e -> {
	    }));
	}

	for (int i = 0; i < 4; i++) {
	    ItemStack slot = slave.getItemFromInventory(i + 4, false);

	    final Integer innerI = new Integer(i);

	    if (slot == null) {
		contents.set(1 + i, 3, ClickableItem.of(null, e -> {
		    addItem(player.getItemOnCursor(), innerI + 4);
		    player.setItemOnCursor(null);
		    init(player, contents);
		}));
	    } else {
		contents.set(1 + i, 3, ClickableItem.of(slot, e -> {
		    if (player.getItemOnCursor() != null) {
			player.getInventory().addItem(player.getItemOnCursor());
			player.setItemOnCursor(null);
		    }

		    player.setItemOnCursor(takeItem(innerI + 4));
		    init(player, contents);
		}));
	    }
	}

    }

    @Override
    public void update(Player player, InventoryContents contents) {
	// TODO Auto-generated method stub

    }

    private void addItem(ItemStack item, int slot) { // Slot IDs: 0 helmet, 1
						     // chestplate, 2 leggings,
						     // 3
						     // boots, 4 hand, 5
						     // pendant, 6 7 misc
	slave.addItemToInventory(item, slot);
    }

    private ItemStack takeItem(int slot) {
	return slave.getItemFromInventory(slot, true);
    }

}
