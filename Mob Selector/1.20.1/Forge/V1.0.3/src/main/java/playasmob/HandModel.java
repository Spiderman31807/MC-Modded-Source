package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.world.entity.HumanoidArm;

@OnlyIn(Dist.CLIENT)
public class HandModel extends HumanoidModel<AbstractClientPlayer> implements Hand {
	public boolean outerLayer = false;
	
   	protected HandModel(ModelPart model, boolean outer) {
   		super(model);
   		this.outerLayer = outer;
   	}

   	public boolean isOuterLayer() {
   		return this.outerLayer;
   	}

   	public void prepareMobModel(AbstractClientPlayer p_102861_, float p_102862_, float p_102863_, float p_102864_) {
   	}

   	public void setupAnim(AbstractClientPlayer player, float p_102002_, float p_102003_, float p_102004_, float p_102005_, float p_102006_) {
      	this.setAllVisible(false);
      	ModelPart arm = this.getArm(player.getMainArm());
      	arm.visible = true;
		MobData data = MobData.get(player);
		boolean leftHanded = player.getMainArm() == HumanoidArm.LEFT;

		if(data.debug) {
      		arm.setRotation(data.xRot, data.yRot, data.zRot);
      		arm.setPos(data.xPos, data.yPos, data.zPos);
      		arm.xScale = data.xScale;
      		arm.yScale = data.yScale;
      		arm.zScale = data.zScale;
		} else {
			HandData hand = data.typeData.getHand();
			arm.setRotation(hand.rotation.x, hand.rotation.y, hand.rotation.z);
			arm.setPos(hand.position.x, hand.position.y, hand.position.z);
      		arm.xScale = hand.scale.x;
      		arm.yScale = hand.scale.y;
      		arm.zScale = hand.scale.z;
		}

      	if(this.outerLayer) {
      		arm.xScale *= 1.1;
      		arm.yScale *= 1.1;
      		arm.zScale *= 1.1;
      	}

      	if(leftHanded) {
      		arm.x *= -1;
      		arm.yRot *= -1;
      		arm.zRot *= -1;
      	}
   	}
}
