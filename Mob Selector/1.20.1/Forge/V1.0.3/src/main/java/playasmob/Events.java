package playasmob;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;
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
import net.minecraft.client.player.AbstractClientPlayer;
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
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.VanillaGameEvent;
import java.lang.reflect.Field;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import java.util.List;
import java.util.ArrayList;

@Mod.EventBusSubscriber
public class Events {

	public static EntityRendererProvider.Context getContext() {
		Minecraft minecraft = Minecraft.getInstance();
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		EntityRenderDispatcher entityDispatcher = minecraft.getEntityRenderDispatcher();
		BlockRenderDispatcher blockDispatcher = minecraft.getBlockRenderer();
		ItemInHandRenderer itemHandRenderer = new ItemInHandRenderer(minecraft, entityDispatcher, itemRenderer);
		EntityRendererProvider.Context context = new EntityRendererProvider.Context(entityDispatcher, itemRenderer, blockDispatcher, itemHandRenderer, minecraft.getResourceManager(), minecraft.getEntityModels(), minecraft.font);
		return context;
	}
	
	@SubscribeEvent
	public static void entityJoin(EntityEvent.EntityConstructing event) {
		PlayasmobMod.queueServerWork(1, () -> {
			GlobalUtils.modifyGoals(event.getEntity());
		});
	}

	@SubscribeEvent
	public static void playerRender(RenderPlayerEvent.Pre event) {
		Player player = event.getEntity();
		MobData data = MobData.get(player);
		if(data.renderAs == EntityType.PLAYER)
			return;
		
		EntityRendererProvider.Context context = getContext();
		EntityRenderer renderer = data.getRenderer(context);
		if(renderer == null)
			return;
		
		MultiBufferSource buffer = event.getMultiBufferSource();
		int packedLight = event.getPackedLight();
		float partialTick = event.getPartialTick();
		PoseStack pose = event.getPoseStack();
		renderer.render((AbstractClientPlayer) player, 0, partialTick, pose, buffer, packedLight);
		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void armRender(RenderArmEvent event) {
		Player player = event.getPlayer();
		MobData data = MobData.get(player);
		if(data.typeData == null)
			return;

		event.setCanceled(data.debug != true);
		if(!data.typeData.renderHand())
			return;
		
		EntityRendererProvider.Context context = getContext();
		HandData hand = data.typeData.getHand(context);
		if(hand == null || hand.model == null)
			return;
	
		HandRenderer renderer = new HandRenderer(context, hand.model.get());
		MultiBufferSource buffer = event.getMultiBufferSource();
		int packedLight = event.getPackedLight();
		PoseStack pose = event.getPoseStack();
		renderer.render((AbstractClientPlayer) player, 0, 0, pose, buffer, packedLight);
		
		if(hand.outerTexture != null && hand.outerModel != null) {
			HandRenderer outer = new HandRenderer(context, hand.outerModel.get());
			outer.render((AbstractClientPlayer) player, 0, 0, pose, buffer, packedLight);
		}
	}
	
	/*@SubscribeEvent
	public static void fovModify(ComputeFovModifierEvent event) {
		Player player = event.getPlayer();
		MobData data = MobData.get(player);
		if(data.mob == EntityType.PLAYER || data.typeData == null)
			return;
		
		float modifer = 1;
      	if(player.getAbilities().flying)
         	modifer *= 1.1;

		double speed = player.getAttributeValue(Attributes.MOVEMENT_SPEED) / data.typeData.fovMultiplier();
        modifer *= ((speed / (player.getAbilities().getWalkingSpeed() + 1)) / 2);
      	if(data.typeData.fovMultiplier() == 0 || player.getAbilities().getWalkingSpeed() == 0 && Float.isNaN(modifer) && Float.isInfinite(modifer))
      		modifer = 1;

		Options options = Minecraft.getInstance().options;
      	ItemStack stack = player.getUseItem();
      	if(player.isUsingItem()) {
         	if (stack.is(Items.BOW)) {
            	int tick = player.getTicksUsingItem();
            	float tickMod = (float)tick / 20;
            	if (tickMod > 1) {
               		tickMod = 1;
            	} else {
               		tickMod *= tickMod;
            	}

            	modifer *= 1 - tickMod * 0.15f;
         	} else if(options.getCameraType().isFirstPerson() && player.isScoping()) {
            	event.setNewFovModifier(0.1f);
            	return;
         	}	
      	}
      	
		float ModiferFOV = (float)Mth.lerp(options.fovEffectScale().get(), 1, modifer);
		event.setNewFovModifier(ModiferFOV);
	}*/

	@SubscribeEvent
	public static void renderInfoHud(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		Player entity = Minecraft.getInstance().player;
		if(entity == null)
			return;
			
		MobData data = MobData.get(entity);
		if(data.typeData == null)
			return;

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		data.typeData.renderInfo(event.getGuiGraphics(), w, h);
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
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
				data.changeMob(mob, null, true);
			return 15;
		}))).then(Commands.literal("menu").executes(arguments -> {
			Player player = (Player)arguments.getSource().getEntity();
			if(player == null)
				return 0;
			MobSelectionMenu.openSelector(player);
			return 15;
		})));

		event.getDispatcher().register(Commands.literal("player").requires(s -> s.hasPermission(2)).executes(arguments -> {
			Player player = (Player)arguments.getSource().getEntity();
			if(player == null)
				return 0;
			MobData data = MobData.get(player);
			if(data.mob != EntityType.PLAYER)
				data.changeMob(EntityType.PLAYER, null, true);
			player.refreshDimensions();
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
			data.sync(false, false);
			return 15;
		}))))).then(Commands.literal("position").then(Commands.argument("x", FloatArgumentType.floatArg()).then(Commands.argument("y", FloatArgumentType.floatArg()).then(Commands.argument("z", FloatArgumentType.floatArg()).executes(arguments -> {
			Player player = (Player)arguments.getSource().getEntity();
			if(player == null)
				return 0;
			MobData data = MobData.get(player);
			data.xPos = FloatArgumentType.getFloat(arguments, "x");
			data.yPos = FloatArgumentType.getFloat(arguments, "y");
			data.zPos = FloatArgumentType.getFloat(arguments, "z");
			data.sync(false, false);
			return 15;
		}))))).then(Commands.literal("scale").then(Commands.argument("x", FloatArgumentType.floatArg()).then(Commands.argument("y", FloatArgumentType.floatArg()).then(Commands.argument("z", FloatArgumentType.floatArg()).executes(arguments -> {
			Player player = (Player)arguments.getSource().getEntity();
			if(player == null)
				return 0;
			MobData data = MobData.get(player);
			data.xScale = FloatArgumentType.getFloat(arguments, "x");
			data.yScale = FloatArgumentType.getFloat(arguments, "y");
			data.zScale = FloatArgumentType.getFloat(arguments, "z");
			data.sync(false, false);
			return 15;
		}))))).then(Commands.literal("debug").then(Commands.argument("toggle", BoolArgumentType.bool()).executes(arguments -> {
			Player player = (Player)arguments.getSource().getEntity();
			if(player == null)
				return 0;
			MobData data = MobData.get(player);
			data.debug = BoolArgumentType.getBool(arguments, "toggle");
			data.sync(false, false);
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
	@SuppressWarnings("removal")
	public static void hitbox(EntityEvent.Size event) {
		if(event.getEntity() instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.renderAs == EntityType.PLAYER)
				return;
			
			EntityDimensions dimensions = data.typeRender.getDimensions(player.getPose());
			if(dimensions == null)
				dimensions = data.renderAs.getDimensions();
			event.setNewSize(dimensions, true);
			if(data.typeData != null)
				event.setNewEyeHeight(data.typeData.getEyeHeight(player.getPose(), dimensions));
			player.setShiftKeyDown(!player.isShiftKeyDown());
			PlayasmobMod.queueServerWork(1, () -> {
				player.setShiftKeyDown(!player.isShiftKeyDown());
			});
		}
	}
	
	/*@SubscribeEvent
	public static void vanilla(VanillaGameEvent event) {
		if(event.getCause() instanceof Player player)
			MobData.get(player).vanillaEvent(event);
	}*/
	
	@SubscribeEvent
	public static void endermanStare(EnderManAngerEvent event) {
		if(MobData.get(event.getPlayer()).typeData instanceof EndermanData)
			event.setCanceled(true);
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
		
		if(event.phase == TickEvent.Phase.START) {
			if(data.typeData != null)
				data.preTick();
		} else {
			if(data.typeData != null)
				data.postTick();
		}
	}

	@SubscribeEvent
	public static void entityTarget(LivingChangeTargetEvent event) {
		if(event.getNewTarget() instanceof Player player) {
			MobData data = MobData.get(player);
			if(data.typeData != null && !data.typeData.allowTargeting(event.getEntity()))
				event.setCanceled(true);
		}
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
			data.entityInteraction(event);

		if(event.getTarget() instanceof Player player) {
			MobData targetData = MobData.get(player);
			if(targetData.typeData != null)
				targetData.typeData.mobInteract(player, event.getHand());
		}
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
		if(event.getEntity() instanceof Player player)
			MobData.get(player).hurt(event);

		if(event.getSource().getDirectEntity() instanceof Player player)
			MobData.get(player).hurtEntity(event);
	}

	@SubscribeEvent
	public static void attacked(LivingAttackEvent event) {
		if(event.getEntity() instanceof Player player)
			MobData.get(player).attacked(event);
			
		if(event.getSource().getDirectEntity() instanceof Player player)
			MobData.get(player).attackEntity(event);
	}

	@SubscribeEvent
	public static void healed(LivingHealEvent event) {
		if(event.getEntity() instanceof Player player)
			MobData.get(player).healed(event);
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
	public static void addEffect(MobEffectEvent.Added event) {
		if(event.getEntity() instanceof Player player)
			MobData.get(player).addEffect(event);
	}

	@SubscribeEvent
	public static void preventEffect(MobEffectEvent.Applicable event) {
		if(event.getEntity() instanceof Player player) {
			if(!GlobalData.canPlayerHaveEffect(event.getEffectInstance().getEffect())) {
				event.setResult(Event.Result.DENY);
				return;
			}
			
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