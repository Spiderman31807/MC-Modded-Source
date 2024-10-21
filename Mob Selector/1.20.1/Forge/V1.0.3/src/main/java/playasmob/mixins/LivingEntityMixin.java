package playasmob.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import playasmob.MobData;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	public LivingEntity self() {
		return (LivingEntity)(Object)this;
	}

	public MobData getData() {
		if(self() instanceof Player player)
			return MobData.get(player);
		return null;
	}

	@Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    private void getSpeed(boolean toggle, CallbackInfo callback) {
		if(!toggle)
			return;
    	
    	MobData data = getData();
    	if(data != null && !data.canSprint())
    		callback.cancel();
    }

	@Inject(method = "isSensitiveToWater", at = @At("HEAD"), cancellable = true)
    private void isSensitiveToWater(CallbackInfoReturnable<Boolean> callback) {
    	MobData data = getData();
    	if(data != null && data.typeData != null && data.typeData.isSensitiveToWater())
    		callback.setReturnValue(true);
    }

	@Inject(method = "canEnterPose", at = @At("HEAD"), cancellable = true)
    private void getSpeed(Pose pose, CallbackInfoReturnable<Boolean> callback) {
		if(pose != Pose.CROUCHING)
			return;
    	
    	MobData data = getData();
    	if(data != null && data.typeData != null && !data.typeData.canCrouch())
    		callback.setReturnValue(false);
    }

	@Inject(method = "getMobType", at = @At("HEAD"), cancellable = true)
    private void getSpeed(CallbackInfoReturnable<MobType> callback) {
    	MobData data = getData();
    	if(data != null && data.typeData != null)
    		callback.setReturnValue(data.typeData.getMobType());
    }
}
