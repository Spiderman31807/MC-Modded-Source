package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;

@OnlyIn(Dist.CLIENT)
public class PillagerRenderer extends IllagerRenderer {
   private static final ResourceLocation PILLAGER = new ResourceLocation("textures/entity/illager/pillager.png");

   public PillagerRenderer(EntityRendererProvider.Context context) {
      super(context, new IllagerModel(context.bakeLayer(ModelLayers.PILLAGER)), 0.5F);
      this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
      return PILLAGER;
   }
}