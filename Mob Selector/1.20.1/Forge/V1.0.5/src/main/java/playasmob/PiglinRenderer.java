package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.client.model.geom.ModelLayers;

@OnlyIn(Dist.CLIENT)
public class PiglinRenderer extends HumanoidRenderer<AbstractClientPlayer, PiglinModel> {
   	private static final Map<EntityType<?>, ResourceLocation> TEXTURES = ImmutableMap.of(EntityType.PIGLIN, new ResourceLocation("textures/entity/piglin/piglin.png"), EntityType.ZOMBIFIED_PIGLIN, new ResourceLocation("textures/entity/piglin/zombified_piglin.png"), EntityType.PIGLIN_BRUTE, new ResourceLocation("textures/entity/piglin/piglin_brute.png"));
   	private static final float PIGLIN_CUSTOM_HEAD_SCALE = 1.0019531F;

   	public PiglinRenderer(EntityRendererProvider.Context context, EntityType type) {
      	this(context, getModel(type, 0), getModel(type, 1), getModel(type, 2), type == EntityType.ZOMBIFIED_PIGLIN);
   	}

   	public static ModelLayerLocation getModel(EntityType type, int kind) {
   		if(type == EntityType.ZOMBIFIED_PIGLIN) {
   			return switch(kind) {
   				default -> ModelLayers.ZOMBIFIED_PIGLIN;
   				case 1 -> ModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR;
   				case 2 -> ModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR;
   			};
   		} else if(type == EntityType.PIGLIN_BRUTE) {
   			return switch(kind) {
   				default -> ModelLayers.PIGLIN_BRUTE;
   				case 1 -> ModelLayers.PIGLIN_BRUTE_INNER_ARMOR;
   				case 2 -> ModelLayers.PIGLIN_BRUTE_OUTER_ARMOR;
   			};
   		}
   		
   		return switch(kind) {
   			default -> ModelLayers.PIGLIN;
   			case 1 -> ModelLayers.PIGLIN_INNER_ARMOR;
   			case 2 -> ModelLayers.PIGLIN_OUTER_ARMOR;
   		};
   	}
   
   	public PiglinRenderer(EntityRendererProvider.Context context, ModelLayerLocation p_174345_, ModelLayerLocation p_174346_, ModelLayerLocation p_174347_, boolean p_174348_) {
      	super(context, createModel(context.getModelSet(), p_174345_, p_174348_), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
      	this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidArmorModel(context.bakeLayer(p_174346_)), new HumanoidArmorModel(context.bakeLayer(p_174347_)), context.getModelManager()));
   	}

   	private static PiglinModel createModel(EntityModelSet p_174350_, ModelLayerLocation p_174351_, boolean p_174352_) {
      	PiglinModel piglinmodel = new PiglinModel(p_174350_.bakeLayer(p_174351_));
      	if (p_174352_)
         	piglinmodel.rightEar.visible = false;

      	return piglinmodel;
   	}

   	public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
      	ResourceLocation resourcelocation = TEXTURES.get(MobData.get(player).renderAs);
      	if (resourcelocation == null) {
         	throw new IllegalArgumentException("I don't know what texture to use for " + MobData.get(player).renderAs);
      	} else {
         	return resourcelocation;
      	}
   	}

   	protected boolean isShaking(AbstractClientPlayer player) {
      	return super.isShaking(player) || MobData.get(player).isShaking();
   	}
}