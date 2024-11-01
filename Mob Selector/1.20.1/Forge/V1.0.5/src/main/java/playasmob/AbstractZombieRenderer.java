package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractZombieRenderer<M extends ZombieModel> extends HumanoidRenderer<AbstractClientPlayer, M> {
   private static final ResourceLocation ZOMBIE_LOCATION = new ResourceLocation("textures/entity/zombie/zombie.png");

   protected AbstractZombieRenderer(EntityRendererProvider.Context p_173910_, M p_173911_, M p_173912_, M p_173913_) {
      super(p_173910_, p_173911_, 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, p_173912_, p_173913_, p_173910_.getModelManager()));
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
      return ZOMBIE_LOCATION;
   }

   protected boolean isShaking(AbstractClientPlayer player) {
      return super.isShaking(player) || MobData.get(player).isShaking();
   }
}
