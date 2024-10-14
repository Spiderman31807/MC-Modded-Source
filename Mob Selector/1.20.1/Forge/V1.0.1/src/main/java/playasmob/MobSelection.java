package playasmob;

import net.minecraft.world.entity.EntityType;

public class MobSelection {
	public MobBackground background = MobBackground.square();
	public EntityType mob;
	public int mobScale = 1;
	public int x;
	public int y;

	public MobSelection(EntityType mob, int x, int y) {
		this.mob = mob;
		this.x = x;
		this.y = y;

		if(GlobalData.rareType.contains(mob))
			this.background = MobBackground.special();

		this.x += background.xOffset;
		this.y += background.yOffset;
	}

	public MobBackground getBackground() {
		return this.background;
	}
}
