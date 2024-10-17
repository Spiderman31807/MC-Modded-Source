package playasmob;

import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;

public class FearGoal extends PanicGoal {
	public FearGoal(PathfinderMob mob) {
		super(mob, 1);
	}

	public boolean shouldPanic() {
		return this.mob.hasEffect(Effects.Fear.get());
	}
}
