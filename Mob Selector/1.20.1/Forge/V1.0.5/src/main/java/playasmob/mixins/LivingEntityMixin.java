package playasmob.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.common.ForgeMod;

import playasmob.CustomEating;
import playasmob.MobData;
import playasmob.Diet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.InteractionHand;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements CustomEating {
	@Shadow
	protected int useItemRemaining;
	@Shadow
	protected ItemStack useItem;
	@Unique
	public ItemStack eatingCustom;

	public ItemStack eating() {
		return this.eatingCustom;
	}

	public LivingEntity self() {
		return (LivingEntity)(Object)this;
	}

	public MobData getData() {
		if(self() instanceof Player player)
			return MobData.get(player);
		return null;
	}

	public void jumpInFluid(FluidType type) {
    	MobData data = getData();
    	if(data == null || data.typeData == null || data.typeData.jumpInFluid(type)) {
        	self().setDeltaMovement(self().getDeltaMovement().add(0.0D, (double)0.04F * self().getAttributeValue(ForgeMod.SWIM_SPEED.get()), 0.0D));
    	} else if(self().onGround()) {
    		this.jumpFromGround();
    	}
	}

	@Shadow
	protected abstract void jumpFromGround();

	@Shadow
	protected abstract void spawnItemParticles(ItemStack stack, int timeUsed);

	@Shadow
	protected abstract void setLivingEntityFlag(int flag, boolean toggle);

	@Inject(method = "triggerItemUseEffects", at = @At("HEAD"), cancellable = true)
    private void triggerItemUseEffects(ItemStack stack, int timeUsed, CallbackInfo callback) {
    	if(!stack.isEmpty() && self().isUsingItem() && this.eatingCustom.equals(stack)) {
    		this.spawnItemParticles(stack, timeUsed);
            self().playSound(self().getEatingSound(stack), 0.5F + 0.5F * (float)self().getRandom().nextInt(2), (self().getRandom().nextFloat() - self().getRandom().nextFloat()) * 0.2F + 1.0F);
         	callback.cancel();
    	}
    }

	@Inject(method = "startUsingItem", at = @At("RETURN"), cancellable = true)
    private void startUsingItem(InteractionHand hand, CallbackInfo callback) {
    	MobData data = getData();
    	if(data == null || data.typeData == null)
    		return;
      	ItemStack stack = self().getItemInHand(hand);
      	if(!data.typeData.notFood().contains(stack.getItem()) && data.typeData.customFood().contains(stack.getItem())) {
      		FoodProperties foodInfo = data.typeData.getFoodInfo(stack.getItem());
         	int duration = net.minecraftforge.event.ForgeEventFactory.onItemUseStart(self(), stack, foodInfo.isFastFood() ? 16 : 32);
         	if(duration <= 0) return;
         	this.eatingCustom = stack;
         	this.useItem = stack;
         	this.useItemRemaining = duration;
         	if(!self().level().isClientSide) {
            	this.setLivingEntityFlag(1, true);
            	this.setLivingEntityFlag(2, hand == InteractionHand.OFF_HAND);
            	self().gameEvent(GameEvent.ITEM_INTERACT_START);
         	}
      	}
    }
   	
	@Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    private void setSprinting(boolean toggle, CallbackInfo callback) {
		if(!toggle)
			return;
    	
    	MobData data = getData();
    	if(data != null && data.typeData != null && !data.typeData.canSprint())
    		callback.cancel();
    }

	@Inject(method = "isBaby", at = @At("HEAD"), cancellable = true)
    private void isBaby(CallbackInfoReturnable<Boolean> callback) {
    	MobData data = getData();
    	if(data != null && data.typeData != null && data.fullyMobMode && data.typeData.isBaby())
    		callback.setReturnValue(true);
    }

	@Inject(method = "blockedByShield", at = @At("HEAD"), cancellable = true)
    private void blockedByShield(LivingEntity blocker, CallbackInfo callback) {
    	MobData data = getData();
    	if(data != null && data.typeData != null)
    		data.typeData.blockedByShield(blocker);
    }

	@Inject(method = "canDisableShield", at = @At("HEAD"), cancellable = true)
    private void canDisableShield(CallbackInfoReturnable<Boolean> callback) {
    	MobData data = getData();
    	if(data != null && data.typeData != null && data.typeData.canDisableShield())
    		callback.setReturnValue(true);
    }

	//For Testing only and should be commented when releasing mod updates
	/*@Inject(method = "isBlocking", at = @At("HEAD"), cancellable = true)
    private void isBlocking(CallbackInfoReturnable<Boolean> callback) {
    	callback.setReturnValue(true);
    }*/

	@Inject(method = "hasLineOfSight", at = @At("HEAD"), cancellable = true)
    private void hasLineOfSight(Entity entity, CallbackInfoReturnable<Boolean> callback) {
    	MobData data = getData();
    	if(data != null && data.typeData != null && !data.typeData.hasLineOfSight(entity))
    		callback.setReturnValue(false);
    }

	@Inject(method = "jumpInLiquid", at = @At("HEAD"), cancellable = true)
    private void jumpInLiquid(TagKey<Fluid> fluid, CallbackInfo callback) {
    	MobData data = getData();
    	if(data != null && data.typeData != null && !data.typeData.canFluidJump(fluid))
    		callback.cancel();
    }

	@Inject(method = "canStandOnFluid", at = @At("HEAD"), cancellable = true)
    private void canStandOnFluid(FluidState state, CallbackInfoReturnable<Boolean> callback) {
    	MobData data = getData();
    	if(data != null && data.typeData != null && data.typeData.walkableFluid(state))
    		callback.setReturnValue(true);
    }

	@Inject(method = "isSensitiveToWater", at = @At("HEAD"), cancellable = true)
    private void isSensitiveToWater(CallbackInfoReturnable<Boolean> callback) {
    	MobData data = getData();
    	if(data != null && data.typeData != null && data.typeData.isSensitiveToWater())
    		callback.setReturnValue(true);
    }

	@Inject(method = "getMobType", at = @At("HEAD"), cancellable = true)
    private void getMobType(CallbackInfoReturnable<MobType> callback) {
    	MobData data = getData();
    	if(data != null && data.typeData != null)
    		callback.setReturnValue(data.typeData.getMobType());
    }
}
