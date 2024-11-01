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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.Foods;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraft.ChatFormatting;

public class SkeletonData extends EntityTypeData implements GolemAttack {
	public boolean strayConversion = false;
   	public int inPowderSnowTime = 0;
   	public int conversionTime = 0;

   	public List<MobAttribute> getPros(EntityType type) {
   		ArrayList<MobAttribute> pros = new ArrayList();
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.undead")));
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.infinity_bow")));
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

   	@Override
   	public List<MobAttribute> getInfo(EntityType type) {
   		ArrayList<MobAttribute> info = new ArrayList();
   		info.add(MobAttribute.info(Component.translatable("playasmob.info.no_diet")));
   		info.add(MobAttribute.info(Component.translatable("playasmob.info.undead")));
   		return info;
   	}

   	public List<MobAttribute> getCons(EntityType type) {
   		ArrayList<MobAttribute> cons = new ArrayList();
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.undead")));
   		if(type != EntityType.WITHER_SKELETON)
   			cons.add(MobAttribute.con(Component.translatable("playasmob.con.sun_burn")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.sprint")).fullOnly());
   		return cons;
   	}
	
	public SkeletonData(MobData data) {
		super(data);
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

	public void activate(boolean firstSpawn, EntityType mob) {
		if(!firstSpawn)
			return;
		GlobalUtils.dropHandItem(data.player);
		ItemStack weapon = new ItemStack(Items.BOW);
		if(mob == EntityType.WITHER_SKELETON)
			weapon = new ItemStack(Items.STONE_SWORD);
		data.player.setItemSlot(EquipmentSlot.MAINHAND, weapon);
		GlobalUtils.randomiseEnchants(data.player);
	}
	
	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder skeletonBuilder = AbstractSkeleton.createAttributes();
		playerBuilder.combine(skeletonBuilder);
		return playerBuilder;
	}

   	public SoundEvent getAmbientSound() {
		if(data.mob == EntityType.STRAY)
			return SoundEvents.STRAY_AMBIENT;
		if(data.mob == EntityType.WITHER_SKELETON)
			return SoundEvents.WITHER_SKELETON_AMBIENT;
		return SoundEvents.SKELETON_AMBIENT;
   	}

	public SoundEvent getHurtSound(DamageSource source) {
		if(data.mob == EntityType.STRAY)
			return SoundEvents.STRAY_AMBIENT;
		if(data.mob == EntityType.WITHER_SKELETON)
			return SoundEvents.WITHER_SKELETON_AMBIENT;
		return SoundEvents.SKELETON_AMBIENT;
	}

	public SoundEvent getDeathSound() {
		if(data.mob == EntityType.STRAY)
			return SoundEvents.STRAY_DEATH;
		if(data.mob == EntityType.WITHER_SKELETON)
			return SoundEvents.WITHER_SKELETON_DEATH;
		return SoundEvents.SKELETON_DEATH;
	}

   	public SoundEvent getStepSound() {
		if(data.mob == EntityType.STRAY)
			return SoundEvents.STRAY_STEP;
		if(data.mob == EntityType.WITHER_SKELETON)
			return SoundEvents.WITHER_SKELETON_STEP;
		return SoundEvents.SKELETON_STEP;
   	}

   	public float stepVolume() {
   		return 0.15f;
   	}

	public double speedMultiplier() {
		return 0.35;
	}

	public boolean canVisuallyCrouch() {
		return true;
	}

	public void respawn() {
		this.data.changeMob(data.mob, null, true);
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
		super.preTick();
		
		if(data.player.isAlive()) {
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
		return dimensions.height * (data.mob == EntityType.WITHER_SKELETON ? 0.875f : 0.874f);
	}

	public void hurtEntity(LivingHurtEvent event) {
		if(data.mob == EntityType.WITHER_SKELETON)
    		event.getEntity().addEffect(new MobEffectInstance(MobEffects.WITHER, 200), data.player);
	}

	public boolean fireImmune() {
		return data.mob == EntityType.WITHER_SKELETON;
	}

   	public boolean isInvulnerableTo(DamageSource source) {
   		if(data.mob == EntityType.SKELETON && source.is(DamageTypeTags.IS_FREEZING))
   			return true;
   		if(data.mob == EntityType.WITHER_SKELETON && source.is(DamageTypeTags.IS_FIRE))
   			return true;
   		return false;
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

	@Override
	public boolean canRide(Entity entity) {
		return entity instanceof Spider;
	}

	@Override
	public boolean canControl(Entity entity) {
		return entity instanceof Spider;
	}

	@Override
	public double getMyRidingOffset() {
		return -0.6;
	}

   	@Override
	public float minimumSaturation() {
		return 0f;
	}

   	@Override
   	public Diet getDiet() {
   		return Diet.None;
   	}

   	@Override
   	public void buildFoodInfo() {
   		this.addFoodInfo(Items.BONE, this.buildFood(2, 0.6f), null);
   		this.addFoodInfo(Items.BONE_MEAL, this.buildFood(1, 0.2f, false, true, false), null);
   		this.addFoodInfo(Items.BONE_BLOCK, this.buildFood(3, 1.2f, new FoodEffect(MobEffects.DAMAGE_RESISTANCE, 600, 1, 1f)), null);
   	}

   	public void startFreezeConversion(int time) {
   		if(data.mob != EntityType.SKELETON || !data.fullyMobMode)
   			return;
   			
      	this.conversionTime = time;
      	this.setFreezeConverting(true);
   	}

   	public void doFreezeConversion() {
   		if(data.mob != EntityType.SKELETON || !data.fullyMobMode)
   			return;
      	data.changeMob(EntityType.STRAY, this.save(), false);
        data.world.levelEvent(null, 1048, data.player.blockPosition(), 0);
   	}

	@Override
   	public void addItemTooltip(ItemTooltipEvent event) {
   		super.addItemTooltip(event);
   		ItemStack stack = event.getItemStack();
   		if(stack.getItem() != Items.BOW)
   			return;

   		List<Component> currentTips = event.getToolTip();
   		if(stack.getEnchantmentLevel(Enchantments.INFINITY_ARROWS) == 0)
   			currentTips.add(1, Component.translatable("enchantment.minecraft.infinity").withStyle(ChatFormatting.GRAY));
   		if(data.mob == EntityType.WITHER_SKELETON && stack.getEnchantmentLevel(Enchantments.FLAMING_ARROWS) == 0)
   			currentTips.add(1, Component.translatable("enchantment.minecraft.flame").withStyle(ChatFormatting.GRAY));
   		if(data.mob == EntityType.STRAY)
   			currentTips.add(1, Component.translatable("effect.minecraft.slowness").withStyle(ChatFormatting.GRAY));
   	}

	@Override
	public ItemStack defaultArrow(ItemStack weapon) {
		if(weapon.getItem() instanceof BowItem)
			return new ItemStack(Items.ARROW);
		return ItemStack.EMPTY;
	}

	@Override
	public void modifyArrow(AbstractArrow arrow) {
		arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
		if(data.mob == EntityType.WITHER_SKELETON)
			arrow.setSecondsOnFire(100);
		if(data.mob == EntityType.STRAY)
			((Arrow)arrow).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600));
	}
}
