package com.egl.hadeslabyrinth;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemFactory {

    public ItemFactory(HLPlugin plugin) {

    }

    public ItemStack create(int id) {
	Material mat = Material.STONE;
	ItemStack i = new ItemStack(mat, 1);
	ItemMeta im = i.getItemMeta();
	im.setCustomModelData(id);

	switch (id) {
	case 1000: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.GOLD + "Player Vault Expansion +1");
	    break;
	}
	case 1001: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Daily Lotto Ticket");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.YELLOW + "Good for one entry into the Daily Lotto.");
	    lore.add(ChatColor.RED + "Right-click to redeem.");
	    im.setLore(lore);
	    break;
	}
	case 1002: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Weekly Lotto Ticket");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.YELLOW + "Good for one entry into the Weekly Lotto.");
	    lore.add(ChatColor.RED + "Right-click to redeem.");
	    im.setLore(lore);
	    break;
	}
	case 1003: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.GREEN + "$5,000 Ticket");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Right click to redeem.");
	    im.setLore(lore);
	    break;
	}
	case 1004: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.GREEN + "$10,000 Ticket");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Right click to redeem.");
	    im.setLore(lore);
	    break;
	}
	case 1005: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.GREEN + "$20,000 Ticket");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Right click to redeem.");
	    im.setLore(lore);
	    break;
	}
	case 1006: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.GREEN + "$40,000 Ticket");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Right click to redeem.");
	    im.setLore(lore);
	    break;
	}
	case 1007: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.GREEN + "$80,000 Ticket");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Right click to redeem.");
	    im.setLore(lore);
	    break;
	}
	case 1008: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.GREEN + "$160,000 Ticket");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Right click to redeem.");
	    im.setLore(lore);
	    break;
	}
	case 1009: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.AQUA + "Chieftain Pickaxe Shard");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Collect 32 to synthesize a Chieftain Pickaxe.");
	    im.setLore(lore);
	    break;
	}
	case 1010: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.AQUA + "War Pickaxe Shard");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Collect 32 to synthesize a War Pickaxe.");
	    im.setLore(lore);
	    break;
	}
	case 1011: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.AQUA + "King Pickaxe Shard");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Collect 32 to synthesize a King Pickaxe.");
	    im.setLore(lore);
	    break;
	}
	case 1012: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.AQUA + "Hero Pickaxe Shard");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Collect 32 to synthesize a Hero Pickaxe.");
	    im.setLore(lore);
	    break;
	}
	case 1013: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.AQUA + "God Pickaxe Shard");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Collect 32 to synthesize a God Pickaxe.");
	    im.setLore(lore);
	    break;
	}
	case 1014: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.AQUA + "Immortal Pickaxe Shard");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Collect 32 to synthesize a Immortal Pickaxe.");
	    im.setLore(lore);
	    break;
	}
	case 1015: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.AQUA + "Titan Pickaxe Shard");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Collect 32 to synthesize a Titan Pickaxe.");
	    im.setLore(lore);
	    break;
	}
	case 1016: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.AQUA + "Olympian Pickaxe Shard");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Collect 32 to synthesize an Olympian Pickaxe.");
	    im.setLore(lore);
	    break;
	}
	case 1017: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.AQUA + "Chieftain Rank Shard");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Collect 32 to get the Chieftain Rank free.");
	    im.setLore(lore);
	    break;
	}
	case 1018: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.AQUA + "Warlord Rank Shard");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Collect 32 to get the Warlord Rank free.");
	    im.setLore(lore);
	    break;
	}
	case 1019: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.AQUA + "King Rank Shard");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Collect 32 to get the King Rank free.");
	    im.setLore(lore);
	    break;
	}
	case 1020: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.AQUA + "Hero Rank Shard");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Collect 32 to get the Hero Rank free.");
	    im.setLore(lore);
	    break;
	}
	case 1021: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.AQUA + "God Rank Shard");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Collect 32 to get the God Rank free.");
	    im.setLore(lore);
	    break;
	}
	case 1022: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.AQUA + "Immortal Rank Shard");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Collect 32 to get the Immortal Rank free.");
	    im.setLore(lore);
	    break;
	}
	case 1023: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.AQUA + "Titan Rank Shard");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Collect 32 to get the Titan Rank free.");
	    im.setLore(lore);
	    break;
	}
	case 1024: {
	    i.setType(Material.PAPER);
	    im.setDisplayName(ChatColor.AQUA + "Olympian Rank Shard");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Collect 32 to get the Olympian Rank free.");
	    im.setLore(lore);
	    break;
	}
	case 1500: {
	    i.setType(Material.DIAMOND);
	    im.setDisplayName(ChatColor.YELLOW + "Crystal");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "Glittering in the dim light, it has");
	    lore.add(ChatColor.DARK_PURPLE + "a seemingly magical aura.");
	    im.setLore(lore);
	    im.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
	    break;
	}
	case 1501: {
	    i.setType(Material.EMERALD);
	    im.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Hyper Crystal");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "A vibrant energy radiates off from");
	    lore.add(ChatColor.DARK_PURPLE + "this colorful stone.");
	    im.setLore(lore);
	    im.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
	    break;
	}
	case 1505: {
	    i.setType(Material.NETHER_STAR);
	    im.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "Paragon");
	    List<String> lore = new ArrayList<String>();
	    lore.add(ChatColor.DARK_PURPLE + "The divine essense is trapped");
	    lore.add(ChatColor.DARK_PURPLE + "within this powerful gem.");
	    im.setLore(lore);
	    im.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
	    break;
	}
	case 1600: {
	    i.setType(Material.DIAMOND_PICKAXE);
	    break;
	}
	case 1700: {
	    i.setType(Material.DIAMOND_SWORD);
	    im.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "Abyssimal Sword");
	    List<String> lore = new ArrayList<String>();
	    im.setLore(lore);
	    im.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
	    break;
	}
	}

	im.setCustomModelData(id);
	i.setItemMeta(im);

	return i;
    }

}
