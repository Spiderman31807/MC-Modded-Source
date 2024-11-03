package playasmob.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import playasmob.MobData;
import playasmob.Diet;

import net.minecraft.client.player.LocalPlayer;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
	public LocalPlayer self() {
		return (LocalPlayer)(Object)this;
	}

	@Inject(method = "hasEnoughFoodToStartSprinting", at = @At("HEAD"), cancellable = true)
    private void hasEnoughFoodToStartSprinting(CallbackInfoReturnable<Boolean> callback) {
    	MobData data = MobData.get(self());
    	if(data != null && data.typeData != null && data.typeData.getDiet() == Diet.None)
	    	callback.setReturnValue(true);
    }
}
