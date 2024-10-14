package playasmob;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MobType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;

public class SkeletonData implements EntityTypeData, GolemAttack {
	public boolean strayConversion = false;
   	public int inPowderSnowTime = 0;
   	public int conversionTime = 0;
	MobData data = null;

   	public List<MobAttribute> getPros(EntityType type) {
   		ArrayList<MobAttribute> pros = new ArrayList();
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.undead")));
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.infinity")));
   		if(type == EntityType.WITHER_SKELETON) {
   			pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.fire_arrow")));
   			pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.fire_immune")));
   			pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.wither_immune")));
   			pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.wither_hit")));
   		}

		if(type == EntityType.STRAY)
   			pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.slowness_arrow")));
   		return pros;
   	}

   	public List<MobAttribute> getCons(EntityType type) {
   		ArrayList<MobAttribute> cons = new ArrayList();
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.undead")));
   		if(type != EntityType.WITHER_SKELETON)
   			cons.add(MobAttribute.con(Component.translatable("playasmob.con.sun_burn")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.sprint")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.crouch")));
   		return cons;
   	}
	
	public SkeletonData(MobData data) {
		this.data = data;
	}

	public void load(CompoundTag compound) {
		if (compound.contains("StrayConversionTime", 99) && compound.getInt("StrayConversionTime") > -1)
         	this.startFreezeConversion(compound.getInt("StrayConversionTime"));
	}

	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
		compound.putInt("StrayConversionTime", this.isFreezeConverting() ? this.conversionTime : -1);
		return compound;
	}

	public List<ResourceKey<Level>> spawnDimension() {
   		if(data.mob == EntityType.STRAY)
   			return List.of(Level.OVERWORLD);
   		if(data.mob == EntityType.WITHER_SKELETON)
   			return List.of(Level.NETHER);
   		return List.of(Level.OVERWORLD, Level.NETHER);
	}
	
	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder skeletonBuilder = AbstractSkeleton.createAttributes();
		playerBuilder.combine(skeletonBuilder);
		return playerBuilder;
	}

	public double speedMultiplier() {
		return 0.35;
	}

	public void respawn() {
		this.data.changeMob(EntityType.SKELETON, null, true);
	}

	public EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		if(data.renderAs == EntityType.STRAY)
			return new StrayRenderer(context);
		if(data.renderAs == EntityType.WITHER_SKELETON)
			return new WitherSkeletonRenderer(context);
		return new SkeletonRenderer(context);
	}
	
	public HandData getHand(EntityRendererProvider.Context context) {
		HandData hand = new HandData(context, this);
		hand.model = new SkeletonHand(context.bakeLayer(ModelLayers.SKELETON), false);
		this.setupHand(hand);
		return hand;
	}

	public void setupHand(HandData hand) {
		String texture = "textures/entity/skeleton/";
		texture += data.mob == EntityType.SKELETON ? "skeleton.png" : (data.mob == EntityType.STRAY ? "stray.png" : "wither_skeleton.png");
		hand.texture = new ResourceLocation(texture);
		hand.setScale(1f, 1f, 1f);
		hand.setPosition(5.25f, 21.5f, -1.1f);
		hand.setRotation(3.2f, 1.6f, 0.15f);
		if(data.mob == EntityType.STRAY)
			hand.outerTexture = new ResourceLocation("textures/entity/skeleton/stray_overlay.png");
	}

	public boolean allowEffect(MobEffectInstance effect) {
		return data.mob != EntityType.WITHER_SKELETON || effect.getEffect() != MobEffects.WITHER;
	}

	public void preTick() {
		if(data.player.isAlive()) {
			data.player.setAirSupply(-20);
			boolean flag = data.mob != EntityType.WITHER_SKELETON && data.isSunBurnTick();
         	if (flag) {
            	ItemStack itemstack = data.player.getItemBySlot(EquipmentSlot.HEAD);
            	if (!itemstack.isEmpty()) {
               		if (itemstack.isDamageableItem()) {
                  		itemstack.setDamageValue(itemstack.getDamageValue() + data.world.random.nextInt(2));
                  		if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                     		data.player.broadcastBreakEvent(EquipmentSlot.HEAD);
                     		data.player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                  		}
               		}

               		flag = false;
            	}

            	if (flag)
               		data.player.setSecondsOnFire(8);
         	}

         	if (!data.world.isClientSide) {
         		if (data.player.isInPowderSnow && data.mob == EntityType.SKELETON) {
            		if (this.isFreezeConverting()) {
               			--this.conversionTime;
               			if (this.conversionTime < 0)
                  			this.doFreezeConversion();
           	 		} else {
               			++this.inPowderSnowTime;
               			if (this.inPowderSnowTime >= 140)
                  			this.startFreezeConversion(300);
            		}
         		} else {
            		this.inPowderSnowTime = -1;
            		this.setFreezeConverting(false);
         		}
      		}
		}
	}

	public float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return data.mob == EntityType.WITHER_SKELETON ? 2.1F : 1.74F;
	}

	public void hurtEntity(LivingHurtEvent event) {
		if(data.mob == EntityType.WITHER_SKELETON)
    		event.getEntity().addEffect(new MobEffectInstance(MobEffects.WITHER, 200), data.player);
	}

   	public void attacked(LivingAttackEvent event) {
		if(event.getSource().is(DamageTypes.FREEZE) && data.mob == EntityType.SKELETON)
			event.setCanceled(true);
		if(event.getSource().is(DamageTypeTags.IS_FIRE) && data.mob == EntityType.WITHER_SKELETON)
			event.setCanceled(true);
   	}

   	public MobType getMobType() {
   		return MobType.UNDEAD;
   	}

   	public boolean isFreezeConverting() {
      	return data.mob == EntityType.SKELETON && this.strayConversion;
   	}

   	public void setFreezeConverting(boolean converting) {
   		if(data.mob == EntityType.SKELETON)
      		this.strayConversion = converting;
   	}
	
   	public boolean isShaking() {
      	return this.isFreezeConverting();
   	}

   	public void startFreezeConversion(int time) {
   		if(data.mob != EntityType.SKELETON)
   			return;
   			
      	this.conversionTime = time;
      	this.setFreezeConverting(true);
   	}

   	public void doFreezeConversion() {
   		if(data.mob != EntityType.SKELETON)
   			return;
   			
      	data.changeMob(EntityType.STRAY, this.save(), false);
        data.world.levelEvent((Player)null, 1048, data.player.blockPosition(), 0);
   	}
   	
	public void getProjectile(LivingGetProjectileEvent event) {
		final ItemStack weapon = event.getProjectileWeaponItemStack();
		if(weapon.getItem() != Items.BOW)
			return;

		final var enchants = EnchantmentHelper.getEnchantments(weapon);
		if(data.mob == EntityType.WITHER_SKELETON)
			weapon.enchant(Enchantments.FLAMING_ARROWS, 1);
		weapon.enchant(Enchantments.INFINITY_ARROWS, 1);
		PlayasmobMod.queueServerWork(1, () -> {
			EnchantmentHelper.setEnchantments(enchants, weapon);
		});
		
		ItemStack ammo = new ItemStack(Items.ARROW);
		if(data.mob == EntityType.STRAY) {
			ammo = new ItemStack(Items.TIPPED_ARROW);
			PotionUtils.setPotion(ammo, Potions.SLOWNESS);
		}
		
		event.setProjectileItemStack(ammo);
		
	}

	public boolean canSprint() {
		return false;
	}

	public boolean canCrouch() {
		return false;
	}
}
