package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;

@OnlyIn(Dist.CLIENT)
public class StrayRenderer extends SkeletonRenderer {
   public StrayRenderer(EntityRendererProvider.Context context) {
      super(context, ModelLayers.STRAY, ModelLayers.STRAY_INNER_ARMOR, ModelLayers.STRAY_OUTER_ARMOR);
      this.addLayer(new StrayClothingLayer(this, context.getModelSet()));
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
      return new ResourceLocation("textures/entity/skeleton/stray.png");
   }
}