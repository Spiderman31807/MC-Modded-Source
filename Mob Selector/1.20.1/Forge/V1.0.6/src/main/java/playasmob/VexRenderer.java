package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;

@OnlyIn(Dist.CLIENT)
public class VexRenderer extends MobRenderer<VexModel> {
   private static final ResourceLocation VEX_LOCATION = new ResourceLocation("textures/entity/illager/vex.png");
   private static final ResourceLocation VEX_CHARGING_LOCATION = new ResourceLocation("textures/entity/illager/vex_charging.png");

   public VexRenderer(EntityRendererProvider.Context context) {
      super(context, new VexModel(context.bakeLayer(ModelLayers.VEX)), 0.3f);
      this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
      this.addLayer(new MobHeadLayer<>(this, context.getModelSet(), 0.61f, 0.61f, 0.61f, context.getItemInHandRenderer()));
      this.addLayer(new MobElytraLayer<>(this, context.getModelSet(), new Vec3(0, 1, -0.1), null, 0.3f));
   }

   protected int getBlockLightLevel(AbstractClientPlayer p_116298_, BlockPos p_116299_) {
      return 15;
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
      return MobData.get(player).isAggressive() ? VEX_CHARGING_LOCATION : VEX_LOCATION;
   }
}