package playasmob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.model.HumanoidModel;

public class MobItemLayer<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> extends ItemInHandLayer<T, M> {
   	private final ItemInHandRenderer itemRenderer;
   	public Vec3 offset = new Vec3(0.0625f, 0.125, -0.625);

   	public MobItemLayer(RenderLayerParent<T, M> parent, ItemInHandRenderer itemRenderer, Vec3 offset) {
      	super(parent, itemRenderer);
      	this.itemRenderer = itemRenderer;
      	if(offset != null)
      		this.offset = this.offset.add(offset);
   	}

	protected void renderArmWithItem(LivingEntity entity, ItemStack itemStack, ItemDisplayContext displayContext, HumanoidArm arm, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        if(!itemStack.isEmpty()) {
         	poseStack.pushPose();
         	boolean leftHand = arm == HumanoidArm.LEFT;
         	this.getParentModel().translateToHand(arm, poseStack);
         	if(this.getParentModel() instanceof HumanoidModel model) {
         		ModelPart armModel = leftHand ? model.leftArm : model.rightArm;
        		float length = armModel.y / 16.0F; 
        		float angle = (float) Math.toRadians(armModel.xRot);
        		poseStack.translate(length * Math.sin(-angle), length * Math.cos(-angle), 0f);
         	}
         	
         	poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
         	poseStack.mulPose(Axis.YP.rotationDegrees(180f));
         	poseStack.translate(leftHand ? -offset.x : offset.x, offset.y, offset.z);
         	this.itemRenderer.renderItem(entity, itemStack, displayContext, leftHand, poseStack, bufferSource, light);
         	poseStack.popPose();
      	}
	}
}
