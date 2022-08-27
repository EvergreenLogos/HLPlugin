package com.egl.hadeslabyrinth;

import java.util.HashMap;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;

public class EntityDeathListener implements Listener {
    HLPlugin plugin;

    HashMap<EntityType, Integer> baseMobExp = new HashMap<>();

    public EntityDeathListener(HLPlugin plugin) {
	this.plugin = plugin;

	baseMobExp.put(EntityType.ZOMBIE, 8);
	baseMobExp.put(EntityType.CREEPER, 14);
	baseMobExp.put(EntityType.SKELETON, 10);
	baseMobExp.put(EntityType.SPIDER, 12);
	baseMobExp.put(EntityType.ENDERMAN, 16);
	baseMobExp.put(EntityType.BLAZE, 20);
	baseMobExp.put(EntityType.CAVE_SPIDER, 15);
	baseMobExp.put(EntityType.DROWNED, 18);
	baseMobExp.put(EntityType.ELDER_GUARDIAN, 23);
	baseMobExp.put(EntityType.ENDER_DRAGON, 50);
	baseMobExp.put(EntityType.ENDERMITE, 16);
	baseMobExp.put(EntityType.EVOKER, 13);
	baseMobExp.put(EntityType.GHAST, 20);
	baseMobExp.put(EntityType.GIANT, 23);
	baseMobExp.put(EntityType.GUARDIAN, 29);
	baseMobExp.put(EntityType.HUSK, 11);
	baseMobExp.put(EntityType.ILLUSIONER, 16);
	baseMobExp.put(EntityType.MAGMA_CUBE, 14);
	baseMobExp.put(EntityType.PHANTOM, 14);
	baseMobExp.put(EntityType.PIG_ZOMBIE, 12);
	baseMobExp.put(EntityType.PILLAGER, 12);
	baseMobExp.put(EntityType.RAVAGER, 15);
	baseMobExp.put(EntityType.SHULKER, 18);
	baseMobExp.put(EntityType.SLIME, 7);
	baseMobExp.put(EntityType.SILVERFISH, 6);
	baseMobExp.put(EntityType.WITCH, 16);
	baseMobExp.put(EntityType.VINDICATOR, 16);
	baseMobExp.put(EntityType.VEX, 16);
	baseMobExp.put(EntityType.PHANTOM, 12);
	baseMobExp.put(EntityType.STRAY, 13);
    }

    private int calculateDamage(int baseDamage, int damagerLevel, int damagedLevel) {
	return (int) (damagerLevel * baseDamage * .14 - (.2 * damagedLevel));
    }

    private int genericDamage(Entity damager, Entity damaged, int damage, int damagerLvl) {
	int damagedLvl = 0;

	// plugin.getLogger().info("running Generic Damage");

	if (damaged instanceof Monster) {
	    // plugin.getLogger().info("It's a monster");
	    try {
		// damagedLvl = damaged.getMetadata("level").get(0).asInt();

		NamespacedKey key = new NamespacedKey(plugin, "lvl");
		damagedLvl = damaged.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
	    } catch (IndexOutOfBoundsException e) {
		return -1;
	    }

	    // if (damager.getMetadata("level").get(0) != null) {
	    // damagerLvl = damager.getMetadata("level").get(0).asInt();
	    // } else {
	    // return -1;
	    // }

	    int maxHP = damaged.getPersistentDataContainer().get(new NamespacedKey(plugin, "maxHP"),
		    PersistentDataType.INTEGER);

	    damage = calculateDamage((int) damage, damagerLvl, damagedLvl);

	    if (((Monster) damaged).getHealth() == 2048) {
		// int extraHealth =
		// damaged.getMetadata("extraHP").get(0).asInt();
		NamespacedKey keyExtraHP = new NamespacedKey(plugin, "extraHP");
		int extraHealth = damaged.getPersistentDataContainer().get(keyExtraHP, PersistentDataType.INTEGER);

		int newExtraHealth = extraHealth - damage;

		if (newExtraHealth < 0) {
		    damage = -1 * newExtraHealth;
		    // damaged.setMetadata("extraHP", new
		    // FixedMetadataValue(plugin, 0));
		    damaged.getPersistentDataContainer().set(keyExtraHP, PersistentDataType.INTEGER, 0);
		    damaged.setCustomName("Lv: " + damagedLvl + " HP: "
			    + ((int) ((Monster) damaged).getHealth() - damage) + "/" + maxHP);
		} else {
		    damage = 0;
		    // amaged.setMetadata("extraHP", new
		    // FixedMetadataValue(plugin, newExtraHealth));
		    damaged.getPersistentDataContainer().set(keyExtraHP, PersistentDataType.INTEGER, newExtraHealth);
		    damaged.setCustomName("Lv: " + damagedLvl + " HP: " + (int) (2048 + newExtraHealth) + "/" + maxHP);
		}
	    } else {
		damaged.setCustomName(
			"Lv: " + damagedLvl + " HP: " + ((int) ((Monster) damaged).getHealth() - damage) + "/" + maxHP);
	    }

	    return damage;

	} else if (damaged instanceof Player) {
	    PlayerAccount otherPlayer = plugin.pm.getPlayer(damaged.getName());
	    damagedLvl = otherPlayer.getLevel();
	    // damage = (int) (lvl*e.getDamage()*.14-(.2*otherLvl));
	    damage = calculateDamage(damage, damagerLvl, damagedLvl);
	    otherPlayer.player.sendMessage("You just took " + damage + " damage.");
	    return damage;
	} else if (damaged instanceof Animals || damaged instanceof Fish || damaged instanceof NPC) {
	    return calculateDamage(damage, damagerLvl, 1);
	}

	return -2;

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamagesEntity(EntityDamageByEntityEvent e) {
	Entity damager = e.getDamager();
	Entity damaged = e.getEntity();

	int damage = 0;

	if (damager instanceof Player) {
	    PlayerAccount pa = plugin.pm.getPlayer(((Player) damager).getName());
	    int lvl = pa.getLevel();

	    damage = genericDamage(damager, damaged, (int) e.getDamage(), lvl);

	    pa.player.sendMessage("You just dealt " + damage + " damage.");

	    // if (damaged instanceof Monster) {
	    // int mobLvl = damaged.getMetadata("level").get(0).asInt();
	    // //damage = (int) (lvl*e.getDamage()*.14-(.2*mobLvl));
	    //
	    //// damage = calculateDamage((int) e.getDamage(), lvl, mobLvl);
	    ////
	    //// if (((Monster) damaged).getHealth() == 2048) {
	    //// int extraHealth =
	    // damaged.getMetadata("extraHP").get(0).asInt();
	    ////
	    //// int newExtraHealth = extraHealth - damage;
	    ////
	    //// if (newExtraHealth < 0) {
	    //// damage = -1*newExtraHealth;
	    //// damaged.setMetadata("extraHP", new FixedMetadataValue(plugin,
	    // 0));
	    //// } else {
	    //// damage = 0;
	    //// damaged.setMetadata("extraHP", new FixedMetadataValue(plugin,
	    // newExtraHealth));
	    //// }
	    //// }
	    //
	    // damage = genericDamage(damager, damaged, (int) e.getDamage(),
	    // mobLvl);
	    //
	    //
	    // } else if (damaged instanceof Player) {
	    // PlayerAccount otherPlayer =
	    // plugin.pm.getPlayer(damaged.getName());
	    // int otherLvl = otherPlayer.getLevel();
	    // //damage = (int) (lvl*e.getDamage()*.14-(.2*otherLvl));
	    // damage = calculateDamage((int) e.getDamage(), lvl, otherLvl);
	    // }
	    // e.setDamage(damage);

	    // e.setDamage(lvl*e.getDamage()-(.5*lvl));
	} else if (damager instanceof Monster) {
	    LivingEntity entity = (LivingEntity) damager;
	    // int lvl = entity.getMetadata("level").get(0).asInt();

	    NamespacedKey key = new NamespacedKey(plugin, "lvl");
	    int lvl = damager.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);

	    damage = genericDamage(damager, damaged, (int) e.getDamage(), lvl);

	    // LivingEntity entity = (LivingEntity) damager;
	    // int damage = 0;
	    // int lvl = entity.getMetadata("level").get(0).asInt();
	    // if (damaged instanceof Player) {
	    // PlayerAccount pa = plugin.pm.getPlayer(((Player)
	    // damaged).getName());
	    // int playerLvl = pa.getLevel();
	    // //damage = (int) (lvl*e.getDamage()*.14-(.2*playerLvl));
	    // damage = calculateDamage((int) e.getDamage(), lvl, playerLvl);
	    // pa.player.sendMessage("You just took "+damage+" damage.");
	    // } else if (damaged instanceof Monster) {
	    // int otherLvl = damaged.getMetadata("level").get(0).asInt();
	    // //damage = (int) (lvl*e.getDamage()*.14-(.2*otherLvl));
	    // damage = calculateDamage((int) e.getDamage(), lvl, otherLvl);
	    // }
	    // e.setDamage(damage);
	    // e.setDamage(lvl*e.getDamage()-(.5*lvl));
	} else if (damager instanceof Projectile) {
	    Projectile arrow = (Projectile) damager;
	    ProjectileSource shooter = arrow.getShooter();

	    int shooterLvl = 0;

	    if (shooter instanceof Monster) {
		// shooterLvl = ((Entity)
		// shooter).getMetadata("level").get(0).asInt();
		NamespacedKey key = new NamespacedKey(plugin, "lvl");
		shooterLvl = ((Entity) shooter).getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
	    } else if (shooter instanceof Player) {
		PlayerAccount pa = plugin.pm.getPlayer(((Player) shooter).getName());
		shooterLvl = pa.getLevel();
	    }

	    damage = genericDamage(arrow, damaged, (int) e.getDamage(), shooterLvl);

	    if (shooter instanceof Player) {
		((Player) shooter).sendMessage("Your arrow just dealt " + damage + " damage.");
	    }
	} else if (damager instanceof TNTPrimed) {
	    damage = genericDamage(damager, damaged, (int) e.getDamage(), 1);
	}

	e.setDamage(damage);
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onEntityDeath(EntityDeathEvent e) {
	LivingEntity deadEntity = e.getEntity();

	Player killer = deadEntity.getKiller();

	if (killer != null) {
	    // PlayerAccount account = plugin.accounts.get(killer.getName());
	    PlayerAccount account = plugin.pm.getPlayer(killer.getName());

	    if (deadEntity instanceof Monster) {
		NamespacedKey key = new NamespacedKey(plugin, "lvl");
		int lvl = deadEntity.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);

		int exp = baseMobExp.get(deadEntity.getType()) * lvl;
		account.addExp(exp);
		account.player.sendMessage("You just earned " + exp + " exp.");

		account.updateScoreboard();
	    }
	}
    }
}
