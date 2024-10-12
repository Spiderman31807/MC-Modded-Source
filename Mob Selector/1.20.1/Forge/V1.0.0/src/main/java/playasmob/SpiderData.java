package playasmob;

import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;

public class SpiderData implements EntityTypeData<SpiderRenderer> {
	public MobData data = null;
	public boolean climbing = false;

	public SpiderData(MobData data) {
		this.data = data;
	}

	public void load(CompoundTag compound) {
    	this.setClimbing(compound.getBoolean("Climbing"));
	}

	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
		compound.putBoolean("Climbing", this.isClimbing());
		return compound;
	}
	
	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder spiderBuilder = Spider.createAttributes();
		if(data.mob == EntityType.CAVE_SPIDER)
			spiderBuilder.add(Attributes.MAX_HEALTH, 12);
		
		playerBuilder.combine(spiderBuilder);
		return playerBuilder;
	}

	public double speedMultiplier() {
		return 0.3;
	}

	public SpiderRenderer getRenderer(EntityRendererProvider.Context context) {
		return new SpiderRenderer(context);
	}

   	public MobType getMobType() {
   		return MobType.ARTHROPOD;
   	}
	
   	public void tick() {
      	if (!data.world.isClientSide)
         	this.setClimbing(data.player.horizontalCollision);
    		
        if (this.isClimbing()) {
        	Vec3 delta = data.player.getDeltaMovement();
    		delta = new Vec3(delta.x, 0.2D, delta.z);
    		data.player.setDeltaMovement(delta);
        }
   	}

   	public boolean isClimbing() {
      	return this.climbing;
   	}

   	public void setClimbing(boolean climbing) {
      	this.climbing = climbing;
   	}

	public float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return data.mob == EntityType.SPIDER ? 0.65F : 0.45F;
	}

	public boolean allowEffect(MobEffectInstance effect) {
		return effect.getEffect() != MobEffects.POISON;
	}
	
	public void hurtEntity(LivingHurtEvent event) {
		if(data.mob == EntityType.SPIDER)
			return;

        int time = switch(data.world.getDifficulty()) {
        	default -> 0;
        	case NORMAL -> 7;
        	case HARD -> 15;
        };

        if (time > 0)
 	       event.getEntity().addEffect(new MobEffectInstance(MobEffects.POISON, time * 20, 0), data.player);
	}

	public boolean canPickup(ItemStack stack) {
		return false;
	}

	public boolean canSprint() {
		return false;
	}

	public boolean canCrouch() {
		return false;
	}

	public boolean preventSleeping() {
		return true;
	}
}
