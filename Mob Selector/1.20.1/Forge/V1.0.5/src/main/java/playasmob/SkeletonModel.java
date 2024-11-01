package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.AnimationUtils;

@OnlyIn(Dist.CLIENT)
public class SkeletonModel extends HumanoidModel<AbstractClientPlayer> {
   public SkeletonModel(ModelPart p_170941_) {
      super(p_170941_);
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

   public void prepareMobModel(AbstractClientPlayer player, float p_103794_, float p_103795_, float p_103796_) {
      this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
      this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
      ItemStack itemstack = player.getItemInHand(InteractionHand.MAIN_HAND);
      if (itemstack.is(Items.BOW) && player.isUsingItem()) {
         if (player.getMainArm() == HumanoidArm.RIGHT) {
            this.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
         } else {
            this.leftArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
         }
      }

      super.prepareMobModel(player, p_103794_, p_103795_, p_103796_);
   }

   public void setupAnim(AbstractClientPlayer player, float p_103799_, float p_103800_, float p_103801_, float p_103802_, float p_103803_) {
      super.setupAnim(player, p_103799_, p_103800_, p_103801_, p_103802_, p_103803_);
      ItemStack itemstack = player.getMainHandItem();
      if(MobData.get(player).isAggressive() && (itemstack.isEmpty() || !itemstack.is(Items.BOW))) {
         float f = Mth.sin(this.attackTime * (float)Math.PI);
         float f1 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * (float)Math.PI);
         this.rightArm.zRot = 0.0F;
         this.leftArm.zRot = 0.0F;
         this.rightArm.yRot = -(0.1F - f * 0.6F);
         this.leftArm.yRot = 0.1F - f * 0.6F;
         this.rightArm.xRot = (-(float)Math.PI / 2F);
         this.leftArm.xRot = (-(float)Math.PI / 2F);
         this.rightArm.xRot -= f * 1.2F - f1 * 0.4F;
         this.leftArm.xRot -= f * 1.2F - f1 * 0.4F;
         AnimationUtils.bobArms(this.rightArm, this.leftArm, p_103801_);
      }
   }

   public void translateToHand(HumanoidArm p_103778_, PoseStack p_103779_) {
      float f = p_103778_ == HumanoidArm.RIGHT ? 1.0F : -1.0F;
      ModelPart modelpart = this.getArm(p_103778_);
      modelpart.x += f;
      modelpart.translateAndRotate(p_103779_);
      modelpart.x -= f;
   }
}