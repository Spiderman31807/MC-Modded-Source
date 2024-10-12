package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

@OnlyIn(Dist.CLIENT)
public class CreeperRenderer extends MobRenderer<CreeperModel<AbstractClientPlayer>> {
   public CreeperRenderer(EntityRendererProvider.Context p_173958_) {
      super(p_173958_, new CreeperModel<>(p_173958_.bakeLayer(ModelLayers.CREEPER)), 0.5F);
      this.addLayer(new CreeperPowerLayer(this, p_173958_.getModelSet()));
   }

   protected void scale(AbstractClientPlayer player, PoseStack pose, float value) {
      float f = getSwellAmount(player, value);
      float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
      f = Mth.clamp(f, 0.0F, 1.0F);
      f *= f;
      f *= f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      pose.scale(f2, f3, f2);
   }

   protected float getWhiteOverlayProgress(AbstractClientPlayer player, float value) {
      float f = getSwellAmount(player, value);
      return (int)(f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
      return new ResourceLocation("textures/entity/creeper/creeper.png");
   }

   public float getSwellAmount(AbstractClientPlayer player, float value) {
   		if(MobData.get(player).typeData instanceof CreeperData data)
   			return data.getSwelling(value);
   		return 0f;
   }
}