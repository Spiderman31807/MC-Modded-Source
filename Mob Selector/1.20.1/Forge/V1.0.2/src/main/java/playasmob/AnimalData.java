package playasmob;

import net.minecraftforge.event.entity.living.LivingFallEvent;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Mth;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.item.ItemEntity;
import java.util.Map;
import net.minecraft.world.level.ItemLike;
import net.minecraft.Util;
import com.google.common.collect.Maps;
import net.minecraft.world.level.block.Blocks;

public class AnimalData implements EntityTypeData, BreedableData {
	public static final Ingredient PigFood = Ingredient.of(Items.CARROT, Items.POTATO, Items.BEETROOT);
	public static final Ingredient ChickenFood = Ingredient.of(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD);
   	public static final Map<DyeColor, ItemLike> DyeWool = Util.make(Maps.newEnumMap(DyeColor.class), (map) -> {
      	map.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
      	map.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
      	map.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
      	map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
      	map.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
      	map.put(DyeColor.LIME, Blocks.LIME_WOOL);
      	map.put(DyeColor.PINK, Blocks.PINK_WOOL);
      	map.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
      	map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
      	map.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
      	map.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
      	map.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
      	map.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
      	map.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
      	map.put(DyeColor.RED, Blocks.RED_WOOL);
      	map.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
   	});
   	
	public MobData data = null;
	public AgeData ageData;
	public BreedData breedData;
   	public int eatAnimationTick = 0;
   	public boolean isSheared = false;
   	public DyeColor color = DyeColor.WHITE;
   	public int eggTime = 6000;
   	public float flapping = 1.0F;
   	public float nextFlap = 1.0F;
   	public float oFlapSpeed;
  	public float flapSpeed;
   	public float oFlap;
   	public float flap;

   	public List<MobAttribute> getAbilities(EntityType type) {
   		ArrayList<MobAttribute> abilities = new ArrayList();
   		if(type == EntityType.CHICKEN)
   			abilities.add(MobAttribute.ability(1, Component.translatable("playasmob.ability.lay_egg")));
   		return abilities;
   	}

   	public List<MobAttribute> getPros(EntityType type) {
   		ArrayList<MobAttribute> pros = new ArrayList();
   		if(type == EntityType.CHICKEN)
   			pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.slow_fall")));
   		return pros;
   	}

   	public List<MobAttribute> getInfo(EntityType type) {
   		ArrayList<MobAttribute> info = new ArrayList();
   		info.add(MobAttribute.info(Component.translatable("playasmob.info.ageable")));
   		info.add(MobAttribute.info(Component.translatable("playasmob.info.breedable")));
   		if(type == EntityType.PIG)
   			info.add(MobAttribute.info(Component.translatable("playasmob.info.transform_pig")));
   		return info;
   	}

   	public List<MobAttribute> getCons(EntityType type) {
   		ArrayList<MobAttribute> cons = new ArrayList();
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.health")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.hand")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.pickup")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.crouch")));
   		return cons;
   	}
	
	public AnimalData(MobData data) {
		this.data = data;
		this.ageData = new AgeData(this, data);
		this.breedData = new BreedData(this, data);
	}

	public void load(CompoundTag compound) {
		if(compound.contains("AgeData"))
			this.ageLoad(compound.getCompound("AgeData"));
		if(compound.contains("BreedData"))
			this.breedLoad(compound.getCompound("BreedData"));

		switch(GlobalUtils.getString(data.mob)) {
			default:
				break;
			case "minecraft:chicken":
				this.eggTime = compound.getInt("EggLayTime");
				this.oFlapSpeed = compound.getFloat("oFlapSpeed");
				this.flapSpeed = compound.getFloat("flapSpeed");
				this.oFlap = compound.getFloat("oFlap");
				this.flap = compound.getFloat("flap");
      			break;
		};
	}

	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
		compound.put("AgeData", this.ageSave());
		compound.put("BreedData", this.breedSave());

		switch(GlobalUtils.getString(data.mob)) {
			default:
				break;
			case "minecraft:chicken":
      			compound.putInt("EggLayTime", this.eggTime);
      			compound.putFloat("oFlapSpeed", this.oFlapSpeed);
      			compound.putFloat("flapSpeed", this.flapSpeed);
      			compound.putFloat("oFlap", this.oFlap);
      			compound.putFloat("flap", this.flap);
      			break;
		};
		
		return compound;
	}

	public AgeData ageData() {
		return this.ageData;
	}

	public BreedData breedData() {
		return this.breedData;
	}

   	public boolean isFood(ItemStack stack) {
   		return switch(GlobalUtils.getString(data.renderAs)) {
			default -> stack.getItem() == Items.WHEAT;
			case "minecraft:pig" -> PigFood.test(stack);
			case "minecraft:chicken" -> ChickenFood.test(stack);
		};
   	}

   	public void mobInteract(Player player, InteractionHand hand) {
   		ItemStack stack = player.getItemInHand(hand);
   		switch(GlobalUtils.getString(data.mob)) {
			case "minecraft:cow":
				if (!stack.is(Items.BUCKET) || this.isBaby())
					break;
         		player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
         		ItemStack newStack = ItemUtils.createFilledResult(stack, player, Items.MILK_BUCKET.getDefaultInstance());
         		player.setItemInHand(hand, newStack);
				return;
			case "minecraft:sheep":
				if (stack.getItem() != Items.SHEARS || !this.readyForShearing() || data.world.isClientSide)
					break;
         		this.shear(SoundSource.PLAYERS);
            	data.player.gameEvent(GameEvent.SHEAR, player);
            	stack.hurtAndBreak(1, player, (p_29822_) -> { p_29822_.broadcastBreakEvent(hand); });
				return;
		};

		if (this.isFood(stack)) {
         	int age = this.getAge();
         	FoodData foodData = data.player.getFoodData();
			if(foodData.needsFood()) {
				foodData.eat(stack.getItem(), stack);
				return;
			}
         	
         	if (!data.world.isClientSide && age == 0 && this.canFallInLove()) {
            	data.usePlayerItem(player, hand, stack);
            	this.setInLove(player);
            	return;
         	}

        	if (this.isBaby()) {
            	data.usePlayerItem(player, hand, stack);
            	this.ageUp(AgeableMob.getSpeedUpSecondsWhenFeeding(-age), true);
            	return;
         	}
      	}
   	}

   	public void shear(SoundSource source) {
      	data.world.playSound((Player)null, data.player, SoundEvents.SHEEP_SHEAR, source, 1.0F, 1.0F);
      	this.setSheared(true);

      	int i = 1 + data.world.random.nextInt(3);
      	for(int j = 0; j < i; ++j) {
         	ItemEntity itementity = data.player.spawnAtLocation(DyeWool.get(this.getColor()), 1);
         	if (itementity != null)
            	itementity.setDeltaMovement(itementity.getDeltaMovement().add((double)((data.world.random.nextFloat() - data.world.random.nextFloat()) * 0.1F), (double)(data.world.random.nextFloat() * 0.05F), (double)((data.world.random.nextFloat() - data.world.random.nextFloat()) * 0.1F)));
      	}
   	}

   	public boolean readyForShearing() {
      	return data.player.isAlive() && !this.isSheared() && !this.isBaby();
   	}
	
   	public void preTick() {
   		if(this.ageData != null)
   			this.ageData.tick();
   		if(this.breedData != null)
   			this.breedData.tick();

   		switch(GlobalUtils.getString(data.mob)) {
			case "minecraft:chicken" -> this.chickenTick();
		};
   	}

   	public void chickenTick() {
      	this.oFlap = this.flap;
      	this.oFlapSpeed = this.flapSpeed;
      	this.flapSpeed += (data.player.onGround() ? -1.0F : 4.0F) * 0.3F;
      	this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
      	if (!data.player.onGround() && this.flapping < 1.0F)
         	this.flapping = 1.0F;

      	this.flapping *= 0.9F;
      	Vec3 vec3 = data.player.getDeltaMovement();
      	if (!data.player.onGround() && vec3.y < 0.0D)
         	data.player.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));

      	this.flap += this.flapping * 2.0F;
      	if(this.eggTime > 0)
      		this.eggTime--;
   	}

   	public void press(int ability, boolean pressed) {
   		if(data.mob == EntityType.CHICKEN) {
   			if(ability == 1 && pressed)
   				this.layEgg();
   		}
   	}

   	public void layEgg() {
   		if ((data.world == null && !data.world.isClientSide) && data.player.isAlive() && !this.isBaby() && this.eggTime <= 0) {
        	data.player.playSound(SoundEvents.CHICKEN_EGG, 1, (data.world.random.nextFloat() - data.world.random.nextFloat()) * 0.2F + 1.0F);
        	data.player.spawnAtLocation(Items.EGG);
        	data.player.gameEvent(GameEvent.ENTITY_PLACE);
       		this.eggTime = data.world.random.nextInt(6000) + 6000;
      	}
   	}

   	public boolean isFlapping() {
      	return data.player.flyDist > this.nextFlap;
   	}

   	public void onFlap() {
      	this.nextFlap = data.player.flyDist + this.flapSpeed / 2.0F;
   	}

   	public void thunderHit(ServerLevel server, LightningBolt bolt) {
      	if(data.mob == EntityType.PIG) {
      		data.changeMob(EntityType.ZOMBIFIED_PIGLIN, null, false);
      		return;
      	}
   	}

	public boolean canSprint() {
		return true;
	}
	
	public void causeFallDamage(LivingFallEvent event) {
		if(data.mob == EntityType.CHICKEN)
			event.setCanceled(true);
	}

	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder animalBuilder = switch(GlobalUtils.getString(data.mob)) {
			default -> null;
			case "minecraft:cow" -> Cow.createAttributes();
			case "minecraft:sheep" -> Sheep.createAttributes();
			case "minecraft:pig" -> Pig.createAttributes();
			case "minecraft:chicken" -> Chicken.createAttributes();
		};

		if(animalBuilder != null)
			playerBuilder.combine(animalBuilder);
		return playerBuilder;
	}

	public double speedMultiplier() {
		if(data.isSprinting)
			return 0.4;
		
		return switch(GlobalUtils.getString(data.mob)) {
			default -> 1;
			case "minecraft:cow" -> 0.3;
			case "minecraft:sheep" -> 0.38;
			case "minecraft:pig" -> 0.4;
			case "minecraft:chicken" -> 0.4;
		};
	}

	public EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		return switch(GlobalUtils.getString(data.renderAs)) {
			default -> null;
			case "minecraft:cow" -> new CowRenderer(context);
			case "minecraft:sheep" -> new SheepRenderer(context);
			case "minecraft:pig" -> new PigRenderer(context);
			case "minecraft:chicken" -> new ChickenRenderer(context);
		};
	}

	public float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return switch(GlobalUtils.getString(data.mob)) {
			default -> dimensions.height * 0.85F;
			case "minecraft:cow" -> this.isBaby() ? dimensions.height * 0.95F : 1.3F;
			case "minecraft:sheep" -> 0.95F * dimensions.height;
			case "minecraft:chicken" -> this.isBaby() ? dimensions.height * 0.85F : dimensions.height * 0.92F;
		};
	}

   	public boolean isSheared() {
      	return this.isSheared;
   	}

   	public void setSheared(boolean sheared) {
      	this.isSheared = sheared;
   	}

   	public DyeColor getColor() {
      	return this.color;
   	}
   
   	public float getHeadEatPositionScale(float p_29881_) {
      	if (this.eatAnimationTick <= 0) {
         	return 0.0F;
      	} else if (this.eatAnimationTick >= 4 && this.eatAnimationTick <= 36) {
         	return 1.0F;
      	} else {
         	return this.eatAnimationTick < 4 ? ((float)this.eatAnimationTick - p_29881_) / 4.0F : -((float)(this.eatAnimationTick - 40) - p_29881_) / 4.0F;
      	}
   	}

   	public float getHeadEatAngleScale(float p_29883_) {
      	if (this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
         	float f = ((float)(this.eatAnimationTick - 4) - p_29883_) / 32.0F;
         	return ((float)Math.PI / 5F) + 0.21991149F * Mth.sin(f * 28.7F);
      	} else {
         	return this.eatAnimationTick > 0 ? ((float)Math.PI / 5F) : data.player.getXRot() * ((float)Math.PI / 180F);
      	}
   	}
}
