package playasmob.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import playasmob.MobData;
import playasmob.WardenData;

import javax.annotation.Nullable;

import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

@Mixin(Warden.class)
public abstract class WardenMixin {
	@Inject(method = "canTargetEntity", at = @At("HEAD"), cancellable = true)
    private void canTargetEntity(@Nullable Entity entity, CallbackInfoReturnable<Boolean> callback) {
    	if(entity instanceof Player player && MobData.get(player).typeData instanceof WardenData)
	    	callback.setReturnValue(false);
    }
}
