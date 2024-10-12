package playasmob;

import net.minecraft.world.entity.EntityType;

public class MobSelection {
	public MobBackground background = MobBackground.square();
	public EntityType mob;
	public int x;
	public int y;

	public MobSelection(EntityType mob, int x, int y) {
		this.mob = mob;
		this.x = x;
		this.y = y;

		if(GlobalData.subType.contains(mob))
			this.background = MobBackground.pill();
		if(GlobalData.rareType.contains(mob))
			this.background = MobBackground.special();
	}

	public MobBackground getBackground() {
		return this.background;
	}
}
