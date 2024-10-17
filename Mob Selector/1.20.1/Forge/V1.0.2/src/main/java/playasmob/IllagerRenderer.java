package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;

@OnlyIn(Dist.CLIENT)
public abstract class IllagerRenderer extends MobRenderer<IllagerModel> {
   protected IllagerRenderer(EntityRendererProvider.Context context, IllagerModel model, float value) {
      super(context, model, value);
      this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
   }

   protected void scale(AbstractClientPlayer player, PoseStack pose, float value) {
      float f = 0.9375F;
      pose.scale(0.9375F, 0.9375F, 0.9375F);
   }
}