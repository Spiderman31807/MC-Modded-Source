package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.player.AbstractClientPlayer;

@OnlyIn(Dist.CLIENT)
public class CreeperPowerLayer extends EnergySwirlLayer<AbstractClientPlayer, CreeperModel<AbstractClientPlayer>> {
   	private static final ResourceLocation POWER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
   	private final CreeperModel<AbstractClientPlayer> model;

   	public CreeperPowerLayer(RenderLayerParent<AbstractClientPlayer, CreeperModel<AbstractClientPlayer>> p_174471_, EntityModelSet p_174472_) {
      	super(p_174471_);
      	this.model = new CreeperModel<>(p_174472_.bakeLayer(ModelLayers.CREEPER_ARMOR));
   	}

   	protected float xOffset(float p_116683_) {
      	return p_116683_ * 0.01F;
   	}

   	protected ResourceLocation getTextureLocation() {
      	return POWER_LOCATION;
   	}

   	protected EntityModel<AbstractClientPlayer> model() {
      	return this.model;
   	}

   	protected boolean isPowered(AbstractClientPlayer player) {
   		MobData data = MobData.get(player);
   		if(data.typeData instanceof CreeperData creeperData)
   			return creeperData.isPowered();
   		return false;
   	}
}