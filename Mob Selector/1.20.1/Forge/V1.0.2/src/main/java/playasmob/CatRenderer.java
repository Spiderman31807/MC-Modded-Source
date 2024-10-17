package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

@OnlyIn(Dist.CLIENT)
public class CatRenderer extends MobRenderer<CatModel> {
   public CatRenderer(EntityRendererProvider.Context context) {
      super(context, new CatModel(context.bakeLayer(ModelLayers.CAT)), 0.4F);
      this.addLayer(new CatCollarLayer(this, context.getModelSet()));
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
   		if(MobData.get(player).typeData instanceof CatData catData)
      		return catData.getResourceLocation();
      	return new ResourceLocation("textures/entity/cat/all_black.png");
   }

   protected void scale(AbstractClientPlayer p_113952_, PoseStack p_113953_, float p_113954_) {
      super.scale(p_113952_, p_113953_, p_113954_);
      p_113953_.scale(0.8F, 0.8F, 0.8F);
   }

   protected void setupRotations(AbstractClientPlayer player, PoseStack p_113957_, float p_113958_, float p_113959_, float p_113960_) {
      super.setupRotations(player, p_113957_, p_113958_, p_113959_, p_113960_);
      float f = 0;
      if(MobData.get(player).typeData instanceof CatData catData)
      	 f = catData.getLieDownAmount(p_113960_);
      if (f > 0.0F) {
         p_113957_.translate(0.4F * f, 0.15F * f, 0.1F * f);
         p_113957_.mulPose(Axis.ZP.rotationDegrees(Mth.rotLerp(f, 0.0F, 90.0F)));
         BlockPos blockpos = player.blockPosition();

         for(Player playerCheck : player.level().getEntitiesOfClass(Player.class, (new AABB(blockpos)).inflate(2.0D, 2.0D, 2.0D))) {
            if (playerCheck.isSleeping()) {
               p_113957_.translate(0.15F * f, 0.0F, 0.0F);
               break;
            }
         }
      }

   }
}