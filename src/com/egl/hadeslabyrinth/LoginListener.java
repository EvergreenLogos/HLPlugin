package com.egl.hadeslabyrinth;

import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class LoginListener implements Listener {
    Logger logger;
    HLPlugin plugin;

    public LoginListener(Plugin plugin) {
	this.logger = plugin.getLogger();
	this.plugin = (HLPlugin) plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoint(PlayerJoinEvent event) {
	Player p = (Player) event.getPlayer();
	if (p.hasPermission("hlp.silentlogin")) {
	    event.setJoinMessage(null);
	}

	// plugin.accounts.get(p.getName()).updateScoreboard();
	plugin.pm.getPlayer(p.getName()).updateScoreboard();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
	Player p = (Player) event.getPlayer();
	if (p.hasPermission("hlp.silentlogout")) {
	    event.setQuitMessage(null);
	}
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLogin(PlayerLoginEvent event) throws SQLException {
	Player p = (Player) event.getPlayer();
	String playerName = p.getName();
	logger.log(Level.INFO, "Player " + playerName + " is logging in!");

	if (!plugin.database.playerHasEntry(p.getUniqueId().toString())) {
	    // logger.log(Level.INFO, "Player " + playerName + " has logged in
	    // for the first time.");
	    Bukkit.broadcastMessage(ChatColor.GREEN + "Player " + playerName + " logged in for the first time.");
	    plugin.database.createPlayerEntry(p);
	}

	// plugin.accounts.put(playerName, new PlayerAccount(p, plugin));
	plugin.pm.loadPlayer(p);

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLogout(PlayerQuitEvent event) {
	Player p = (Player) event.getPlayer();
	logger.log(Level.INFO, "Player " + p.getName() + " is logging out!");

	// if (plugin.accounts.get(p.getName()).monthlyPass!=-1)
	// if (plugin.pm.getPlayer(p.getName()).monthlyPass!=-1)
	// {
	// plugin.revokeExtraPerms((Player) p);
	// }

	// plugin.accounts.get(p.getName()).sync();
	plugin.pm.getPlayer(p.getName()).sync();

	// plugin.accounts.remove(p.getName());
	plugin.pm.unloadPlayer(p.getName());

	// dissolve player's formations

	if (plugin.slaveFormations.containsKey(p.getName())) {
	    plugin.slaveFormations.remove(p.getName());
	}

	// despawn all the player's slaves

	plugin.getLogger().info(String.valueOf(plugin.slaves.size()) + " slaves");

	Iterator<Entry<UUID, Slave>> it = plugin.slaves.entrySet().iterator();
	while (it.hasNext()) {
	    Slave slave = it.next().getValue();

	    if (slave.getMaster().equals(p.getUniqueId())) {
		slave.updateLastPosition();
		// plugin.database.updateSlaveData(slave);
		slave.updateData();
		slave.despawn();

		it.remove();
	    }
	}
    }
}
