package playasmob;

import javax.annotation.Nullable;
import java.util.UUID;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.nbt.CompoundTag;

public interface TamableData {
	abstract TamedData tameData();

   	default CompoundTag tameSave() {
      	return tameData().save();
   	}
   	
   	default void tameLoad(CompoundTag compound) {
      	tameData().load(compound);
   	}
   	
   	default boolean isTame() {
      	return tameData().isTame();
   	}

   	default void setTame(boolean tamed) {
   		tameData().setTame(tamed);
   	}

   	default boolean isInSittingPose() {
      	return tameData().isInSittingPose();
   	}

   	default void setInSittingPose(boolean sitting) {
   		tameData().setInSittingPose(sitting);
   	}
   
   	default DyeColor getCollarColor() {
      	return tameData().getCollarColor();
   	}

   	default void setCollarColor(DyeColor color) {
   		tameData().setCollarColor(color);
   	}

   	@Nullable
   	default UUID getOwnerUUID() {
      	return tameData().getOwnerUUID();
   	}

   	default void setOwnerUUID(@Nullable UUID uuid) {
      	tameData().setOwnerUUID(uuid);
   	}

   	default void tame(Player player) {
      	tameData().tame(player);
   	}

   	default LivingEntity getOwner() {
      	return tameData().getOwner();
   	}

   	default boolean isOwnedBy(LivingEntity entity) {
      	return entity == this.getOwner();
   	}
}
