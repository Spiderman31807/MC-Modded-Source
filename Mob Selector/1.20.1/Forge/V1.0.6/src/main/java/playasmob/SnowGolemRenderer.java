package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;

@OnlyIn(Dist.CLIENT)
public class SnowGolemRenderer extends MobRenderer<SnowGolemModel<AbstractClientPlayer>> {
   private static final ResourceLocation SNOW_GOLEM_LOCATION = new ResourceLocation("textures/entity/snow_golem.png");

   public SnowGolemRenderer(EntityRendererProvider.Context context) {
      super(context, new SnowGolemModel<>(context.bakeLayer(ModelLayers.SNOW_GOLEM)), 0.5F);
      this.addLayer(new SnowGolemHeadLayer(this, context.getBlockRenderDispatcher(), context.getItemRenderer()));
      this.addLayer(new MobElytraLayer<>(this, context.getModelSet(), new Vec3(0, -0.2, 0), null, 0.8f));
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer p_115993_) {
      return SNOW_GOLEM_LOCATION;
   }
}