package playasmob;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.EntityType;

public class SelectionScreenData {
	public static ArrayList<SelectionGroup> groups = new ArrayList();
	public static ArrayList<EntityType> unsorted = new ArrayList(GlobalData.supported);

	public static SelectionGroup getGroup(EntityType type) {
		for(SelectionGroup group : groups) {
			if(group.displayMob == type)
				return group;
		}

		return null;
	}

	public static void init() {
		addGroup(new SelectionGroup(EntityType.WARDEN, getBossMobs()));
		addGroup(new SelectionGroup(EntityType.ZOMBIE, getZombies()));
		addGroup(new SelectionGroup(EntityType.SKELETON, List.of(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON)));
		addGroup(new SelectionGroup(EntityType.SPIDER, List.of(EntityType.SPIDER, EntityType.CAVE_SPIDER)));
		addGroup(new SelectionGroup(EntityType.COW, List.of(EntityType.COW, EntityType.SHEEP, EntityType.PIG, EntityType.CHICKEN)));
	}

	public static MobPreview previewPlayer() {
		MobPreview preview = new MobPreview(null, EntityType.PLAYER);
		return preview;
	}

	public static void addGroup(SelectionGroup group) {
		List<EntityType> mobs = new ArrayList(group.mobs);
		for(EntityType mob : mobs) {
			if(!GlobalData.supported.contains(mob))
				group.mobs.remove(mob);
		}

		if(!group.mobs.contains(group.displayMob))
			group.displayMob = group.mobs.get(0);
		if(group.mobs.size() <= 1)
			return;
			
		group.mobs.forEach((mob) -> unsorted.remove(mob));
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
