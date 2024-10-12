package playasmob;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntityType;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

@Mod.EventBusSubscriber
public class Events {
	
	@SubscribeEvent
	public static void playerRender(RenderPlayerEvent.Pre event) {
		Player player = event.getEntity();
		MobData data = MobData.get(player);
		if(data.renderAs == EntityType.PLAYER)
			return;
		
		Minecraft minecraft = Minecraft.getInstance();
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		EntityRenderDispatcher entityDispatcher = minecraft.getEntityRenderDispatcher();
		BlockRenderDispatcher blockDispatcher = minecraft.getBlockRenderer();
		ItemInHandRenderer itemHandRenderer = new ItemInHandRenderer(minecraft, entityDispatcher, itemRenderer);
		EntityRendererProvider.Context context = new EntityRendererProvider.Context(entityDispatcher, itemRenderer, blockDispatcher, itemHandRenderer, minecraft.getResourceManager(), minecraft.getEntityModels(), minecraft.font);
		EntityRenderer renderer = data.getRenderer(context);
		if(renderer == null)
			return;
		
		MultiBufferSource buffer = event.getMultiBufferSource();
		int packedLight = event.getPackedLight();
		float partialTick = event.getPartialTick();
		PoseStack pose = event.getPoseStack();
		renderer.render((LocalPlayer) player, 0, partialTick, pose, buffer, packedLight);
		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void armRender(RenderArmEvent event) {
		Player player = event.getPlayer();
		MobData data = MobData.get(player);
		if(data.typeData == null)
			return;
			
		event.setCanceled(data.debug != true);
		if(!data.typeData.hasHand())
			return;
		
		Minecraft minecraft = Minecraft.getInstance();
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		EntityRenderDispatcher entityDispatcher = minecraft.getEntityRenderDispatcher();
		BlockRenderDispatcher blockDispatcher = minecraft.getBlockRenderer();
		ItemInHandRenderer itemHandRenderer = new ItemInHandRenderer(minecraft, entityDispatcher, itemRenderer);
		EntityRendererProvider.Context context = new EntityRendererProvider.Context(entityDispatcher, itemRenderer, blockDispatcher, itemHandRenderer, minecraft.getResourceManager(), minecraft.getEntityModels(), minecraft.font);
		HandData hand = data.typeData.getHand(context);
		if(hand == null || hand.model == null)
			return;
	
		HandRenderer renderer = new HandRenderer(context, hand.model);
		MultiBufferSource buffer = event.getMultiBufferSource();
		int packedLight = event.getPackedLight();
		PoseStack pose = event.getPoseStack();
		renderer.render((LocalPlayer) player, 0, 0, pose, buffer, packedLight);
		
		if(hand.outerTexture != null && hand.outerModel != null) {
			HandRenderer outer = new HandRenderer(context, hand.outerModel);
			outer.render((LocalPlayer) player, 0, 0, pose, buffer, packedLight);
		}
	}

	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		CommandBuildContext context = event.getBuildContext();
		event.getDispatcher().register(Commands.literal("mob").requires(s -> s.hasPermission(2)).then(Commands.literal("set").then(Commands.argument("mob", ResourceArgument.resource(context, Registries.ENTITY_TYPE)).suggests(GlobalData.suggest()).executes(arguments -> {
			Player player = (Player)arguments.getSource().getEntity();
			if(player == null)
				return 0;
			MobData data = MobData.get(player);
			EntityType mob = getType(ResourceArgument.getSummonableEntityType(arguments, "mob"));
			if(data.mob != mob)
				data.changeMob(mob, null);
			return 15;
		}))));

		event.getDispatcher().register(Commands.literal("player").requires(s -> s.hasPermission(2)).executes(arguments -> {
			Player player = (Player)arguments.getSource().getEntity();
			if(player == null)
				return 0;
			MobData data = MobData.get(player);
			if(data.mob != EntityType.PLAYER)
				data.changeMob(EntityType.PLAYER, null);
			return 15;
		}));

		event.getDispatcher().register(Commands.literal("hand").requires(s -> s.hasPermission(2)).then(Commands.literal("rotation").then(Commands.argument("x", FloatArgumentType.floatArg()).then(Commands.argument("y", FloatArgumentType.floatArg()).then(Commands.argument("z", FloatArgumentType.floatArg()).executes(arguments -> {
			Player player = (Player)arguments.getSource().getEntity();
			if(player == null)
				return 0;
			MobData data = MobData.get(player);
			data.xRot = FloatArgumentType.getFloat(arguments, "x");
			data.yRot = FloatArgumentType.getFloat(arguments, "y");
			data.zRot = FloatArgumentType.getFloat(arguments, "z");
			data.sync();
			return 15;
		}))))).then(Commands.literal("position").then(Commands.argument("x", FloatArgumentType.floatArg()).then(Commands.argument("y", FloatArgumentType.floatArg()).then(Commands.argument("z", FloatArgumentType.floatArg()).executes(arguments -> {
			Player player = (Player)arguments.getSource().getEntity();
			if(player == null)
				return 0;
			MobData data = MobData.get(player);
			data.xPos = FloatArgumentType.getFloat(arguments, "x");
			data.yPos = FloatArgumentType.getFloat(arguments, "y");
			data.zPos = FloatArgumentType.getFloat(arguments, "z");
			data.sync();
			return 15;
		}))))).then(Commands.literal("scale").then(Commands.argument("x", FloatArgumentType.floatArg()).then(Commands.argument("y", FloatArgumentType.floatArg()).then(Commands.argument("z", FloatArgumentType.floatArg()).executes(arguments -> {
			Player player = (Player)arguments.getSource().getEntity();
			if(player == null)
				return 0;
			MobData data = MobData.get(player);
			data.xScale = FloatArgumentType.getFloat(arguments, "x");
			data.yScale = FloatArgumentType.getFloat(arguments, "y");
			data.zScale = FloatArgumentType.getFloat(arguments, "z");
			data.sync();
			return 15;
		}))))).then(Commands.literal("debug").then(Commands.argument("toggle", BoolArgumentType.bool()).executes(arguments -> {
			Player player = (Player)arguments.getSource().getEntity();
			if(player == null)
				return 0;
			MobData data = MobData.get(player);
			data.debug = BoolArgumentType.getBool(arguments, "toggle");
			data.sync();
			return 15;
		}))));
	}

	public static EntityType getType(Holder.Reference<EntityType<?>> holder) {
		Optional<EntityType<?>> type = EntityType.byString(holder.key().location().toString());
		if(type.isPresent())
			return type.get();
		return EntityType.PLAYER;
	}
	
	@SubscribeEvent
	public static void hitbox(EntityEvent.Size event) {
		if(event.getEntity() instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.mob == EntityType.PLAYER)
				return;
			
			EntityDimensions dimensions = data.mob.getDimensions();
			event.setNewSize(dimensions, true);
			if(data.typeData != null)
				event.setNewEyeHeight(data.typeData.getEyeHeight(player.getPose(), dimensions));
			player.setShiftKeyDown(!player.isShiftKeyDown());
			PlayasmobMod.queueServerWork(1, () -> {
				player.setShiftKeyDown(!player.isShiftKeyDown());
			});
		}
	}

	@SubscribeEvent
	public static void input(InputEvent.Key event) {
		Player player = Minecraft.getInstance().player;
		if(player == null)
			return;
		MobData data = MobData.get(player);
		if(data.mob == EntityType.PLAYER)
			return;
		
		Options options = Minecraft.getInstance().options;
		KeyMapping crouch = options.keyShift;
		KeyMapping sprint = options.keySprint;
		KeyMapping jump = options.keyJump;
		int key = event.getKey();
		if(player.getAbilities().flying)
			return;

		if(key == sprint.getKey().getValue() && !data.typeData.canSprint())
			negateInput(sprint);
		if(key == jump.getKey().getValue() && !data.typeData.canJump())
			negateInput(jump);
		if(key == crouch.getKey().getValue() && !player.isPassenger() && !data.typeData.canCrouch())
			negateInput(crouch);
	}

	public static void negateInput(KeyMapping key) {
		if(key instanceof ToggleKeyMapping toggleKey && toggleKey.isDown())
			toggleKey.setDown(true);
		key.setDown(false);
	}
	
	@SubscribeEvent
	public static void sleep(PlayerSleepInBedEvent event) {
		if(MobData.get(event.getEntity()).typeData.preventSleeping() == true)
			event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
	}

	@SubscribeEvent
	public static void tick(TickEvent.PlayerTickEvent event) {
		Player player = event.player;
		MobData data = MobData.get(player);
		if(data.typeData != null) {
			if(player.isSprinting() && !data.typeData.canSprint())
				player.setSprinting(false);
			if(player.isCrouching() && !data.typeData.canCrouch())
				player.setShiftKeyDown(false);
		}
		
		if(event.phase != TickEvent.Phase.START)
			return;
		if(data.typeData != null)
			data.typeData.tick();
	}

	@SubscribeEvent
	public static void thunderHit(EntityStruckByLightningEvent event) {
		if(event.getEntity() instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.typeData != null && player.level() instanceof ServerLevel server)
				data.typeData.thunderHit(server, event.getLightning());
		}
	}
	
	@SubscribeEvent
	public static void pickupItem(EntityItemPickupEvent event) {
		MobData data = MobData.get(event.getEntity());
		if(data.typeData != null)
			event.setCanceled(!data.typeData.canPickup(event.getItem().getItem()));
	}
	
	@SubscribeEvent
	public static void usedItem(LivingEntityUseItemEvent.Finish event) {
		if(event.getEntity() instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.typeData != null)
				data.typeData.usedItem(event);
		}
	}
	
	@SubscribeEvent
	public static void useItem(LivingEntityUseItemEvent.Start event) {
		if(event.getEntity() instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.typeData != null)
				data.typeData.useItem(event);
		}
	}
	
	@SubscribeEvent
	public static void itemInteraction(PlayerInteractEvent.RightClickItem event) {
		MobData data = MobData.get(event.getEntity());
		if(data.typeData != null)
			data.typeData.itemInteraction(event);
	}
	
	@SubscribeEvent
	public static void entityInteraction(PlayerInteractEvent.EntityInteract event) {
		MobData data = MobData.get(event.getEntity());
		if(data.typeData != null)
			data.typeData.entityInteraction(event);
	}

	@SubscribeEvent
	public static void killed(LivingDeathEvent event) {
		if(event.getEntity() instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.typeData != null)
				data.typeData.killed(event);
		}

		if(event.getSource().getDirectEntity() instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.typeData != null)
				data.typeData.killedEntity(event);
		}
	}

	@SubscribeEvent
	public static void hurt(LivingHurtEvent event) {
		if(event.getEntity() instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.typeData != null)
				data.hurt(event);
		}

		if(event.getSource().getDirectEntity() instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.typeData != null)
				data.typeData.hurtEntity(event);
		}
	}

	@SubscribeEvent
	public static void attacked(LivingAttackEvent event) {
		if(event.getEntity() instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.typeData != null)
				data.attacked(event);
		}
	}

	@SubscribeEvent
	public static void causeFallDamage(LivingFallEvent event) {
		if(event.getEntity() instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.typeData != null)
				data.typeData.causeFallDamage(event);
		}
	}

	@SubscribeEvent
	public static void preventEffect(MobEffectEvent.Applicable event) {
		if(event.getEntity() instanceof Player player) {
			MobData data = MobData.get(player);
			if(!data.allowEffect(event.getEffectInstance()))
				event.setResult(Event.Result.DENY);
		}
	}

	@SubscribeEvent
	public static void getProjectile(LivingGetProjectileEvent event) {
		if(event.getEntity() instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.typeData != null)
				data.typeData.getProjectile(event);
		}
	}
}
