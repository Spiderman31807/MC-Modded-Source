package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;

@OnlyIn(Dist.CLIENT)
public class RavagerRenderer extends MobRenderer<RavagerModel> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/illager/ravager.png");

   public RavagerRenderer(EntityRendererProvider.Context context) {
      super(context, new RavagerModel(context.bakeLayer(ModelLayers.RAVAGER)), 1.1F);
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer p_115811_) {
      return TEXTURE_LOCATION;
   }
}