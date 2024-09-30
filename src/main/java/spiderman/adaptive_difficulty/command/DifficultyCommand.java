package spiderman.adaptive_difficulty.command;

import spiderman.adaptive_difficulty.Adaptive;

import org.checkerframework.checker.units.qual.s;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

@Mod.EventBusSubscriber
public class DifficultyCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher()
				.register(Commands.literal("adaptive").executes(arguments -> {
					if (arguments.getSource().getEntity() instanceof Player player) {
						String mode = Adaptive.getDifficultyName(player);
						player.displayClientMessage(Component.literal("Adaptive Difficulty is " + mode + " Mode"), false);
					}
					
					return 0;
				}).then(Commands.argument("target", EntityArgument.player()).executes(arguments -> {
					if (arguments.getSource().getEntity() instanceof Player player && EntityArgument.getEntity(arguments, "target") instanceof Player target) {
						String mode = Adaptive.getDifficultyName(target);
						player.displayClientMessage(Component.literal("Adaptive Difficulty is " + mode + " Mode"), false);
					}
					
					return 0;
				}).requires(s -> s.hasPermission(2)).then(Commands.literal("peaceful").executes(arguments -> {
					if (arguments.getSource().getEntity() instanceof Player player && EntityArgument.getEntity(arguments, "target") instanceof Player target) {
						Adaptive.setDifficulty(target, "peaceful");
						player.displayClientMessage(Component.literal("Set Adaptive Difficulty to Peaceful Mode"), false);
					}
					
					return 0;
				})).requires(s -> s.hasPermission(2)).then(Commands.literal("easy").executes(arguments -> {
					if (arguments.getSource().getEntity() instanceof Player player && EntityArgument.getEntity(arguments, "target") instanceof Player target) {
						Adaptive.setDifficulty(target, "easy");
						player.displayClientMessage(Component.literal("Set Adaptive Difficulty to Easy Mode"), false);
					}
					
					return 0;
				})).requires(s -> s.hasPermission(2)).then(Commands.literal("normal").executes(arguments -> {
					if (arguments.getSource().getEntity() instanceof Player player && EntityArgument.getEntity(arguments, "target") instanceof Player target) {
						Adaptive.setDifficulty(target, "normal");
						player.displayClientMessage(Component.literal("Set Adaptive Difficulty to Normal Mode"), false);
					}
					
					return 0;
				})).requires(s -> s.hasPermission(2)).then(Commands.literal("hard").executes(arguments -> {
					if (arguments.getSource().getEntity() instanceof Player player && EntityArgument.getEntity(arguments, "target") instanceof Player target) {
						Adaptive.setDifficulty(target, "hard");
						player.displayClientMessage(Component.literal("Set Adaptive Difficulty to Hard Mode"), false);
					}
					
					return 0;
				})).requires(s -> s.hasPermission(2)).then(Commands.literal("hardcore").executes(arguments -> {
					if (arguments.getSource().getEntity() instanceof Player player && EntityArgument.getEntity(arguments, "target") instanceof Player target) {
						Adaptive.setDifficulty(target, "hardcore");
						player.displayClientMessage(Component.literal("Set Adaptive Difficulty to Hardcore Mode"), false);
					}
					
					return 0;
				}))).then(Commands.literal("peaceful").executes(arguments -> {
					if (arguments.getSource().getEntity() instanceof Player player) {
						Adaptive.setDifficulty(player, "peaceful");
						player.displayClientMessage(Component.literal("Set Adaptive Difficulty to Peaceful Mode"), false);
					}
					
					return 0;
				})).then(Commands.literal("easy").executes(arguments -> {
					if (arguments.getSource().getEntity() instanceof Player player) {
						Adaptive.setDifficulty(player, "easy");
						player.displayClientMessage(Component.literal("Set Adaptive Difficulty to Easy Mode"), false);
					}
					
					return 0;
				})).then(Commands.literal("normal").executes(arguments -> {
					if (arguments.getSource().getEntity() instanceof Player player) {
						Adaptive.setDifficulty(player, "normal");
						player.displayClientMessage(Component.literal("Set Adaptive Difficulty to Normal Mode"), false);
					}
					
					return 0;
				})).then(Commands.literal("hard").executes(arguments -> {
					if (arguments.getSource().getEntity() instanceof Player player) {
						Adaptive.setDifficulty(player, "hard");
						player.displayClientMessage(Component.literal("Set Adaptive Difficulty to Hard Mode"), false);
					}
					
					return 0;
				})).then(Commands.literal("hardcore").executes(arguments -> {
					if (arguments.getSource().getEntity() instanceof Player player) {
						Adaptive.setDifficulty(player, "hardcore");
						player.displayClientMessage(Component.literal("Set Adaptive Difficulty to Hardcore Mode"), false);
					}
					
					return 0;
				})));
	}
}
