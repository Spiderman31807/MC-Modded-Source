package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.WitchModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@OnlyIn(Dist.CLIENT)
public class WitchItemLayer extends net.minecraft.client.renderer.entity.layers.WitchItemLayer<AbstractClientPlayer> {
   	public final ItemInHandRenderer itemInHandRenderer;

   	public WitchItemLayer(RenderLayerParent<AbstractClientPlayer, WitchModel<AbstractClientPlayer>> renderLayer, ItemInHandRenderer itemRenderer) {
      	super(renderLayer, itemRenderer);
      	this.itemInHandRenderer = itemRenderer;
   	}

   	public void render(PoseStack pose, MultiBufferSource buffer, int value1, AbstractClientPlayer player, float value2, float value3, float value4, float value5, float value6, float value7) {
		if(MobData.get(player).typeData instanceof RaiderData data) {
      		ItemStack itemstack = player.getMainHandItem();
			if(data.isDrinkingPotion())
				itemstack = data.getPotion();
      		
      		pose.pushPose();
      		if (itemstack.is(Items.POTION)) {
         		this.getParentModel().getHead().translateAndRotate(pose);
         		this.getParentModel().getNose().translateAndRotate(pose);
         		pose.translate(0.0625F, 0.25F, 0.0F);
         		pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
         		pose.mulPose(Axis.XP.rotationDegrees(140.0F));
         		pose.mulPose(Axis.ZP.rotationDegrees(10.0F));
         		pose.translate(0.0F, -0.4F, 0.4F);
      		}

      		pose.translate(0.0F, 0.4F, -0.4F);
      		pose.mulPose(Axis.XP.rotationDegrees(180.0F));
      		this.itemInHandRenderer.renderItem(player, itemstack, ItemDisplayContext.GROUND, false, pose, buffer, value1);
      		pose.popPose();
		}
   	}
}