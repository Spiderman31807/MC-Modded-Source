package mobfactions;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.level.LevelEvent;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Optional;
import java.util.Set;

import net.minecraft.network.chat.Component;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;

@Mod.EventBusSubscriber
public class Events {
	@SubscribeEvent
	public static void worldLoad(LevelEvent.Load event) {
		if(event.getLevel() instanceof Level world && !Factions.isLoaded)
			Factions.load(FactionData.MapVariables.get(world));
	}

	@SubscribeEvent
	public static void worldUnload(LevelEvent.Unload event) {
		if(event.getLevel() instanceof Level world && Factions.isLoaded)
			Factions.save(FactionData.MapVariables.get(world));
	}

	public static int goalPriority(Mob mob) {
		Set<WrappedGoal> goals = mob.goalSelector.getAvailableGoals();
		for(WrappedGoal wrapped : goals) {
			if(wrapped.getGoal() instanceof NearestAttackableTargetGoal)
				return wrapped.getPriority();
			if(wrapped.getGoal() instanceof HurtByTargetGoal)
				return wrapped.getPriority() + 1;
		}
		return goals.size();
	}

	@SubscribeEvent
	public static void entitySetup(EntityJoinLevelEvent event) {
		if(event.getEntity() instanceof Mob mob)
			mob.goalSelector.addGoal(goalPriority(mob), new FactionAttackGoal(mob, true, true));
	}

	@SubscribeEvent
	public static void entityTick(LivingEvent.LivingTickEvent event) {
		if(event.getEntity() instanceof Mob mob && mob.getTarget() != null) {
			LivingEntity target = mob.getTarget();
			if(target.isDeadOrDying() || target.isRemoved())
				mob.setTarget(null);
		}
	}

	@SubscribeEvent
	public static void targetChange(LivingChangeTargetEvent event) {
		if(event.getEntity() instanceof Mob mob && event.getNewTarget() instanceof Mob target && Factions.isAlly(mob, target))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void hurt(LivingDamageEvent event) {
		if(event.getEntity() instanceof Mob mob && Factions.has(mob)) {
      		double distance = mob.getAttributeValue(Attributes.FOLLOW_RANGE);
      		Entity attacker = event.getSource().getDirectEntity();
      		if(attacker instanceof LivingEntity target) {
      			AABB aabb = AABB.unitCubeFromLowerCorner(mob.position()).inflate(distance, 10.0D, distance);
      			for(Mob ally : mob.level().getEntitiesOfClass(Mob.class, aabb)) {
      				if(mob == ally || !Factions.isAlly(mob, ally) || (target instanceof Mob mobTarget && Factions.isAlly(ally, mobTarget)))
      					continue;
      				ally.setTarget(target);
      			}
      		}
		}
	}
	
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		CommandBuildContext context = event.getBuildContext();
		event.getDispatcher().register(Commands.literal("faction").requires(s -> s.hasPermission(2)).then(Commands.literal("reset").executes(arguments -> {	
			FactionData.MapVariables factionData = FactionData.MapVariables.get(arguments.getSource().getLevel());
			factionData.reset();
			Factions.load(factionData);
			if(arguments.getSource().getEntity() instanceof Player player)
				player.displayClientMessage(Component.translatable("faction.reset"), false);
			return 0;
		})).then(Commands.literal("get").executes(arguments -> {	
			if(arguments.getSource().getEntity() instanceof Player player)
				player.displayClientMessage(Factions.displayData(), false);
			return 0;
		}).then(Commands.argument("Faction", StringArgumentType.string()).suggests(Factions.suggest()).executes(arguments -> {
			if(arguments.getSource().getEntity() instanceof Player player)
				player.displayClientMessage(Factions.displayData(StringArgumentType.getString(arguments, "Faction")), false);
			return 0;
		}).then(Commands.literal("entities").executes(arguments -> {
			if(arguments.getSource().getEntity() instanceof Player player)
				player.displayClientMessage(Factions.displayEntities(StringArgumentType.getString(arguments, "Faction"), true), false);
			return 0;
		})).then(Commands.literal("enemies").executes(arguments -> {
			if(arguments.getSource().getEntity() instanceof Player player)
				player.displayClientMessage(Factions.displayEnemies(StringArgumentType.getString(arguments, "Faction"), true), false);
			return 0;
		})).then(Commands.literal("allies").executes(arguments -> {
			if(arguments.getSource().getEntity() instanceof Player player)
				player.displayClientMessage(Factions.displayAllies(StringArgumentType.getString(arguments, "Faction"), true), false);
			return 0;
		})))).then(Commands.literal("create").then(Commands.argument("Faction", StringArgumentType.string()).executes(arguments -> {
			if(arguments.getSource().getEntity() instanceof Player player)
				player.displayClientMessage(Factions.create(StringArgumentType.getString(arguments, "Faction")), false);
			Factions.save(FactionData.MapVariables.get(arguments.getSource().getLevel()));
			return 0;
		}))).then(Commands.literal("remove").then(Commands.argument("Faction", StringArgumentType.string()).suggests(Factions.suggest()).executes(arguments -> {
			if(arguments.getSource().getEntity() instanceof Player player)
				player.displayClientMessage(Factions.remove(StringArgumentType.getString(arguments, "Faction")), false);
			Factions.save(FactionData.MapVariables.get(arguments.getSource().getLevel()));
			return 0;
		}))).then(Commands.literal("modify").then(Commands.argument("Faction", StringArgumentType.string()).suggests(Factions.suggest()).then(Commands.literal("add").then(Commands.literal("entities").then(Commands.argument("type", ResourceArgument.resource(context, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(arguments -> {
			if(arguments.getSource().getEntity() instanceof Player player)
				player.displayClientMessage(Factions.addEntity(StringArgumentType.getString(arguments, "Faction"), getType(ResourceArgument.getEntityType(arguments, "type"))), false);
			Factions.save(FactionData.MapVariables.get(arguments.getSource().getLevel()));
			return 0;
		}))).then(Commands.literal("enemies").then(Commands.argument("faction", StringArgumentType.string()).suggests(Factions.suggest()).executes(arguments -> {
			if(arguments.getSource().getEntity() instanceof Player player)
				player.displayClientMessage(Factions.addEnemy(StringArgumentType.getString(arguments, "Faction"), StringArgumentType.getString(arguments, "faction")), false);
			Factions.save(FactionData.MapVariables.get(arguments.getSource().getLevel()));
			return 0;
		}))).then(Commands.literal("allies").then(Commands.argument("faction", StringArgumentType.string()).suggests(Factions.suggest()).executes(arguments -> {
			if(arguments.getSource().getEntity() instanceof Player player)
				player.displayClientMessage(Factions.addAlly(StringArgumentType.getString(arguments, "Faction"), StringArgumentType.getString(arguments, "faction")), false);
			Factions.save(FactionData.MapVariables.get(arguments.getSource().getLevel()));
			return 0;
		})))).then(Commands.literal("remove").then(Commands.literal("entities").then(Commands.argument("type", ResourceArgument.resource(context, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(arguments -> {
			if(arguments.getSource().getEntity() instanceof Player player)
				player.displayClientMessage(Factions.removeEntity(StringArgumentType.getString(arguments, "Faction"), getType(ResourceArgument.getEntityType(arguments, "type"))), false);
			Factions.save(FactionData.MapVariables.get(arguments.getSource().getLevel()));
			return 0;
		}))).then(Commands.literal("enemies").then(Commands.argument("faction", StringArgumentType.string()).suggests(Factions.suggest()).executes(arguments -> {
			if(arguments.getSource().getEntity() instanceof Player player)
				player.displayClientMessage(Factions.removeEnemy(StringArgumentType.getString(arguments, "Faction"), StringArgumentType.getString(arguments, "faction")), false);
			return 0;
		}))).then(Commands.literal("allies").then(Commands.argument("faction", StringArgumentType.string()).suggests(Factions.suggest()).executes(arguments -> {
			if(arguments.getSource().getEntity() instanceof Player player)
				player.displayClientMessage(Factions.removeAlly(StringArgumentType.getString(arguments, "Faction"), StringArgumentType.getString(arguments, "faction")), false);
			Factions.save(FactionData.MapVariables.get(arguments.getSource().getLevel()));
			return 0;
		})))))));
	}

	public static EntityType getType(Holder.Reference<EntityType<?>> holder) {
		Optional<EntityType<?>> type = EntityType.byString(holder.key().location().toString());
		if(type.isPresent())
			return type.get();
		return EntityType.TEXT_DISPLAY;
	}
}
