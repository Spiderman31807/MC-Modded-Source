package playasmob;

import net.minecraft.nbt.CompoundTag;

public interface AgeableData {
	abstract AgeData ageData();

   	default CompoundTag ageSave() {
      	return ageData().save();
   	}
   	
   	default void ageLoad(CompoundTag compound) {
      	ageData().load(compound);
   	}

   	default int getAge() {
   		return this.ageData().getAge();
   	}

   	default void ageUp(int amount, boolean forced) {
   		this.ageData().ageUp(amount, forced);
   	}

   	default void ageUp(int amount) {
      	this.ageUp(amount, false);
   	}

   	default void setAge(int amount) {
   		this.ageData().setAge(amount);
   	}

   	default void ageBoundaryReached() {
   		this.ageData().ageBoundaryReached();
   	}

   	default boolean isBaby() {
   		return this.getAge() < 0;
   	}

   	default void setBaby(boolean baby) {
   		this.setAge(baby ? -24000 : 0);
   	}
}
