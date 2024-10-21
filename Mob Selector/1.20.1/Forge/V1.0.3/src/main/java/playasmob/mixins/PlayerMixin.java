package playasmob.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import playasmob.MobData;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityDimensions;


@Mixin(Player.class)
public abstract class PlayerMixin {
	public Player self() {
		return (Player)(Object)this;
	}

	@Inject(method = "getSpeed", at = @At("RETURN"), cancellable = true)
    private void getSpeed(CallbackInfoReturnable<Float> callback) {
    	callback.setReturnValue(callback.getReturnValue() * (float)MobData.get(self()).getSpeedMultiplier());
    }

	@Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void jumpFromGround(CallbackInfo callback) {
    	if(!MobData.get(self()).canJump())
    		callback.cancel();
    }

	@Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void isInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> callback) {
    	if(MobData.get(self()).isInvulnerableTo(source))
    		callback.setReturnValue(true);
    }

	@Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    private void getHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> callback) {
        SoundEvent sound = MobData.get(self()).getHurtSound(source);
        if(sound != null)
            callback.setReturnValue(sound);
    }

	@Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
    private void getDeathSound(CallbackInfoReturnable<SoundEvent> callback) {
        SoundEvent sound = MobData.get(self()).getDeathSound();
        if(sound != null)
            callback.setReturnValue(sound);
    }

	@Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void getDeathSound(CallbackInfo callback) {
    	MobData data = MobData.get(self());
        SoundEvent sound = data.getStepSound();
        if(sound != null) {
            self().playSound(sound, data.stepVolume(), 1);
            callback.cancel();
        }
    }
}
