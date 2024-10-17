package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;

@OnlyIn(Dist.CLIENT)
public class PigRenderer extends MobRenderer<PigModel<AbstractClientPlayer>> {
   private static final ResourceLocation PIG_LOCATION = new ResourceLocation("textures/entity/pig/pig.png");

   public PigRenderer(EntityRendererProvider.Context context) {
      super(context, new PigModel<>(context.bakeLayer(ModelLayers.PIG)), 0.7F);
      //this.addLayer(new SaddleLayer<>(this, new PigModel<>(p_174340_.bakeLayer(ModelLayers.PIG_SADDLE)), new ResourceLocation("textures/entity/pig/pig_saddle.png")));
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
      return PIG_LOCATION;
   }
}