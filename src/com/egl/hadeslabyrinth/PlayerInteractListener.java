package com.egl.hadeslabyrinth;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    Logger logger;
    HLPlugin plugin;

    public PlayerInteractListener(HLPlugin plugin) {
	this.logger = plugin.getLogger();
	this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
	if ((Player) event.getPlayer() != null) {
	    Player p = (Player) event.getPlayer();
	    ItemStack i = p.getItemInHand();

	    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) // we don't care
							       // if the player
							       // has anything
							       // in hand
	    {
		Block block = (Block) event.getClickedBlock();

		if (block.getState() instanceof Sign) {
		    Sign sign = (Sign) block.getState();

		    if (sign.getLine(0).equals("[Sell Inv]")) {
			if (sign.getLine(1).equals("Artefacts")) {
			    plugin.dManager.sellArtefacts(p);
			}
		    }
		}
	    }

	    if (i.getType() != Material.AIR) // if the player has nothing in
					     // hand.
	    {
		if (i.getItemMeta().hasCustomModelData()) {
		    int id = i.getItemMeta().getCustomModelData();
		    switch (event.getAction()) {
		    case RIGHT_CLICK_AIR:

			switch (id) {
			case 1000: {
			    p.sendMessage("ayy lmao");
			    // p.setHealth(0.0D);
			    p.getTargetBlockExact(200).breakNaturally();
			    break;
			}
			case 1001: {
			    int quantity = i.getAmount();
			    i.setAmount(0);

			    FileConfiguration config = plugin.getConfig("lotto");

			    int currentTotal = config.getInt("daily.total");
			    int newTotal = currentTotal + quantity;

			    for (int it = 1; it != quantity + 1; it++) {
				config.set("daily.tickets." + Integer.toString(currentTotal + it), p.getName());
			    }
			    config.set("daily.total", newTotal);
			    int newPot = newTotal * 100;
			    config.set("daily.pot", newPot);

			    plugin.saveConfig("lotto");

			    p.sendMessage(ChatColor.AQUA + "You have entered " + ChatColor.YELLOW + quantity
				    + ChatColor.AQUA + " tickets into the Daily Lotto.");
			    p.sendMessage(ChatColor.AQUA + "The total amount in the pot is now " + ChatColor.YELLOW
				    + "$" + newPot);

			    break;
			}
			case 1002: {
			    int quantity = i.getAmount();
			    i.setAmount(i.getAmount() - 1);
			    p.sendMessage(ChatColor.AQUA + "You have entered " + ChatColor.YELLOW + quantity
				    + ChatColor.AQUA + " tickets into the Weekly Lotto.");
			    break;
			}
			case 1003: {
			    i.setAmount(i.getAmount() - 1);
			    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
				    "eco give " + p.getName() + " " + 5000);
			    break;
			}
			case 1004: {
			    i.setAmount(i.getAmount() - 1);
			    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
				    "eco give " + p.getName() + " " + 10000);
			    break;
			}
			case 1005: {
			    i.setAmount(i.getAmount() - 1);
			    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
				    "eco give " + p.getName() + " " + 20000);
			    break;
			}
			case 1006: {
			    i.setAmount(i.getAmount() - 1);
			    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
				    "eco give " + p.getName() + " " + 40000);
			    break;
			}
			case 1007: {
			    i.setAmount(i.getAmount() - 1);
			    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
				    "eco give " + p.getName() + " " + 80000);
			    break;
			}
			case 1008: {
			    i.setAmount(i.getAmount() - 1);
			    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
				    "eco give " + p.getName() + " " + 160000);
			    break;
			}
			}

			break;
		    case LEFT_CLICK_AIR:
			break;
		    case LEFT_CLICK_BLOCK:
			break;
		    case PHYSICAL:
			break;
		    case RIGHT_CLICK_BLOCK: {
			switch (id) {
			case 1000: {
			    p.sendMessage("ayy lmao");
			    // p.setHealth(0.0D);
			    p.getTargetBlockExact(200).breakNaturally();
			    break;
			}
			case 1001: {
			    int quantity = i.getAmount();
			    i.setAmount(0);

			    FileConfiguration config = plugin.getConfig("lotto");

			    int currentTotal = config.getInt("daily.total");
			    int newTotal = currentTotal + quantity;

			    for (int it = 1; it != quantity + 1; it++) {
				config.set("daily.tickets." + Integer.toString(currentTotal + it), p.getName());
			    }
			    config.set("daily.total", newTotal);
			    int newPot = newTotal * 100;
			    config.set("daily.pot", newPot);

			    plugin.saveConfig("lotto");

			    p.sendMessage(ChatColor.AQUA + "You have entered " + ChatColor.YELLOW + quantity
				    + ChatColor.AQUA + " tickets into the Daily Lotto.");
			    p.sendMessage(ChatColor.AQUA + "The total amount in the pot is now " + ChatColor.YELLOW
				    + "$" + newPot);

			    break;
			}
			case 1002: {
			    int quantity = i.getAmount();
			    i.setAmount(i.getAmount() - 1);
			    p.sendMessage(ChatColor.AQUA + "You have entered " + ChatColor.YELLOW + quantity
				    + ChatColor.AQUA + " tickets into the Weekly Lotto.");
			    break;
			}
			case 1003: {
			    i.setAmount(i.getAmount() - 1);
			    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
				    "eco give " + p.getName() + " " + 5000);
			    break;
			}
			case 1004: {
			    i.setAmount(i.getAmount() - 1);
			    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
				    "eco give " + p.getName() + " " + 10000);
			    break;
			}
			case 1005: {
			    i.setAmount(i.getAmount() - 1);
			    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
				    "eco give " + p.getName() + " " + 20000);
			    break;
			}
			case 1006: {
			    i.setAmount(i.getAmount() - 1);
			    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
				    "eco give " + p.getName() + " " + 40000);
			    break;
			}
			case 1007: {
			    i.setAmount(i.getAmount() - 1);
			    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
				    "eco give " + p.getName() + " " + 80000);
			    break;
			}
			case 1008: {
			    i.setAmount(i.getAmount() - 1);
			    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
				    "eco give " + p.getName() + " " + 160000);
			    break;
			}
			}

			Block block = (Block) event.getClickedBlock();

			if (block.getType() == Material.CHEST) {
			    FileConfiguration kit = plugin.getConfig("kit");
			    ConfigurationSection section = (ConfigurationSection) kit.getConfigurationSection("chests");

			    for (final String chestName : section.getKeys(false)) {
				if (block.getX() == kit.getInt("chests." + chestName + ".x")) {
				    if (block.getY() == kit.getInt("chests." + chestName + ".y")) {
					if (block.getZ() == kit.getInt("chests." + chestName + ".z")) {
					    String kitName = kit.getString("chests." + chestName + ".kit");
					    plugin.getServer().dispatchCommand(p, "kit " + kitName);
					    event.setCancelled(true);
					}
				    }
				}
			    }
			}

		    }

			break;
		    default:
			break;

		    }
		}
	    } else // if user is not holding an item
	    {
		switch (event.getAction()) {
		case RIGHT_CLICK_AIR:
		    break;
		case LEFT_CLICK_AIR:
		    break;
		case LEFT_CLICK_BLOCK:
		    break;
		case PHYSICAL:
		    break;
		case RIGHT_CLICK_BLOCK:

		    Block block = (Block) event.getClickedBlock();

		    if (block.getState() instanceof Sign) {
			Sign sign = (Sign) event.getClickedBlock().getState();

			if (sign.getLine(0).equals("[Sell Inv]")) {
			    if (sign.getLine(1).equals("Artefacts")) {
				plugin.dManager.sellArtefacts(p);
			    }
			}
		    } else if (block.getType() == Material.CHEST) {
			FileConfiguration kit = plugin.getConfig("kit");
			ConfigurationSection section = (ConfigurationSection) kit.getConfigurationSection("chests");

			for (final String chestName : section.getKeys(false)) {
			    if (block.getX() == kit.getInt("chests." + chestName + ".x")) {
				if (block.getY() == kit.getInt("chests." + chestName + ".y")) {
				    if (block.getZ() == kit.getInt("chests." + chestName + ".z")) {
					String kitName = kit.getString("chests." + chestName + ".kit");
					plugin.getServer().dispatchCommand(p, "kit " + kitName);
					event.setCancelled(true);
				    }
				}
			    }
			}
		    }

		    break;
		default:
		    break;
		}
	    }
	}
    }
}
