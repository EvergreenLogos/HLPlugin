package com.egl.hadeslabyrinth;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.event.CancelReason;
import net.citizensnpcs.api.ai.event.NavigatorCallback;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;

public class Slave {
    UUID id;
    UUID master;
    String name;

    int lastX;
    int lastY;
    int lastZ;
    String lastWorld;

    int homeX;
    int homeY;
    int homeZ;
    String homeWorld;

    int type;
    // 0 = soldier
    // 1 = girl

    int lvl;
    int skin;

    NPC npc;

    HLPlugin plugin;

    SlaveTrait trait;

    boolean follow;

    ItemStack[] inventory;

    ArrayList<Object> data;

    public Slave(HLPlugin plugin, UUID id) {
	this.plugin = plugin;
	this.id = id;

	this.follow = false;

	this.inventory = new ItemStack[8];

	this.data = new ArrayList<Object>();
    }

    public void spawn() {
	if (data == null) {
	    plugin.getLogger().info("It's null");
	}

	plugin.getLogger().info("Spawning NPC.");

	npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
	npc.setName(ChatColor.GREEN + name);

	// switch(type) {
	// case 0: // soldier
	// trait = new SlaveSoldierTrait(this);
	// skin = ((SlaveSoldierTrait) trait).randomSkin();
	// npc.addTrait((SlaveSoldierTrait) trait);
	// }

	if (data.size() >= 2) {
	    trait.loadData((ArrayList<Object>) data.get(1)); // insert the trait
							     // data, from index
							     // 1
	}

	npc.addTrait(trait);

	ArrayList<String> tuple = null;

	try {
	    tuple = plugin.database.getSkin(skin);
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	String texture = tuple.get(0);
	String signature = tuple.get(1);

	LookClose lc = new LookClose();
	lc.lookClose(true);
	lc.setRange(8);
	npc.addTrait(lc);

	npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, texture);
	npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, signature);
	npc.data().set("cached-skin-uuid-name", "null");
	npc.data().set("player-skin-name", "null");
	npc.data().set("cached-skin-uuid", UUID.randomUUID().toString());
	npc.data().set(NPC.PLAYER_SKIN_USE_LATEST, false);

	npc.data().set(NPC.PATHFINDER_OPEN_DOORS_METADATA, true);
	npc.data().set(NPC.COLLIDABLE_METADATA, true);

	if (npc instanceof SkinnableEntity) {
	    ((SkinnableEntity) npc).getSkinTracker().notifySkinChange(true);
	}
	if (npc.getEntity() instanceof SkinnableEntity) {
	    ((SkinnableEntity) npc.getEntity()).getSkinTracker().notifySkinChange(true);
	}

	try {
	    inventory = (ItemStack[]) data.get(0);
	} catch (Exception e) {

	}

	for (int i = 0; i < inventory.length; i++) {
	    ItemStack item = inventory[i];

	    switch (i) {
	    case 0:
		npc.getTrait(Equipment.class).set(EquipmentSlot.HELMET, item);
		break;
	    case 1:
		npc.getTrait(Equipment.class).set(EquipmentSlot.CHESTPLATE, item);
		break;
	    case 2:
		npc.getTrait(Equipment.class).set(EquipmentSlot.LEGGINGS, item);
		break;
	    case 3:
		npc.getTrait(Equipment.class).set(EquipmentSlot.BOOTS, item);
		break;
	    case 4:
		npc.getTrait(Equipment.class).set(EquipmentSlot.HAND, item);
		break;
	    }
	}

	npc.spawn(new Location(plugin.getServer().getWorld(this.lastWorld), lastX, lastY, lastZ));
    }

    public void despawn() {
	plugin.getLogger().info("Despawning NPC.");
	npc.despawn();
	CitizensAPI.getNPCRegistry().deregister(npc);
    }

    public void setLastPosition(int x, int y, int z, String world) {
	this.lastX = x;
	this.lastY = y;
	this.lastZ = z;
	this.lastWorld = world;
    }

    public void updateLastPosition() {
	// plugin.getLogger().info(String.valueOf(npc.getEntity().getLocation().getX()));
	// plugin.getLogger().info(npc.getEntity().getLocation().getWorld().getName());
	this.lastX = (int) npc.getEntity().getLocation().getX();
	this.lastY = (int) npc.getEntity().getLocation().getY();
	this.lastZ = (int) npc.getEntity().getLocation().getZ();
	this.lastWorld = npc.getEntity().getLocation().getWorld().getName();
    }

    public void setName(String name) {
	this.name = name;// StringUtils.capitalize(name);
    }

    public void setType(int type) {
	this.type = type;
    }

    public void setMaster(UUID master) {
	this.master = master;
    }

    public void setSkin(int x) {
	skin = x;
    }

    public void setLevel(int x) {
	this.lvl = x;
    }

    public void setTrait(SlaveTrait trait) {
	this.trait = trait;
    }

    public UUID getID() {
	return this.id;
    }

    public String getName() {
	return this.name;
    }

    public int getType() {
	return this.type;
    }

    public UUID getMaster() {
	return this.master;
    }

    public int getLastX() {
	return this.lastX;
    }

    public int getLastY() {
	return this.lastY;
    }

    public int getLastZ() {
	return this.lastZ;
    }

    public String getLastWorld() {
	return this.lastWorld;
    }

    public Object getTrait() {
	return trait;
    }

    public int getLevel() {
	return lvl;
    }

    public int getSkin() {
	return skin;
    }

    public void follow(Entity en) {
	if (!follow) {
	    npc.getNavigator().setTarget(en, false);
	    follow = true;
	} else {
	    npc.getNavigator().cancelNavigation();
	    follow = false;
	}
    }

    public void follow(Location loc) {
	npc.getNavigator().getLocalParameters().distanceMargin(0);
	npc.getNavigator().setTarget(loc);
    }

    public boolean isFollowing() {
	return follow;
    }

    public void teleport(Location loc) {
	npc.teleport(loc, TeleportCause.COMMAND);
    }

    public void addItemToInventory(ItemStack item, int slot) { // Slot IDs: 0
							       // helmet, 1
							       // chestplate, 2
							       // leggings, 3
							       // boots, 4 hand,
							       // 5 pendant,
							       // 6 7 misc
	inventory[slot] = item;

	switch (slot) {
	case 0:
	    npc.getTrait(Equipment.class).set(EquipmentSlot.HELMET, item);
	    break;
	case 1:
	    npc.getTrait(Equipment.class).set(EquipmentSlot.CHESTPLATE, item);
	    break;
	case 2:
	    npc.getTrait(Equipment.class).set(EquipmentSlot.LEGGINGS, item);
	    break;
	case 3:
	    npc.getTrait(Equipment.class).set(EquipmentSlot.BOOTS, item);
	    break;
	case 4:
	    npc.getTrait(Equipment.class).set(EquipmentSlot.HAND, item);
	    break;
	}

    }

    public ItemStack getItemFromInventory(int slot, boolean delete) {
	if (inventory[slot] == null) {
	    return null;
	} else {
	    ItemStack item = inventory[slot];
	    if (delete) {
		inventory[slot] = null;

		switch (slot) {
		case 0:
		    npc.getTrait(Equipment.class).set(EquipmentSlot.HELMET, null);
		    break;
		case 1:
		    npc.getTrait(Equipment.class).set(EquipmentSlot.CHESTPLATE, null);
		    break;
		case 2:
		    npc.getTrait(Equipment.class).set(EquipmentSlot.LEGGINGS, null);
		    break;
		case 3:
		    npc.getTrait(Equipment.class).set(EquipmentSlot.BOOTS, null);
		    break;
		case 4:
		    npc.getTrait(Equipment.class).set(EquipmentSlot.HAND, null);
		    break;
		}
	    }
	    return item;
	}
    }

    public boolean inventorySlotFilled(int slot) {
	if (inventory[slot] == null) {
	    return false;
	} else {
	    return true;
	}
    }

    public ArrayList<Object> getData() {
	return data;
    }

    public void setData(ArrayList<Object> data) {
	this.data = data;
    }

    public void updateData() {
	try {
	    ArrayList<Object> traitData = trait.updateData();

	    data.clear();
	    data.add(0, inventory);

	    if (!traitData.isEmpty()) {
		data.add(1, traitData);
	    }

	    plugin.getLogger().info("DATA: " + data.toString());
	    plugin.getLogger().info("INVENTORY: " + inventory.toString());

	    plugin.database.updateSlaveData(this);
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }
}
