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
public class VindicatorRenderer extends IllagerRenderer {
   private static final ResourceLocation VINDICATOR = new ResourceLocation("textures/entity/illager/vindicator.png");

   public VindicatorRenderer(EntityRendererProvider.Context context) {
      super(context, new IllagerModel(context.bakeLayer(ModelLayers.VINDICATOR)), 0.5F);
      this.addLayer(new ItemInHandLayer<AbstractClientPlayer, IllagerModel>(this, context.getItemInHandRenderer()));
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer p_116324_) {
      return VINDICATOR;
   }
}