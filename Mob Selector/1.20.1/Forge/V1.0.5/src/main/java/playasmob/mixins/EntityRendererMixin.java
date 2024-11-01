package playasmob.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import playasmob.MobData;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.Minecraft;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
	@Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void shouldRender(Entity entity, Frustum frustum, double value1, double value2, double value3, CallbackInfoReturnable<Boolean> callback) {
    	Player player = Minecraft.getInstance().player;
    	if(player == null)
    		return;
    	MobData data = MobData.get(player);
    	if(data != null && data.typeData != null && !data.typeData.renderEntity(entity))
    		callback.setReturnValue(false);
    }
}
