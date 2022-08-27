package com.egl.hadeslabyrinth;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class DungeonManager {

    private HLPlugin plugin;

    private List<ChestLocation> chests;

    int chestsPerSecond;
    int thisIteration;
    int chestsRemaining;

    public DungeonManager(HLPlugin plugin) {
	this.plugin = plugin;
    }

    public void initialize() {
	chests = new ArrayList<ChestLocation>();

	chestsPerSecond = 10;
	thisIteration = 1;

	loadChests();
	// stockChests();
    }

    public void createDungeon(String name, Region region) {
	FileConfiguration dungeon = plugin.getConfig("dungeon");

	dungeon.createSection(name);

	dungeon.set(name + ".location.xmin", region.getMinimumPoint().getBlockX());
	dungeon.set(name + ".location.ymin", region.getMinimumPoint().getBlockY());
	dungeon.set(name + ".location.zmin", region.getMinimumPoint().getBlockZ());
	dungeon.set(name + ".location.xmax", region.getMaximumPoint().getBlockX());
	dungeon.set(name + ".location.ymax", region.getMaximumPoint().getBlockY());
	dungeon.set(name + ".location.zmax", region.getMaximumPoint().getBlockZ());

	dungeon.createSection(name + ".chests");

	plugin.saveConfig("dungeon");
    }

    public void addChest(String dungeonName, Location loc) {
	FileConfiguration dungeon = plugin.getConfig("dungeon");
	int num = dungeon.getConfigurationSection(dungeonName + ".chests").getKeys(false).size() + 1;
	dungeon.createSection(dungeonName + ".chests." + Integer.toString(num));

	String path = dungeonName + ".chests." + num;

	dungeon.set(path + ".x", loc.getBlockX());
	dungeon.set(path + ".y", loc.getBlockY());
	dungeon.set(path + ".z", loc.getBlockZ());

	plugin.saveConfig("dungeon");

	loadChests();
    }

    public void refillChests() {
	stockChests();
    }

    private void loadChests() {
	if (!chests.isEmpty()) {
	    chests.clear();
	}
	FileConfiguration dungeon = plugin.getConfig("dungeon");
	if (!dungeon.getKeys(false).isEmpty()) {
	    for (String key : dungeon.getKeys(false)) {
		if (!dungeon.getConfigurationSection(key + ".chests").getKeys(false).isEmpty()) {
		    for (String key2 : dungeon.getConfigurationSection(key + ".chests").getKeys(false)) {
			int x = dungeon.getInt(key + ".chests." + key2 + ".x");
			int y = dungeon.getInt(key + ".chests." + key2 + ".y");
			int z = dungeon.getInt(key + ".chests." + key2 + ".z");
			Location place = new Location(Bukkit.getWorld("prison"), x, y, z);
			chests.add(new ChestLocation(place, key));
		    }
		}
	    }
	}
    }

    private void stockChests() {
	plugin.getLogger().info("Starting to stock all the dungeon chests.");

	if (!chests.isEmpty()) {
	    World world = Bukkit.getWorld("prison");

	    int total = chests.size();
	    plugin.getLogger().info(total + " chests need to be restocked.");
	    chestsRemaining = total;
	    int requiredIterations = total / chestsPerSecond;

	    BukkitRunnable restocking = new BukkitRunnable() {
		int x;
		List<ChestLocation> chestChunk;

		@Override
		public void run() {
		    plugin.getLogger().info("Restocking a group of " + chestsPerSecond + " chests this second. ("
			    + thisIteration + "/" + requiredIterations + ")");

		    x = (thisIteration - 1) * chestsPerSecond;

		    if (chestsRemaining < chestsPerSecond) {
			chestChunk = chests.subList(x, x + (chestsPerSecond - chestsRemaining));
		    } else {
			chestChunk = chests.subList(x, x + chestsPerSecond);
		    }

		    for (ChestLocation c : chestChunk) {
			Block b = world.getBlockAt(c.loc);
			if (b.getType() == Material.CHEST) {
			    Chest ch = (Chest) b.getState();
			    ch.getInventory().clear();
			    int times = (int) (Math.random() * 10);

			    for (int i = 0; i != times; i++) {
				ch.getInventory().addItem(createArtefact());
			    }
			}

			chestsRemaining--;
		    }

		    if (thisIteration == requiredIterations) {
			thisIteration = 1;
			plugin.getLogger().info("All dungeon chests have been stocked.");
			plugin.getServer().broadcastMessage(
				ChatColor.BOLD + "" + ChatColor.BLUE + "All dungeon chests have been restocked!");
			this.cancel();
		    } else {
			thisIteration++;
		    }
		}
	    };

	    restocking.runTaskTimer(plugin, 0L, 20L);

	}
    }

    private ItemStack createArtefact() {
	Material mat = Material.STONE;
	double rand = Math.random();
	String itemType;
	String qualityOne;
	String qualityTwo;

	boolean glow = false;

	if (rand < .01) {
	    glow = true;
	    mat = Material.EMERALD;
	    itemType = " Gemstone";
	} else if (rand < .19) {
	    mat = Material.HEART_OF_THE_SEA;
	    itemType = " Orb";
	} else {
	    double randrand = Math.random();

	    if (randrand <= .25) {
		mat = Material.BOWL;
		itemType = " Bowl";
	    } else if (randrand > .25 && randrand <= .5) {
		mat = Material.FLINT;
		itemType = " Tool";
	    } else if (randrand > .5 && randrand <= .75) {
		mat = Material.SHULKER_SHELL;
		itemType = " Tripod";
	    } else {
		mat = Material.BOOK;
		itemType = " Tome";
	    }
	}

	double rand2 = Math.random();
	double rand2rand = Math.random();

	if (rand2 < .01) {
	    qualityOne = "Primordial";
	    qualityTwo = " Transcendental";
	    glow = true;
	} else if (rand2 < .09) {
	    glow = true;
	    if (rand2rand <= .25) {
		qualityOne = "Antiquarian";
		qualityTwo = " Transcendental";
	    } else if (rand2rand > .25 && rand2rand <= .5) {
		qualityOne = "Ancient";
		qualityTwo = " Transcendental";
	    } else if (rand2rand > .5 && rand2rand <= .75) {
		qualityOne = "Primordial";
		qualityTwo = " Mystic";
	    } else {
		qualityOne = "Primordial";
		qualityTwo = " Esoteric";
	    }
	} else {
	    if (rand2rand <= .25) {
		qualityOne = "Antiquarian";
		qualityTwo = " Mystic";
	    } else if (rand2rand > .25 && rand2rand <= .5) {
		qualityOne = "Antiquarian";
		qualityTwo = " Esoteric";
	    } else if (rand2rand > .5 && rand2rand <= .75) {
		qualityOne = "Ancient";
		qualityTwo = " Mystic";
	    } else {
		qualityOne = "Ancient";
		qualityTwo = " Esoteric";
	    }
	}

	ItemStack item = new ItemStack(mat, 1);
	ItemMeta meta = item.getItemMeta();
	if (glow) {
	    meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
	}
	meta.setDisplayName(ChatColor.YELLOW + qualityOne + "" + qualityTwo + "" + itemType);
	List<String> lore = new ArrayList<String>();
	lore.add(ChatColor.DARK_PURPLE + "Artefact");
	meta.setLore(lore);
	item.setItemMeta(meta);

	return item;
    }

    private class ChestLocation {
	Location loc;
	String dungeon;

	public ChestLocation(Location loc, String dungeon) {
	    this.loc = loc;
	    this.dungeon = dungeon;
	}
    }

    void sellArtefacts(Player p) {
	PlayerInventory inv = p.getInventory();
	int total = 0;

	ItemStack[] items = inv.getContents();

	for (ItemStack is : items) {
	    if (is != null && is.getItemMeta().hasLore() && is.getItemMeta().getLore().get(0).contains("Artefact")) {
		int itemValue = 0;

		if (is.getType() == Material.BOWL) {
		    itemValue += 100;
		} else if (is.getType() == Material.FLINT) {
		    itemValue += 100;
		} else if (is.getType() == Material.SHULKER_SHELL) {
		    itemValue += 300;
		} else if (is.getType() == Material.BOOK) {
		    itemValue += 300;
		} else if (is.getType() == Material.HEART_OF_THE_SEA) {
		    itemValue += 600;
		} else if (is.getType() == Material.EMERALD) {
		    itemValue += 1000;
		}

		if (is.getItemMeta().getDisplayName().contains("Antiquarian")) {
		    itemValue *= 1;
		} else if (is.getItemMeta().getDisplayName().contains("Ancient")) {
		    itemValue *= 10;
		} else if (is.getItemMeta().getDisplayName().contains("Primordial")) {
		    itemValue *= 100;
		}

		if (is.getItemMeta().getDisplayName().contains("Mystic")) {
		    itemValue *= 1;
		} else if (is.getItemMeta().getDisplayName().contains("Esoteric")) {
		    itemValue *= 10;
		} else if (is.getItemMeta().getDisplayName().contains("Transcendental")) {
		    itemValue *= 100;
		}

		itemValue *= is.getAmount();
		is.setAmount(0);

		total += itemValue;
	    }
	}

	inv.setContents(items);

	plugin.e.depositPlayer(p, total);
	p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD
		+ " >> All artefacts in your inventory have been sold for a total of $" + Integer.toString(total)
		+ "!");
    }
}
