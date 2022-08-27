package com.egl.hadeslabyrinth;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Location;

import net.citizensnpcs.api.trait.Trait;
//import scala.util.Random;

public class SlaveFactory {
    HLPlugin plugin;

    public SlaveFactory(HLPlugin plugin) {
	this.plugin = plugin;
    }

    public UUID createSlave(String name, int type, UUID master) {
	plugin.getLogger().info("Creating new slave.");

	UUID slaveID = UUID.randomUUID();
	Slave slave = new Slave(plugin, slaveID);

	// slave.setName(name);
	slave.setType(type);
	slave.setMaster(master);

	switch (type) {
	case 0: // soldier female
	    slave.setName(plugin.database.getRandomName('f'));
	    SlaveSoldierTrait slaveSoldierTrait = new SlaveSoldierTrait(slave, true);
	    slave.setSkin(slaveSoldierTrait.randomSkin());
	    break;
	case 1: // soldier male
	    break;
	case 2: // wife
	    slave.setName(plugin.database.getRandomName('f'));
	    WifeTrait wifeTrait = new WifeTrait(slave);
	    slave.setSkin(wifeTrait.randomSkin());
	    break;
	}

	try {
	    plugin.database.saveSlaveData(slave);
	    plugin.database.updateSlaveData(slave);
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	return slaveID;
    }

    public Slave summonSlave(UUID id) {
	plugin.getLogger().info("Summoning slave with ID " + id);
	Slave slave;
	try {
	    slave = plugin.database.loadSlaveData(id);

	    SlaveTrait trait = null;

	    switch (slave.getType()) {
	    case 0: // soldier
		plugin.getLogger().info("It's a soldier");
		trait = new SlaveSoldierTrait(slave, true);
		// slave.setSkin(slave.getSkin());
		break;
	    case 1:
		break;
	    case 2:
		plugin.getLogger().info("lksadjfliajsi328u8u324324");
		trait = new WifeTrait(slave);
		break;
	    }

	    slave.setTrait(trait);

	    slave.spawn();

	    return slave;
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return null;
    }

    public Slave summonSlave(UUID id, Location loc) {
	plugin.getLogger().info("Summoning slave with ID " + id + " at a specific location");
	Slave slave;
	try {
	    slave = plugin.database.loadSlaveData(id);
	    slave.setLastPosition((int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), loc.getWorld().getName());

	    SlaveTrait trait = null;

	    switch (slave.getType()) {
	    case 0: // soldier
		plugin.getLogger().info("It's a soldier");
		trait = new SlaveSoldierTrait(slave, true);
		// slave.setSkin(slave.getSkin());
		break;
	    case 1:
		break;
	    case 2:
		plugin.getLogger().info("lksadjfliajsi328u8u324324");
		trait = new WifeTrait(slave);
		break;
	    }

	    slave.setTrait(trait);

	    slave.spawn();

	    plugin.slaves.put(slave.getID(), slave);

	    plugin.database.updateSlaveData(slave);

	    return slave;
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return null;
    }
}
