package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;

@OnlyIn(Dist.CLIENT)
public class HandRenderer<M extends EntityModel<AbstractClientPlayer>> extends MobRenderer<M> {
	public boolean outerRender = false;
	
   	protected HandRenderer(EntityRendererProvider.Context context, M model) {
      	super(context, model, 0.5F);
      	if(model instanceof Hand hand)
   			this.outerRender = hand.isOuterLayer();
   	}

   	protected void setupRotations(AbstractClientPlayer p_115317_, PoseStack p_115318_, float p_115319_, float p_115320_, float p_115321_) {
   	}

   	public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
   		HandData hand = MobData.get(player).typeData.getHand();
      	return this.outerRender ? hand.outerTexture : hand.texture;
   	}
   	
   	@Nullable
  	protected RenderType getRenderType(AbstractClientPlayer player, boolean p_115323_, boolean p_115324_, boolean p_115325_) {
      	ResourceLocation resourcelocation = this.getTextureLocation(player);
      	if (p_115324_) {
         	return RenderType.itemEntityTranslucentCull(resourcelocation);
      	} else if (p_115323_) {
         	return RenderType.entityCutout(resourcelocation);
      	} else {
         	return p_115325_ ? RenderType.outline(resourcelocation) : null;
      	}
   	}
}
