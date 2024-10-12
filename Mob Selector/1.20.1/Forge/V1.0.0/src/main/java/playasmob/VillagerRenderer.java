package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

@OnlyIn(Dist.CLIENT)
public class VillagerRenderer extends MobRenderer<VillagerModel<AbstractClientPlayer>> {
   private static final ResourceLocation VILLAGER_BASE_SKIN = new ResourceLocation("textures/entity/villager/villager.png");

   public VillagerRenderer(EntityRendererProvider.Context context) {
      super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
      this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
      this.addLayer(new VillagerProfessionLayer<>(this, context.getResourceManager(), "villager"));
      this.addLayer(new CrossedArmsItemLayer<>(this, context.getItemInHandRenderer()));
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
      return VILLAGER_BASE_SKIN;
   }

   protected void scale(AbstractClientPlayer player, PoseStack pose, float p_116316_) {
      float f = 0.9375F;
      if (MobData.get(player).typeData instanceof VillagerData villagerData && villagerData.isBaby()) {
         f *= 0.5F;
         this.shadowRadius = 0.25F;
      } else {
         this.shadowRadius = 0.5F;
      }

      pose.scale(f, f, f);
   }
}