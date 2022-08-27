package com.egl.hadeslabyrinth;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class PlayerManager {
    HLPlugin plugin;

    HashMap<String, PlayerAccount> onlinePlayers;

    public PlayerManager(HLPlugin plugin) {
	this.plugin = plugin;

	onlinePlayers = new HashMap<String, PlayerAccount>();
    }

    public void loadPlayer(Player player) { // call this when a player logs in
	PlayerAccount account = new PlayerAccount(player, plugin);

	// if (account.allegiance == 1) {
	// plugin.om.loadOfficial(account);
	// }

	onlinePlayers.put(player.getName(), account);
    }

    public void unloadPlayer(String name) { // call this when a player logs out
	PlayerAccount account = onlinePlayers.get(name);
	// if (account.allegiance == 1) {
	// plugin.om.unloadOfficial(name);
	// }

	onlinePlayers.remove(name);
    }

    public PlayerAccount getPlayer(String name) {
	return onlinePlayers.get(name);
    }
}
