package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import com.mojang.blaze3d.vertex.PoseStack;

@OnlyIn(Dist.CLIENT)
public class SpiderRenderer extends MobRenderer<SpiderModel<AbstractClientPlayer>> {
   	private static final ResourceLocation SPIDER_LOCATION = new ResourceLocation("textures/entity/spider/spider.png");
   	private static final ResourceLocation CAVE_SPIDER_LOCATION = new ResourceLocation("textures/entity/spider/cave_spider.png");

   	public SpiderRenderer(EntityRendererProvider.Context context) {
      	this(context, ModelLayers.SPIDER);
   	}

   	public SpiderRenderer(EntityRendererProvider.Context context, ModelLayerLocation model) {
      	super(context, new SpiderModel<>(context.bakeLayer(model)), 0.8F);
      	this.addLayer(new SpiderEyesLayer<>(this));
   	}

   	protected void scale(AbstractClientPlayer player, PoseStack pose, float p_113976_) {
   		float scale = MobData.get(player).renderAs == EntityType.CAVE_SPIDER ? 0.7f : 1f;
      	pose.scale(scale, scale, scale);
      	this.shadowRadius *= scale;
   	}

   	protected float getFlipDegrees(AbstractClientPlayer player) {
      	return 180.0F;
   	}

   	public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
   		if(MobData.get(player).renderAs == EntityType.CAVE_SPIDER)
   			return CAVE_SPIDER_LOCATION;
      	return SPIDER_LOCATION;
   	}
}