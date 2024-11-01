package playasmob;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntityType;
import net.minecraft.client.model.geom.ModelPart;

@OnlyIn(Dist.CLIENT)
public class MobHeadLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends CustomHeadLayer<T, M> {
   	public final Map<SkullBlock.Type, SkullModelBase> skullModels;
   	public final ItemInHandRenderer itemRenderer;
   	public final float scaleX;
   	public final float scaleY;
   	public final float scaleZ;

   	public MobHeadLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet, ItemInHandRenderer itemRenderer) {
      	this(parent, modelSet, 1f, 1f, 1f, itemRenderer);
   	}

   	public MobHeadLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet, float xScale, float yScale, float zScale, ItemInHandRenderer itemRenderer) {
      	super(parent, modelSet, xScale, yScale, zScale, itemRenderer);
      	this.skullModels = SkullBlockRenderer.createSkullRenderers(modelSet);
      	this.itemRenderer = itemRenderer;
      	this.scaleX = xScale;
      	this.scaleY = yScale;
      	this.scaleZ = zScale;
   	}

   	public boolean isVillager(LivingEntity entity, boolean includeZombie) {
   		if(entity instanceof Player player) {
   			MobData data = MobData.get(player);
   			if(includeZombie)
   				return data != null && (data.mob == EntityType.VILLAGER || data.mob == EntityType.ZOMBIE_VILLAGER);
   			return data != null && data.mob == EntityType.VILLAGER;
   		}

   		return entity instanceof Villager || (entity instanceof ZombieVillager && includeZombie);
   	}

	//Parameters were renamed using AI, i know scale ins't scale, but i dont know what it really is & i dont care as long its not p_123134141.
	public void render(PoseStack pose, MultiBufferSource buffer, int light, T mob, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      	ItemStack itemstack = mob.getItemBySlot(EquipmentSlot.HEAD);
      	if(!itemstack.isEmpty()) {
         	Item item = itemstack.getItem();
         	pose.pushPose();
         	pose.scale(this.scaleX, this.scaleY, this.scaleZ);
         	boolean isVillagerKind = isVillager(mob, true);
         	boolean isVillager = isVillager(mob, false);
         	if(mob.isBaby() && !isVillager) {
            	float f = 2.0F;
            	float f1 = 1.4F;
            	pose.translate(0.0F, 0.03125F, 0.0F);
            	pose.scale(0.7F, 0.7F, 0.7F);
            	pose.translate(0.0F, 1.0F, 0.0F);
         	}

			ModelPart head = this.getParentModel().getHead();
         	float heightFix = (head.y / 16) * (1 - this.scaleY);
         	pose.translate(0f, heightFix, 0f);
         	head.translateAndRotate(pose);
         	
         	if(item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
            	float f2 = 1.1875F;
            	pose.scale(1.1875F, -1.1875F, -1.1875F);
            	if(isVillagerKind)
              		pose.translate(0.0F, 0.0625F, 0.0F);

            	GameProfile gameprofile = null;
            	if(itemstack.hasTag()) {
               		CompoundTag compoundtag = itemstack.getTag();
               		if(compoundtag.contains("SkullOwner", 10))
                  		gameprofile = NbtUtils.readGameProfile(compoundtag.getCompound("SkullOwner"));
            	}

            	pose.translate(-0.5D, 0.0D, -0.5D);
            	SkullBlock.Type skullblock$type = ((AbstractSkullBlock)((BlockItem)item).getBlock()).getType();
            	SkullModelBase skullmodelbase = this.skullModels.get(skullblock$type);
            	RenderType rendertype = SkullBlockRenderer.getRenderType(skullblock$type, gameprofile);
            	Entity entity = mob.getVehicle();
            	WalkAnimationState walkanimationstate;
            	if(entity instanceof LivingEntity) {
               		LivingEntity livingentity = (LivingEntity)entity;
               		walkanimationstate = livingentity.walkAnimation;
            	} else {
               		walkanimationstate = mob.walkAnimation;
            	}

            	float f3 = walkanimationstate.position(ageInTicks);
            	SkullBlockRenderer.renderSkull((Direction)null, 180.0F, f3, pose, buffer, light, skullmodelbase, rendertype);
         	} else {
            	label60: {
               		if(item instanceof ArmorItem) {
                  		ArmorItem armoritem = (ArmorItem)item;
                  		if (armoritem.getEquipmentSlot() == EquipmentSlot.HEAD) {
                     		break label60;
                  		}
               		}

               		translateToHead(pose, isVillagerKind);
               		this.itemRenderer.renderItem(mob, itemstack, ItemDisplayContext.HEAD, false, pose, buffer, light);
            	}
         	}

         	pose.popPose();
      	}
   	}

   	public static void translateToHead(PoseStack pose, boolean isVillagerKind) {
      	pose.translate(0.0F, -0.25F, 0.0F);
      	pose.mulPose(Axis.YP.rotationDegrees(180.0F));
      	pose.scale(0.625F, -0.625F, -0.625F);
      	if (isVillagerKind)
         	pose.translate(0.0F, 0.1875F, 0.0F);
   	}
}
