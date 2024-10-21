
package playasmob;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.ParticleTypes;

public class AgeData {
	public MobData data;
	public EntityTypeData typeData;
	public boolean isBaby = false;
	public int forcedAgeTimer = 0;
	public int forcedAge = 0;
	public int age = 0;
	
	public AgeData(EntityTypeData typeData, MobData data) {
		this.typeData = typeData;
		this.data = data;
	}

   	public CompoundTag save() {
   		CompoundTag compound = new CompoundTag();
      	compound.putInt("Age", this.getAge());
      	compound.putInt("ForcedAge", this.forcedAge);
      	compound.putInt("ForcedAgeTimer", this.forcedAgeTimer);
      	return compound;
   	}
   	
   	public void load(CompoundTag compound) {
      	this.setAge(compound.getInt("Age"));
      	this.forcedAge = compound.getInt("ForcedAge");
      	this.forcedAgeTimer = compound.getInt("ForcedAgeTimer");
   	}

   	public void tick() {
      	if (data.world.isClientSide) {
         	if (this.forcedAgeTimer > 0) {
            	if (this.forcedAgeTimer % 4 == 0)
               		data.world.addParticle(ParticleTypes.HAPPY_VILLAGER, data.player.getRandomX(1.0D), data.player.getRandomY() + 0.5D, data.player.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
            	--this.forcedAgeTimer;
         	}
      	} else if (data.player.isAlive()) {
         	int i = this.getAge();
         	if (i < 0) {
            	++i;
            	this.setAge(i);
         	} else if (i > 0) {
            	--i;
            	this.setAge(i);
         	}
   		}
   	}

   	public int getAge() {
      	return this.age;
   	}

   	public void ageUp(int amount, boolean forced) {
      	int i = this.getAge();
      	i += amount * 20;
      	if (i > 0)
         	i = 0;

      	int j = i - i;
      	this.setAge(i);
      	if (forced) {
         	this.forcedAge += j;
         	if (this.forcedAgeTimer == 0)
            	this.forcedAgeTimer = 40;
      	}

      	if (this.getAge() == 0)
         	this.setAge(this.forcedAge);
   	}

   	public void setAge(int amount) {
      	int i = this.getAge();
      	this.age = amount;
      	if (i < 0 && amount >= 0 || i >= 0 && amount < 0) {
         	this.isBaby = amount < 0;
         	this.ageBoundaryReached();
      	}
   	}

   	public boolean isBaby() {
   		return this.getAge() < 0;
   	}

   	public void ageBoundaryReached() {
      	if (!this.isBaby() && data.player.isPassenger()) {
         	Entity entity = data.player.getVehicle();
         	if (entity instanceof Boat) {
            	Boat boat = (Boat)entity;
            	if (!boat.hasEnoughSpaceFor(data.player))
               		data.player.stopRiding();
         	}
      	}
   	}
}
