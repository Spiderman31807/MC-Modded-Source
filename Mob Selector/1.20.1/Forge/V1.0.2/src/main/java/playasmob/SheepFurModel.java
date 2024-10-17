package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.model.QuadrupedModel;

@OnlyIn(Dist.CLIENT)
public class SheepFurModel extends QuadrupedModel<AbstractClientPlayer> {
   	private float headXRot;

   	public SheepFurModel(ModelPart p_170900_) {
      	super(p_170900_, false, 8.0F, 4.0F, 2.0F, 2.0F, 24);
   	}

  	public static LayerDefinition createFurLayer() {
      	MeshDefinition meshdefinition = new MeshDefinition();
      	PartDefinition partdefinition = meshdefinition.getRoot();
      	partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.6F)), PartPose.offset(0.0F, 6.0F, -8.0F));
      	partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 8).addBox(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F, new CubeDeformation(1.75F)), PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));
      	CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.5F));
      	partdefinition.addOrReplaceChild("right_hind_leg", cubelistbuilder, PartPose.offset(-3.0F, 12.0F, 7.0F));
      	partdefinition.addOrReplaceChild("left_hind_leg", cubelistbuilder, PartPose.offset(3.0F, 12.0F, 7.0F));
      	partdefinition.addOrReplaceChild("right_front_leg", cubelistbuilder, PartPose.offset(-3.0F, 12.0F, -5.0F));
      	partdefinition.addOrReplaceChild("left_front_leg", cubelistbuilder, PartPose.offset(3.0F, 12.0F, -5.0F));
      	return LayerDefinition.create(meshdefinition, 64, 32);
   	}

   	public void prepareMobModel(AbstractClientPlayer player, float p_103662_, float p_103663_, float p_103664_) {
      	super.prepareMobModel(player, p_103662_, p_103663_, p_103664_);
      	float PositionScale = 0;
      	float AngleScale = player.getXRot() * ((float)Math.PI / 180F);
		if(MobData.get(player).typeData instanceof AnimalData animalData) {
			PositionScale = animalData.getHeadEatPositionScale(p_103664_);
			AngleScale = animalData.getHeadEatAngleScale(p_103664_);
		}
		
      	this.head.y = 6.0F + PositionScale * 9.0F;
      	this.headXRot = AngleScale;
   	}

   	public void setupAnim(AbstractClientPlayer player, float p_103667_, float p_103668_, float p_103669_, float p_103670_, float p_103671_) {
      	super.setupAnim(player, p_103667_, p_103668_, p_103669_, p_103670_, p_103671_);
      	this.head.xRot = this.headXRot;
   	}
}
