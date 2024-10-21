package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

@OnlyIn(Dist.CLIENT)
public class WitchRenderer extends MobRenderer<WitchModel<AbstractClientPlayer>> {
   	private static final ResourceLocation WITCH_LOCATION = new ResourceLocation("textures/entity/witch.png");

   	public WitchRenderer(EntityRendererProvider.Context context) {
      	super(context, new WitchModel<>(context.bakeLayer(ModelLayers.WITCH)), 0.5F);
      	this.addLayer(new WitchItemLayer(this, context.getItemInHandRenderer()));
   	}

   	public void render(AbstractClientPlayer player, float p_116413_, float p_116414_, PoseStack p_116415_, MultiBufferSource p_116416_, int p_116417_) {
	  	if(MobData.get(player).typeData instanceof RaiderData data)
	  		this.model.setHoldingItem(data.isDrinkingPotion());
      	super.render(player, p_116413_, p_116414_, p_116415_, p_116416_, p_116417_);
   	}

   	public ResourceLocation getTextureLocation(AbstractClientPlayer p_116410_) {
      	return WITCH_LOCATION;
   	}

   	protected void scale(AbstractClientPlayer p_116419_, PoseStack p_116420_, float p_116421_) {
      	float f = 0.9375F;
      	p_116420_.scale(0.9375F, 0.9375F, 0.9375F);
   	}
}