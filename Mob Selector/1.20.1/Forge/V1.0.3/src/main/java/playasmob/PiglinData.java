package playasmob;

import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;

public class PiglinData implements EntityTypeData, GolemAttack, SkeletonAttack {
	public static final UUID ModifierUUID = UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667");
   	public static final AttributeModifier SpeedModifer = new AttributeModifier(ModifierUUID, "Baby speed boost", 0.2, AttributeModifier.Operation.MULTIPLY_BASE);
	public MobData data;
	public boolean isBaby = false;
	public boolean isDancing = false;
	public boolean isChargingCrossbow = false;
	public boolean zombieConvertImmune = false;
  	public int timeInOverworld = 0;

  	public boolean skeletonAttack(EntityType type) {
  		return type == EntityType.WITHER_SKELETON;
  	}

   	public List<MobAttribute> getAbilities(EntityType type) {
   		ArrayList<MobAttribute> abilities = new ArrayList();
   		abilities.add(MobAttribute.ability(1, Component.translatable("playasmob.ability.dance")));
   		return abilities;
   	}

   	public List<MobAttribute> getPros(EntityType type) {
   		ArrayList<MobAttribute> pros = new ArrayList();
   		if(type == EntityType.PIGLIN_BRUTE)
   			pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.health")));
   		return pros;
   	}

   	public List<MobAttribute> getCons(EntityType type) {
   		ArrayList<MobAttribute> cons = new ArrayList();
   		if(type == EntityType.PIGLIN)
   			cons.add(MobAttribute.con(Component.translatable("playasmob.con.health")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.transform_piglin")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.crouch")));
   		return cons;
   	}

	public PiglinData(MobData data) {
		this.data = data;
	}

	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
		
      	if (this.isBaby())
         	compound.putBoolean("IsBaby", true);
      	if (this.isDancing())
         	compound.putBoolean("isDancing", true);
      	if (this.isChargingCrossbow())
         	compound.putBoolean("isChargingCrossbow", true);
      	if (this.isImmuneToZombification())
         	compound.putBoolean("IsImmuneToZombification", true);
      	compound.putInt("TimeInOverworld", this.timeInOverworld);

      	return compound;
	}

	public void load(CompoundTag compound) {
      	this.setBaby(compound.getBoolean("IsBaby"));
      	this.setDancing(compound.getBoolean("isDancing"));
      	this.setChargingCrossbow(compound.getBoolean("isChargingCrossbow"));
      	this.setImmuneToZombification(compound.getBoolean("IsImmuneToZombification"));
      	this.timeInOverworld = compound.getInt("TimeInOverworld");
	}

   	public boolean isHoldingMeleeWeapon() {
      	return data.player.getMainHandItem().getItem() instanceof TieredItem;
   	}

   	public static boolean isLovedItem(ItemStack stack) {
      	return stack.is(ItemTags.PIGLIN_LOVED);
   	}

   	public boolean canCrouch() {
   		return false;
   	}

	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder piglinBuilder = data.mob == EntityType.PIGLIN_BRUTE ? PiglinBrute.createAttributes() : Piglin.createAttributes();
			playerBuilder.combine(piglinBuilder);
		return playerBuilder;
	}

   	public SoundEvent getAmbientSound() {
		return data.mob == EntityType.PIGLIN_BRUTE ? SoundEvents.PIGLIN_BRUTE_AMBIENT : SoundEvents.PIGLIN_AMBIENT;
   	}

	public SoundEvent getHurtSound(DamageSource source) {
		return data.mob == EntityType.PIGLIN_BRUTE ? SoundEvents.PIGLIN_BRUTE_HURT : SoundEvents.PIGLIN_HURT;
	}

	public SoundEvent getDeathSound() {
		return data.mob == EntityType.PIGLIN_BRUTE ? SoundEvents.PIGLIN_BRUTE_DEATH : SoundEvents.PIGLIN_DEATH;
	}

   	public SoundEvent getStepSound() {
		return data.mob == EntityType.PIGLIN_BRUTE ? SoundEvents.PIGLIN_BRUTE_STEP : SoundEvents.PIGLIN_STEP;
   	}

   	public float stepVolume() {
   		return 0.15f;
   	}

	public double speedMultiplier() {
		return data.isSprinting ? 0.25 : 0.15;
	}

	public ModelLayerLocation getModelLayer(boolean outer) {
		return data.renderAs == EntityType.PIGLIN_BRUTE ? ModelLayers.PIGLIN_BRUTE : ModelLayers.PIGLIN;
	}

	public EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		return new PiglinRenderer(context, data.renderAs);
	}

	public void setupHand(HandData hand) {
		String texture = "textures/entity/piglin/";
		texture += data.mob == EntityType.PIGLIN_BRUTE ? "piglin_brute.png" : "piglin.png";
		hand.texture = new ResourceLocation(texture);
		hand.setScale(1f, 1f, 1f);
		hand.setPosition(5.25f, 21.5f, -1.1f);
		hand.setRotation(3.2f, 1.6f, 0.15f);
	}

	public void press(int ability, boolean pressed) {
		if(ability == 1 && pressed)
			this.setDancing(!this.isDancing());
	}

   	public void setImmuneToZombification(boolean immune) {
      	this.zombieConvertImmune = immune;
   	}

   	public boolean isImmuneToZombification() {
      	return this.zombieConvertImmune;
   	}

   	public boolean isConverting() {
		if(data.world == null)
			return false;   		
      	return !data.world.dimensionType().piglinSafe() && !this.isImmuneToZombification();
   	}

   	public boolean isShaking() {
   		return this.isConverting();
   	}

   	public void finishConversion() {
   		CompoundTag savedData = this.save();
   		savedData.putString("cureInto", EntityType.getKey(data.mob).toString());
   		
      	data.changeMob(EntityType.ZOMBIFIED_PIGLIN, savedData, false);
    	data.player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
   	}
	
	public float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return this.isBaby() ? 0.97f : 1.79f;
	}

   	public void setBaby(boolean baby) {
		if(data.mob == EntityType.PIGLIN_BRUTE)
			return;
   		
      	this.isBaby = baby;
      	if(data.world != null && !data.world.isClientSide) {
         	AttributeInstance attributeinstance = data.player.getAttribute(Attributes.MOVEMENT_SPEED);
         	attributeinstance.removeModifier(SpeedModifer);
         	if (baby)
            	attributeinstance.addTransientModifier(SpeedModifer);
      	}

   	}

   	public boolean isBaby() {
      	return data.mob != EntityType.PIGLIN_BRUTE && this.isBaby;
   	}

   	public void preTick() {
   		ItemStack stack = data.player.isUsingItem() ? data.player.getItemInHand(data.player.getUsedItemHand()) : ItemStack.EMPTY;
		this.setChargingCrossbow(stack.getItem() == Items.CROSSBOW);
   		
      	if (this.isConverting()) {
         	++this.timeInOverworld;
      	} else {
         	this.timeInOverworld = 0;
      	}

      	if (this.timeInOverworld > 300 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(data.player, EntityType.ZOMBIFIED_PIGLIN, (timer) -> this.timeInOverworld = timer)) {
         	this.playConvertedSound();
         	this.finishConversion();
      	}
   	}
   	
   	public boolean isChargingCrossbow() {
      	return this.isChargingCrossbow;
   	}

   	public void setChargingCrossbow(boolean charging) {
      	this.isChargingCrossbow = charging;
      	data.sync(false, false);
   	}

   	public void onCrossbowAttackPerformed() {
      	data.player.setNoActionTime(0);
   	}

	public PiglinArmPose getArmPose() {
		if(data.mob == EntityType.PIGLIN_BRUTE)
			return data.isAggressive() && this.isHoldingMeleeWeapon() ? PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON : PiglinArmPose.DEFAULT;
		
      	if(this.isDancing())
         	return PiglinArmPose.DANCING;
      	if(this.isLovedItem(data.player.getOffhandItem()))
         	return PiglinArmPose.ADMIRING_ITEM;
      	if(data.isAggressive() && this.isHoldingMeleeWeapon())
         	return PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON;
      	if(this.isChargingCrossbow())
         	return PiglinArmPose.CROSSBOW_CHARGE;
        return data.isAggressive() && data.player.isHolding(is -> is.getItem() instanceof net.minecraft.world.item.CrossbowItem) ? PiglinArmPose.CROSSBOW_HOLD : PiglinArmPose.DEFAULT;
	}

   	public boolean isDancing() {
      	return this.isDancing;
   	}

   	public void setDancing(boolean dancing) {
      	this.isDancing =  dancing;
      	data.sync(false, false);
   	}

   	public void playSoundEvent(SoundEvent sound) {
      	data.player.playSound(sound);
   	}

   	public void playConvertedSound() {
      	this.playSoundEvent(data.mob == EntityType.PIGLIN_BRUTE ? SoundEvents.PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED : SoundEvents.PIGLIN_CONVERTED_TO_ZOMBIFIED);
   	}
}