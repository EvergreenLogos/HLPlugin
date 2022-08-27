package com.egl.hadeslabyrinth;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DuelManager {
    ArrayList<Duel> duels = new ArrayList<Duel>();
    ArrayList<Duel> pendingDuels = new ArrayList<Duel>();

    HLPlugin plugin;

    int timer = 0;

    public DuelManager(HLPlugin plugin) {
	this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    public void runDuels() {
	timer = 10;

	plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {

	    @Override
	    public void run() {
		Duel currentDuel = duels.get(0);
		Player p1 = currentDuel.p1;
		Player p2 = currentDuel.p2;

		if (timer == 0) {
		    p1.teleport(new Location(plugin.getServer().getWorld("prison"), 115, 105, 49));
		    p2.teleport(new Location(plugin.getServer().getWorld("prison"), 115, 105, 79));
		    timer = 300;
		}

		if (p1.isDead()) {

		}

		timer--;
	    }

	}, 0L, 20L);
    }

    public void addDuel(Player p1, Player p2) {
	Duel duel = new Duel(p1, p2);
	pendingDuels.add(duel);
    }

}
