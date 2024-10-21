package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.player.AbstractClientPlayer;

@OnlyIn(Dist.CLIENT)
public class ChickenRenderer extends MobRenderer<ChickenModel<AbstractClientPlayer>> {
   	private static final ResourceLocation CHICKEN_LOCATION = new ResourceLocation("textures/entity/chicken.png");

   	public ChickenRenderer(EntityRendererProvider.Context context) {
      	super(context, new ChickenModel<>(context.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
   	}

   	public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
      	return CHICKEN_LOCATION;
   	}

   	protected float getBob(AbstractClientPlayer player, float p_114001_) {
		float flap = 0;
		float flapSpeed = 0;
		float oFlap = 0;
		float oFlapSpeed = 0;
		if(MobData.get(player).typeData instanceof AnimalData animalData) {
			flap = animalData.flap;
			flapSpeed = animalData.flapSpeed;
			oFlap = animalData.oFlap;
			oFlapSpeed = animalData.oFlapSpeed;
		}
		
      	float f = Mth.lerp(p_114001_, oFlap, flap);
      	float f1 = Mth.lerp(p_114001_, oFlapSpeed, flapSpeed);
      	return (Mth.sin(f) + 1.0F) * f1;
   	}
}