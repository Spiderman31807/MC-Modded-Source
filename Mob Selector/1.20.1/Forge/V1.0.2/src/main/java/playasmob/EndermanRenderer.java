package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.EnderEyesLayer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

@OnlyIn(Dist.CLIENT)
public class EndermanRenderer extends MobRenderer<EndermanModel<AbstractClientPlayer>> {
   private static final ResourceLocation ENDERMAN_LOCATION = new ResourceLocation("textures/entity/enderman/enderman.png");
   private final RandomSource random = RandomSource.create();

   public EndermanRenderer(EntityRendererProvider.Context context) {
      super(context, new EndermanModel<>(context.bakeLayer(ModelLayers.ENDERMAN)), 0.5F);
      this.addLayer(new EnderEyesLayer<>(this));
      this.addLayer(new CarriedBlockLayer(this, context.getBlockRenderDispatcher()));
   }

   public void render(AbstractClientPlayer player, float p_114340_, float p_114341_, PoseStack p_114342_, MultiBufferSource p_114343_, int p_114344_) {
      BlockState blockstate = null;
      boolean creepy = false;
      if(MobData.get(player).typeData instanceof EndermanData data) {
          blockstate = data.getCarriedBlock();
          creepy = data.isCreepy();
      }
      
      EndermanModel<AbstractClientPlayer> endermanmodel = this.getModel();
      endermanmodel.carrying = blockstate != null;
      endermanmodel.creepy = creepy;
      super.render(player, p_114340_, p_114341_, p_114342_, p_114343_, p_114344_);
   }

   public Vec3 getRenderOffset(AbstractClientPlayer player, float p_114337_) {
      if (MobData.get(player).typeData instanceof EndermanData data && data.isCreepy()) {
         double d0 = 0.02D;
         return new Vec3(this.random.nextGaussian() * 0.02D, 0.0D, this.random.nextGaussian() * 0.02D);
      } else {
         return super.getRenderOffset(player, p_114337_);
      }
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer p_114334_) {
      return ENDERMAN_LOCATION;
   }
}