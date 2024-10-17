package playasmob;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import java.util.List;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;

public interface BreedableData extends AgeableData {
	abstract BreedData breedData();

   	default CompoundTag breedSave() {
      	return breedData().save();
   	}
   	
   	default void breedLoad(CompoundTag compound) {
      	breedData().load(compound);
   	}
   	
   	@Nullable
   	default AgeableMob getBreedOffspring(ServerLevel server, Entity partner) {
   		return null;
   	}

   	default boolean canBreed() {
      	return false;
   	}
   	
   	default boolean canFallInLove() {
      	return breedData().getInLoveTime() <= 0;
   	}
   	
   	default void setInLove(@Nullable Player player) {
      	breedData().setInLove(player);
   	}

   	default void setInLoveTime(int time) {
      	breedData().setInLoveTime(time);
   	}

   	default int getInLoveTime() {
      	return breedData().getInLoveTime();
   	}

   	@Nullable
   	default ServerPlayer getLoveCause() {
      	return breedData().getLoveCause();
   	}

   	default boolean isInLove() {
      	return breedData().getInLoveTime() > 0;
   	}

   	default void resetLove() {
      	this.setInLoveTime(0);
   	}
   	
   	default void createHeart() {
		Level world = breedData().data.world;
		Player player = breedData().data.player;
       	double d0 = world.random.nextGaussian() * 0.02D;
       	double d1 = world.random.nextGaussian() * 0.02D;
       	double d2 = world.random.nextGaussian() * 0.02D;
       	world.addParticle(ParticleTypes.HEART, player.getRandomX(1.0D), player.getRandomY() + 0.5D, player.getRandomZ(1.0D), d0, d1, d2);
   	}

   	default void createHearts() {
   		for(int i = 0; i < 7; ++i) {
            this.createHeart();
        }
   	}

   	default List<EntityType> vaildTypes() {
   		return List.of(breedData().data.mob);
   	}

   	default boolean isVaildType(Entity entity) {
   		EntityType type = entity.getType();
		if(entity instanceof Player player)
			type = MobData.get(player).mob;
   		return this.vaildTypes().contains(type);
   	}

   	default boolean isEntityInLove(Entity entity) {
   		if(entity instanceof Player player && MobData.get(player).typeData instanceof BreedableData breedData)
   			return breedData.isInLove();
   		if(entity instanceof Animal animal)
   			return animal.isInLove();
   		return false;
   	}

   	default void resetEntityLove(Entity entity) {
   		if(entity instanceof Player player && MobData.get(player).typeData instanceof BreedableData breedData) {
			breedData.setAge(6000);
   			breedData.resetLove();
   		}
   		
   		if(entity instanceof Animal animal) {
   			animal.setAge(6000);
   			animal.resetLove();
   		}
   	}

   	default boolean canMate(Entity target) {
      	if (!this.canBreed())
         	return false;
      	if (target == this)
         	return false;
      	if (!this.isVaildType(target))
         	return false;
         return this.isInLove() && isEntityInLove(target);
   	}

   	default void spawnChildFromBreeding(ServerLevel server, Entity partner) {
      	Player player = breedData().data.player;
      	AgeableMob ageablemob = this.getBreedOffspring(server, partner);
      	
      	if (ageablemob != null) {
         	ageablemob.setBaby(true);
         	ageablemob.moveTo(player.getX(), player.getY(), player.getZ(), 0.0F, 0.0F);
         	this.finalizeSpawnChildFromBreeding(server, partner, ageablemob);
         	server.addFreshEntityWithPassengers(ageablemob);
      	}
   	}

   	default void finalizeSpawnChildFromBreeding(ServerLevel server, Entity partner, @Nullable AgeableMob baby) {
      	this.setAge(6000);
      	this.resetLove();
      	this.resetEntityLove(partner);
      	Player player = breedData().data.player;
      	this.createHearts();
      	
      	if (server.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))
         	server.addFreshEntity(new ExperienceOrb(server, player.getX(), player.getY(), player.getZ(), player.getRandom().nextInt(7) + 1));
   	}
}
