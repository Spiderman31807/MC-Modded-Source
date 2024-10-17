package playasmob;

import net.minecraft.world.entity.EntityType;
import java.util.List;

public class SelectionGroup {
	public EntityType displayMob;
	public List<EntityType> mobs;
	public MobBackground background;
	public String name;

	public SelectionGroup(String name, EntityType display, List<EntityType> mobs) {
		this(name, display, mobs, MobBackground.square());
	}

	public SelectionGroup(String name, EntityType display, List<EntityType> mobs, MobBackground background) {
		this.displayMob = display;
		this.mobs = mobs;
		this.background = background;
		this.name = name;
	}
}
