package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;

@OnlyIn(Dist.CLIENT)
public class ZombieRenderer extends AbstractZombieRenderer<ZombieModel> {
   public ZombieRenderer(EntityRendererProvider.Context context) {
      this(context, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE_INNER_ARMOR, ModelLayers.ZOMBIE_OUTER_ARMOR);
   }

   public ZombieRenderer(EntityRendererProvider.Context context, ModelLayerLocation p_174459_, ModelLayerLocation p_174460_, ModelLayerLocation p_174461_) {
      super(context, new ZombieModel(context.bakeLayer(p_174459_)), new ZombieModel(context.bakeLayer(p_174460_)), new ZombieModel(context.bakeLayer(p_174461_)));
   }
}