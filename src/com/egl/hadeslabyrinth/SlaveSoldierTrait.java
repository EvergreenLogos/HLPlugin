package com.egl.hadeslabyrinth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;

import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.goals.TargetNearbyEntityGoal;
import net.citizensnpcs.api.ai.tree.Behavior;
import net.citizensnpcs.api.event.NPCRightClickEvent;
//import scala.collection.Set;

public class SlaveSoldierTrait extends SlaveTrait {

    ArrayList<String> lines;

    ArrayList<Integer> skins;

    boolean autoCombat = false;

    public SlaveSoldierTrait(Slave slave, boolean sex) {
	super(slave, new SlaveSoldierGUI(slave), sex);

	lines = new ArrayList<String>();
	skins = new ArrayList<Integer>();

	lines.add("My loyalty will never wane, my lord.");
	lines.add("I am willing to die in battle for our cause.");
	lines.add("Long live the Emperor! Long live the Great Federated Empire!");
	lines.add("No enemy is too strong for me!");

	if (sex) { // male skins
	    skins.add(55);
	    skins.add(50);
	    skins.add(43);
	    skins.add(13);
	    skins.add(40);
	    skins.add(26);
	    skins.add(7);
	} else { // female skins
	    skins.add(3);
	    skins.add(2);
	    skins.add(1);
	    skins.add(14);
	    skins.add(18);
	    skins.add(20);
	    skins.add(19);
	    skins.add(36);
	    skins.add(39);
	    skins.add(45);
	    skins.add(33);
	    skins.add(29);
	    skins.add(30);
	}
    }

    @EventHandler
    public void click(NPCRightClickEvent event) {
	super.click(event);
    }

    public String randomLine() {
	return super.randomLine(lines);
    }

    public int randomSkin() {
	return super.randomSkin(skins);
    }

    public void startCombat() {
	HashSet<EntityType> targets = new HashSet<EntityType>();

	targets.add(EntityType.ZOMBIE);
	targets.add(EntityType.CREEPER);
	targets.add(EntityType.SKELETON);
	targets.add(EntityType.ENDERMAN);
	targets.add(EntityType.DROWNED);
	targets.add(EntityType.EVOKER);
	targets.add(EntityType.BLAZE);
	targets.add(EntityType.SPIDER);
	targets.add(EntityType.CAVE_SPIDER);

	LivingEntity npc = (LivingEntity) this.getNPC().getEntity();

	this.getNPC().getDefaultGoalController().addGoal(
		TargetNearbyEntityGoal.builder(getNPC()).targets(targets).aggressive(true).radius(10.0).build(), 10);
    }

    public void endCombat() {
	this.getNPC().getDefaultGoalController().cancelCurrentExecution();
    }
}
