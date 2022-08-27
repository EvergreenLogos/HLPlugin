package com.egl.hadeslabyrinth;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkListener implements Listener {

    HLPlugin plugin;

    NodeManager nm;

    public void Listener(HLPlugin plugin, NodeManager nm) {
	this.plugin = plugin;
	this.nm = nm;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChunkLoad(ChunkLoadEvent e) {
	Chunk c = e.getChunk();
	nm.loadChunk(c.getX(), c.getZ());
    }
}
