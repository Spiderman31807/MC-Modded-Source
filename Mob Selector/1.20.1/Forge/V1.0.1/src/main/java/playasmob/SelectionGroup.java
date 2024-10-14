package playasmob;

import net.minecraft.world.entity.EntityType;
import java.util.List;

public class SelectionGroup {
	public EntityType displayMob;
	public List<EntityType> mobs;
	public MobBackground background;

	public SelectionGroup(EntityType display, List<EntityType> mobs) {
		this(display, mobs, MobBackground.square());
	}

	public SelectionGroup(EntityType display, List<EntityType> mobs, MobBackground background) {
		this.displayMob = display;
		this.mobs = mobs;
		this.background = background;
	}
}
