package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.animal.Sheep;

@OnlyIn(Dist.CLIENT)
public class SheepFurLayer extends RenderLayer<AbstractClientPlayer, SheepModel> {
   	private static final ResourceLocation SHEEP_FUR_LOCATION = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
   	private final SheepFurModel model;

   	public SheepFurLayer(RenderLayerParent<AbstractClientPlayer, SheepModel> p_174533_, EntityModelSet p_174534_) {
      	super(p_174533_);
      	this.model = new SheepFurModel(p_174534_.bakeLayer(ModelLayers.SHEEP_FUR));
   	}

   	public void render(PoseStack p_117421_, MultiBufferSource p_117422_, int p_117423_, AbstractClientPlayer player, float p_117425_, float p_117426_, float p_117427_, float p_117428_, float p_117429_, float p_117430_) {
		boolean sheared = false;
		DyeColor color = DyeColor.WHITE;
		if(MobData.get(player).typeData instanceof AnimalData animalData) {
			sheared = animalData.isSheared();
			color = animalData.getColor();
		}

      	if (!sheared) {
         	if (player.isInvisible()) {
            	Minecraft minecraft = Minecraft.getInstance();
            	boolean flag = minecraft.shouldEntityAppearGlowing(player);
            	if (flag) {
               		this.getParentModel().copyPropertiesTo(this.model);
               		this.model.prepareMobModel(player, p_117425_, p_117426_, p_117427_);
               		this.model.setupAnim(player, p_117425_, p_117426_, p_117428_, p_117429_, p_117430_);
               		VertexConsumer vertexconsumer = p_117422_.getBuffer(RenderType.outline(SHEEP_FUR_LOCATION));
               		this.model.renderToBuffer(p_117421_, vertexconsumer, p_117423_, LivingEntityRenderer.getOverlayCoords(player, 0.0F), 0.0F, 0.0F, 0.0F, 1.0F);
            	}
         	} else {
            	float f;
            	float f1;
            	float f2;
            	if (player.hasCustomName() && "jeb_".equals(player.getName().getString())) {
               		int i1 = 25;
               		int i = player.tickCount / 25 + player.getId();
               		int j = DyeColor.values().length;
               		int k = i % j;
               		int l = (i + 1) % j;
               		float f3 = ((float)(player.tickCount % 25) + p_117427_) / 25.0F;
               		float[] afloat1 = Sheep.getColorArray(DyeColor.byId(k));
               		float[] afloat2 = Sheep.getColorArray(DyeColor.byId(l));
               		f = afloat1[0] * (1.0F - f3) + afloat2[0] * f3;
               		f1 = afloat1[1] * (1.0F - f3) + afloat2[1] * f3;
               		f2 = afloat1[2] * (1.0F - f3) + afloat2[2] * f3;
            	} else {
               		float[] afloat = Sheep.getColorArray(color);
               		f = afloat[0];
               		f1 = afloat[1];
               		f2 = afloat[2];
            	}

            	coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, SHEEP_FUR_LOCATION, p_117421_, p_117422_, p_117423_, player, p_117425_, p_117426_, p_117428_, p_117429_, p_117430_, p_117427_, f, f1, f2);
         	}
      	}
   	}
}