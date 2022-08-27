package com.egl.hadeslabyrinth;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SlaveFormation {
    HLPlugin plugin;

    ArrayList<Slave> members;

    Player player;

    ArrayList<Double[]> offsets; // [x,z]

    public SlaveFormation(HLPlugin plugin, Player player) {
	this.plugin = plugin;
	this.player = player;

	members = new ArrayList<Slave>();

	offsets = new ArrayList<Double[]>();
    }

    public void update() { // called every second
	Location loc = player.getLocation();

	create(loc);

	double playerY = loc.getY();

	int direction = 0;

	Vector normalizedDirection = loc.getDirection().normalize();

	// if (normalizedDirection.getX() >= -.5 && normalizedDirection.getX() <
	// .5 && normalizedDirection.getZ() > 1) {
	if (normalizedDirection.getZ() >= -.5 && normalizedDirection.getZ() <= .5 && normalizedDirection.getX() > 0) {
	    direction = 1;
	} else if (normalizedDirection.getX() >= -.5 && normalizedDirection.getX() <= .5
		&& normalizedDirection.getZ() < 0) {
	    direction = 2;
	} else if (normalizedDirection.getZ() >= -.5 && normalizedDirection.getZ() <= .5
		&& normalizedDirection.getX() < 0) {
	    direction = 3;
	}

	// plugin.getLogger().info("Direction: "+direction+", Direction vector:
	// "+loc.getDirection().normalize());

	for (int i = 0; i < members.size(); i++) {
	    Slave s = members.get(i);
	    Location target = new Location(loc.getWorld(), offsets.get(i)[0], playerY, offsets.get(i)[1]);
	    if (!s.npc.getEntity().getLocation().equals(target)) {
		// s.follow(target);
		s.follow(calculateLocation(player.getLocation(), target, direction));
	    }
	}
    }

    public void addSlave(Slave slave) {
	members.add(slave);
    }

    public void create(Location loc) {
	int membersSize = members.size();
	double playerX = loc.getX();
	double playerZ = loc.getZ();

	offsets.clear();

	switch (membersSize) {
	case 3: {
	    Double[] pos1 = { playerX - 3, playerZ - 3 };
	    offsets.add(pos1);
	    Double[] pos2 = { playerX, playerZ - 3 };
	    offsets.add(pos2);
	    Double[] pos3 = { playerX + 3, playerZ - 3 };
	    offsets.add(pos3);
	}
	case 4: {
	    Double[] pos1 = { playerX - 3, playerZ - 3 };
	    offsets.add(pos1);
	    Double[] pos2 = { playerX, playerZ - 3 };
	    offsets.add(pos2);
	    Double[] pos3 = { playerX + 3, playerZ - 3 };
	    offsets.add(pos3);
	    Double[] pos4 = { playerX, playerZ - 6 };
	    offsets.add(pos4);
	}
	case 5: {
	    Double[] pos1 = { playerX - 3, playerZ - 3 };
	    offsets.add(pos1);
	    Double[] pos2 = { playerX, playerZ - 3 };
	    offsets.add(pos2);
	    Double[] pos3 = { playerX + 3, playerZ - 3 };
	    offsets.add(pos3);
	    Double[] pos4 = { playerX - 3, playerZ - 6 };
	    offsets.add(pos4);
	    Double[] pos5 = { playerX + 3, playerZ - 6 };
	    offsets.add(pos5);
	}
	case 6: {
	    Double[] pos1 = { playerX - 3, playerZ - 3 };
	    offsets.add(pos1);
	    Double[] pos2 = { playerX, playerZ - 3 };
	    offsets.add(pos2);
	    Double[] pos3 = { playerX + 3, playerZ - 3 };
	    offsets.add(pos3);

	    Double[] pos4 = { playerX - 3, playerZ - 6 };
	    offsets.add(pos4);
	    Double[] pos5 = { playerX, playerZ - 6 };
	    offsets.add(pos5);
	    Double[] pos6 = { playerX + 3, playerZ - 6 };
	    offsets.add(pos6);
	}
	case 7: {
	    Double[] pos1 = { playerX - 3, playerZ - 3 };
	    offsets.add(pos1);
	    Double[] pos2 = { playerX, playerZ - 3 };
	    offsets.add(pos2);
	    Double[] pos3 = { playerX + 3, playerZ - 3 };
	    offsets.add(pos3);

	    Double[] pos4 = { playerX - 3, playerZ - 6 };
	    offsets.add(pos4);
	    Double[] pos5 = { playerX - 1, playerZ - 6 };
	    offsets.add(pos5);
	    Double[] pos6 = { playerX + 1, playerZ - 6 };
	    offsets.add(pos6);
	    Double[] pos7 = { playerX + 3, playerZ - 6 };
	    offsets.add(pos7);
	}
	case 8: {
	    Double[] pos1 = { playerX - 3, playerZ - 3 };
	    offsets.add(pos1);
	    Double[] pos2 = { playerX - 1, playerZ - 3 };
	    offsets.add(pos2);
	    Double[] pos3 = { playerX + 1, playerZ - 3 };
	    offsets.add(pos3);
	    Double[] pos4 = { playerX + 3, playerZ - 3 };
	    offsets.add(pos4);

	    Double[] pos5 = { playerX - 3, playerZ - 6 };
	    offsets.add(pos5);
	    Double[] pos6 = { playerX - 1, playerZ - 6 };
	    offsets.add(pos6);
	    Double[] pos7 = { playerX + 1, playerZ - 6 };
	    offsets.add(pos7);
	    Double[] pos8 = { playerX + 3, playerZ - 6 };
	    offsets.add(pos8);
	}
	case 9: {
	    Double[] pos1 = { playerX - 3, playerZ - 3 };
	    offsets.add(pos1);
	    Double[] pos2 = { playerX, playerZ - 3 };
	    offsets.add(pos2);
	    Double[] pos3 = { playerX + 3, playerZ - 3 };
	    offsets.add(pos3);

	    Double[] pos4 = { playerX - 3, playerZ - 6 };
	    offsets.add(pos4);
	    Double[] pos5 = { playerX, playerZ - 6 };
	    offsets.add(pos5);
	    Double[] pos6 = { playerX + 3, playerZ - 6 };
	    offsets.add(pos6);

	    Double[] pos7 = { playerX - 3, playerZ - 9 };
	    offsets.add(pos7);
	    Double[] pos8 = { playerX, playerZ - 9 };
	    offsets.add(pos8);
	    Double[] pos9 = { playerX + 3, playerZ - 9 };
	    offsets.add(pos9);
	}
	case 10: {
	    Double[] pos1 = { playerX - 3, playerZ - 3 };
	    offsets.add(pos1);
	    Double[] pos2 = { playerX, playerZ - 3 };
	    offsets.add(pos2);
	    Double[] pos3 = { playerX + 3, playerZ - 3 };
	    offsets.add(pos3);

	    Double[] pos4 = { playerX - 3, playerZ - 6 };
	    offsets.add(pos4);
	    Double[] pos5 = { playerX, playerZ - 6 };
	    offsets.add(pos5);
	    Double[] pos6 = { playerX + 3, playerZ - 6 };
	    offsets.add(pos6);

	    Double[] pos7 = { playerX - 3, playerZ - 9 };
	    offsets.add(pos7);
	    Double[] pos8 = { playerX - 1, playerZ - 9 };
	    offsets.add(pos8);
	    Double[] pos9 = { playerX + 1, playerZ - 9 };
	    offsets.add(pos9);
	    Double[] pos10 = { playerX + 3, playerZ - 9 };
	    offsets.add(pos10);
	}
	}
    }

    public int getSize() {
	return members.size();
    }

    public Location calculateLocation(Location playerLoc, Location target, int direction) {
	if (direction == 3) {
	    double originX = playerLoc.getX();
	    double targetX = target.getX();
	    double difference = originX - targetX;

	    double originZ = playerLoc.getZ();
	    double targetZ = target.getZ();
	    double difference2 = originZ - targetZ;

	    double xPrime = originX + difference2;
	    double zPrime = originZ + difference;// + (-1*difference2);

	    return new Location(target.getWorld(), xPrime, playerLoc.getY(), zPrime);

	} else if (direction == 2) {
	    double originX = playerLoc.getX();
	    double targetX = target.getX();
	    double difference = originX - targetX;
	    double xPrime = originX + difference;

	    double originZ = playerLoc.getZ();
	    double targetZ = target.getZ();
	    double difference2 = originZ - targetZ;
	    double zPrime = originZ + difference2;

	    return new Location(target.getWorld(), xPrime, playerLoc.getY(), zPrime);

	} else if (direction == 1) {
	    double originX = playerLoc.getX();
	    double targetX = target.getX();
	    double difference = originX - targetX;

	    double originZ = playerLoc.getZ();
	    double targetZ = target.getZ();
	    double difference2 = originZ - targetZ;

	    double xPrime = originX - difference2;
	    double zPrime = originZ - difference;// + difference2;

	    return new Location(target.getWorld(), xPrime, playerLoc.getY(), zPrime);
	} else {
	    return target;
	}
    }
}
