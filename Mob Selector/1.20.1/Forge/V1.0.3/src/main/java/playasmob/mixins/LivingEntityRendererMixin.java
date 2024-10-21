package playasmob.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import playasmob.MobData;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
	public LivingEntityRenderer self() {
		return (LivingEntityRenderer) (Object)this;
	}

	@Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void getRenderType(LivingEntity entity, boolean flag1, boolean flag2, boolean flag3, CallbackInfoReturnable<RenderType> callback) {
    	Player player = Minecraft.getInstance().player;
    	if(player == null)
    		return;
    	MobData data = MobData.get(player);
    	if(data != null && data.typeData != null && data.typeData.forceGlow(entity))
    		callback.setReturnValue(RenderType.outline(self().getTextureLocation(entity)));
    }
}
