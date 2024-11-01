package playasmob;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.entity.MobType;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fluids.FluidType;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.Foods;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.food.FoodData;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffect;

public class EntityTypeData {
	public MobData data;
	public Map<Item, FoodProperties> foodInfo = new HashMap();
	public Map<Item, SoundEvent> foodSounds = new HashMap();
	public ArrayList<Item> customFood = new ArrayList();

	public EntityTypeData(MobData data) {
		this.data = data;
		this.buildFoodInfo();
	}
	
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	public void deactivate() {
	}

	public void activate(boolean firstSpawn, EntityType mob) {
	}
	
	public void load(CompoundTag compound) {
	}

	public CompoundTag save() {
		return new CompoundTag();
	}

	public List<MobAttribute> getAbilities(EntityType type) {
		return new ArrayList();
	}

	public List<MobAttribute> getPros(EntityType type) {
		return new ArrayList();
	}

	public List<MobAttribute> getInfo(EntityType type) {
		return new ArrayList();
	}

	public List<MobAttribute> getCons(EntityType type) {
		return new ArrayList();
	}
	
	public void addAttributeInfo(MobPreview preview) {
		preview.abilityData = this.getAbilities(preview.mob);
		preview.proData = this.getPros(preview.mob);
		preview.infoData = this.getInfo(preview.mob);
		preview.conData = this.getCons(preview.mob);
		preview.updateAbilities();
	}

	public SoundEvent getAmbientSound() {
		return null;
	}

	public SoundEvent getEatingSound(ItemStack stack) {
		if(stack.isEdible())
			return stack.getEatingSound();
		if(this.customFood.contains(stack.getItem()) && this.foodSounds.get(stack.getItem()) != null)
			return foodSounds.get(stack.getItem());
		return SoundEvents.GENERIC_EAT;
	}

	public SoundEvent getHurtSound(DamageSource source) {
		return null;
	}

	public SoundEvent getDeathSound() {
		return null;
	}

	public SoundEvent getStepSound() {
		return null;
	}

	public float stepVolume() {
		return 1f;
	}

	public boolean isImmobile() {
		return this.speedMultiplier() <= 0;
	}

	public boolean isInvulnerableTo(DamageSource source) {
		return false;
	}

	public boolean jumpInFluid(FluidType type) {
		return true;
	}

	public boolean canFluidJump(TagKey<Fluid> fluid) {
		if(fluid == FluidTags.WATER)
			return this.jumpInFluid(Fluids.WATER.getFluidType());
		if(fluid == FluidTags.LAVA)
			return this.jumpInFluid(Fluids.LAVA.getFluidType());
		return true;
	}

	public boolean walkableFluid(FluidState state) {
		return false;
	}

	public boolean projectileImmune() {
		return false;
	}

	public boolean hasLineOfSight(Entity entity) {
		return true;
	}

	public boolean canDisableShield() {
		return false;
	}
	
   	public void blockedByShield(LivingEntity blocker) {
   	}

	public boolean isSensitiveToWater() {
		return false;
	}

	public boolean canGetStuck(BlockState state) {
		return true;
	}

	public boolean hasWorldCollision() {
		return true;
	}

	public boolean flying() {
		return this.canFly() && !this.canLand();
	}

	public boolean canFly() {
		return false;
	}

	public boolean canLand() {
		return true;
	}

	public Pose customPose() {
		return null;
	}

	public void modifyDigSpeed(PlayerEvent.BreakSpeed event) {
		if(!event.getEntity().onGround() && this.flying())
			event.setNewSpeed(event.getNewSpeed() * 5);
	}

	public void preTick() {
		FoodData foodData = data.player.getFoodData();
		if(foodData.getSaturationLevel() < this.minimumSaturation() && this.minimumSaturation() > 0)
			foodData.setSaturation(this.minimumSaturation());
		if(this.getDiet() == Diet.None && foodData.getFoodLevel() < 17)
			foodData.setFoodLevel(17);
	}

	public void postTick() {
	}
	
   	public void thunderHit(ServerLevel server, LightningBolt bolt) {
   	}
	
   	public void renderInfo(GuiGraphics graphics, int width, int height) {
   	}
	
   	public void addItemTooltip(ItemTooltipEvent event) {
   		ItemStack stack = event.getItemStack();
   		List<Component> currentTips = event.getToolTip();
   		FoodProperties foodInfo = stack.getFoodProperties(data.player);
   		if(stack.isEdible()) {
   			String errorText = "";
			if(!this.vaildFood(stack.getItem()) || this.getDiet() == Diet.None)
				errorText = this.getDiet() == Diet.None ? "playasmob.diet_prevent.none" : "playasmob.diet_invaild";

			boolean canEat = stack.is(ItemTags.create(new ResourceLocation("playasmob:food_" + this.dietName())));
			if(foodInfo.isMeat() && (this.getDiet() == Diet.Carnivore || this.getDiet() == Diet.Piscivore))
				canEat = true;
			if(this.customFood().contains(stack.getItem()) && this.getDiet() == Diet.Custom)
				canEat = true;
			if(!canEat)
				errorText = "playasmob.diet_prevent." + this.dietName();
	
			if(errorText != "" && this.getDiet() != Diet.Omnivore && !this.customFood().contains(stack.getItem())) {
				Component text = Component.translatable(errorText).withStyle(ChatFormatting.RED);
				if(this.getDiet() != Diet.None)
					text = Component.literal(text.getString() + Component.translatable("playasmob.diet_prevent").getString()).withStyle(ChatFormatting.RED);
   				currentTips.add(1, text);
			}
   		} else if(this.customFood().contains(stack.getItem()) && this.vaildFood(stack.getItem())) {
   			currentTips.add(1, Component.translatable("playasmob.diet_custom").withStyle(ChatFormatting.BLUE));
   		}
   	}

	public boolean isFood(ItemStack stack) {
		if(this.customFood().contains(stack.getItem()))
			return this.vaildFood(stack.getItem());
		FoodProperties foodInfo = stack.getFoodProperties(data.player);
		if(stack.isEdible()) {
			if(!this.vaildFood(stack.getItem()) || this.getDiet() == Diet.None)
				return false;
			if(this.getDiet() == Diet.Omnivore)
				return true;
			boolean canEat = stack.is(ItemTags.create(new ResourceLocation("playasmob:food_" + this.dietName())));
			if(foodInfo.isMeat() && (this.getDiet() == Diet.Carnivore || this.getDiet() == Diet.Piscivore))
				canEat = true;
			return canEat;
   		}
		return false;
	}

	public float minimumSaturation() {
		return this.getDiet() == Diet.None ? 1f : 0f;
	}

	public String dietName() {
		return ("" + this.getDiet()).toLowerCase();
	}

	public Diet getDiet() {
		return Diet.Omnivore;
	}

	public void buildFoodInfo() {
	}

	public void addFoodInfo(Item item, FoodProperties info, @Nullable SoundEvent sound) {
		if(item.isEdible())
			info = item.getFoodProperties();
		this.foodInfo.put(item, info);
		this.foodSounds.put(item, sound);
		this.customFood.add(item);
	}

	public FoodProperties buildFood(int nutrition, float saturation, FoodEffect... effects) {
		return this.buildFood(nutrition, saturation, nutrition <= 0, false, false, effects);
	}

	public FoodProperties buildFood(int nutrition, float saturation, boolean alwaysEat, boolean fast, boolean meat, FoodEffect... effects) {
		FoodProperties.Builder builder = new FoodProperties.Builder();
		builder.nutrition(nutrition).saturationMod(saturation);
		if(alwaysEat)
			builder.alwaysEat();
		if(fast)
			builder.fast();
		if(meat)
			builder.meat();
			
		for(FoodEffect foodEffect : effects) {
			MobEffectInstance instance = new MobEffectInstance(foodEffect.effect, foodEffect.duration, foodEffect.amplifier);
			builder.effect(instance, foodEffect.chance);
		}

		return builder.build();
	}

	public FoodProperties getFoodInfo(Item item) {
		if(this.customFood().contains(item))
			return this.foodInfo.get(item);
		return Foods.DRIED_KELP;
	}

	public List<Item> customFood() {
		return this.customFood;
	}

	public boolean vaildFood(Item item) {
		return true;
	}

	public List<Item> notFood() {
		return List.of();
	}

	public void ate(ItemStack stack) {
	}
	
	public void press(int ability, boolean pressed) {
	}

	public void login() {
	}

	public void respawn() {
	}

	public EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		return null;
	}

   	public EntityDimensions getDimensions(Pose pose) {
   		EntityDimensions hitbox = data.mob.getDimensions();
   		float smallerSide = Math.min(hitbox.width, hitbox.height);
   		EntityDimensions smallHitbox = new EntityDimensions(smallerSide, smallerSide, false);
   		EntityDimensions tinyHitbox = new EntityDimensions(smallHitbox.width * 0.666f, smallHitbox.height * 0.666f, false);
		hitbox = switch(pose) {
			default -> hitbox;
			case FALL_FLYING -> smallHitbox;
			case SLEEPING -> tinyHitbox;
			case SWIMMING -> smallHitbox;
			case SPIN_ATTACK -> smallHitbox;
			case CROUCHING -> new EntityDimensions(hitbox.width, hitbox.height * 0.833f, false);
			case DYING -> tinyHitbox;
		};

		return hitbox.scale(data.player.getScale());
	}

	public float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.85f;
	}

	public ModelLayerLocation getModelLayer(boolean outer) {
		return ModelLayers.PLAYER;
	}

	public HandData getHand(EntityRendererProvider.Context context) {
		HandData hand = new HandData(context, this);
		this.setupHand(hand);
		return hand;
	}

	public HandData getHand() {
		HandData hand = new HandData();
		this.setupHand(hand);
		return hand;
	}

	public double entityReach() {
		return 3;
	}

	public double blockReach() {
		return !this.hasHand() ? 0 : 4.5;
	}

	public boolean hasHand() {
		return this.renderHand() || !data.fullyMobMode;
	}

	public boolean renderHand() {
		return this.getHand().texture != null;
	}

	public void setupHand(HandData hand) {
	}

	public boolean canPickup(ItemStack stack) {
		return this.hasHand();
	}

	public boolean allowTargeting(LivingEntity entity) {
		return true;
	}

	public boolean forceGlow(Entity entity) {
		return false;
	}

	public boolean renderEntity(Entity entity) {
		return true;
	}

	public boolean isShaking() {
		return false;
	}

	public boolean canSprint() {
		return (data.player.isUnderWater() && this.canSwim()) || !data.fullyMobMode;
	}

	public boolean canSwim() {
		return !this.flying();
	}

	public float swimAngle() {
		return -90;
	}

	public boolean canGlide() {
		return !this.flying();
	}

	public float glideAngle() {
		return -90;
	}

	public boolean canRiptide() {
		return !this.flying();
	}

	public boolean canCrouch() {
		return true;
	}

	public boolean canVisuallyCrouch() {
		return false || !data.fullyMobMode;
	}

	public boolean canJump() {
		return this.speedMultiplier() > 0;
	}

	public boolean preventSleeping() {
		return true;
	}

	public float sleepingYaw() {
		return -data.player.getYRot();
	}

	public float sleepingPitch() {
		return 0;
	}

	public boolean dampensVibrations() {
		return false;
	}

	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder attributes) {
		return attributes;
	}

	public double speedMultiplier() {
		return 1;
	}

	public float flyingUpDownSpeed() {
		return 1;
	}

	public double entityReachMultiplier() {
		return 1;
	}

	public double blockReachMultiplier() {
		return this.hasHand() ? 1 : 0;
	}

	public double selectionSizeMultiplier() {
		return 1;
	}

	public double getMyRidingOffset() {
		return -0.35;
	}

	public boolean canRide(Entity entity) {
		return false;
	}

	public boolean canControl(Entity entity) {
		return false;
	}

	public InteractionResult interact(Player player, InteractionHand hand) {
		return InteractionResult.PASS;
	}

	public InteractionResult interactOn(Entity entity, InteractionHand hand) {
		if(data.player.getItemInHand(hand) == ItemStack.EMPTY && this.canRide(entity)) {
			data.player.startRiding(entity);
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}

	public void setBaby(boolean baby) {
		data.sync(true, false);
	}

	public boolean isBaby() {
		return this instanceof AgeableData ageData && ageData.isBabyAge();
	}

	public void addEatingEffect(MobEffectInstance effect, ItemStack stack) {
		if(this.getDiet() == Diet.Detritivore) {
			if(effect.getEffect() == MobEffects.HUNGER && stack.getItem() == Items.ROTTEN_FLESH)
				return;
			if(effect.getEffect() == MobEffects.POISON && (stack.getItem() == Items.SPIDER_EYE || stack.getItem() == Items.POISONOUS_POTATO))
				return;
		}
		
		data.player.addEffect(effect);
	}

	public float getSaturation(ItemStack stack, float orignalSaturation) {
		if(this.getDiet() == Diet.Detritivore) {
			if(stack.getItem() == Items.ROTTEN_FLESH)
				return 0.6f;
			if(stack.getItem() == Items.SPIDER_EYE)
				return 0.3f;
			if(stack.getItem() == Items.POISONOUS_POTATO)
				return 0.5f;
		}
		
		return orignalSaturation;
	}

	public int getNutrition(ItemStack stack, int orignalNutrition) {
		if(this.getDiet() == Diet.Detritivore) {
			if(stack.getItem() == Items.ROTTEN_FLESH)
				return 6;
			if(stack.getItem() == Items.SPIDER_EYE)
				return 4;
			if(stack.getItem() == Items.POISONOUS_POTATO)
				return 5;
		}
		
		return orignalNutrition;
	}

	public void usedItem(LivingEntityUseItemEvent.Finish event) {
	}

	public void useItem(LivingEntityUseItemEvent.Start event) {
	}

	public void itemInteraction(PlayerInteractEvent.RightClickItem event) {
		this.canEatItem(event);
	}

	public void canEatItem(PlayerInteractEvent.RightClickItem event) {
		ItemStack stack = event.getEntity().getItemInHand(event.getHand());
		if(stack.getItem().isEdible())
			event.setCanceled(!this.isFood(stack));
	}

	public void entityInteraction(PlayerInteractEvent.EntityInteract event) {
		if(!this.hasHand())
			event.setCanceled(true);
	}

	public void killedEntity(LivingDeathEvent event) {
	}

	public void hurtEntity(LivingHurtEvent event) {
	}

	public void killed(LivingDeathEvent event) {
	}

	public void hurt(LivingHurtEvent event) {
	}

	public void attackEntity(LivingAttackEvent event) {
	}

	public void attacked(LivingAttackEvent event) {
	}

	public void healed(LivingHealEvent event) {
	}
	
	public void causeFallDamage(LivingFallEvent event) {
	}

	public void modifyBreathing(LivingBreatheEvent event) {
	}

	public ItemStack defaultArrow(ItemStack weapon) {
		return ItemStack.EMPTY;
	}

	public void modifyArrow(AbstractArrow arrow) {
	}

	public void addEffect(MobEffectEvent.Added effect) {
	}

	public boolean allowEffect(MobEffectInstance effect) {
		return true;
	}
}