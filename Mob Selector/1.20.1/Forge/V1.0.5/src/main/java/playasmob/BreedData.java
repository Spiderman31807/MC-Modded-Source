package playasmob;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.nbt.CompoundTag;

public class BreedData {
	public MobData data;
	public EntityTypeData typeData;
	
   	@Nullable
   	public UUID loveCause;
   	public int inLove;
	
	public BreedData(EntityTypeData typeData, MobData data) {
		this.typeData = typeData;
		this.data = data;
	}

   	public CompoundTag save() {
   		CompoundTag compound = new CompoundTag();
      	compound.putInt("InLove", this.inLove);
      	if (this.loveCause != null)
         	compound.putUUID("LoveCause", this.loveCause);
      	return compound;
   	}
   	
   	public void load(CompoundTag compound) {
      	this.inLove = compound.getInt("InLove");
      	this.loveCause = compound.hasUUID("LoveCause") ? compound.getUUID("LoveCause") : null;
   	}
   	
   	public void tick() {
      	if (this.typeData instanceof AgeableData ageable && ageable.getAge() != 0)
         	this.inLove = 0;

      	if (this.inLove > 0) {
        	--this.inLove;
         	if (this.typeData instanceof BreedableData breedable && this.inLove % 10 == 0)
            	breedable.createHeart();
      	}
   	}
   	
   	public void setInLove(@Nullable Player player) {
      	this.inLove = 600;
      	if (player != null)
         	this.loveCause = player.getUUID();
        if(this.typeData instanceof BreedableData breedable)
        	breedable.createHearts();
   	}

   	public void setInLoveTime(int time) {
      	this.inLove = time;
   	}

   	public int getInLoveTime() {
      	return this.inLove;
   	}

   	@Nullable
   	public ServerPlayer getLoveCause() {
      	if (this.loveCause == null) {
         	return null;
      	} else {
         	Player player = data.world.getPlayerByUUID(this.loveCause);
         	return player instanceof ServerPlayer ? (ServerPlayer)player : null;
      	}
   	}
}