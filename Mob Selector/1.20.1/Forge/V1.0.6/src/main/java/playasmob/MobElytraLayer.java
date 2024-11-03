package playasmob;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.entity.ItemRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.math.Axis;

public class MobElytraLayer<T extends LivingEntity, M extends EntityModel<T>> extends ElytraLayer<T, M> {
   	public final ElytraModel<T> elytraModel;
   	public Vec3 offset = new Vec3(0, 0, 0);
   	public Vec3 rotation = new Vec3(0, 0, 0);
   	public float scale = 1f;

   	public MobElytraLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet, Vec3 offset, Vec3 rotation, float scale) {
      	super(parent, modelSet);
      	this.elytraModel = new ElytraModel<>(modelSet.bakeLayer(ModelLayers.ELYTRA));
      	if(offset != null)
      		this.offset = offset;
      	if(rotation != null)
      		this.rotation = rotation;

      	if(this.elytraModel instanceof ModelGetter getter) {
      		ModelPart rightWing = getter.getModel(0);
      		ModelPart leftWing = getter.getModel(1);
      		rightWing.xScale = scale;
      		rightWing.yScale = scale;
      		rightWing.zScale = scale;
      		rightWing.x *= scale;
      		leftWing.xScale = scale;
      		leftWing.yScale = scale;
      		leftWing.zScale = scale;
      		leftWing.x *= scale;
      	}
   	}

	//Parameters were renamed using AI, i know scale ins't scale, but i dont know what it really is & i dont care as long its not p_123134141.
   	public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
   		ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.CHEST);
      	if(shouldRender(itemstack, entity)) {
         	ResourceLocation resourcelocation;
         	if(entity instanceof AbstractClientPlayer) {
            	AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)entity;
            	if(abstractclientplayer.isElytraLoaded() && abstractclientplayer.getElytraTextureLocation() != null) {
               		resourcelocation = abstractclientplayer.getElytraTextureLocation();
            	} else if(abstractclientplayer.isCapeLoaded() && abstractclientplayer.getCloakTextureLocation() != null && abstractclientplayer.isModelPartShown(PlayerModelPart.CAPE)) {
               		resourcelocation = abstractclientplayer.getCloakTextureLocation();
            	} else {
              		resourcelocation = getElytraTexture(itemstack, entity);
            	}
         	} else {
            	resourcelocation = getElytraTexture(itemstack, entity);
         	}

         	poseStack.pushPose();
         	poseStack.translate(0.0F, 0.0F, 0.125F);
         	poseStack.translate(offset.x, offset.y, offset.z);
         	poseStack.mulPose(Axis.XP.rotationDegrees((float)rotation.x));
         	poseStack.mulPose(Axis.YP.rotationDegrees((float)rotation.y));
         	poseStack.mulPose(Axis.ZP.rotationDegrees((float)rotation.z));
         	this.getParentModel().copyPropertiesTo(this.elytraModel);
         	this.elytraModel.setupAnim(entity, limbSwing, limbSwingAmount, netHeadYaw, headPitch, scale);
         	VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(bufferSource, RenderType.armorCutoutNoCull(resourcelocation), false, itemstack.hasFoil());
         	this.elytraModel.renderToBuffer(poseStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
         	poseStack.popPose();
      	}
   	}
}
