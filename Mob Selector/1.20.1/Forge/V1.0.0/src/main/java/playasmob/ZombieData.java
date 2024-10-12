package playasmob;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;
import java.util.UUID;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.item.Items;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.npc.AbstractVillager;

public class ZombieData implements EntityTypeData<AbstractZombieRenderer> {
   	public static final UUID ModifierUUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
   	public static final AttributeModifier SpeedModifer = new AttributeModifier(ModifierUUID, "Baby speed boost", 0.5D, AttributeModifier.Operation.MULTIPLY_BASE);
	public boolean drownedConverting = false;
   	public int villagerConversionTime = 0;
   	public UUID conversionStarter = null;
   	public boolean converting = false;
   	public boolean isBaby = false;
   	public int conversionTime = 0;
   	public int inWaterTime = -1;
	public MobData data = null;

	public ZombieData(MobData data) {
		this.data = data;
	}

	public void load(CompoundTag compound) {
      	this.setBaby(compound.getBoolean("IsBaby"));
      	this.inWaterTime = compound.getInt("InWaterTime");
      	if (compound.contains("DrownedConversionTime", 99) && compound.getInt("DrownedConversionTime") > -1)
         	this.startUnderWaterConversion(compound.getInt("DrownedConversionTime"));

      	if (compound.contains("ConversionTime", 99) && compound.getInt("ConversionTime") > -1)
         	this.startConverting(compound.hasUUID("ConversionPlayer") ? compound.getUUID("ConversionPlayer") : null, compound.getInt("ConversionTime"));
	}

	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
      	compound.putBoolean("IsBaby", this.isBaby());
      	if(data.player != null)
      		compound.putInt("InWaterTime", data.player.isInWater() ? this.inWaterTime : -1);
     	compound.putInt("DrownedConversionTime", this.isUnderWaterConverting() ? this.conversionTime : -1);
     	compound.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
      	if (this.conversionStarter != null)
         	compound.putUUID("ConversionPlayer", this.conversionStarter);
		return compound;
	}

	public CompoundTag saveVillager() {
		CompoundTag compound = new CompoundTag();
		return compound;
	}
	
	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder zombieBuilder = Zombie.createAttributes();
		playerBuilder.combine(zombieBuilder);
		return playerBuilder;
	}

	public double speedMultiplier() {
		return 0.25;
	}

	public AbstractZombieRenderer getRenderer(EntityRendererProvider.Context context) {
		if(data.renderAs == EntityType.HUSK)
			return new HuskRenderer(context);
		if(data.renderAs == EntityType.DROWNED)
			return new DrownedRenderer(context);
		return new ZombieRenderer(context);
	}

	public boolean hasHand() {
		return true;
	}

	public void setupHand(HandData hand) {
		String texture = "textures/entity/zombie/";
		texture += data.mob == EntityType.ZOMBIE ? "zombie.png" : (data.mob == EntityType.HUSK ? "husk.png" : "drowned.png");
		hand.texture = new ResourceLocation(texture);
		hand.setScale(1f, 1f, 1f);
		hand.setPosition(5.25f, 21.5f, -1.1f);
		hand.setRotation(3.2f, 1.6f, 0.15f);
		if(data.mob == EntityType.DROWNED)
			hand.outerTexture = new ResourceLocation("textures/entity/zombie/drowned_outer_layer.png");
	}

	public void respawn() {
		this.data.changeMob(EntityType.ZOMBIE, null);
	}

	public float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return this.isBaby() ? 0.93F : 1.74F;
	}

	public boolean isShaking() {
		return this.isUnderWaterConverting() || this.isConverting();
	}

   	public boolean isUnderWaterConverting() {
      	return this.drownedConverting;
   	}

   	public boolean isBaby() {
      	return this.isBaby;
   	}

   	public void setBaby(boolean baby) {
      	this.isBaby = baby;
      	if (data.world != null && !data.world.isClientSide) {
         	AttributeInstance attributeinstance = data.player.getAttribute(Attributes.MOVEMENT_SPEED);
         	attributeinstance.removeModifier(SpeedModifer);
         	if (baby)
            	attributeinstance.addTransientModifier(SpeedModifer);
      	}
   	}

   	public boolean convertsInWater() {
      	return data.mob != EntityType.DROWNED && data.mob != EntityType.ZOMBIE_VILLAGER;
   	}

   	public void usedItem(LivingEntityUseItemEvent.Finish event) {
		ItemStack stack = event.getItem();
		if(stack.getItem() == Items.ROTTEN_FLESH) {
			FoodData food = data.player.getFoodData();
			food.setFoodLevel(food.getFoodLevel() + 4);
			food.setSaturation(food.getSaturationLevel() + 0.7f);
			if(data.player.hasEffect(MobEffects.HUNGER))
				data.player.removeEffect(MobEffects.HUNGER);
		}
   	}

   	public void itemInteraction(PlayerInteractEvent.RightClickItem event) {
		ItemStack stack = data.player.getItemInHand(event.getHand());
		if(!this.isConverting() && data.player.hasEffect(MobEffects.WEAKNESS) && stack.is(Items.GOLDEN_APPLE)) {
			if (!data.player.getAbilities().instabuild)
				stack.shrink(1);
			this.startConverting(data.player.getUUID(), data.world.random.nextInt(2401) + 3600);
		} else if(stack.getItem().isEdible() && !stack.getFoodProperties(data.player).isMeat()) {
			event.setCanceled(true);
		}
   	}

   	public void killedEntity(LivingDeathEvent event) {
		DamageSource source = event.getSource();
		if(source.isIndirect())
			return;

		Player player = data.player;
		if(player.level() instanceof ServerLevel world) {
			boolean canConvert = world.getDifficulty() == Difficulty.NORMAL || world.getDifficulty() == Difficulty.HARD;
			if(event.getEntity() instanceof Villager villager && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(villager, EntityType.ZOMBIE_VILLAGER, (timer) -> {})) {
         		FoodData food = player.getFoodData();
				food.setFoodLevel(food.getFoodLevel() + 8);
				food.setSaturation(food.getSaturationLevel() + 0.8f);
         		if(!canConvert || (world.getDifficulty() != Difficulty.HARD && world.random.nextBoolean()))
         			return;

         		ZombieVillager zombievillager = villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
         		if(zombievillager != null) {
            		zombievillager.finalizeSpawn(world, world.getCurrentDifficultyAt(zombievillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true), (CompoundTag)null);
            		zombievillager.setVillagerData(villager.getVillagerData());
            		zombievillager.setGossips(villager.getGossips().store(NbtOps.INSTANCE));
            		zombievillager.setTradeOffers(villager.getOffers().createTag());
            		zombievillager.setVillagerXp(villager.getVillagerXp());
         			net.minecraftforge.event.ForgeEventFactory.onLivingConvert(villager, zombievillager);
               		world.levelEvent((Player)null, 1026, player.blockPosition(), 0);
         		}
      		} else if(event.getEntity() instanceof Horse horse && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(horse, EntityType.ZOMBIE_HORSE, (timer) -> {})) {
				FoodData food = player.getFoodData();
				food.setFoodLevel(food.getFoodLevel() + 8);
				food.setSaturation(food.getSaturationLevel() + 0.8f);
         		if(!canConvert || (world.getDifficulty() != Difficulty.HARD && world.random.nextBoolean()))
         			return;

         		ZombieHorse zombiehorse = horse.convertTo(EntityType.ZOMBIE_HORSE, false);
         		if(zombiehorse != null) {
         			AgeableMob.AgeableMobGroupData ageableGroup = horse.isBaby() ? new AgeableMob.AgeableMobGroupData(100f) : new AgeableMob.AgeableMobGroupData(false);
            		zombiehorse.finalizeSpawn(world, world.getCurrentDifficultyAt(zombiehorse.blockPosition()), MobSpawnType.CONVERSION, ageableGroup, (CompoundTag)null);
         			net.minecraftforge.event.ForgeEventFactory.onLivingConvert(horse, zombiehorse);
               		world.levelEvent((Player)null, 1026, player.blockPosition(), 0);
      				zombiehorse.setOwnerUUID(player.getUUID());
      				zombiehorse.setTamed(true);
         		}
      		} else if(event.getEntity() instanceof Player victim) {
      			MobData data = MobData.get(victim);
      			if(data.mob == EntityType.PLAYER) {
					FoodData food = player.getFoodData();
					food.setFoodLevel(food.getFoodLevel() + 8);
					food.setSaturation(food.getSaturationLevel() + 0.8f);
               		victim.level().levelEvent((Player)null, 1026, victim.blockPosition(), 0);
					data.changeMob(this.data.mob, null);
      			}
      		}
		}
	}

	public void entityInteraction(PlayerInteractEvent.EntityInteract event) {
		if(event.getTarget() instanceof AbstractVillager)
			event.setCanceled(true);
	}

   	public MobType getMobType() {
   		return MobType.UNDEAD;
   	}

   	public void tick() {
		int maxAir = data.player.getMaxAirSupply();
		if(!data.world.isClientSide)
			data.player.setAirSupply(maxAir);
		
   		if(data.player.isAlive()) {
			boolean flag = this.isSunSensitive() && data.isSunBurnTick();
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
      			if (this.isConverting()) {
         			int i = this.getConversionProgress();
         			this.villagerConversionTime -= i;
         			if (this.villagerConversionTime <= 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(data.player, EntityType.VILLAGER, (timer) -> this.villagerConversionTime = timer))
            			this.finishConversion((ServerLevel)data.world);
      			}
      		
         		if (this.isUnderWaterConverting()) {
            		--this.conversionTime;
            		if (this.conversionTime < 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(data.player, EntityType.DROWNED, (timer) -> this.conversionTime = timer))
               			this.doUnderWaterConversion();
					data.player.setAirSupply(-20);
         		} else if (this.convertsInWater()) {
           			if (data.player.isEyeInFluid(FluidTags.WATER)) {
               			++this.inWaterTime;
               			if (this.inWaterTime >= 600)
                  			this.startUnderWaterConversion(300);
						data.player.setAirSupply(300 - (this.inWaterTime / 2));
            		} else {
               			this.inWaterTime = -1;
            		}
         		}
      		}
   		}
  	}

   	public void startUnderWaterConversion(int time) {
      	this.conversionTime = time;
      	this.drownedConverting = true;
      	data.sync();
   	}

   	public void doUnderWaterConversion() {
      	data.changeMob(data.mob == EntityType.HUSK ? EntityType.ZOMBIE : EntityType.DROWNED, this.save());
        data.world.levelEvent((Player)null, 1040, data.player.blockPosition(), 0);
   	}

   	public boolean isSunSensitive() {
      	return data.mob != EntityType.HUSK;
   	}

   	public boolean isConverting() {
      	return this.converting;
   	}

   	public void startConverting(@Nullable UUID uuid, int time) {
      	this.conversionStarter = uuid;
      	this.villagerConversionTime = time;
      	this.converting = true;
      	data.player.removeEffect(MobEffects.WEAKNESS);
      	data.player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, time, Math.min(data.world.getDifficulty().getId() - 1, 0)));
      	data.world.playLocalSound(data.player.getX(), data.player.getEyeY(), data.player.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, data.player.getSoundSource(), 1.0F + data.world.random.nextFloat(), data.world.random.nextFloat() * 0.7F + 0.3F, false);
   	}

   	public void finishConversion(ServerLevel server) {
     	if (this.conversionStarter != null) {
         	if (server.getPlayerByUUID(this.conversionStarter) instanceof ServerPlayer serverPlayer) {
            	Advancement advancement = serverPlayer.server.getAdvancements().getAdvancement(new ResourceLocation("minecraft:story/cure_zombie_villager"));
            	serverPlayer.getAdvancements().award(advancement, "cured_zombie_villager");
         	}
      	}

		data.changeMob(data.mob == EntityType.ZOMBIE_VILLAGER ? EntityType.VILLAGER : EntityType.PLAYER, this.saveVillager());
      	data.player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        server.levelEvent((Player)null, 1027, data.player.blockPosition(), 0);
   	}

  	public int getConversionProgress() {
      	int i = 1;
      	if (data.world.random.nextFloat() < 0.01F) {
         	int j = 0;
         	BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
         	for(int k = (int)data.player.getX() - 4; k < (int)data.player.getX() + 4 && j < 14; ++k) {
            	for(int l = (int)data.player.getY() - 4; l < (int)data.player.getY() + 4 && j < 14; ++l) {
               		for(int i1 = (int)data.player.getZ() - 4; i1 < (int)data.player.getZ() + 4 && j < 14; ++i1) {
                  		BlockState blockstate = data.world.getBlockState(blockpos$mutableblockpos.set(k, l, i1));
                  		if (blockstate.is(Blocks.IRON_BARS) || blockstate.getBlock() instanceof BedBlock) {
                     		if (data.world.random.nextFloat() < 0.3F)
                        		++i;
                     		++j;
                  		}
               		}
            	}
         	}
      	}

      	return i;
   	}

	public boolean canSprint() {
		return data.player.isUnderWater() && data.mob == EntityType.DROWNED;
	}

	public boolean canCrouch() {
		return false;
	}

	public boolean preventSleeping() {
		return true;
	}
}
