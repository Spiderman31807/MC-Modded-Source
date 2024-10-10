package zombiemode;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.event.TickEvent;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;

import net.minecraft.client.Options;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.food.FoodData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.Commands;
import net.minecraft.tags.FluidTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.client.model.geom.ModelLayers;

@Mod.EventBusSubscriber
public class Events {

	@SubscribeEvent
	public static void entityJoin(EntityEvent.EntityConstructing event) {
		final Entity entity = event.getEntity();
		ZombieModeMod.queueServerWork(1, () -> {
			modifyGoals(entity);
		});
	}

	public static void modifyGoals(Entity entity) {
		if(entity instanceof Zombie zombie) {
			Set<WrappedGoal> wrapped = zombie.targetSelector.getAvailableGoals();
			zombie.targetSelector.removeGoal(wrapped.toArray(new WrappedGoal[wrapped.size()])[1].getGoal());
			zombie.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(zombie, Player.class, true, (player) -> !ZombieUtils.isZombie(player)));
		} else if(entity instanceof AbstractGolem golem) {
      		golem.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(golem, Player.class, true, (player) -> ZombieUtils.isZombie(player)));
		}
	}
	
	@SubscribeEvent
	public static void playerRender(RenderPlayerEvent.Pre event) {
		Player player = event.getEntity();
		ZombieData data = ZombieUtils.getData(player);
		if(!data.isZombie())
			return;
		
		MultiBufferSource buffer = event.getMultiBufferSource();
		int packedLight = event.getPackedLight();
		float partialTick = event.getPartialTick();
		PoseStack pose = event.getPoseStack();
		event.setCanceled(true);

		Minecraft minecraft = Minecraft.getInstance();
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		EntityRenderDispatcher entityDispatcher = minecraft.getEntityRenderDispatcher();
		BlockRenderDispatcher blockDispatcher = minecraft.getBlockRenderer();
		ItemInHandRenderer itemHandRenderer = new ItemInHandRenderer(minecraft, entityDispatcher, itemRenderer);
		EntityRendererProvider.Context context = new EntityRendererProvider.Context(entityDispatcher, itemRenderer, blockDispatcher, itemHandRenderer, minecraft.getResourceManager(), minecraft.getEntityModels(), minecraft.font); 
		AbstractZombieRenderer renderer = data.getRenderer(context);
		renderer.render((LocalPlayer) player, 0, partialTick, pose, buffer, packedLight);
	}

	@SubscribeEvent
	public static void armRender(RenderArmEvent event) {
		Player player = event.getPlayer();
		ZombieData data = ZombieUtils.getData(player);
		if(!data.isZombie())
			return;
			
		Minecraft minecraft = Minecraft.getInstance();
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		EntityRenderDispatcher entityDispatcher = minecraft.getEntityRenderDispatcher();
		BlockRenderDispatcher blockDispatcher = minecraft.getBlockRenderer();
		ItemInHandRenderer itemHandRenderer = new ItemInHandRenderer(minecraft, entityDispatcher, itemRenderer);
		EntityRendererProvider.Context context = new EntityRendererProvider.Context(entityDispatcher, itemRenderer, blockDispatcher, itemHandRenderer, minecraft.getResourceManager(), minecraft.getEntityModels(), minecraft.font);
		HandRenderer renderer = new HandRenderer(context, new HandModel(context.bakeLayer(ModelLayers.ZOMBIE), false));
		MultiBufferSource buffer = event.getMultiBufferSource();
		int packedLight = event.getPackedLight();
		PoseStack pose = event.getPoseStack();
		renderer.render((LocalPlayer) player, 0, 0, pose, buffer, packedLight);
		event.setCanceled(true);
		
		if(data.getHand().outerTexture != null) {
			HandRenderer outer = new HandRenderer(context, new HandModel(context.bakeLayer(ModelLayers.ZOMBIE), true));
			outer.render((LocalPlayer) player, 0, 0, pose, buffer, packedLight);
		}
	}
	
	@SubscribeEvent
	public static void hitbox(EntityEvent.Size event) {
		if(event.getEntity() instanceof Player player) {
			ZombieData data = ZombieUtils.getData(player);
			if(!data.isZombie())
				return;
			event.setNewSize(data.getDimensions(), false);
			event.setNewEyeHeight(1.74f);
		}
	}

	@SubscribeEvent
	public static void input(InputEvent.Key event) {
		Player player = Minecraft.getInstance().player;
		ZombieData data = ZombieUtils.getData(player);
		if(!data.isZombie())
			return;
		
		Options options = Minecraft.getInstance().options;
		KeyMapping crouch = options.keyShift;
		KeyMapping sprint = options.keySprint;
		KeyMapping jump = options.keyJump;
		int key = event.getKey();

		if(key == sprint.getKey().getValue() && !data.canSprint())
			negateInput(sprint);
		if(key == jump.getKey().getValue() && !data.canJump())
			negateInput(jump);
		if(key == crouch.getKey().getValue() && !data.canCrouch())
			negateInput(crouch);
	}

	public static void negateInput(KeyMapping key) {
		if(key instanceof ToggleKeyMapping toggleKey && toggleKey.isDown()) {
			toggleKey.setDown(true);
		}
		key.setDown(false);
	}
	
	@SubscribeEvent
	public static void sleep(PlayerSleepInBedEvent event) {
		if(ZombieUtils.isZombie(event.getEntity()))
			event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
	}
	
	@SubscribeEvent
	public static void entityInteraction(PlayerInteractEvent.EntityInteract event) {
		Entity target = event.getTarget();
		if(ZombieUtils.isZombie(event.getEntity())) {
			if(target instanceof AbstractVillager || (target instanceof Horse && !(target instanceof ZombieHorse)))
				event.setCanceled(true);
			if(target instanceof ZombieHorse mount && mount.getOwner() != event.getEntity())
				event.setCanceled(true);
		} else {
			if(target instanceof ZombieHorse mount)
				event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void respawn(PlayerEvent.PlayerRespawnEvent event) {
		if(event.isEndConquered())
			return;
		ZombieUtils.setup(event.getEntity(), EntityType.PLAYER);
	}
	
	@SubscribeEvent
	public static void login(PlayerEvent.PlayerLoggedInEvent event) {
		ZombieUtils.getData(event.getEntity()).loadAttributes(false);
	}
	
	@SubscribeEvent
	public static void eatFood(VanillaGameEvent event) {
		if(event.getVanillaEvent() == GameEvent.EAT) {
			if(event.getCause() instanceof Player player) {
				ZombieData data = ZombieUtils.getData(player);
				if(!data.isZombie())
					return;

				if(player.hasEffect(MobEffects.HUNGER)) {
					MobEffectInstance hungerEffect = player.getEffect(MobEffects.HUNGER);
					int hungerTime = hungerEffect.getDuration();
					if(hungerTime == 600) {
						player.removeEffect(MobEffects.HUNGER);
						FoodData food = player.getFoodData();
						food.setFoodLevel(food.getFoodLevel() + 4);
						food.setSaturation(food.getSaturationLevel() + 0.7f);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void itemInteraction(PlayerInteractEvent.RightClickItem event) {
		Player player = event.getEntity();
		ZombieData data = ZombieUtils.getData(player);
		if(!data.isZombie())
			return;

		ItemStack stack = player.getItemInHand(event.getHand());
		if(!data.isConverting() && player.hasEffect(MobEffects.WEAKNESS) && stack.is(Items.GOLDEN_APPLE)) {
			if (!player.getAbilities().instabuild)
				stack.shrink(1);
			data.startConverting(player.getUUID(), player.level().random.nextInt(2401) + 3600);
		} else if(stack.getItem().isEdible() && !stack.getFoodProperties(player).isMeat()) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void killed(LivingDeathEvent event) {
		DamageSource source = event.getSource();
		if(source.isIndirect())
			return;
			
		Entity killer = source.getDirectEntity();
		if(killer instanceof Player player && player.level() instanceof ServerLevel world) {
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
      		}
		}
	}

	@SubscribeEvent
	public static void hurt(LivingAttackEvent event) {
		if(event.getEntity() instanceof Player player) {
			ZombieData data = ZombieUtils.getData(player);
			if(!data.isZombie()) {
				humanHurt(player, event);
			} else {
				zombieHurt(player, event);
			}
		}
	}

	public static void humanHurt(Player player, LivingAttackEvent event) {
		DamageSource source = event.getSource();
		Entity killer = source.isIndirect() ? source.getEntity() : source.getDirectEntity();
		if(player.getHealth() - event.getAmount() > 0) {
			
		} else {
			if(killer instanceof Zombie) {
               	player.level().levelEvent((Player)null, 1026, player.blockPosition(), 0);
				ZombieUtils.setup(player, killer.getType());
				ZombieUtils.resetAttackers(player);
				event.setCanceled(true);
			} else if(killer instanceof Player attacker) {
				ZombieData data = ZombieUtils.getData(attacker);
				if(data.isZombie()) {
					FoodData food = attacker.getFoodData();
					food.setFoodLevel(food.getFoodLevel() + 8);
					food.setSaturation(food.getSaturationLevel() + 0.8f);
               		player.level().levelEvent((Player)null, 1026, player.blockPosition(), 0);
					ZombieUtils.setup(player, data.type);
					ZombieUtils.resetAttackers(player);
					event.setCanceled(true);
				}
			}
		}
	}

	public static void zombieHurt(Player player, LivingAttackEvent event) {
		DamageSource source = event.getSource();
		if(source.is(DamageTypes.DROWN))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("zombie").requires(s -> s.hasPermission(2)).then(Commands.argument("player", EntityArgument.player()).then(Commands.literal("cure").executes(arguments -> {
			Player player = EntityArgument.getPlayer(arguments, "player");
			ZombieUtils.setup(player, EntityType.PLAYER);
			return 0;
		})).then(Commands.literal("infect").executes(arguments -> {
			Player player = EntityArgument.getPlayer(arguments, "player");
			ZombieUtils.setup(player, EntityType.ZOMBIE);
			return 0;
		}).then(Commands.literal("husk").executes(arguments -> {
			Player player = EntityArgument.getPlayer(arguments, "player");
			ZombieUtils.setup(player, EntityType.HUSK);
			return 0;
		})).then(Commands.literal("drowned").executes(arguments -> {
			Player player = EntityArgument.getPlayer(arguments, "player");
			ZombieUtils.setup(player, EntityType.DROWNED);
			return 0;
		})))));
	}
		
	@SubscribeEvent
	public static void tick(TickEvent.PlayerTickEvent event) {
		if(event.phase == TickEvent.Phase.START) {
			Player player = event.player;
			if(player.level().isClientSide())
				return;
		
			ZombieData data = ZombieUtils.getData(player);
			if(!data.isZombie())
				return;
				
			if(player.isSprinting() && !data.canSprint())
				player.setSprinting(false);
			if(player.isCrouching() && !data.canCrouch())
				player.setShiftKeyDown(false);
			
      		if (!player.level().isClientSide && player.isAlive() && data.isConverting()) {
         		int i = data.getConversionProgress();
         		data.cureConversionTime -= i;
         		if (data.cureConversionTime <= 0)
            		data.finishConversion((ServerLevel)player.level());
      		}

			int maxAir = player.getMaxAirSupply();
			player.setAirSupply(maxAir);
			if (!player.level().isClientSide && player.isAlive()) {
         		if (data.isUnderWaterConverting()) {
            		--data.conversionTime;
            		if (data.conversionTime < 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(player, data.drownInto(), (timer) -> data.conversionTime = timer))
               			data.doUnderWaterConversion();
					player.setAirSupply(0);
         		} else if (data.convertsInWater()) {
            		if (player.isEyeInFluid(FluidTags.WATER)) {
               			++data.inWaterTime;
               			if (data.inWaterTime >= 600)
                  			data.startUnderWaterConversion(300);
						player.setAirSupply(300 - (data.inWaterTime / 2));
            		} else {
               			data.inWaterTime = -1;
            		}
         		}
      		}
      		
			if (player.isAlive()) {
         		boolean flag = data.isSunSensitive() && data.isSunBurnTick();
         		if (flag) {
            		ItemStack itemstack = player.getItemBySlot(EquipmentSlot.HEAD);
            		if (!itemstack.isEmpty()) {
               			if (itemstack.isDamageableItem()) {
                  			itemstack.setDamageValue(itemstack.getDamageValue() + data.world.random.nextInt(2));
                  			if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                     			player.broadcastBreakEvent(EquipmentSlot.HEAD);
                     			player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                  			}
              			}
               			flag = false;
            		}
            		
            		if (flag)
               			player.setSecondsOnFire(8);
         		}
      		}
		}
	}
}
