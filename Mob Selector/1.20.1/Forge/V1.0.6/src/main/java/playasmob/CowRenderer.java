package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;

@OnlyIn(Dist.CLIENT)
public class CowRenderer extends MobRenderer<CowModel<AbstractClientPlayer>> {
   private static final ResourceLocation COW_LOCATION = new ResourceLocation("textures/entity/cow/cow.png");

   public CowRenderer(EntityRendererProvider.Context context) {
      super(context, new CowModel<>(context.bakeLayer(ModelLayers.COW)), 0.7F);
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
      return COW_LOCATION;
   }
}