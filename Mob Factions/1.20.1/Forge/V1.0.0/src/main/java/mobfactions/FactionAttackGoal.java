package mobfactions;

import java.util.function.Predicate;

import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.Mob;

public class FactionAttackGoal extends NearestAttackableTargetGoal {
	public FactionAttackGoal(Mob mob, boolean canSee, boolean canReach) {
		super(mob, Mob.class, 20, canSee, canReach, ((Predicate<Mob>)(entity) -> { return Factions.isEnemy(mob, entity); }));
	}

	public boolean canUse() {
		return super.canUse() && Factions.has(this.mob);
	}
}
