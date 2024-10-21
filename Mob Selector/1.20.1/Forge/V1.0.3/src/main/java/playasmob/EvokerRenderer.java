package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

@OnlyIn(Dist.CLIENT)
public class EvokerRenderer extends IllagerRenderer {
   private static final ResourceLocation EVOKER_ILLAGER = new ResourceLocation("textures/entity/illager/evoker.png");

   public EvokerRenderer(EntityRendererProvider.Context context) {
      super(context, new IllagerModel(context.bakeLayer(ModelLayers.EVOKER)), 0.5F);
      this.addLayer(new ItemInHandLayer<AbstractClientPlayer, IllagerModel>(this, context.getItemInHandRenderer()));
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer p_114541_) {
      return EVOKER_ILLAGER;
   }
}