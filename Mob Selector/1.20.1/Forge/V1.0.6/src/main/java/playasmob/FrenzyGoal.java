package playasmob;

import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import java.util.function.Predicate;

public class FrenzyGoal extends NearestAttackableTargetGoal {
	public FrenzyGoal(Mob mob) {
		super(mob, LivingEntity.class, true, false);
	}

	public boolean canUse() {
		return this.mob.hasEffect(Effects.Frenzy.get()) && super.canUse();
	}

	public void stop() {
		this.mob.setTarget(null);
	}
}