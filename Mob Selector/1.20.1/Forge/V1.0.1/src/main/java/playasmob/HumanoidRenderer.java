package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;

@OnlyIn(Dist.CLIENT)
public abstract class HumanoidRenderer<T extends LivingEntity, M extends HumanoidModel<T>> extends LivingEntityRenderer<T, M> {
   public HumanoidRenderer(EntityRendererProvider.Context p_174169_, M p_174170_, float p_174171_) {
      this(p_174169_, p_174170_, p_174171_, 1.0F, 1.0F, 1.0F);
      p_174170_.crouching = false;
   }

   public HumanoidRenderer(EntityRendererProvider.Context p_174173_, M p_174174_, float p_174175_, float p_174176_, float p_174177_, float p_174178_) {
      super(p_174173_, p_174174_, p_174175_);
      this.addLayer(new CustomHeadLayer<>(this, p_174173_.getModelSet(), p_174176_, p_174177_, p_174178_, p_174173_.getItemInHandRenderer()));
      this.addLayer(new ElytraLayer<>(this, p_174173_.getModelSet()));
      this.addLayer(new ItemInHandLayer<>(this, p_174173_.getItemInHandRenderer()));
   }
}
