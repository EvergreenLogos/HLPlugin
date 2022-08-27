package com.egl.hadeslabyrinth;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryProvider;
import net.citizensnpcs.api.ai.speech.SpeechContext;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;

public abstract class SlaveTrait extends Trait {
    HLPlugin plugin;

    Slave slave;

    Object inventory;

    ArrayList<Object> data;

    Location target;

    public SlaveTrait(Slave slave, Object inventory, boolean sex) { // 0/false =
								    // male,
								    // 1/true =
								    // female
	super("SlaveTrait");
	plugin = JavaPlugin.getPlugin(HLPlugin.class);

	this.slave = slave;
	this.inventory = inventory;

	this.data = new ArrayList<Object>();
    }

    @EventHandler
    public void click(NPCRightClickEvent event) {
	if (event.getNPC() == this.getNPC() && slave.getMaster().equals(event.getClicker().getUniqueId())) {
	    SmartInventory gui;
	    gui = SmartInventory.builder().id("gui_main").provider((InventoryProvider) inventory).size(6, 9)
		    .title(ChatColor.AQUA + slave.getName()).closeable(true).build();

	    gui.open(event.getClicker());
	}
    }

    public String randomLine(ArrayList<String> list) {
	int numberOfLines = list.size();

	Random random = new Random();
	int selection = random.nextInt(numberOfLines);

	return list.get(selection);
    }

    public int randomSkin(ArrayList<Integer> choices) {
	int numberOfChoices = choices.size();

	Random random = new Random();
	int selection = random.nextInt(numberOfChoices);

	plugin.getLogger().info("Random choice: " + selection + ", points to " + choices.get(selection)
		+ ", the size was " + choices.size());

	return choices.get(selection);
    }

    public void loadData(ArrayList<Object> data) {

    }

    public ArrayList<Object> updateData() {
	return data;
    }
}
