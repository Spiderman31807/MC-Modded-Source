package playasmob;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

@OnlyIn(Dist.CLIENT)
public class ZombieVillagerRenderer extends HumanoidRenderer<AbstractClientPlayer, ZombieVillagerModel> {
   private static final ResourceLocation ZOMBIE_VILLAGER_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");

   public ZombieVillagerRenderer(EntityRendererProvider.Context context) {
      super(context, new ZombieVillagerModel(context.bakeLayer(ModelLayers.ZOMBIE_VILLAGER)), 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, new ZombieVillagerModel(context.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)), new ZombieVillagerModel(context.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR)), context.getModelManager()));
      this.addLayer(new VillagerProfessionLayer<>(this, context.getResourceManager(), "zombie_villager"));
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
      return ZOMBIE_VILLAGER_LOCATION;
   }

   protected boolean isShaking(AbstractClientPlayer player) {
      return super.isShaking(player) || MobData.get(player).isShaking();
   }
}