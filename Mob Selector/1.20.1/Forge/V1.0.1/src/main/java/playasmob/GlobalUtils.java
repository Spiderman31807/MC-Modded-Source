package playasmob;

import java.util.Set;

import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.*;

public class GlobalUtils {
	public static String getString(EntityType type) {
		return EntityType.getKey(type).toString();
	}

	public static void resetAttackers(Player player) {
		final Player victim = player;
		player.setInvulnerable(true);
		PlayasmobMod.queueServerWork(20, () -> {
			victim.setInvulnerable(false);
		});
	}
	
	public static void modifyGoals(Entity entity) {
		if(entity instanceof Zombie zombie) {
			removeGoal(zombie, 1);
			zombie.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(zombie, Player.class, true, (player) -> zombieAttack(player, zombie)));
		} else if(entity instanceof AbstractGolem golem) {
      		golem.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(golem, Player.class, true, (player) -> golemAttack(player, golem)));
		} else if(entity instanceof Creeper creeper) {
			removeGoal(creeper, 0);
			creeper.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(creeper, Player.class, true, (player) -> isPlayer(player)));
      		creeper.goalSelector.addGoal(3, new AvoidEntityGoal<>(creeper, Player.class, 6.0F, 1.0D, 1.2D, (player) -> isCat(player)));
		} else if(entity instanceof AbstractSkeleton skeleton) {
			removeGoal(skeleton, skeleton instanceof WitherSkeleton ? 2 : 1);
			skeleton.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(skeleton, Player.class, true, (player) -> skeletonAttack(player, skeleton)));
		} else if(entity instanceof Spider spider) {
			removeGoal(spider, 1);
			spider.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(spider, Player.class, true, (player) -> spiderAttack(player, spider)));
		} else if(entity instanceof Witch witch) {
			removeGoal(witch, 2);
			witch.targetSelector.addGoal(2, new NearestHealableRaiderTargetGoal<>(witch, Player.class, true, (player) -> { return witch.hasActiveRaid() && isRaider(player) && getType(player) != EntityType.WITCH; } ));
			witch.targetSelector.addGoal(3, new NearestAttackableWitchTargetGoal<>(witch, Player.class, 10, true, false, (player) -> isPlayer(player)));
		} else if(entity instanceof AbstractIllager illager) {
			removeGoal(illager, 1);
			illager.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(illager, Player.class, true, (player) -> illagerAttack(player, illager)));
		}
	}

	public static void removeGoal(Mob entity, int index) {
		Set<WrappedGoal> wrapped = entity.targetSelector.getAvailableGoals();
		entity.targetSelector.removeGoal(wrapped.toArray(new WrappedGoal[wrapped.size()])[index].getGoal());
	}

	public static EntityType getType(LivingEntity entity) {
		if(entity instanceof Player player)
			return MobData.get(player).mob;
		return entity.getType();
	}

	public static boolean isPlayer(LivingEntity entity) {
		return getType(entity) == EntityType.PLAYER;
	}

	public static boolean isRaider(LivingEntity entity) {
		if(entity instanceof Player player)
			return MobData.get(player).typeData instanceof RaiderData;
		return false;
	}

	public static boolean isCat(LivingEntity entity) {
		EntityType type = getType(entity);
		return type == EntityType.CAT || type == EntityType.OCELOT;
	}

	public static boolean zombieAttack(LivingEntity entity, Zombie zombie) {
		if(entity instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.mob == EntityType.PLAYER)
				return true;
			if(data.typeData instanceof ZombieAttack attack)
				return attack.zombieAttack(zombie.getType());
		}
		
		return false;
	}

	public static boolean skeletonAttack(LivingEntity entity, AbstractSkeleton skeleton) {
		if(entity instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.mob == EntityType.PLAYER)
				return true;
			if(data.typeData instanceof SkeletonAttack attack)
				return attack.skeletonAttack(skeleton.getType());
		}
		
		return false;
	}

	public static boolean spiderAttack(LivingEntity entity, Spider spider) {
		if(entity instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.mob == EntityType.PLAYER)
				return true;
			if(data.typeData instanceof SpiderAttack attack)
				return attack.spiderAttack(spider.getType());
		}
		
		return false;
	}

	public static boolean illagerAttack(LivingEntity entity, AbstractIllager illager) {
		if(entity instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.mob == EntityType.PLAYER)
				return true;
			if(data.typeData instanceof IllagerAttack attack)
				return attack.illagerAttack(illager.getType());
		}
		
		return false;
	}

	public static boolean golemAttack(LivingEntity entity, AbstractGolem golem) {
		if(entity instanceof Player player) {
			if(MobData.get(player).typeData instanceof GolemAttack attack)
				return attack.golemAttack(golem.getType());
		}
	
		return false;
	}
}
