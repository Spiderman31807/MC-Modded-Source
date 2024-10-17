package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

@OnlyIn(Dist.CLIENT)
public class SheepRenderer extends MobRenderer<SheepModel> {
   private static final ResourceLocation SHEEP_LOCATION = new ResourceLocation("textures/entity/sheep/sheep.png");

   public SheepRenderer(EntityRendererProvider.Context context) {
      super(context, new SheepModel(context.bakeLayer(ModelLayers.SHEEP)), 0.7F);
      this.addLayer(new SheepFurLayer(this, context.getModelSet()));
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
      return SHEEP_LOCATION;
   }
}