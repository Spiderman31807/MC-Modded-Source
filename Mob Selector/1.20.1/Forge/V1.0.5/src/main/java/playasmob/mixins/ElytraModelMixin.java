package playasmob.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import playasmob.ModelGetter;
import playasmob.MobData;

import net.minecraft.world.entity.Entity;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@Mixin(ElytraModel.class)
public abstract class ElytraModelMixin implements ModelGetter {
	@Shadow
	@Final
	public ModelPart rightWing;
	@Shadow
	@Final
	public ModelPart leftWing;

	public ModelPart getModel(int value) {
		if(value == 0)
			return this.rightWing;
		return this.leftWing;
	}

	public boolean canCrouch(LivingEntity entity) {
		if(entity instanceof Player player) {
			MobData data = MobData.get(player);
			if(data == null || data.typeData == null)
				return true;
			return data.typeData.canVisuallyCrouch();
		}
		return true;
	}

	@Inject(method = "setupAnim", at = @At("HEAD"), cancellable = true)
	public void setupAnim(LivingEntity entity, float p_102545_, float p_102546_, float p_102547_, float p_102548_, float p_102549_, CallbackInfo callback) {
      	float f = 0.2617994F;
      	float f1 = -0.2617994F;
      	float f2 = 0.0F;
      	float f3 = 0.0F;
      	if(entity.isFallFlying()) {
        	float f4 = 1.0F;
         	Vec3 vec3 = entity.getDeltaMovement();
         	if (vec3.y < 0.0D) {
            	Vec3 vec31 = vec3.normalize();
            	f4 = 1.0F - (float)Math.pow(-vec31.y, 1.5D);
         	}

         	f = f4 * 0.34906584F + (1.0F - f4) * f;
         	f1 = f4 * (-(float)Math.PI / 2F) + (1.0F - f4) * f1;
      	} else if (entity.isCrouching() && canCrouch(entity)) {
         	f = 0.6981317F;
         	f1 = (-(float)Math.PI / 4F);
         	f2 = 3.0F;
         	f3 = 0.08726646F;
      	}

      	this.leftWing.y = f2;
      	if(entity instanceof AbstractClientPlayer abstractclientplayer) {
         	abstractclientplayer.elytraRotX += (f - abstractclientplayer.elytraRotX) * 0.1F;
         	abstractclientplayer.elytraRotY += (f3 - abstractclientplayer.elytraRotY) * 0.1F;
         	abstractclientplayer.elytraRotZ += (f1 - abstractclientplayer.elytraRotZ) * 0.1F;
         	this.leftWing.xRot = abstractclientplayer.elytraRotX;
         	this.leftWing.yRot = abstractclientplayer.elytraRotY;
         	this.leftWing.zRot = abstractclientplayer.elytraRotZ;
     	} else {
         	this.leftWing.xRot = f;
         	this.leftWing.zRot = f1;
         	this.leftWing.yRot = f3;
      	}

      	this.rightWing.yRot = -this.leftWing.yRot;
      	this.rightWing.y = this.leftWing.y;
      	this.rightWing.xRot = this.leftWing.xRot;
      	this.rightWing.zRot = -this.leftWing.zRot;
      	callback.cancel();
   	}
}
