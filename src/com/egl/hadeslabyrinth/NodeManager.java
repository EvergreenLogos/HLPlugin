package com.egl.hadeslabyrinth;

import java.util.HashMap;
import java.util.UUID;

public class NodeManager {

    HLPlugin plugin;

    private HashMap<UUID, Node> nodes;

    public void NodeManager(HLPlugin plugin) {
	nodes = new HashMap<UUID, Node>();
    }

    public void loadChunk(int chunkX, int chunkY) {

    }
}
