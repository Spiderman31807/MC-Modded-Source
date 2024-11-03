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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.client.gui.GuiGraphics;

public class PiglinData extends EntityTypeData implements GolemAttack, SkeletonAttack {
	public boolean isBaby = false;
	public boolean isDancing = false;
	public boolean isChargingCrossbow = false;
	public boolean zombieConvertImmune = false;
  	public int timeInOverworld = 0;

  	public boolean skeletonAttack(EntityType type) {
  		return type == EntityType.WITHER_SKELETON;
  	}

   	@Override
   	public List<MobAttribute> getAbilities(EntityType type) {
   		ArrayList<MobAttribute> abilities = new ArrayList();
   		abilities.add(MobAttribute.ability(1, Component.translatable("playasmob.ability.dance")).fullOnly());
   		return abilities;
   	}

   	@Override
   	public List<MobAttribute> getPros(EntityType type) {
   		ArrayList<MobAttribute> pros = new ArrayList();
   		if(type == EntityType.PIGLIN_BRUTE)
   			pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.health")));
   		return pros;
   	}

   	@Override
   	public List<MobAttribute> getInfo(EntityType type) {
   		ArrayList<MobAttribute> info = new ArrayList();
   		info.add(MobAttribute.info(Component.translatable("playasmob.info.piglin_diet")).tooltip(Component.translatable("playasmob.tooltip.piglin_diet")));
   		return info;
   	}

   	@Override
   	public List<MobAttribute> getCons(EntityType type) {
   		ArrayList<MobAttribute> cons = new ArrayList();
   		if(type == EntityType.PIGLIN)
   			cons.add(MobAttribute.con(Component.translatable("playasmob.con.health")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.transform_piglin")).fullOnly());
   		return cons;
   	}

	public PiglinData(MobData data) {
		super(data);
	}

   	@Override
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

   	@Override
	public void load(CompoundTag compound) {
      	this.setBaby(compound.getBoolean("IsBaby"));
      	this.setDancing(compound.getBoolean("isDancing"));
      	this.setChargingCrossbow(compound.getBoolean("isChargingCrossbow"));
      	this.setImmuneToZombification(compound.getBoolean("IsImmuneToZombification"));
      	this.timeInOverworld = compound.getInt("TimeInOverworld");
	}

   	@Override
	public void activate(boolean firstSpawn, EntityType mob) {
		if(!firstSpawn)
			return;
		GlobalUtils.dropHandItem(data.player);
		ItemStack weapon = data.player.getRandom().nextFloat() < 0.5D ? new ItemStack(Items.CROSSBOW) : new ItemStack(Items.GOLDEN_SWORD);
		if(mob == EntityType.PIGLIN_BRUTE)
			weapon = new ItemStack(Items.GOLDEN_AXE);
		data.player.setItemSlot(EquipmentSlot.MAINHAND, weapon);
		GlobalUtils.randomiseEnchants(data.player);
	}

   	public boolean isHoldingMeleeWeapon() {
      	return data.player.getMainHandItem().getItem() instanceof TieredItem;
   	}

   	public static boolean isLovedItem(ItemStack stack) {
      	return stack.is(ItemTags.PIGLIN_LOVED);
   	}

   	@Override
   	public double getMyRidingOffset() {
      	return this.isBaby() ? -0.05 : -0.45;
   	}

   	@Override
	public boolean canVisuallyCrouch() {
		return true;
	}

   	@Override
	public void renderInfo(GuiGraphics graphics, int width, int height) {
		if(data.mob == EntityType.PIGLIN && data.fullyMobMode)
			RenderUtils.abilityIcon(graphics, "dance", this.isDancing(), width - 30, height - 22, -1, Keybinds.Ability1);
	}

   	@Override
	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder piglinBuilder = data.mob == EntityType.PIGLIN_BRUTE ? PiglinBrute.createAttributes() : Piglin.createAttributes();
			playerBuilder.combine(piglinBuilder);
		return playerBuilder;
	}

   	@Override
   	public SoundEvent getAmbientSound() {
		return data.mob == EntityType.PIGLIN_BRUTE ? SoundEvents.PIGLIN_BRUTE_AMBIENT : SoundEvents.PIGLIN_AMBIENT;
   	}

   	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return data.mob == EntityType.PIGLIN_BRUTE ? SoundEvents.PIGLIN_BRUTE_HURT : SoundEvents.PIGLIN_HURT;
	}

   	@Override
	public SoundEvent getDeathSound() {
		return data.mob == EntityType.PIGLIN_BRUTE ? SoundEvents.PIGLIN_BRUTE_DEATH : SoundEvents.PIGLIN_DEATH;
	}

   	@Override
   	public SoundEvent getStepSound() {
		return data.mob == EntityType.PIGLIN_BRUTE ? SoundEvents.PIGLIN_BRUTE_STEP : SoundEvents.PIGLIN_STEP;
   	}

   	@Override
   	public float stepVolume() {
   		return 0.15f;
   	}

   	@Override
	public double speedMultiplier() {
		return (data.isSprinting ? 0.25 : 0.15) * (this.isBaby() ? 1.2 : 1);
	}

   	@Override
	public ModelLayerLocation getModelLayer(boolean outer) {
		return data.renderAs == EntityType.PIGLIN_BRUTE ? ModelLayers.PIGLIN_BRUTE : ModelLayers.PIGLIN;
	}

   	@Override
	public EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		return new PiglinRenderer(context, data.renderAs);
	}

   	@Override
	public void setupHand(HandData hand) {
		String texture = "textures/entity/piglin/";
		texture += data.mob == EntityType.PIGLIN_BRUTE ? "piglin_brute.png" : "piglin.png";
		hand.texture = new ResourceLocation(texture);
		hand.setScale(1f, 1f, 1f);
		hand.setPosition(5.25f, 21.5f, -1.1f);
		hand.setRotation(3.2f, 1.6f, 0.15f);
	}

   	@Override
	public boolean renderHand() {
		return !this.isDancing();
	}

   	@Override
	public void press(int ability, boolean pressed) {
		if(ability == 1 && pressed)
			this.setDancing(!this.isDancing());
	}

   	public void setImmuneToZombification(boolean immune) {
      	this.zombieConvertImmune = immune;
   	}

   	public boolean isImmuneToZombification() {
      	return this.zombieConvertImmune || !data.fullyMobMode;
   	}

   	public boolean isConverting() {
		if(data.world == null)
			return false;   		
      	return !data.world.dimensionType().piglinSafe() && !this.isImmuneToZombification();
   	}

   	@Override
   	public boolean canSprint() {
   		return true;
   	}

   	@Override
   	public boolean isShaking() {
   		return this.isConverting();
   	}

   	public void finishConversion() {
   		CompoundTag savedData = this.save();
   		savedData.putString("cureInto", EntityType.getKey(data.mob).toString());
   		
      	data.changeMob(EntityType.ZOMBIFIED_PIGLIN, savedData, false);
    	data.player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
   	}
	
   	@Override
	public float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return dimensions.height * (this.isBaby() ? 0.994f : 0.917f);
	}

   	@Override
   	public void setBaby(boolean baby) {
		if(data.mob != EntityType.PIGLIN_BRUTE)
      		this.isBaby = baby;
   		super.setBaby(baby);
   	}

   	@Override
   	public boolean isBaby() {
      	return data.mob != EntityType.PIGLIN_BRUTE && this.isBaby;
   	}

   	@Override
   	public void preTick() {
		super.preTick();
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

   	@Override
	public String dietName() {
		return "piglin";
	}

   	@Override
   	public Diet getDiet() {
   		return Diet.Custom;
   	}

   	@Override
   	public void buildFoodInfo() {
   		super.buildFoodInfo();
   		this.addFoodInfo(Items.PORKCHOP, null, null);
   		this.addFoodInfo(Items.COOKED_PORKCHOP, null, null);
   		this.addFoodInfo(Items.GOLDEN_CARROT, null, null);
   		this.addFoodInfo(Items.CARROT, null, null);
   		this.addFoodInfo(Items.POTATO, null, null);
   		this.addFoodInfo(Items.BEETROOT, null, null);
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
      	return this.isDancing && data.mob == EntityType.PIGLIN;
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