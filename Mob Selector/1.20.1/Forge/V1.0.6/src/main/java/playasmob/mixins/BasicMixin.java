package playasmob.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import playasmob.MobData;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.client.Minecraft;

@Mixin(Entity.class)
public abstract class BasicMixin {
	public Entity self() {
		return (Entity)(Object)this;
	}

	public MobData getData() {
		if(self() instanceof Player player)
			return MobData.get(player);
		return null;
	}

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> callback) {
    	MobData data = getData();
    	if(data != null && data.typeData != null) {
    		InteractionResult result = data.typeData.interact(player, hand);
    		if(result != InteractionResult.PASS)
    			callback.setReturnValue(result);
    	}
    }

	@Inject(method = "dampensVibrations", at = @At("HEAD"), cancellable = true)
    private void dampensVibrations(CallbackInfoReturnable<Boolean> callback) {
    	MobData data = getData();
    	if(data != null && data.typeData != null && data.typeData.dampensVibrations())
    		callback.setReturnValue(true);
    }

	@Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
    private void fireImmune(CallbackInfoReturnable<Boolean> callback) {
    	MobData data = getData();
    	if(data != null && data.mob != EntityType.PLAYER && data.mob.fireImmune())
    		callback.setReturnValue(true);
    }
}
