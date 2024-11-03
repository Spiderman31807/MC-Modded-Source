package playasmob;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class TamedData {
	public MobData data = null;
	public EntityTypeData typeData = null;
	public DyeColor collarColor = DyeColor.RED;
	public Optional<UUID> owner = Optional.empty();
	public boolean sitting = false;
	public boolean tamed = false;
	public Level world;

	public TamedData(EntityTypeData typeData, MobData data) {
		this.typeData = typeData;
		this.data = data;
	}

   	public CompoundTag save() {
   		CompoundTag compound = new CompoundTag();
      	if (this.getOwnerUUID() != null)
        	compound.putUUID("Owner", this.getOwnerUUID());
        	
        compound.putInt("Collar", this.getCollarColor().getId());
    	compound.putBoolean("Sitting", this.isInSittingPose());
    	return compound;
   	}
   	
   	public void load(CompoundTag compound) {
      	if (compound.hasUUID("Owner")) {
      		this.setOwnerUUID(compound.getUUID("Owner"));
        	this.setTame(true);
      	}

		this.setCollarColor(DyeColor.byId(compound.getInt("Collar")));
      	this.setInSittingPose(compound.getBoolean("Sitting"));
   	}
   	
   	public boolean isTame() {
      	return this.tamed;
   	}

   	public void setTame(boolean tamed) {
   		this.tamed = tamed;
   	}

   	public boolean isInSittingPose() {
      	return this.sitting;
   	}

   	public void setInSittingPose(boolean sitting) {
   		this.sitting = sitting;
   	}
   
   	public DyeColor getCollarColor() {
      	return this.collarColor;
   	}

   	public void setCollarColor(DyeColor color) {
   		this.collarColor = color;
   	}

   	@Nullable
   	public UUID getOwnerUUID() {
      	if(this.owner.isPresent())
      		return this.owner.get();
      	return null;
   	}

   	public void setOwnerUUID(@Nullable UUID uuid) {
      	this.owner = Optional.ofNullable(uuid);
   	}

   	public LivingEntity getOwner() {
   		UUID uuid = this.getOwnerUUID();
   		if(typeData == null || uuid == null)
   			return null;
      	return data.world.getPlayerByUUID(uuid);
   	}

   	public void tame(Player player) {
      	this.setTame(true);
      	this.setOwnerUUID(player.getUUID());
   	}
}
