package playasmob.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraftforge.common.ForgeMod;

import playasmob.MobData;
import playasmob.Diet;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.effect.MobEffectInstance;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Item;


@Mixin(Player.class)
public abstract class PlayerMixin {
	public Player self() {
		return (Player)(Object)this;
	}

    public double getEntityReach() {
    	MobData data = MobData.get(self());
    	double range = self().getAttributeValue(ForgeMod.ENTITY_REACH.get());
    	if(data != null && data.typeData != null)
        	range *= data.typeData.entityReachMultiplier();
    	return range == 0 ? 0 : range + (self().isCreative() ? 0.5 : 0);
    }

    public double getBlockReach() {
    	MobData data = MobData.get(self());
    	double reach = self().getAttributeValue(ForgeMod.BLOCK_REACH.get());
    	if(data != null && data.typeData != null)
        	reach *= data.typeData.blockReachMultiplier();
    	return reach == 0 ? 0 : reach + (self().isCreative() ? 0.5 : 0);
    }

   	public BlockPos getOnPos(float p_216987_) {
   		Vec3 position = self().position();
      	if(self().mainSupportingBlockPos.isPresent()) {
         	BlockPos blockpos = self().mainSupportingBlockPos.get();
         	if(!(p_216987_ > 1.0E-5F)) {
            	return blockpos;
         	} else {
            	BlockState blockstate = self().level().getBlockState(blockpos);
            	return (!((double)p_216987_ <= 0.5D) || !blockstate.collisionExtendsVertically(self().level(), blockpos, self())) ? blockpos.atY(Mth.floor(position.y - (double)p_216987_)) : blockpos;
         	}
    	} else {
         	int i = Mth.floor(position.x);
         	int j = Mth.floor(position.y - (double)p_216987_);
         	int k = Mth.floor(position.z);
         	return new BlockPos(i, j, k);
      	}
   	}

   	public AABB getBoundingBoxForPose(Pose pose) {
      	EntityDimensions entitydimensions = self().getDimensions(pose);
      	float f = entitydimensions.width / 2.0F;
      	Vec3 vec3 = new Vec3(self().getX() - (double)f, self().getY(), self().getZ() - (double)f);
      	Vec3 vec31 = new Vec3(self().getX() + (double)f, self().getY() + (double)entitydimensions.height, self().getZ() + (double)f);
      	return new AABB(vec3, vec31);
   	}

   	public boolean canEnterPose(Pose pose) {
      	return self().level().noCollision(self(), this.getBoundingBoxForPose(pose).deflate(1.0E-7D));
   	}

	@Inject(method = "updatePlayerPose", at = @At("HEAD"), cancellable = true)
    private void updatePlayerPose(CallbackInfo callback) {
    	Player This = self();
    	MobData data = MobData.get(This);
    	if(data != null && data.typeData != null && data.fullyMobMode) {
			if(This.isSprinting() && !data.typeData.canSprint())
				This.setSprinting(false);
			if(This.isSwimming() && !data.typeData.canSwim())
				This.setSwimming(false);
			if(This.isFallFlying() && !data.typeData.canGlide())
				This.stopFallFlying();
    		
    		Pose forcedPose = This.getForcedPose();
      		if(forcedPose != null && (forcedPose != Pose.CROUCHING || data.typeData.canCrouch()) && (forcedPose != Pose.SWIMMING || data.typeData.canSwim())) {
         		This.setPose(forcedPose);
    			callback.cancel();
    			return;
      		}
      		
      		if(this.canEnterPose(Pose.SWIMMING)) {
        		Pose pose;
        		boolean flying = This.getAbilities().flying || data.typeData.flying();
         		if(This.isFallFlying() && data.typeData.canGlide()) {
            		pose = Pose.FALL_FLYING;
         		} else if(This.isSleeping() && !data.typeData.preventSleeping()) {
            		pose = Pose.SLEEPING;
         		} else if(This.isSwimming() && data.typeData.canSwim()) {
            		pose = Pose.SWIMMING;
         		} else if(This.isAutoSpinAttack() && data.typeData.canRiptide()) {
            		pose = Pose.SPIN_ATTACK;
         		} else if(This.isShiftKeyDown() && ((!flying && data.typeData.canCrouch()) || data.hitboxUpdate)) {
            		pose = Pose.CROUCHING;
         		} else if(data.typeData.customPose() != null) {
            		pose = data.typeData.customPose();
         		} else {
            		pose = Pose.STANDING;
         		}

         		Pose pose1 = pose;
         		if(!This.isSpectator() && !This.isPassenger() && !this.canEnterPose(pose) && !self().noPhysics) {
            		if(this.canEnterPose(Pose.CROUCHING) && data.typeData.canCrouch()) {
               			pose1 = Pose.CROUCHING;
            		} else if(data.typeData.canSwim()) {
               			pose1 = Pose.SWIMMING;
            		}
         		}

         		This.setPose(pose1);
      		}
    		callback.cancel();
    	}
    }

	@Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void travel(Vec3 pos, CallbackInfo callback) {
    	MobData data = MobData.get(self());
    	if(data != null && data.typeData != null) {
    		if(data.typeData.flying()) {
	    		data.flightTravel(pos, this.getOnPos(0.500001f));
	    		callback.cancel();
    		}
    	}
    }

	@Inject(method = "interactOn", at = @At("RETURN"), cancellable = true)
    private void interactOn(Entity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> callback) {
		if(callback.getReturnValue() != InteractionResult.PASS)
			return;

    	MobData data = MobData.get(self());
    	if(data != null && data.typeData != null) {
    		InteractionResult result = data.typeData.interactOn(entity, hand);
    		if(result != InteractionResult.PASS)
    			callback.setReturnValue(result);
    	}
    }

	@Inject(method = "isImmobile", at = @At("HEAD"), cancellable = true)
    private void isImmobile(CallbackInfoReturnable<Boolean> callback) {
    	MobData data = MobData.get(self());
    	if(data != null && data.typeData != null && data.typeData.isImmobile())
	    	callback.setReturnValue(true);
    }

	@Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void tryToStartFallFlying(CallbackInfoReturnable<Boolean> callback) {
    	MobData data = MobData.get(self());
    	if(data != null && data.typeData != null && !data.typeData.canGlide() && data.fullyMobMode)
	    	callback.setReturnValue(false);
    }

	@Inject(method = "getMovementEmission", at = @At("HEAD"), cancellable = true)
    private void getMovementEmission(CallbackInfoReturnable<Entity.MovementEmission> callback) {
    	MobData data = MobData.get(self());
    	if(data != null && data.typeData != null && data.typeData.flying())
    		callback.setReturnValue(Entity.MovementEmission.NONE);
    }

	@Inject(method = "getMyRidingOffset", at = @At("HEAD"), cancellable = true)
    private void getMyRidingOffset(CallbackInfoReturnable<Double> callback) {
    	MobData data = MobData.get(self());
    	if(data != null && data.typeData != null && data.fullyMobMode)
    		callback.setReturnValue(data.typeData.getMyRidingOffset());
    }

	@Inject(method = "getSpeed", at = @At("RETURN"), cancellable = true)
    private void getSpeed(CallbackInfoReturnable<Float> callback) {
    	MobData data = MobData.get(self());
    	if(data != null && data.typeData != null && data.fullyMobMode)
    		callback.setReturnValue(callback.getReturnValue() * (float)data.typeData.speedMultiplier());
    }

	@Inject(method = "makeStuckInBlock", at = @At("HEAD"), cancellable = true)
    private void makeStuckInBlock(BlockState state, Vec3 pos, CallbackInfo callback) {
    	MobData data = MobData.get(self());
    	if(data != null && data.typeData != null && !data.typeData.canGetStuck(state))
    		callback.cancel();
    }

	@Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void jumpFromGround(CallbackInfo callback) {
    	MobData data = MobData.get(self());
    	if(data != null && data.typeData != null && !data.typeData.canJump())
    		callback.cancel();
    }

	@Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
   	public void getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> callback) {
    	MobData data = MobData.get(self());
    	if(data != null && data.typeData != null && data.fullyMobMode) {
    		EntityDimensions hitbox = data.typeData.getDimensions(pose);
    		if(hitbox != null)
    			callback.setReturnValue(hitbox);
    	}
   	}

	@Inject(method = "canBeHitByProjectile", at = @At("HEAD"), cancellable = true)
    private void canBeHitByProjectile(CallbackInfoReturnable<Boolean> callback) {
    	MobData data = MobData.get(self());
    	if(data != null && data.typeData != null && data.typeData.projectileImmune())
    		callback.setReturnValue(false);
    }

	public void addEatEffect(ItemStack stack, Level world, LivingEntity entity, MobData data) {
      	Item item = stack.getItem();
      	if(stack.isEdible()) {
         	for(Pair<MobEffectInstance, Float> pair : stack.getFoodProperties(entity).getEffects()) {
            	if(!world.isClientSide && pair.getFirst() != null && world.random.nextFloat() < pair.getSecond())
               		data.typeData.addEatingEffect(new MobEffectInstance(pair.getFirst()), stack);
         	}
      	} else if(data.typeData.customFood().contains(stack.getItem())) {
         	for(Pair<MobEffectInstance, Float> pair : data.typeData.getFoodInfo(stack.getItem()).getEffects()) {
            	if(!world.isClientSide && pair.getFirst() != null && world.random.nextFloat() < pair.getSecond())
               		data.typeData.addEatingEffect(new MobEffectInstance(pair.getFirst()), stack);
         	}
      	}
   	}

	@Inject(method = "eat", at = @At("HEAD"), cancellable = true)
    private void eat(Level world, ItemStack stack, CallbackInfoReturnable<ItemStack> callback) {
		MobData data = MobData.get(self());
    	if(data != null && data.typeData != null && !data.typeData.notFood().contains(stack.getItem())) {
	      	self().getFoodData().eat(stack.getItem(), stack);
	      	self().awardStat(Stats.ITEM_USED.get(stack.getItem()));
	      	world.playSound(null, self().getX(), self().getY(), self().getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5f, world.random.nextFloat() * 0.1f + 0.9f);
	      	if(self() instanceof ServerPlayer player)
	         	CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);

    		if(stack.isEdible() || data.typeData.customFood().contains(stack.getItem())) {
				world.playSound(null, self().getX(), self().getY(), self().getZ(), data.typeData.getEatingSound(stack), SoundSource.NEUTRAL, 1, 1 + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
         		this.addEatEffect(stack, world, self(), data);
         		data.typeData.ate(stack);
         		if(!self().getAbilities().instabuild)
            		stack.shrink(1);
         		self().gameEvent(GameEvent.EAT);
    			callback.setReturnValue(stack);
    		}
    	}
    }

	@Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void isInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> callback) {
    	MobData data = MobData.get(self());
    	if(data != null && data.typeData != null && data.typeData.isInvulnerableTo(source))
    		callback.setReturnValue(true);
    }

	@Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    private void getHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> callback) {
    	MobData data = MobData.get(self());
        SoundEvent sound = data == null || data.typeData == null ? null : data.typeData.getHurtSound(source);
        if(sound != null && data.fullyMobMode)
            callback.setReturnValue(sound);
    }

	@Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
    private void getDeathSound(CallbackInfoReturnable<SoundEvent> callback) {
    	MobData data = MobData.get(self());
        SoundEvent sound = data == null || data.typeData == null ? null : data.typeData.getDeathSound();
        if(sound != null && data.fullyMobMode)
            callback.setReturnValue(sound);
    }

	@Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void playStepSound(CallbackInfo callback) {
    	MobData data = MobData.get(self());
        SoundEvent sound = data == null || data.typeData == null ? null : data.typeData.getStepSound();
        if(sound != null && data.fullyMobMode) {
       		float volume = data.typeData.stepVolume();
            self().playSound(sound, volume, 1);
            callback.cancel();
        }
    }
}
