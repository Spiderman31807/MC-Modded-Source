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
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;

@OnlyIn(Dist.CLIENT)
public class IllusionerRenderer extends IllagerRenderer {
   private static final ResourceLocation ILLUSIONER = new ResourceLocation("textures/entity/illager/illusioner.png");

   public IllusionerRenderer(EntityRendererProvider.Context context) {
      super(context, new IllagerModel(context.bakeLayer(ModelLayers.ILLUSIONER)), 0.5F);
      this.addLayer(new ItemInHandLayer<AbstractClientPlayer, IllagerModel>(this, context.getItemInHandRenderer()));
      this.model.getHat().visible = true;
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer p_114950_) {
      return ILLUSIONER;
   }

   public void render(AbstractClientPlayer player, float p_114953_, float p_114954_, PoseStack p_114955_, MultiBufferSource p_114956_, int p_114957_) {
      if(player.isInvisible() && MobData.get(player).typeData instanceof RaiderData data) {
         Vec3[] avec3 = data.getIllusionOffsets(p_114954_);
         float f = this.getBob(player, p_114954_);

         for(int i = 0; i < avec3.length; ++i) {
            p_114955_.pushPose();
            p_114955_.translate(avec3[i].x + (double)Mth.cos((float)i + f * 0.5F) * 0.025D, avec3[i].y + (double)Mth.cos((float)i + f * 0.75F) * 0.0125D, avec3[i].z + (double)Mth.cos((float)i + f * 0.7F) * 0.025D);
            super.render(player, p_114953_, p_114954_, p_114955_, p_114956_, p_114957_);
            p_114955_.popPose();
         }
      } else {
         super.render(player, p_114953_, p_114954_, p_114955_, p_114956_, p_114957_);
      }

   }

   protected boolean isBodyVisible(AbstractClientPlayer p_114959_) {
      return true;
   }
}