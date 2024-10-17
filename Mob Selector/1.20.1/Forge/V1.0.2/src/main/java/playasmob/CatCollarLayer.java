package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

@OnlyIn(Dist.CLIENT)
public class CatCollarLayer extends RenderLayer<AbstractClientPlayer, CatModel> {
   private static final ResourceLocation CAT_COLLAR_LOCATION = new ResourceLocation("textures/entity/cat/cat_collar.png");
   private final CatModel catModel;

   public CatCollarLayer(RenderLayerParent<AbstractClientPlayer, CatModel> p_174468_, EntityModelSet p_174469_) {
      super(p_174468_);
      this.catModel = new CatModel(p_174469_.bakeLayer(ModelLayers.CAT_COLLAR));
   }

   public void render(PoseStack p_116666_, MultiBufferSource p_116667_, int p_116668_, AbstractClientPlayer player, float p_116670_, float p_116671_, float p_116672_, float p_116673_, float p_116674_, float p_116675_) {
      if(MobData.get(player).typeData instanceof CatData catData && catData.isTame()) {
         float[] afloat = catData.getCollarColor().getTextureDiffuseColors();
         coloredCutoutModelCopyLayerRender(this.getParentModel(), this.catModel, CAT_COLLAR_LOCATION, p_116666_, p_116667_, p_116668_, player, p_116670_, p_116671_, p_116673_, p_116674_, p_116675_, p_116672_, afloat[0], afloat[1], afloat[2]);
      }
   }
}
