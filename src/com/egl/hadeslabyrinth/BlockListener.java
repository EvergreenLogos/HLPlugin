package com.egl.hadeslabyrinth;

import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BlockListener implements Listener {
    Logger logger;
    HLPlugin plugin;

    public BlockListener(HLPlugin plugin) {
	this.logger = plugin.getLogger();
	this.plugin = plugin;
    }

    private int calculateFortune(int fortune) {
	float x = ((1 / (fortune + 2)) + ((fortune + 1) / 2));

	return (int) Math.round(x);
    }

    private void autosell(Player p) {
	PlayerInventory i = p.getInventory();
	int total = 0;

	ItemStack[] items = i.getContents();

	for (ItemStack is : items) {
	    if (is != null && is.getType() == Material.COBBLESTONE) {
		total += (3 * is.getAmount());
		is.setAmount(0);
	    } else if (is != null && is.getType() == Material.STONE) {
		total += (12 * is.getAmount());
		is.setAmount(0);
	    } else if (is != null && is.getType() == Material.COAL) {
		total += (8 * is.getAmount());
		is.setAmount(0);
	    } else if (is != null && is.getType() == Material.IRON_INGOT) {
		total += (14 * is.getAmount());
		is.setAmount(0);
	    } else if (is != null && is.getType() == Material.GOLD_INGOT) {
		total += (20 * is.getAmount());
		is.setAmount(0);
	    } else if (is != null && is.getType() == Material.IRON_BLOCK) {
		total += (126 * is.getAmount());
		is.setAmount(0);
	    } else if (is != null && is.getType() == Material.GOLD_BLOCK) {
		total += (180 * is.getAmount());
		is.setAmount(0);
	    } else if (is != null && is.getType() == Material.DIAMOND && !is.getItemMeta().hasCustomModelData()) {
		total += (58 * is.getAmount());
		is.setAmount(0);
	    } else if (is != null && is.getType() == Material.DIAMOND_BLOCK) {
		total += (522 * is.getAmount());
		is.setAmount(0);
	    } else if (is != null && is.getType() == Material.EMERALD && !is.getItemMeta().hasCustomModelData()) {
		total += (82 * is.getAmount());
		is.setAmount(0);
	    } else if (is != null && is.getType() == Material.EMERALD_BLOCK) {
		total += (738 * is.getAmount());
		is.setAmount(0);
	    } else if (is != null && is.getType() == Material.LAPIS_LAZULI) {
		total += (16 * is.getAmount());
		is.setAmount(0);
	    }
	}

	i.setContents(items);

	plugin.e.depositPlayer(p, total);
	p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD
		+ " >> The contents of your inventory have been sold for a total of $" + Integer.toString(total) + "!");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onBlockBreak(BlockBreakEvent event) throws SQLException {
	if (!event.isCancelled()) {
	    if ((Player) event.getPlayer() != null) {
		int fortuneMultiplier = 1;

		Player p = (Player) event.getPlayer();
		Block b = (Block) event.getBlock();
		boolean autosell = p.hasPermission("hlp.autosell");
		if (p.getWorld() == plugin.getServer().getWorld("prison")) {
		    // int VIP = (int)
		    // plugin.database.getPlayerData(p.getName(), "vip");
		    // int rank = (int)
		    // plugin.database.getPlayerData(p.getName(), "rank");
		    // int prestige = (int)
		    // plugin.database.getPlayerData(p.getName(), "prestige");

		    // int VIP = (int)
		    // plugin.accounts.get(p.getName()).getVip();
		    int VIP = (int) plugin.pm.getPlayer(p.getName()).getVip();
		    // int rank = (int)
		    // plugin.accounts.get(p.getName()).getRank();
		    int rank = (int) plugin.pm.getPlayer(p.getName()).getRank();
		    // int prestige = (int)
		    // plugin.accounts.get(p.getName()).getPrestige();
		    int prestige = (int) plugin.pm.getPlayer(p.getName()).getPrestige();

		    double rate = (.05 * VIP) + (.01 * rank) + (.05 * prestige);

		    if (p.hasPermission("hlp.mp1")) {
			rate += .1;
			fortuneMultiplier = 2;
		    } else if (p.hasPermission("hlp.mp2")) {
			rate += .2;
			fortuneMultiplier = 3;
		    } else if (p.hasPermission("hlp.mp3")) {
			rate += .3;
			fortuneMultiplier = 4;
		    }

		    double rand = Math.random();
		    if (rand < rate) {
			// p.getWorld().dropItemNaturally(b.getLocation(),
			// plugin.itemf.create(1500));
			ItemStack i = plugin.itemf.create(1500);
			if (!p.getInventory().addItem(i).isEmpty()) {
			    p.sendMessage("INVENTORY FULL");
			    b.breakNaturally(i);
			    if (autosell) {
				autosell(p);
			    }
			}
		    }

		}

		if (p.getWorld() == plugin.getServer().getWorld("prison")) {

		    if (b.getType() == Material.IRON_ORE) {
			if (p.hasPermission("hlp.autosmelt")) {
			    ItemStack i = new ItemStack(Material.IRON_INGOT, 1);
			    p.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 2003);
			    if (!p.getInventory().addItem(i).isEmpty()) {
				p.sendMessage("INVENTORY FULL");
				b.breakNaturally(i);
				if (autosell) {
				    autosell(p);
				}
			    }
			    b.setType(Material.AIR);
			}
		    } else if (b.getType() == Material.GOLD_ORE) {
			if (p.hasPermission("hlp.autosmelt")) {
			    ItemStack i = new ItemStack(Material.GOLD_INGOT, 1);
			    p.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 2003);
			    if (!p.getInventory().addItem(i).isEmpty()) {
				p.sendMessage("INVENTORY FULL");
				b.breakNaturally(i);
				if (autosell) {
				    autosell(p);
				}
			    }
			    b.setType(Material.AIR);
			}
		    } else if (b.getType() == Material.COAL_ORE) {
			ItemStack pick = (ItemStack) p.getItemInHand();
			int fortune = pick.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
			int amount = calculateFortune(fortune);
			ItemStack i = new ItemStack(Material.COAL, amount);
			if (!p.getInventory().addItem(i).isEmpty()) {
			    p.sendMessage("INVENTORY FULL");
			    b.breakNaturally(i);
			    if (autosell) {
				autosell(p);
			    }
			}
			b.setType(Material.AIR);
		    } else if (b.getType() == Material.DIAMOND_ORE) {
			ItemStack pick = (ItemStack) p.getItemInHand();
			int fortune = pick.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
			int amount = (calculateFortune(fortune) * fortuneMultiplier);
			ItemStack i = new ItemStack(Material.DIAMOND, amount);
			if (!p.getInventory().addItem(i).isEmpty()) {
			    p.sendMessage("INVENTORY FULL");
			    b.breakNaturally(i);
			    if (autosell) {
				autosell(p);
			    }
			}
			b.setType(Material.AIR);
		    } else if (b.getType() == Material.EMERALD_ORE) {
			ItemStack pick = (ItemStack) p.getItemInHand();
			int fortune = pick.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
			int amount = (calculateFortune(fortune) * fortuneMultiplier);
			ItemStack i = new ItemStack(Material.EMERALD, amount);
			if (!p.getInventory().addItem(i).isEmpty()) {
			    p.sendMessage("INVENTORY FULL");
			    b.breakNaturally(i);
			    if (autosell) {
				autosell(p);
			    }
			}
			b.setType(Material.AIR);
		    } else if (b.getType() == Material.REDSTONE_ORE) {
			ItemStack pick = (ItemStack) p.getItemInHand();
			int fortune = pick.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
			int amount = (calculateFortune(fortune) * fortuneMultiplier);
			ItemStack i = new ItemStack(Material.REDSTONE, amount * 2);
			if (!p.getInventory().addItem(i).isEmpty()) {
			    p.sendMessage("INVENTORY FULL");
			    b.breakNaturally(i);
			    if (autosell) {
				autosell(p);
			    }
			}
			b.setType(Material.AIR);
		    } else if (b.getType() == Material.STONE) {
			ItemStack pick = (ItemStack) p.getItemInHand();
			int fortune = pick.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
			int amount = (calculateFortune(fortune) * fortuneMultiplier);
			ItemStack i = new ItemStack(Material.COBBLESTONE, amount);
			if (!p.getInventory().addItem(i).isEmpty()) {
			    p.sendMessage("INVENTORY FULL");
			    b.breakNaturally(i);
			    if (autosell) {
				autosell(p);
			    }
			}
			b.setType(Material.AIR);
		    } else if (b.getType() == Material.IRON_BLOCK) {
			ItemStack i = new ItemStack(Material.IRON_BLOCK, 1);
			if (!p.getInventory().addItem(i).isEmpty()) {
			    p.sendMessage("INVENTORY FULL");
			    b.breakNaturally(i);
			    if (autosell) {
				autosell(p);
			    }
			}
			b.setType(Material.AIR);
		    } else if (b.getType() == Material.GOLD_BLOCK) {
			ItemStack i = new ItemStack(Material.GOLD_BLOCK, 1);
			if (!p.getInventory().addItem(i).isEmpty()) {
			    p.sendMessage("INVENTORY FULL");
			    b.breakNaturally(i);
			    if (autosell) {
				autosell(p);
			    }
			}
			b.setType(Material.AIR);
		    } else if (b.getType() == Material.DIAMOND_BLOCK) {
			ItemStack i = new ItemStack(Material.DIAMOND_BLOCK, 1);
			if (!p.getInventory().addItem(i).isEmpty()) {
			    p.sendMessage("INVENTORY FULL");
			    b.breakNaturally(i);
			    if (autosell) {
				autosell(p);
			    }
			}
			b.setType(Material.AIR);
		    } else if (b.getType() == Material.EMERALD_BLOCK) {
			ItemStack i = new ItemStack(Material.EMERALD_BLOCK, 1);
			if (!p.getInventory().addItem(i).isEmpty()) {
			    p.sendMessage("INVENTORY FULL");
			    b.breakNaturally(i);
			    if (autosell) {
				autosell(p);
			    }
			}
			b.setType(Material.AIR);
		    }
		}
	    }
	}
    }
}
