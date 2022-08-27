package com.egl.hadeslabyrinth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

public class MobSpawnListener implements Listener {

    HLPlugin plugin;

    Random random = new Random();

    HashMap<String, Integer[]> worldParameters = new HashMap<>();

    public MobSpawnListener(HLPlugin plugin) {
	this.plugin = plugin;
	// base level, increment
	worldParameters.put("spawn", new Integer[] { 0, 26 });
	worldParameters.put("cave", new Integer[] { 75, 18 });
	worldParameters.put("world_nether", new Integer[] { 150, 29 });
	worldParameters.put("flatland", new Integer[] { 500, 29 });
	worldParameters.put("prison", new Integer[] { 0, 29 });
	worldParameters.put("world", new Integer[] { 0, 1 });
	worldParameters.put("world_the_end", new Integer[] { 800, 10 });
	worldParameters.put("Capital", new Integer[] { 1, 26 });
	worldParameters.put("Gensokyo", new Integer[] { 200, 3 });
	worldParameters.put("netherworld", new Integer[] { 1500, 26 });
	worldParameters.put("heaven", new Integer[] { 5000, 5 });
	worldParameters.put("LunarCapital", new Integer[] { 3000, 5 });
	worldParameters.put("three", new Integer[] { 1, 26 });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
	World w = event.getEntity().getWorld();
	Entity e = event.getEntity();

	if (e instanceof Monster) {
	    int variation = random.nextInt(5 - 0) + 0;
	    int baseLevel = worldParameters.get(w.getName())[0];
	    int increment = worldParameters.get(w.getName())[1];
	    int level = (int) ((e.getLocation().distance(new Location(w, 0, 64, 0))) / increment) + variation
		    + baseLevel;

	    double baseHealth = ((Monster) e).getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
	    double hp = .65 * baseHealth * level;

	    // e.setMetadata("level", new FixedMetadataValue(plugin, level));

	    NamespacedKey key = new NamespacedKey(plugin, "lvl");
	    e.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, level);

	    NamespacedKey key2 = new NamespacedKey(plugin, "maxHP");
	    e.getPersistentDataContainer().set(key2, PersistentDataType.INTEGER, (int) hp);

	    if (hp <= 2048) {
		((Monster) e).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hp);
		((Monster) e).setHealth((int) hp);
	    } else {
		((Monster) e).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2048);
		((Monster) e).setHealth(2048);

		// e.setMetadata("extraHP", new FixedMetadataValue(plugin,
		// hp-2048));

		NamespacedKey key3 = new NamespacedKey(plugin, "extraHP");

		e.getPersistentDataContainer().set(key3, PersistentDataType.INTEGER, (int) hp - 2048);
	    }

	    e.setCustomName("Lv: " + level + " HP: " + (int) hp + "/" + (int) hp);
	    e.setCustomNameVisible(true);

	}

	// if (w==plugin.getServer().getWorld("prison")) {
	// EntityInsentient nmsEntity = (EntityInsentient) ((CraftEntity)
	// e).getHandle();
	// AttributeInstance attributes =
	// nmsEntity.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE);
	// AttributeModifier modifier = new AttributeModifier("damage", 17d,
	// AttributeModifier.Operation.ADDITION);
	// attributes.b(modifier.getUniqueId());
	// attributes.a(modifier);
	// }
    }
}
