package com.egl.hadeslabyrinth;

import java.util.UUID;

import org.bukkit.Location;

public class Node {

    HLPlugin plugin;

    UUID id;
    int chunkX, chunkY;

    int type; // 0 = chest, 1 = gathering point, 3 = spawner

    String region;

    Location location;

    public void Node(HLPlugin plugin, UUID id, int chunkX, int chunkY, int type, String region, Location location,
	    String[] options) {
	this.plugin = plugin;
	this.id = id;
	this.chunkX = chunkX;
	this.chunkY = chunkY;
	this.type = type;
	this.region = region;
    }
}
