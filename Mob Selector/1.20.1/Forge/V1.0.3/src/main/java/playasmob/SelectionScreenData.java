package playasmob;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.EntityType;

public class SelectionScreenData {
	public static ArrayList<SelectionGroup> groups = new ArrayList();
	public static ArrayList<EntityType> unsorted = new ArrayList(GlobalData.supported);

	public static SelectionGroup getGroup(EntityType type, boolean onlyDisplay) {
		for(SelectionGroup group : groups) {
			if(group.displayMob == type)
				return group;
			if(!onlyDisplay && group.mobs.contains(type))
				return group;
		}

		return null;
	}

	public static SelectionGroup getGroup(String name) {
		for(SelectionGroup group : groups) {
			if(group.name == name)
				return group;
		}

		return null;
	}

	public static boolean withinGroup(EntityType type, String name) {
		SelectionGroup group = getGroup(name);
		if(group == null)
			return false;
		return group.mobs.contains(type);
	}

	public static void init() {
		GlobalData.disabled.forEach((type) -> unsorted.remove(type));
		addGroup(new SelectionGroup("boss", EntityType.WARDEN, getBossMobs()));
		addGroup(new SelectionGroup("human", EntityType.VILLAGER, List.of(EntityType.VILLAGER, EntityType.WANDERING_TRADER)));
		addGroup(new SelectionGroup("illager", EntityType.WITCH, List.of(EntityType.WITCH, EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.RAVAGER, EntityType.EVOKER, EntityType.ILLUSIONER)));
		addGroup(new SelectionGroup("piglin", EntityType.PIGLIN, List.of(EntityType.PIGLIN, EntityType.PIGLIN_BRUTE)));
		addGroup(new SelectionGroup("undead", EntityType.ZOMBIE, getZombies()));
		addGroup(new SelectionGroup("undead", EntityType.SKELETON, List.of(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON)));
		addGroup(new SelectionGroup("arthropod", EntityType.SPIDER, List.of(EntityType.SPIDER, EntityType.CAVE_SPIDER)));
		addGroup(new SelectionGroup("animal", EntityType.COW, List.of(EntityType.COW, EntityType.SHEEP, EntityType.PIG, EntityType.CHICKEN)));
	}

	public static MobPreview previewPlayer() {
		MobPreview preview = new MobPreview(null, EntityType.PLAYER);
		return preview;
	}

	public static void mergeGroup(SelectionGroup original, SelectionGroup merge) {
		List<EntityType> mobs = new ArrayList(original.mobs);
		merge.mobs.forEach((mob) -> mobs.add(mob));
		original.mobs = mobs;
	}

	public static void addGroup(SelectionGroup group) {
		List<EntityType> mobs = new ArrayList(group.mobs);
		for(EntityType mob : group.mobs) {
			if(!GlobalData.supported.contains(mob) || GlobalData.disabled.contains(mob))
				mobs.remove(mob);
		}

		group.mobs = mobs;
		if(!group.mobs.contains(group.displayMob))
			group.displayMob = group.mobs.get(0);
		if(group.mobs.size() <= 1)
			return;
			
		for(EntityType mob : group.mobs) {
			if(unsorted.contains(mob))
				unsorted.remove(mob);
		}

		SelectionGroup existingGroup = getGroup(group.name);
		if(existingGroup != null) {
			mergeGroup(existingGroup, group);
			return;
		}
		
		groups.add(group);
	}

	public static List<EntityType> getBossMobs() {
		ArrayList<EntityType> entities = new ArrayList();
		entities.add(EntityType.WARDEN);
		entities.add(EntityType.ENDER_DRAGON);
		entities.add(EntityType.WITHER);
		entities.add(EntityType.ELDER_GUARDIAN);
		entities.add(EntityType.PIGLIN_BRUTE);
		return entities;
	}

	public static List<EntityType> getZombies() {
		ArrayList<EntityType> entities = new ArrayList();
		entities.add(EntityType.ZOMBIE);
		entities.add(EntityType.HUSK);
		entities.add(EntityType.DROWNED);
		entities.add(EntityType.ZOMBIE_VILLAGER);
		entities.add(EntityType.ZOMBIFIED_PIGLIN);
		return entities;
	}
}