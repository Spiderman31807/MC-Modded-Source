package playasmob;

import javax.annotation.Nullable;
import java.util.Set;

import net.minecraft.util.Mth;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.Vec2;
import java.util.function.Predicate;
import java.util.List;

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
		if(entity instanceof Zombie zombie && !(entity instanceof ZombifiedPiglin)) {
			removeTargetGoal(zombie, 1);
			zombie.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(zombie, Player.class, true, (player) -> zombieAttack(player, zombie)));
		} else if(entity instanceof AbstractGolem golem) {
      		golem.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(golem, Player.class, true, (player) -> golemAttack(player, golem)));
		} else if(entity instanceof Creeper creeper) {
			removeTargetGoal(creeper, 0);
			creeper.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(creeper, Player.class, true, (player) -> isPlayer(player)));
      		creeper.goalSelector.addGoal(3, new AvoidEntityGoal<>(creeper, Player.class, 6, 1, 1, (player) -> isCat(player)));
		} else if(entity instanceof AbstractSkeleton skeleton) {
			removeTargetGoal(skeleton, skeleton instanceof WitherSkeleton ? 2 : 1);
			skeleton.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(skeleton, Player.class, true, (player) -> skeletonAttack(player, skeleton)));
		} else if(entity instanceof Spider spider) {
			removeTargetGoal(spider, 1);
			spider.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(spider, Player.class, true, (player) -> spiderAttack(player, spider)));
		} else if(entity instanceof Witch witch) {
			removeTargetGoal(witch, 2);
			witch.targetSelector.addGoal(2, new NearestHealableRaiderTargetGoal<>(witch, Player.class, true, (player) -> { return witch.hasActiveRaid() && isRaider(player) && getType(player) != EntityType.WITCH; } ));
			witch.targetSelector.addGoal(3, new NearestAttackableWitchTargetGoal<>(witch, Player.class, 10, true, false, (player) -> isPlayer(player)));
		} else if(entity instanceof AbstractIllager illager) {
			removeTargetGoal(illager, 1);
			illager.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(illager, Player.class, true, (player) -> illagerAttack(player, illager)));
			if(illager instanceof Evoker evoker) {
				removeGoal(evoker, 7);
				evoker.goalSelector.addGoal(2, new AvoidEntityGoal<>(evoker, Player.class, 8, 0.6, 1, (player) -> !isRaider(player)));
			}
		} else if(entity instanceof Ravager ravager) {
			removeTargetGoal(ravager, 1);
			ravager.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(ravager, Player.class, true, (player) -> ravagerAttack(player, ravager)));
		} else if(entity instanceof Endermite endermite) {
			removeTargetGoal(endermite, 1);
			endermite.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(endermite, Player.class, true, (player) -> isPlayer(player)));
		} else if(entity instanceof Blaze blaze) {
			removeTargetGoal(blaze, 1);
			blaze.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(blaze, Player.class, true, (player) -> isPlayer(player)));
		} else if(entity instanceof Ghast ghast) {
			removeTargetGoal(ghast, 1);
			ghast.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(ghast, Player.class, 10, true, false, (player) -> isPlayer(player) && vaildHeight(player, ghast, 4)));
		} else if(entity instanceof Silverfish silverfish) {
			removeTargetGoal(silverfish, 1);
			silverfish.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(silverfish, Player.class, true, (player) -> isPlayer(player)));
		} else if(entity instanceof Slime slime) {
			removeTargetGoal(slime, 0);
			slime.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(slime, Player.class, 10, true, false, (player) -> isPlayer(player) && vaildHeight(player, slime, 4)));
		} else if(entity instanceof Vex vex) {
			removeTargetGoal(vex, 2);
			vex.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(vex, Player.class, 10, true, false, (player) -> isPlayer(player)));
		}

		if(entity instanceof Mob mob)
			mob.targetSelector.addGoal(0, new FrenzyGoal(mob));
		if(entity instanceof PathfinderMob mob)
			mob.goalSelector.addGoal(0, new FearGoal(mob));
	}

	public static void removeTargetGoal(Mob entity, int index) {
		Set<WrappedGoal> wrapped = entity.targetSelector.getAvailableGoals();
		entity.targetSelector.removeGoal(wrapped.toArray(new WrappedGoal[wrapped.size()])[index].getGoal());
	}

	public static void removeGoal(Mob entity, int index) {
		Set<WrappedGoal> wrapped = entity.goalSelector.getAvailableGoals();
		entity.goalSelector.removeGoal(wrapped.toArray(new WrappedGoal[wrapped.size()])[index].getGoal());
	}

	public static EntityType getType(LivingEntity entity) {
		if(entity instanceof Player player)
			return MobData.get(player).mob;
		return entity.getType();
	}

	public static boolean isPlayer(LivingEntity entity) {
		return getType(entity) == EntityType.PLAYER;
	}

	public static boolean vaildHeight(LivingEntity entity, LivingEntity self, double height) {
		return Math.abs(entity.getY() - self.getY()) <= height;
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

	public static boolean ravagerAttack(LivingEntity entity, Ravager ravager) {
		if(entity instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.mob == EntityType.PLAYER)
				return true;
			if(data.typeData instanceof IllagerAttack attack)
				return attack.illagerAttack(ravager.getType());
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

	public static List<Entity> getNearby(Player player, double distance) {
    	return getNearby(player, distance, (entity) -> { return true; });
	}
	
	public static List<Entity> getNearby(Player player, double distance, Predicate filter) {
    	return player.level().getEntities(player, player.getBoundingBox().inflate(distance), filter);
	}

	@Nullable
	public static Entity getClosestTarget(Player player, double distance) {
    	return getClosestTarget(player, distance, (entity) -> { return true; });
	}

	@Nullable
	public static Entity getClosestTarget(Player player, double distance, Predicate filter) {
    	Vec3 startVec = player.position().add(0, player.getEyeHeight(), 0);
    	Vec3 endVec = startVec.add(player.getLookAngle().scale(distance));
    	AABB boundingBox = player.getBoundingBox().inflate(distance);
    	EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(player, startVec, endVec, boundingBox, filter, distance);
    	return entityHitResult == null ? null : entityHitResult.getEntity();
	}
}
