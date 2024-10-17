package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;

@OnlyIn(Dist.CLIENT)
public class WardenRenderer extends MobRenderer<WardenModel> {
   	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/warden/warden.png");
   	private static final ResourceLocation BIOLUMINESCENT_LAYER_TEXTURE = new ResourceLocation("textures/entity/warden/warden_bioluminescent_layer.png");
   	private static final ResourceLocation HEART_TEXTURE = new ResourceLocation("textures/entity/warden/warden_heart.png");
   	private static final ResourceLocation PULSATING_SPOTS_TEXTURE_1 = new ResourceLocation("textures/entity/warden/warden_pulsating_spots_1.png");
  	private static final ResourceLocation PULSATING_SPOTS_TEXTURE_2 = new ResourceLocation("textures/entity/warden/warden_pulsating_spots_2.png");

   	public WardenRenderer(EntityRendererProvider.Context context) {
      	super(context, new WardenModel(context.bakeLayer(ModelLayers.WARDEN)), 0.9F);
	
      	this.addLayer(new WardenEmissiveLayer(this, BIOLUMINESCENT_LAYER_TEXTURE, (p_234809_, p_234810_, p_234811_) -> {
         	return 1.0F;
      	}, WardenModel::getBioluminescentLayerModelParts));
      	this.addLayer(new WardenEmissiveLayer(this, PULSATING_SPOTS_TEXTURE_1, (p_234805_, p_234806_, p_234807_) -> {
         	return Math.max(0.0F, Mth.cos(p_234807_ * 0.045F) * 0.25F);
      	}, WardenModel::getPulsatingSpotsLayerModelParts));
      	this.addLayer(new WardenEmissiveLayer(this, PULSATING_SPOTS_TEXTURE_2, (p_234801_, p_234802_, p_234803_) -> {
         	return Math.max(0.0F, Mth.cos(p_234803_ * 0.045F + (float)Math.PI) * 0.25F);
      	}, WardenModel::getPulsatingSpotsLayerModelParts));
      	this.addLayer(new WardenEmissiveLayer(this, TEXTURE, (p_234797_, p_234798_, p_234799_) -> {
      		if(MobData.get(p_234797_).typeData instanceof WardenData warden)
         		return warden.getTendrilAnimation(p_234798_);
         	return 0;
      	}, WardenModel::getTendrilsLayerModelParts));
      	this.addLayer(new WardenEmissiveLayer(this, HEART_TEXTURE, (p_234793_, p_234794_, p_234795_) -> {
      		if(MobData.get(p_234793_).typeData instanceof WardenData warden)
         		return warden.getHeartAnimation(p_234794_);
         	return 0;
      	}, WardenModel::getHeartLayerModelParts));
   	}

   	public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
      	return TEXTURE;
   	}
}