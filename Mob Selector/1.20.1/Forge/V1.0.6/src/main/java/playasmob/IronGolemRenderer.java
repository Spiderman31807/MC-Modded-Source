package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;

@OnlyIn(Dist.CLIENT)
public class IronGolemRenderer extends MobRenderer<IronGolemModel> {
   private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

   public IronGolemRenderer(EntityRendererProvider.Context context) {
      super(context, new IronGolemModel(context.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7F);
      this.addLayer(new IronGolemCrackinessLayer(this));
      this.addLayer(new IronGolemItemLayer(this, context.getBlockRenderDispatcher()));
      this.addLayer(new MobHeadLayer<>(this, context.getModelSet(), 1.21f, 1.21f, 1.21f, context.getItemInHandRenderer()));
      this.addLayer(new MobElytraLayer<>(this, context.getModelSet(), new Vec3(0, -0.5, 0), null, 1.35f));
      this.addLayer(new MobItemLayer<>(this, context.getItemInHandRenderer(), new Vec3(0.625f, 0.05f, -1f)));
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer p_115012_) {
      return GOLEM_LOCATION;
   }

   protected void setupRotations(AbstractClientPlayer p_115014_, PoseStack p_115015_, float p_115016_, float p_115017_, float p_115018_) {
      super.setupRotations(p_115014_, p_115015_, p_115016_, p_115017_, p_115018_);
      if (!((double)p_115014_.walkAnimation.speed() < 0.01D)) {
         float f = 13.0F;
         float f1 = p_115014_.walkAnimation.position(p_115018_) + 6.0F;
         float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
         p_115015_.mulPose(Axis.ZP.rotationDegrees(6.5F * f2));
      }
   }
}