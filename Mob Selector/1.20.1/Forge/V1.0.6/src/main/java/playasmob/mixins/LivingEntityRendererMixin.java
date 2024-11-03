package playasmob.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import playasmob.MobData;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Pose;
import net.minecraft.util.Mth;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
	public LivingEntityRenderer self() {
		return (LivingEntityRenderer) (Object)this;
	}

    @Shadow
    protected abstract float getFlipDegrees(LivingEntity entity);

    @Shadow
    protected abstract boolean isShaking(LivingEntity entity);

	@Inject(method = "setupRotations", at = @At("HEAD"), cancellable = true)
    private void setupRotations(LivingEntity entity, PoseStack pose, float p_115319_, float p_115320_, float p_115321_, CallbackInfo callback) {
		MobData data = entity instanceof Player player ? MobData.get(player) : null;
		if(data == null || data.typeData == null || !data.fullyMobMode)
			return;
    	
      	if (this.isShaking(entity))
         	p_115320_ += (float)(Math.cos((double)entity.tickCount * 3.25) * Math.PI * (double)0.4f);
      	if (!entity.hasPose(Pose.SLEEPING))
         	pose.mulPose(Axis.YP.rotationDegrees(180.0F - p_115320_));

      	if (entity.deathTime > 0) {
         	float f = ((float)entity.deathTime + p_115321_ - 1f) / 20f * 1.6f;
         	f = Mth.sqrt(f);
         	if (f > 1f)
           		f = 1f;

         	pose.mulPose(Axis.ZP.rotationDegrees(f * this.getFlipDegrees(entity)));
      	} else if(entity.isAutoSpinAttack()) {
         	pose.mulPose(Axis.XP.rotationDegrees(-90f - entity.getXRot()));
         	pose.mulPose(Axis.YP.rotationDegrees(((float)entity.tickCount + p_115321_) * -75f));
      	} else if(entity.isFallFlying()) {
         	float flightTick = (float)entity.getFallFlyingTicks() + p_115321_;
         	float flyAngle = Mth.clamp(flightTick * flightTick / 100f, 0f, 1f);
            pose.mulPose(Axis.XP.rotationDegrees(flyAngle * (data.typeData.glideAngle() - entity.getXRot())));
      	} else if(entity.isVisuallySwimming()) {
      		float swimmingAngle = data.typeData.swimAngle();
         	float flipAngle = entity.isInWater() || entity.isInFluidType((fluidType, height) -> entity.canSwimInFluidType(fluidType)) ? swimmingAngle - entity.getXRot() : swimmingAngle;
         	float applyAngle = Mth.lerp(entity.getSwimAmount(p_115321_), 0.0F, flipAngle);
         	pose.mulPose(Axis.XP.rotationDegrees(applyAngle));
         	if (entity.isVisuallySwimming())
            	pose.translate(0f, -1f, 0.3f);
      	} else if (entity.hasPose(Pose.SLEEPING)) {
      		boolean forcedSleeping = data.typeData.customPose() == Pose.SLEEPING;
         	Direction direction = entity.getBedOrientation();
         	float f1 = direction != null ? direction.toYRot() : p_115320_;
         	if(forcedSleeping)
         		f1 = data.typeData.sleepingYaw();
         	pose.mulPose(Axis.YP.rotationDegrees(f1));
         	if(!forcedSleeping)
         		pose.mulPose(Axis.ZP.rotationDegrees(this.getFlipDegrees(entity)));
         	pose.mulPose(Axis.YP.rotationDegrees(270f));
      	} else if(LivingEntityRenderer.isEntityUpsideDown(entity)) {
         	pose.translate(0f, entity.getBbHeight() + 0.1f, 0f);
         	pose.mulPose(Axis.ZP.rotationDegrees(180f));
      	}
      	callback.cancel();
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
