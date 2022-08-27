package com.egl.hadeslabyrinth;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.Collator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.internal.annotation.Selection;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionOwner;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import fr.minuskube.inv.InventoryManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

class FurnaceLock {
    public Location loc;
    public String owner;
}

public class HLPlugin extends JavaPlugin {
    ItemFactory itemf;
    MySQL database;
    // HTTPServer httpServer;

    // private List<FurnaceLock> furnaceLocks = new ArrayList<FurnaceLock>();

    Economy e;
    private Permission perms;
    private Chat chat;

    private File lottoFile = null;
    private FileConfiguration lotto = null;

    private File kitFile = null;
    private FileConfiguration kit = null;

    private File dungeonFile = null;
    private FileConfiguration dungeon = null;

    // private DuelManager dm;

    public DungeonManager dManager;

    private ConsoleCommandSender cons;

    // public HashMap<String, PlayerAccount> accounts = new HashMap<>();

    int time;
    int taskID;

    public HashMap<Integer, String> staffRanks = new HashMap<>();

    // private List<String> MIDIQueue = new ArrayList<String>();

    // private HashMap<Integer, Integer> rankPrices = new HashMap<>();

    private HashMap<Integer, String> rankNames = new HashMap<>();
    private HashMap<Integer, Integer[]> rankupConditions = new HashMap<>();

    private int[] levelUpExp = new int[50];

    RegionContainer rContainer;
    RegionManager regions;
    ProtectedRegion citadel;

    // int radioTimeRemaining = 0;
    // private File radioCurrentlyPlaying;
    // private File radioTime;

    SlaveFactory sf;
    HashMap<UUID, Slave> slaves;

    PlayerManager pm;

    InventoryManager im;

    HashMap<String, SlaveFormation> slaveFormations;

    @Override
    public void onEnable() {
	// Initialize

	getLogger().info("Enabling Hades Labyrinth plugin.");

	cons = getServer().getConsoleSender();

	// Set up Vault

	if (!setupEconomy()) {
	    this.getLogger().severe("Severe! No vault dependency found.");
	    Bukkit.getPluginManager().disablePlugin(this);
	}
	setupChat();
	setupPermissions();

	// Default configuration

	saveDefaultConfig();

	// Set up the configuration files.

	lottoFile = new File(getDataFolder(), "lotto.yml");
	if (!lottoFile.exists()) {
	    saveResource("lotto.yml", false);
	}
	lotto = YamlConfiguration.loadConfiguration(lottoFile);

	kitFile = new File(getDataFolder(), "kit.yml");
	if (!kitFile.exists()) {
	    saveResource("kit.yml", false);
	}
	kit = YamlConfiguration.loadConfiguration(kitFile);

	dungeonFile = new File(getDataFolder(), "dungeon.yml");
	if (!dungeonFile.exists()) {
	    saveResource("dungeon.yml", false);
	}
	dungeon = YamlConfiguration.loadConfiguration(dungeonFile);

	// radioCurrentlyPlaying = new File(getDataFolder(),
	// "radioCurrentlyPlaying.txt");
	// if (!radioCurrentlyPlaying.exists())
	// {
	// saveResource("radioCurrentlyPlaying.txt", false);
	// }
	//
	// radioTime = new File(getDataFolder(), "radioTime.txt");
	// if (!radioTime.exists())
	// {
	// saveResource("radioTime.txt", false);
	// }

	// Start the event listeners

	getServer().getPluginManager().registerEvents(new LoginListener(this), this);
	getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
	getServer().getPluginManager().registerEvents(new BlockListener(this), this);
	getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
	getServer().getPluginManager().registerEvents(new MobSpawnListener(this), this);
	getServer().getPluginManager().registerEvents(new EconomyListener(this), this);

	// Create the item factory

	this.itemf = new ItemFactory(this);

	// Create the duel manager

	// this.dm = new DuelManager(this);

	// Create the database interface

	this.database = new MySQL(this);
	database.connect();

	// Create the Slave Factory

	this.sf = new SlaveFactory(this);

	this.slaves = new HashMap<UUID, Slave>();

	// Create the player manager

	this.pm = new PlayerManager(this);

	// Create the inventory manager

	this.im = new InventoryManager(this);
	im.init();

	// Start the timer

	setTimer(3600);
	runTimer();

	// Start the HTTP Server

	// this.httpServer = new HTTPServer(this, "192.168.1.237", 9000);
	// try {
	// httpServer.run();
	// } catch (UnknownHostException e) {
	// // TODO Auto-generated catch block
	// this.getLogger().severe("Error: Unknown Host");
	// e.printStackTrace();
	// }

	rContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
	regions = rContainer.get(BukkitAdapter.adapt(Bukkit.getWorld("prison")));
	citadel = regions.getRegion("citadel");

	this.dManager = new DungeonManager(this);
	dManager.initialize();

	// try {
	// this.rankPrices = database.loadRankPrices(this.rankPrices);
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }

	rankNames.put(0, "Unranked");
	rankNames.put(1, "Lower 8");
	rankNames.put(2, "Upper 8");
	rankNames.put(3, "Lower 7");
	rankNames.put(4, "Upper 7");
	rankNames.put(5, "Lower 6");
	rankNames.put(6, "Upper 6");
	rankNames.put(7, "Lower 5");
	rankNames.put(8, "Upper 5");
	rankNames.put(9, "Lower 4");
	rankNames.put(10, "Upper 4");
	rankNames.put(11, "Lower 3");
	rankNames.put(12, "Upper 3");
	rankNames.put(13, "Lower 2");
	rankNames.put(14, "Upper 2");
	rankNames.put(15, "Lower 1");
	rankNames.put(16, "Upper 1");

	// formula for levels: y = (2x^2) + 5

	// money, level
	rankupConditions.put(1, new Integer[] { 1000, 7 });
	rankupConditions.put(2, new Integer[] { 3000, 13 });
	rankupConditions.put(3, new Integer[] { 6000, 23 });
	rankupConditions.put(4, new Integer[] { 12000, 37 });
	rankupConditions.put(5, new Integer[] { 26000, 55 });
	rankupConditions.put(6, new Integer[] { 38000, 76 });
	rankupConditions.put(7, new Integer[] { 52000, 103 });
	rankupConditions.put(8, new Integer[] { 66000, 133 });
	rankupConditions.put(9, new Integer[] { 80000, 167 });
	rankupConditions.put(10, new Integer[] { 98000, 206 });
	rankupConditions.put(11, new Integer[] { 116000, 246 });
	rankupConditions.put(12, new Integer[] { 134000, 293 });
	rankupConditions.put(13, new Integer[] { 162000, 345 });
	rankupConditions.put(14, new Integer[] { 192000, 397 });
	rankupConditions.put(15, new Integer[] { 240000, 455 });
	rankupConditions.put(16, new Integer[] { 300000, 517 });

	// First 50 levels exp costs are stored on memory, levels 51+ are
	// calculated on demand, levels 250+ will use different algorithm

	calculateFirst50Levels();

	// levelUpExp[0] = 1000; // from lv. 1 to 2
	// levelUpExp[1] = 5000; // from lv. 2 to 3 etc.

	slaveFormations = new HashMap<String, SlaveFormation>();
    }

    @Override
    public void onDisable() {
	getLogger().info("Disabling Hades Labyrinth plugin.");
	database.disconnect();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	String commandName = cmd.getName().toLowerCase();

	switch (commandName) {
	case "procure": {
	    if (args.length < 2) {
		return false;
	    }
	    try {
		Player p = (Player) sender;
		ItemStack i = itemf.create(Integer.parseInt(args[0]));
		if (args[1] != null) {
		    i.setAmount(Integer.parseInt(args[1]));
		}
		p.getInventory().addItem(i);
		sender.sendMessage("Procured " + i.getItemMeta().getDisplayName() + " (ID: " + args[0] + ")");
	    } catch (Error e) {
		e.printStackTrace();
		return false;
	    }

	    return true;
	}
	case "bestow": {
	    if (args.length < 3) {
		return false;
	    }
	    try {
		Player p = (Player) Bukkit.getPlayer(args[2]);
		ItemStack i = itemf.create(Integer.parseInt(args[0]));
		if (args[1] != null) {
		    i.setAmount(Integer.parseInt(args[1]));
		}
		p.getInventory().addItem(i);
		sender.sendMessage(
			"Bestowed " + i.getItemMeta().getDisplayName() + " (ID: " + args[0] + ") upon " + args[2]);
	    } catch (Error e) {
		e.printStackTrace();
		return false;
	    }

	    return true;
	}
	case "lotto": {
	    if (args.length == 0) {
		return false;
	    }

	    switch (args[0]) {
	    case "draw": {
		int tickets;
		int winningNumber;
		int pot;
		Player winner;
		Random rand = new Random();

		switch (args[1]) {
		case "daily": {
		    tickets = lotto.getInt("daily.total");
		    winningNumber = 1 + rand.nextInt(tickets);
		    winner = getServer().getPlayer(lotto.getString("daily.tickets." + Integer.toString(winningNumber)));
		    pot = Integer.parseInt(lotto.getString("daily.pot"));
		    winner.sendMessage(
			    ChatColor.AQUA + "" + ChatColor.BOLD + "You have won the Daily Lotto with ticket number "
				    + winningNumber + "! Congradulations!");
		    winner.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD
			    + "You have been awarded the pot, totalling " + ChatColor.YELLOW + "" + ChatColor.BOLD + "$"
			    + pot + ChatColor.AQUA + "" + ChatColor.BOLD + "!");
		    getServer().dispatchCommand(getServer().getConsoleSender(),
			    "eco give " + winner.getName() + " " + pot);

		    lotto.set("daily.total", 0);
		    lotto.set("daily.pot", 0);
		    lotto.set("daily.tickets", null);

		    return true;
		}
		case "weekly": {
		    tickets = lotto.getInt("weekly.total");
		    return true;
		}
		default: {
		    return false;
		}
		}
	    }
	    default:
		return false;
	    }
	}
	case "rankup": {
	    Player p = (Player) sender;
	    int rank = 0;

	    // try {
	    // rank = (Integer)
	    // database.getPlayerData(p.getUniqueId().toString(), "Rank");
	    // prestige = (Integer)
	    // database.getPlayerData(p.getUniqueId().toString(), "Prestige") +
	    // 1;
	    // } catch (SQLException e) {
	    // e.printStackTrace();
	    // return false;
	    // }

	    // TODO: Change this so that we store the rank and price information
	    // in RAM, so as not to waste resources on an SQL call.
	    // Looks like there are a lot of SQL calls in this function, which
	    // is probably why it is hindering performance.

	    // rank = (Integer) accounts.get(p.getName()).getRank();
	    rank = (Integer) pm.getPlayer(p.getName()).getRank();
	    // prestige = (Integer) accounts.get(p.getName()).getPrestige();
	    // prestige = (Integer) pm.getPlayer(p.getName()).getPrestige();

	    int nextRank = rank + 1;
	    int price = 0;
	    int levelRequirement = 0;

	    price = rankupConditions.get(nextRank)[0];

	    levelRequirement = rankupConditions.get(nextRank)[1];

	    if (e.has((OfflinePlayer) sender, Double.valueOf(price))
		    && pm.getPlayer(sender.getName()).getLevel() >= levelRequirement) {
		try {
		    e.withdrawPlayer((OfflinePlayer) sender, Double.valueOf(price));
		    promote((Player) sender, 0);
		    sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD
			    + " >> You have successfully been promoted to rank " + rankNames.get(nextRank) + "!");
		} catch (SQLException e1) {
		    e1.printStackTrace();
		    return false;
		}
	    } else {
		sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD
			+ " >> Sorry! You do not meet the requirements for promotion!");
		int need = (int) (price - e.getBalance((OfflinePlayer) sender));
		if (need < 0) {
		    need = 0;
		}
		sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " >> You still need " + ChatColor.BLUE + ""
			+ ChatColor.BOLD + "$" + need + ChatColor.RED + "" + ChatColor.BOLD + " and attain level "
			+ ChatColor.BLUE + "" + ChatColor.BOLD + levelRequirement + ChatColor.RED + "" + ChatColor.BOLD
			+ ".");
	    }

	    return true;
	}
	case "prestige": {
	    Player p = (Player) sender;
	    // int currentPrestige = accounts.get(p.getName()).getPrestige();
	    int currentPrestige = pm.getPlayer(p.getName()).getPrestige();
	    int price = (currentPrestige + 1) * 200000000;

	    if (e.has(p, Double.valueOf(price))) {
		e.withdrawPlayer((OfflinePlayer) sender, Double.valueOf(price));
		sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD
			+ " >> You have successfully prestiged to prestige " + (currentPrestige + 1) + "!");
		pm.getPlayer(p.getName()).setPrestige(currentPrestige + 1);
		// accounts.get(p.getName()).setPrestige(currentPrestige+1);
	    } else {
		sender.sendMessage(
			ChatColor.RED + "" + ChatColor.BOLD + " >> Sorry! You do not have enough money to prestige!");
		int need = (int) (price - e.getBalance((OfflinePlayer) sender));
		sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " >> You still need " + ChatColor.BLUE + ""
			+ ChatColor.BOLD + "$" + need + ChatColor.RED + "" + ChatColor.BOLD + " more to rank up.");
	    }

	    return true;
	}
	case "setkitchest": {
	    Player player = (Player) sender;
	    Block chest = (Block) player.getTargetBlock(null, 5);

	    if (chest.getType() == Material.CHEST) {
		String name = args[0];
		kit.createSection("chests." + name);
		kit.set("chests." + name + ".x", chest.getX());
		kit.set("chests." + name + ".y", chest.getY());
		kit.set("chests." + name + ".z", chest.getZ());
		kit.set("chests." + name + ".kit", args[1]);

		saveConfig("kit");

		sender.sendMessage("Succeed!");
	    } else {
		sender.sendMessage("You must be looking at a chest.");
		return false;
	    }

	    return true;
	}
	case "duel": {
	    return false;
	}
	case "promote": {
	    if (args.length < 2) {
		return false;
	    }
	    Player target = getServer().getPlayer(args[0]);

	    if (args[1].equals("Staff")) {
		try {
		    if ((Integer) database.getStaffData(target.getName(), "Rank") <= (Integer) database
			    .getStaffData(sender.getName(), "Rank")) {
			sender.sendMessage(ChatColor.RED
				+ "You cannot promote someone of a staff rank higher than or equal to your own.");
			return false;
		    }
		} catch (SQLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }

	    try {
		if (!promote(target, Integer.parseInt(args[1]))) {
		    sender.sendMessage(ChatColor.BLUE
			    + "You have entered an invalid ladder. Valid ladders are: 'Prison', 'VIP', 'Staff'.");
		} else {
		    sender.sendMessage(ChatColor.BLUE + "Promotion of player " + target.getName() + " successful.");
		}

	    } catch (SQLException e) {
		e.printStackTrace();
		return false;
	    }
	    return true;
	}
	case "demote": {
	    if (args.length < 2) {
		return false;
	    }
	    Player target = getServer().getPlayer(args[0]);

	    if (args[1].equals("Staff")) {
		try {
		    if ((Integer) database.getStaffData(target.getName(), "Rank") <= (Integer) database
			    .getStaffData(sender.getName(), "Rank")) {
			sender.sendMessage(ChatColor.RED
				+ "You cannot demote someone of a staff rank higher than or equal to your own.");
			return false;
		    }
		} catch (SQLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }

	    try {
		if (!demote(target, Integer.parseInt(args[1]))) {
		    sender.sendMessage(ChatColor.BLUE
			    + "You have entered an invalid ladder. Valid ladders are: 'Prison', 'VIP', 'Staff'.");
		} else {
		    sender.sendMessage(ChatColor.BLUE + "Demotion of player " + target.getName() + " successful.");
		}

	    } catch (SQLException e) {
		e.printStackTrace();
		return false;
	    }
	    return true;
	}
	case "sql-reload": {
	    database.reconnect();
	    return true;
	}
	// case "serve-donors":
	// {
	// try {
	// database.serveDonors();
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return true;
	// }
	case "topup": {
	    if (args.length < 2) {
		return false;
	    }

	    int amount = Integer.parseInt(args[0]);
	    Player target = getServer().getPlayer(args[1]);

	    ItemStack i = itemf.create(1505);
	    i.setAmount(amount);
	    target.getInventory().addItem(i);

	    try {
		int oldtopup = Math.toIntExact((Long) database.getPlayerData(target.getUniqueId().toString(), "topup"));
		int newtotal = oldtopup + amount;

		database.setPlayerData(target.getUniqueId().toString(), "topup", newtotal);

		getServer().getLogger()
			.info("Player  " + target.getName() + " has successfully topped up " + amount + " paragons.");
		target.sendMessage(
			ChatColor.GREEN + "" + ChatColor.BOLD + " >> You have topped up " + amount + " paragons!");
		target.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + " >> Thank you for your contribution!");
		target.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + " >> You have topped up a total of "
			+ newtotal + " paragons!");

		if (amount == 3) {
		    exec("crates givekey " + target.getName() + " Rare 1");
		}

	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    // adjustDonorRank(target);

	    return true;
	}
	case "staff": {
	    if (args.length < 2) {
		return false;
	    }

	    switch (args[0]) {
	    case "appoint": {
		try {
		    database.createStaffEntry(Bukkit.getPlayer(args[1]));
		    sender.sendMessage(
			    "Player " + args[1] + " has been appointed to staff position Trial Moderator (Rank 9).");
		    Bukkit.dispatchCommand(cons, "pex user " + args[1] + " group add trialmoderator");
		} catch (SQLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

		return true;
	    }
	    case "dismiss": {
		try {
		    database.removeStaffEntry(args[1]);
		    sender.sendMessage("Player " + args[1] + " has been dismissed from staff.");
		    Bukkit.dispatchCommand(cons, "pex user " + args[1] + " group remove trialmoderator");
		    Bukkit.dispatchCommand(cons, "pex user " + args[1] + " group remove moderator");
		    Bukkit.dispatchCommand(cons, "pex user " + args[1] + " group remove headmoderator");
		    Bukkit.dispatchCommand(cons, "pex user " + args[1] + " group remove trialadmin");
		    Bukkit.dispatchCommand(cons, "pex user " + args[1] + " group remove admin");
		    Bukkit.dispatchCommand(cons, "pex user " + args[1] + " group remove headadmin");
		    Bukkit.dispatchCommand(cons, "pex user " + args[1] + " group remove senioradmin");
		} catch (SQLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

		return true;
	    }
	    default: {
		return false;
	    }
	    }
	}
	// case "radio":
	// {
	// if (sender instanceof Player)
	// {
	// //PlayerAccount account = accounts.get(sender.getName());
	// PlayerAccount account = pm.getPlayer(sender.getName());
	// if (account.radio)
	// {
	// account.radio=false;
	// sender.sendMessage(ChatColor.BLUE+"You have turned off the radio.
	// This is the last song you will hear.");
	// } else {
	// account.radio=true;
	// sender.sendMessage(ChatColor.BLUE+"You have turned on the radio. You
	// will be able to hear the next song.");
	// }
	// return true;
	// }
	// }
	case "setdungeonchest": {
	    if (args[0] != null) {
		if (sender instanceof Player) {
		    Player player = (Player) sender;
		    Block chest = (Block) player.getTargetBlock(null, 5);

		    if (chest.getType() == Material.CHEST) {
			dManager.addChest(args[0], chest.getLocation());

			sender.sendMessage("You added the chest to dungeon " + args[0]);
		    } else {
			sender.sendMessage("You must be looking at a chest.");
			return false;
		    }

		    return true;
		}
	    }
	    return false;
	}
	case "createdungeon": {
	    if (sender instanceof Player) {
		if (args.length < 1) {
		    sender.sendMessage("You are missing some argument");
		    return false;
		}
		try {
		    Region sel = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt((Player) sender))
			    .getSelection(BukkitAdapter.adapt(Bukkit.getWorld("prison")));

		    dManager.createDungeon(args[0], sel);

		    sender.sendMessage("Created a new dungeon named " + args[0] + ", "
			    + sel.getMinimumPoint().toString() + " -> " + sel.getMaximumPoint().toString());
		    return true;

		} catch (IncompleteRegionException e) {
		    sender.sendMessage("Make a region selection first");
		    e.printStackTrace();
		    return false;
		}
	    }
	}
	case "refillchests": {
	    dManager.refillChests();
	    sender.sendMessage("All the dungeon chests have been refilled.");
	    return true;
	}
	// case "monthlypass":
	// {
	//
	// OfflinePlayer target = Bukkit.getPlayer(args[1]);
	// switch (args[0])
	// {
	// case "start":
	// {
	// if (args.length<3)
	// {
	// sender.sendMessage("You are missing some argument, 3 expected.");
	// return false;
	// }
	//
	// try {
	// database.startMonthlyPass(target, Integer.parseInt(args[2]));
	// sender.sendMessage("Successfully started monthly pass for player
	// "+args[1]+".");
	//
	// switch (Integer.parseInt(args[2]))
	// {
	// case 1:
	// {
	// applyExtraPerms("monthlypass1", (Player) target);
	// break;
	// }
	// case 2:
	// {
	// applyExtraPerms("monthlypass2", (Player) target);
	// break;
	// }
	// case 3:
	// {
	// applyExtraPerms("monthlypass3", (Player) target);
	// break;
	// }
	// }
	//
	// //accounts.get(args[1]).sync();
	// pm.getPlayer(args[1]).sync();
	// return true;
	// } catch (NumberFormatException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// case "end":
	// {
	// if (args.length<2)
	// {
	// sender.sendMessage("You are missing some argument, 2 expected.");
	// return false;
	// }
	//
	// try {
	// database.endMonthlyPass(args[1]);
	// sender.sendMessage("Successfully ended monthly pass for player
	// "+args[1]+".");
	//
	// revokeExtraPerms((Player) target);
	//
	// //accounts.get(args[1]).sync();
	// pm.getPlayer(args[1]).sync();
	// return true;
	// } catch (SQLException e) {
	// sender.sendMessage("Error: Maybe this player does not have an active
	// monthly pass?");
	// e.printStackTrace();
	// return false;
	// }
	// }
	// default:
	// {
	// return false;
	// }
	// }
	// }
	case "playerinfo": {
	    if (args.length < 1) {
		return false;
	    }
	    // sender.sendMessage(accounts.get(args[0]).printInfo());
	    sender.sendMessage(pm.getPlayer(args[0]).printInfo());
	    return true;
	}
	case "open-inventory": {
	    if (args.length < 2) {
		return false;
	    }
	    switch (args[0]) {
	    case "upgradeMerchant":
		Player target = getServer().getPlayer(args[1]);
		UpgradeMerchant merchant = new UpgradeMerchant(this, target);
		getServer().getPluginManager().registerEvents(merchant, this);
		merchant.openInventory(target);
		return true;
	    default:
		return false;
	    }
	}
	case "slave": {
	    if (args.length < 1 || !(sender instanceof Player)) {
		return false;
	    }

	    Player player = (Player) sender;

	    switch (args[0]) {
	    case "create":
		try {
		    UUID slaveID = sf.createSlave("", Integer.parseInt(args[1]), player.getUniqueId());
		    sf.summonSlave(slaveID, player.getLocation());
		} catch (NumberFormatException e) {
		    player.sendMessage("Type must be an integer.");
		    return false;
		}

		return true;
	    case "summon":
		UUID slaveID2;
		try {
		    slaveID2 = database.lookupSlave(player.getUniqueId(), args[1]);
		    if (!slaves.containsKey(slaveID2)) {
			player.sendMessage(ChatColor.GREEN + "Summoning " + args[1] + ".");
			slaves.put(slaveID2, sf.summonSlave(slaveID2, player.getLocation()));
		    } else {
			player.sendMessage(ChatColor.GREEN + args[1] + " is already summoned. Teleporting to you.");
			slaves.get(slaveID2).teleport(player.getLocation());
		    }

		} catch (SQLException e) {
		    player.sendMessage(ChatColor.RED + "You have no slave by that name.");
		}

		return true;
	    case "unsummon":
		boolean removed = false;
		Iterator<Entry<UUID, Slave>> it = slaves.entrySet().iterator();
		while (it.hasNext()) {
		    Slave slave2 = it.next().getValue();

		    Collator col = Collator.getInstance();
		    col.setStrength(Collator.NO_DECOMPOSITION);

		    boolean exists = false;
		    if (col.compare(slave2.getName(), args[1]) == 0) {
			exists = true;
		    }

		    // if (slave2.getName().equalsIgnoreCase(args[1]) &&
		    // slave2.getMaster().equals(player.getUniqueId())) {
		    if (exists && slave2.getMaster().equals(player.getUniqueId())) {
			slave2.updateLastPosition();
			// database.updateSlaveData(slave2);
			slave2.updateData();
			slave2.despawn();

			it.remove();

			player.sendMessage(ChatColor.GREEN + "You unsummoned " + slave2.getName() + ".");
			removed = true;
		    }
		}

		// for (int i=0; i!=slavesSize; i++) {
		// if (slaves.get(i).getName().equalsIgnoreCase(args[1]) &&
		// slaves.get(i).getMaster().equals(player.getUniqueId())) {
		// Slave slave2 = slaves.get(i);
		// slave2.updateLastPosition();
		// database.updateSlaveData(slave2);
		// slave2.despawn();
		//
		// slaves.remove(slave2);
		// slavesSize--;
		//
		// player.sendMessage(ChatColor.GREEN+"You unsummoned
		// "+slave2.getName()+".");
		// removed = true;
		// }
		// }

		if (!removed) {
		    player.sendMessage(ChatColor.RED + "You have no active slave by that name.");
		}

		return true;
	    case "delete":
		UUID slaveID3;
		try {
		    slaveID3 = database.lookupSlave(player.getUniqueId(), args[1]);
		    database.deleteSlaveData(slaveID3);
		    player.sendMessage(ChatColor.GREEN + "You permanently deleted " + args[1] + ".");
		    if (slaves.containsKey(slaveID3)) {
			Slave slave = slaves.get(slaveID3);
			slave.despawn();
			slaves.remove(slaveID3);
		    }

		} catch (SQLException e) {
		    player.sendMessage(ChatColor.RED + "You have no slave by that name.");
		}

		return true;

	    // boolean removed2 = true;
	    // Iterator<Entry<UUID, Slave>> it2 = slaves.entrySet().iterator();
	    // while (it2.hasNext()) {
	    // Slave slave3 = it2.next().getValue();
	    //
	    // if (slave3.getName().equalsIgnoreCase(args[1]) &&
	    // slave3.getMaster().equals(player.getUniqueId())) {
	    // slave3.despawn();
	    // database.deleteSlaveData(slave3.getID());
	    // it2.remove();
	    //
	    // player.sendMessage(ChatColor.GREEN+"You permanently deleted
	    // "+slave3.getName()+".");
	    // removed2 = true;
	    // }
	    // }

	    // if (!removed2) {
	    // player.sendMessage(ChatColor.RED+"You have no active slave by
	    // that name.");
	    // }

	    // return true;
	    case "list":
		ArrayList<String> playersSlaves;
		try {
		    playersSlaves = database.listPlayersSlavesNames(player.getUniqueId());

		    if (!playersSlaves.isEmpty()) {
			player.sendMessage(ChatColor.GREEN + player.getName() + "' slaves:");
			for (int i = 0; i != playersSlaves.size(); i++) {
			    player.sendMessage(ChatColor.GREEN + "" + (i + 1) + ": " + playersSlaves.get(i));
			}
		    } else {
			player.sendMessage(ChatColor.RED + "You have no slaves.");
		    }
		} catch (SQLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

		return true;
	    case "formation":
		if (args.length < 1 || !(sender instanceof Player)) {
		    return false;
		}

		switch (args[1]) {
		case "create":
		    if (!slaveFormations.containsKey(player.getName())) {
			SlaveFormation form = new SlaveFormation(this, player);
			Iterator<Entry<UUID, Slave>> it2 = slaves.entrySet().iterator();
			while (it2.hasNext()) {
			    Slave slave = it2.next().getValue();
			    if (slave.getMaster().equals(player.getUniqueId())) {
				form.addSlave(slave);
			    }
			}
			if (form.getSize() < 3 || form.getSize() > 10) {
			    player.sendMessage(ChatColor.RED
				    + "Too many or too few slaves active. A formation may have between 3 and 10 slaves.");
			} else {
			    player.sendMessage(ChatColor.GREEN + "Creating formation...");
			    form.create(player.getLocation());
			    slaveFormations.put(player.getName(), form);
			}

		    } else {
			player.sendMessage(ChatColor.RED + "You have already created the formation.");
		    }
		    break;

		case "dissolve":
		    player.sendMessage(ChatColor.GREEN + "Dissolving formation...");
		    slaveFormations.remove(player.getName());

		    break;
		}

		return true;
	    }
	}
	case "level": {
	    if (args[0] != null) {
		switch (args[0]) {
		case "set": {
		    PlayerAccount target = pm.getPlayer(args[1]);

		    if (target == null) {
			sender.sendMessage("Player not found");
			return false;
		    }

		    try {
			target.setLevel(Integer.parseInt(args[2]));
			sender.sendMessage("Successfully set player's level to " + args[2]);
			return true;
		    } catch (NumberFormatException e) {
			sender.sendMessage("Not a valid level number");
			return false;
		    }
		}
		case "clear": {
		    PlayerAccount target = pm.getPlayer(args[1]);
		    if (target == null) {
			sender.sendMessage("Player not found");
			return false;
		    }

		    target.setLevel(0);
		    sender.sendMessage("Successfully reset level to 0");
		    return true;
		}
		default: {
		    sender.sendMessage("Correct usage: /level set/clear <name> (level)");
		    return false;
		}
		}
	    } else {
		sender.sendMessage("Correct usage: /level set/clear <name> (level)");
		return false;
	    }
	}
	default: {
	    return false;
	}
	}
    }

    private boolean setupEconomy() {
	if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
	    return false;
	}

	RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	if (rsp == null) {
	    return false;
	}

	e = rsp.getProvider();
	return e != null;
    }

    private boolean setupChat() {
	RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
	chat = rsp.getProvider();
	return chat != null;
    }

    private boolean setupPermissions() {
	RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
	perms = rsp.getProvider();
	return perms != null;
    }

    public FileConfiguration getConfig(String tag) {
	switch (tag) {
	case "lotto":
	    return this.lotto;

	case "kit":
	    return this.kit;

	case "dungeon":
	    return this.dungeon;

	default:
	    return null;
	}
    }

    public void saveConfig(String tag) {
	switch (tag) {
	case "lotto":
	    try {
		lotto.save(lottoFile);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    break;
	case "kit":
	    try {
		kit.save(kitFile);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	case "dungeon":
	    try {
		dungeon.save(dungeonFile);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	default:
	    break;
	}
    }

    public boolean promote(Player player, int ladder) throws SQLException // ladder:
									  // 0 =
									  // rank;
									  // 1 =
									  // staff
    {
	String name = player.getName();
	String UUID = player.getUniqueId().toString();
	PlayerAccount pa = pm.getPlayer(name);

	int to;

	switch (ladder) {
	// case "Prison":
	// exec("pex promote "+name+" Prison");
	// to = (Integer) database.getPlayerData(UUID, "Rank")+1;
	// //database.setPlayerData(name, "Rank", to);
	// //accounts.get(name).setRank(to);
	// pm.getPlayer(name).setRank(to);
	// player.sendMessage(ChatColor.GREEN+""+ChatColor.BOLD+" >> You have
	// been promoted on ladder "+ladder+" to rank "+to);
	// return true;
	// case "VIP":
	// exec("pex promote "+name+" Paid");
	// to = (Integer) database.getPlayerData(UUID, "VIP")+1;
	// //database.setPlayerData(name, "VIP", to);
	// player.sendMessage(ChatColor.GREEN+""+ChatColor.BOLD+" >> You have
	// been promoted on ladder "+ladder+" to rank "+to);
	// pm.getPlayer(name).setVip(to);
	// //accounts.get(name).setVip(to);
	// return true;
	case 0: // rank
	{
	    exec("lp user " + name + " promote rank");
	    int newRank = pa.getRank() + 1;
	    pa.setRank(newRank);
	    player.sendMessage(
		    ChatColor.GREEN + "" + ChatColor.BOLD + " >> You have been promoted to " + rankNames.get(newRank));
	    return true;
	}
	case 1: // staff
	{
	    exec("lp user " + name + " promote staff");
	    return true;
	}
	default: {
	    return false;
	}
	}
    }

    public boolean demote(Player player, int ladder) throws SQLException {
	String name = player.getName();
	String UUID = player.getUniqueId().toString();
	PlayerAccount pa = pm.getPlayer(name);

	int to;

	switch (ladder) {
	case 0: // rank
	{
	    exec("lp user " + name + " demote rank");
	    int newRank = pa.getRank() - 1;
	    pa.setRank(newRank);
	    player.sendMessage(
		    ChatColor.RED + "" + ChatColor.BOLD + " >> You have been demoted to " + rankNames.get(newRank));
	    return true;
	}
	// case "Prison":
	// exec("pex demote "+name+" Prison");
	// to = (Integer) database.getPlayerData(UUID, "Rank")-1;
	// if (to<0)
	// {
	// to=0;
	// }
	// //database.setPlayerData(name, "Rank", to);
	// //accounts.get(name).setRank(to);
	// pm.getPlayer(name).setRank(to);
	// player.sendMessage(ChatColor.RED+""+ChatColor.BOLD+" >> You have been
	// demoted on ladder "+ladder+" to rank "+to);
	// return true;
	// case "VIP":
	// exec("pex demote "+name+" Paid");
	// to = (Integer) database.getPlayerData(UUID, "VIP")-1;
	// if (to<0)
	// {
	// to=0;
	// }
	// //database.setPlayerData(name, "VIP", to);
	// //accounts.get(name).setVip(to);
	// pm.getPlayer(name).setVip(to);
	// player.sendMessage(ChatColor.RED+""+ChatColor.BOLD+" >> You have been
	// demoted on ladder "+ladder+" to rank "+to);
	// return true;
	// case "Staff":
	// exec("pex demote "+name+" Staff");
	// to = (Integer) database.getStaffData(name, "Rank")+1;
	// if (to==10)
	// {
	// to=9;
	// }
	// database.setStaffData(name, "Rank", to);
	// player.sendMessage(ChatColor.RED+""+ChatColor.BOLD+" >> You have been
	// demoted on ladder "+ladder+" to rank "+to);
	// return true;
	default:
	    return false;
	}
    }

    // public void promoteTo(Player player, int VIP)
    // {
    // try {
    // int currentVIP = (Integer)
    // database.getPlayerData(player.getUniqueId().toString(), "VIP");
    //
    // int distance = (currentVIP - VIP)*-1;
    //
    // if (distance<0)
    // {
    // return;
    // }
    //
    // for (int i=0; i!=distance; i++)
    // {
    // promote(player, "VIP");
    // }
    //
    // } catch (SQLException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    //
    // return;
    // }

    // public void adjustDonorRank(Player player)
    // {
    // int topup = 0;
    //
    // try {
    // topup = Math.toIntExact((Long)
    // database.getPlayerData(player.getUniqueId().toString(), "topup"));
    // } catch (SQLException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    //
    // if (topup<16)
    // {
    // return;
    // }
    // else if (topup>=16 && topup<32)
    // {
    // promoteTo(player, 1);
    // }
    // else if (topup>=32 && topup<48)
    // {
    // promoteTo(player, 2);
    // }
    // else if (topup>=48 && topup<64)
    // {
    // promoteTo(player, 3);
    // }
    // else if (topup>=64 && topup<128)
    // {
    // promoteTo(player, 4);
    // }
    // else if (topup>=128 && topup<256)
    // {
    // promoteTo(player, 5);
    // }
    // else if (topup>=256 && topup<512)
    // {
    // promoteTo(player, 6);
    // }
    // else if (topup>=512 && topup<768)
    // {
    // promoteTo(player, 7);
    // }
    // else if (topup>=768)
    // {
    // promoteTo(player, 8);
    // }
    //
    // return;
    // }

    public void exec(String cmd) throws CommandException {
	getServer().dispatchCommand(cons, cmd);
    }

    public MySQL getDatabase() {
	return database;
    }

    public void setTimer(int amount) {
	time = amount;
    }

    public void runTimer() {
	BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	taskID = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
	    @Override
	    public void run() {
		if (time % 300 == 0) // Every 5 minutes
		{
		    try {
			Bukkit.broadcastMessage(ChatColor.AQUA + "" + database.getRandomBroadcast());
			// database.serveDonors();
		    } catch (SQLException e) {
			e.printStackTrace();
		    }

		    if (time == 0) {
			// Bukkit.broadcastMessage("Resetting
			// timer...");

			database.reconnect();

			setTimer(3600);
			return;
		    }
		}

		if (time % 600 == 0) // every 10 minutes
		{
		    dManager.refillChests();
		    Bukkit.broadcastMessage(ChatColor.DARK_BLUE + "Resetting all mines.");
		    exec("mines reset all");
		}

		if (time % 5 == 0) // every 5 seconds
		{
		    List<? extends Player> players = (List<? extends Player>) Bukkit.getOnlinePlayers();

		    for (int i = 0; i != players.size(); i++) {
			if (players.get(i).getWorld() == Bukkit.getWorld("prison")) {
			    Location loc = players.get(i).getLocation();
			    if (citadel.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
				players.get(i).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 3), true);
			    }

			} else if (players.get(i).getWorld() == getServer().getWorld("Factionspawn")) {
			    players.get(i).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 5), true);
			}
		    }
		}

		// if (!MIDIQueue.isEmpty())
		// {
		// if (radioTimeRemaining == 0)
		// {
		// int current = MIDIQueue.size()-1;
		// String name = MIDIQueue.get(current);
		//
		// MidiFileFormat mff;
		// try {
		// File file = new
		// File("/home/minecraft/server/midi/"+name+".mid");
		// Sequence seq = MidiSystem.getSequence(file);
		//
		// int length = (int)
		// seq.getMicrosecondLength()/1000000;
		// radioTimeRemaining=length+1;
		// } catch (
		// InvalidMidiDataException
		// | IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// Bukkit.getLogger().info("Length of MIDI File
		// in seconds: "+radioTimeRemaining);
		// getServer().broadcastMessage(ChatColor.BOLD+""+ChatColor.BLUE+"NOW
		// PLAYING: "+name);
		//
		// //for (String pName : accounts.keySet()))
		// //{
		// // if (accounts.get(pName).radio)
		// // {
		// // exec("midi player start "+pName+" "+name);
		// // }
		// //}
		// //MIDIQueue.clear();
		//
		// try {
		// FileWriter writer = new
		// FileWriter(radioCurrentlyPlaying);
		// writer.write(name);
		// writer.close();
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// MIDIQueue.remove(current);
		// Bukkit.getLogger().info("Now there are
		// "+MIDIQueue.size()+" songs in the queue.");
		// }
		// }

		// if (radioTimeRemaining!=0)
		// {
		// radioTimeRemaining--;
		//
		// try {
		// FileWriter writer = new
		// FileWriter(radioTime);
		// writer.write(Integer.toString(radioTimeRemaining));
		// writer.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }

		Iterator<Entry<String, SlaveFormation>> it = slaveFormations.entrySet().iterator();
		while (it.hasNext()) {
		    SlaveFormation form = it.next().getValue();
		    form.update();
		}

		time--;
	    }
	}, 0L, 20L);
    }

    // public void addMIDIToQueue(String name)
    // {
    // MIDIQueue.add(0, name);
    // Bukkit.getLogger().info("MIDI "+name+" added to the queue, now there are
    // "+MIDIQueue.size()+" songs in the queue.");
    //
    //
    // }

    // public void applyExtraPerms(String set, Player p)
    // {
    // Bukkit.getLogger().info("Apply extra permissions for player
    // "+p.getName()+" in accordance with set "+set+".");
    //
    // exec("pex user "+p.getName()+" add hlp.autosell"); // This permission is
    // applied to all Monthly Passes
    //
    // switch (set)
    // {
    // case "monthlypass1":
    // {
    // p.sendMessage(ChatColor.GREEN+""+ChatColor.BOLD+" >> Your Tier 1 Monthly
    // Pass extra permissions have been applied!");
    // exec("pex user "+p.getName()+" add hlp.mp1");
    // exec("pex user "+p.getName()+" add essentials.kits.mp1");
    // break;
    // }
    // case "monthlypass2":
    // {
    // p.sendMessage(ChatColor.GREEN+""+ChatColor.BOLD+" >> Your Tier 2 Monthly
    // Pass extra permissions have been applied!");
    // exec("pex user "+p.getName()+" add hlp.mp2");
    // exec("pex user "+p.getName()+" add essentials.kits.mp2");
    // break;
    // }
    // case "monthlypass3":
    // {
    // p.sendMessage(ChatColor.GREEN+""+ChatColor.BOLD+" >> Your Tier 3 Monthly
    // Pass extra permissions have been applied!");
    // exec("pex user "+p.getName()+" add hlp.mp3");
    // exec("pex user "+p.getName()+" add essentials.kits.mp3");
    // break;
    // }
    // }
    // }

    // public void revokeExtraPerms(Player p)
    // {
    // Bukkit.getLogger().info("Revoking all extra permissions for player
    // "+p.getName()+".");
    //
    // exec("pex user "+p.getName()+" remove hlp.autosell"); // This permission
    // is applied to all Monthly Passes
    //
    // exec("pex user "+p.getName()+" remove hlp.mp1");
    // exec("pex user "+p.getName()+" remove essentials.kits.mp1");
    // exec("pex user "+p.getName()+" remove hlp.mp2");
    // exec("pex user "+p.getName()+" remove essentials.kits.mp2");
    // exec("pex user "+p.getName()+" remove hlp.mp3");
    // exec("pex user "+p.getName()+" remove essentials.kits.mp3");
    // }

    public String getRankName(int rank) {
	return rankNames.get(rank);
    }

    public static char getChar(int i) {
	return i < 0 || i > 25 ? '★' : (char) ('A' + i);
    }

    void calculateFirst50Levels() {
	getLogger().info("Calculating first 50 level costs:");
	for (int i = 1; i < 50; i++) {
	    // getLogger().info("This one: "+i+" "+(i^2)+" "+(90 * (i^2)));
	    levelUpExp[i - 1] = 90 * (i * i) - (50 * i);
	    getLogger().info(i + " -> " + (i + 1) + ": " + levelUpExp[i - 1]);
	}
    }

    int getLevelCost(int level) {
	return levelUpExp[level - 1];
    }
}
