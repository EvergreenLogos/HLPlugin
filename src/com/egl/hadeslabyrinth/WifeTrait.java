package com.egl.hadeslabyrinth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;

import net.citizensnpcs.api.event.NPCRightClickEvent;

public class WifeTrait extends SlaveTrait {

    ArrayList<String> lines;

    ArrayList<Integer> skins;

    int affection;
    int personalityType; // 0 affectionate, 1 silly, 2 childish, 3 serious, 4
			 // quiet, 5 distant/tsundere, 6 combative, 7 fiery,
			 // 8 caring

    ArrayList<Material> favoriteItems;
    HashMap<Integer, Integer> skills; // 0 cooking, 1 fighting, 2 dancing, 3
				      // singing, 4 poetry, 5 intelligence, 6
				      // healing, 7 crafting, 8
				      // scavenging/treasure hunting
    // first integer is skill ID
    // second integer is skill level

    public WifeTrait(Slave slave) {
	super(slave, new WifeGUI(slave), true);

	this.slave = slave;

	lines = new ArrayList<String>();
	skins = new ArrayList<Integer>();

	lines.add("Nothing beneath Heaven could ever separate us.");
	lines.add("I will follow you to the ends of the Earth.");
	lines.add("Could this be the promise we made in a past life?");
	lines.add("The spring breeze feels nice.");

	skins.addAll(Arrays.asList(1, 2, 4, 5, 10, 11, 14, 17, 19, 20, 21, 22, 27, 28, 32, 34, 35, 38, 39, 42, 45, 46,
		47, 48, 49, 51, 53));

	plugin.getLogger().info(skins.toString());

	ArrayList<Material> items = new ArrayList<Material>();

	items.addAll(Arrays.asList(Material.CAKE, Material.POPPY, Material.OXEYE_DAISY, Material.DANDELION,
		Material.LILY_OF_THE_VALLEY, Material.RED_MUSHROOM, Material.SUNFLOWER, Material.BAMBOO, Material.APPLE,
		Material.GOLDEN_APPLE, Material.SWEET_BERRIES, Material.COOKED_SALMON, Material.GOLDEN_SWORD,
		Material.LAPIS_LAZULI, Material.GLOWSTONE_DUST, Material.BOOK, Material.DIAMOND,
		Material.LEGACY_EYE_OF_ENDER));

	Random rand = new Random();
	Material favorite1 = items.get(rand.nextInt(items.size()));
	Material favorite2 = items.get(rand.nextInt(items.size()));

	favoriteItems = new ArrayList<Material>();

	favoriteItems.add(favorite1);
	favoriteItems.add(favorite2);

	plugin.getLogger().info("Her favorite items are: " + favoriteItems.toString());
    }

    @EventHandler
    public void click(NPCRightClickEvent event) {
	((WifeGUI) this.inventory).setTrait(this);

	super.click(event);
    }

    public String randomLine() {
	return super.randomLine(lines);
    }

    public int randomSkin() {
	return super.randomSkin(skins);
    }

    public String giveGift(Material item) {
	if (item == favoriteItems.get(0) || item == favoriteItems.get(1)) {
	    affection += 10;
	    return "It's perfect! " + item.toString() + "s are my favorite. Thank you so much!";
	} else {
	    affection++;
	    return "Thank you for the gift!";
	}
    }

    @Override
    public void loadData(ArrayList<Object> data) {
	favoriteItems = (ArrayList<Material>) data.get(0);
	affection = (Integer) data.get(1);
    }

    @Override
    public ArrayList<Object> updateData() {
	data.add(0, favoriteItems);
	data.add(1, affection);

	return data;
    }

    public int getAffection() {
	return affection;
    }
}
