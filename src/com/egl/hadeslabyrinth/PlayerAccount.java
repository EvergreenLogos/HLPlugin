package com.egl.hadeslabyrinth;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class PlayerAccount {
    Player player;
    HLPlugin plugin;
    MySQL database;

    String name;
    UUID UUID;

    int rank;
    int vip;
    int prestige;

    int exp;
    int lvl;

    int expForNextLevel;
    // int _class;

    // Class IDs
    // 0 Swordsmaster sword damage x1.5 Item: sword
    // 1 Archer arrow damage x1.5 Item: Bow
    // 2 Gunsmith gun damage x1.5 Item: Gun
    // 3 Artillery man explosion damage x1.5 Item: Bomb
    // 4 Scholar Item: Book
    // 5 Physician can heal self/other Item: Elixir
    // 6 Priest
    // 7 Mage Item: Staff
    // 8 Demon Item: Scythe
    // 9 Gangster

    // int allegiance;

    // Allegiance IDs
    // 0 None
    // 1 Official
    // 2 Mobster

    // int monthlyPass;

    // public boolean radio;

    public PlayerAccount(Player player, HLPlugin plugin) {
	this.player = player;
	this.plugin = plugin;
	this.database = plugin.getDatabase();

	this.name = player.getName();
	this.UUID = player.getUniqueId();

	try { // load all the player data from SQL
	    String UUIDString = UUID.toString();

	    this.rank = (int) database.getPlayerData(UUIDString, "rank");
	    this.vip = (int) database.getPlayerData(UUIDString, "vip");
	    this.prestige = (int) database.getPlayerData(UUIDString, "prestige");
	    // this.radio = false;
	    this.lvl = (int) database.getPlayerData(UUIDString, "level");
	    this.exp = (int) database.getPlayerData(UUIDString, "exp");
	    this.expForNextLevel = (int) database.getPlayerData(UUIDString, "expForNextLevel");

	    // this.allegiance = (int) database.getPlayerData(UUID.toString(),
	    // "allegiance");
	    // this._class = (int) database.getPlayerData(UUID.toString(),
	    // "class");

	    // this.monthlyPass = database.checkMonthlyPass(player);
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	// if (monthlyPass!=-1)
	// {
	// switch (monthlyPass)
	// {
	// case 1:
	// {
	// plugin.applyExtraPerms("monthlypass1", player);
	// break;
	// }
	// case 2:
	// {
	// plugin.applyExtraPerms("monthlypass2", player);
	// break;
	// }
	// case 3:
	// {
	// plugin.applyExtraPerms("monthlypass3", player);
	// break;
	// }
	// }
	// }
	//
	// if (allegiance == 1) {
	// plugin.om.loadOfficial(this);
	// }
    }

    public int getRank() {
	return rank;
    }

    public void setRank(int rank) {
	this.rank = rank;
	sync();
    }

    public int getVip() {
	return vip;
    }

    public void setVip(int vip) {
	this.vip = vip;
	sync();
    }

    public int getPrestige() {
	return prestige;
    }

    public void setPrestige(int prestige) {
	this.prestige = prestige;
	sync();
    }

    public int getExp() {
	return exp;
    }

    public void addExp(int amount) {
	exp += amount;
	if (exp >= expForNextLevel) {
	    increaseLevel(1);
	    exp -= expForNextLevel;
	    player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + " >> You are now level " + lvl);
	    calculateExpForNextLevel();
	} else {
	    updateScoreboard();
	}
    }

    public int getLevel() {
	return lvl;
    }

    public void setLevel(int level) {
	this.lvl = level;
	updateScoreboard();
    }

    public void increaseLevel(int amount) {
	lvl += amount;
	sync();
    }

    public String getName() {
	return name;
    }

    // public int getAllegiance() {
    // return allegiance;
    // }
    //
    // public void setAllegiance(int x) {
    // allegiance = x;
    // if (x==1) { // make the player an official
    // plugin.om.loadOfficial(this);
    // }
    // }

    public UUID getUUID() {
	return this.UUID;
    }

    public void sync() {
	try {
	    database.setPlayerData(UUID.toString(), "rank", rank);
	    database.setPlayerData(UUID.toString(), "vip", vip);
	    database.setPlayerData(UUID.toString(), "prestige", prestige);
	    database.setPlayerData(UUID.toString(), "exp", exp);
	    database.setPlayerData(UUID.toString(), "level", lvl);
	    database.setPlayerData(UUID.toString(), "expForNextLevel", expForNextLevel);
	    // database.setPlayerData(UUID.toString(), "allegiance",
	    // allegiance);
	    // database.setPlayerData(UUID.toString(), "class", _class);

	    // monthlyPass = database.checkMonthlyPass(player);

	    // if (allegiance == 1) {
	    // plugin.om.getOfficial(name).sync();
	    // }
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	updateScoreboard();

	plugin.getLogger().info("Syncing account: " + this.player.getName());
    }

    public String printInfo() {
	StringBuilder sb = new StringBuilder();

	sb.append("INFO FOR PLAYER ACCOUNT " + name + ", UUID " + UUID);
	sb.append("\nPrison rank: " + rank);
	sb.append("\nVIP rank: " + vip);
	sb.append("\nPrestige: " + prestige);
	// sb.append("\nMonthly pass: "+monthlyPass);
	// sb.append("\nRadio on: "+radio);
	sb.append("\nLevel: " + lvl);
	sb.append("\nExp: " + exp + " / " + expForNextLevel);

	return sb.toString();
    }

    public void updateScoreboard() {
	// scoreboard

	ScoreboardManager manager = Bukkit.getScoreboardManager();
	Scoreboard board = manager.getNewScoreboard();

	Objective ob = board.registerNewObjective("name", "dummy", ChatColor.AQUA + "Name: " + player.getName());
	ob.setDisplaySlot(DisplaySlot.SIDEBAR);

	// PlayerAccount thisPlayer = plugin.accounts.get(player.getName());

	ArrayList<String> entries = new ArrayList<String>();

	entries.add("Level: " + lvl);
	entries.add("Exp: " + exp + " / " + expForNextLevel);
	String prison = "";
	char prisonLetter = HLPlugin.getChar(this.getRank());
	// if (prisonLetter == '★' && prestige > 1) {
	// prison = "Rank: "+prestige+"x★";
	// } else {
	// prison = "Rank: "+prisonLetter;
	// }
	entries.add("Rank: " + plugin.getRankName(this.rank));
	entries.add("Balance: $" + plugin.e.getBalance(player));

	// if (allegiance == 1) {
	// entries.add(ChatColor.YELLOW+"Official");
	// entries.add("Rank: "+plugin.om.getOfficialTitle(player.getName()));
	// }

	if (vip > 0) {
	    entries.add(ChatColor.AQUA + "VIP " + vip);
	}

	ArrayList<String> reverse = new ArrayList<String>();
	for (int i = entries.size() - 1; i >= 0; i--) {
	    reverse.add(entries.get(i));
	}

	for (int i = 0; i != reverse.size(); i++) {
	    Score score = ob.getScore(reverse.get(i));
	    score.setScore(i);
	}

	player.setScoreboard(board);
    }

    void calculateExpForNextLevel() {
	int nextLevel = lvl + 1;

	if (nextLevel <= 50) {
	    expForNextLevel = plugin.getLevelCost(nextLevel);
	} else {
	    expForNextLevel = 90 * (nextLevel * nextLevel) - (50 * nextLevel);
	}

	updateScoreboard();
    }
}
