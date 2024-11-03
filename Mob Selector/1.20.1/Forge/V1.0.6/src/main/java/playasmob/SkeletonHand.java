package playasmob;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.client.model.HumanoidModel;

public class SkeletonHand extends HandModel {
	protected SkeletonHand(ModelPart model, boolean outer) {
   		super(model, outer);
   	}

   	public static LayerDefinition createBodyLayer() {
      	MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      	PartDefinition partdefinition = meshdefinition.getRoot();
      	partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-5.0F, 2.0F, 0.0F));
      	partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
      	partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-2.0F, 12.0F, 0.0F));
      	partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(2.0F, 12.0F, 0.0F));
      	return LayerDefinition.create(meshdefinition, 64, 32);
   	}

  	public void translateToHand(HumanoidArm p_103778_, PoseStack p_103779_) {
      	float f = p_103778_ == HumanoidArm.RIGHT ? 1.0F : -1.0F;
      	ModelPart modelpart = this.getArm(p_103778_);
      	modelpart.x += f;
      	modelpart.translateAndRotate(p_103779_);
      	modelpart.x -= f;
   	}
}
