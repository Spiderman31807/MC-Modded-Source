package chunkbychunk;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import io.netty.buffer.Unpooled;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;

import net.minecraft.world.entity.player.Player;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;

@Mod.EventBusSubscriber
public class ChunkCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("chunk").requires(s -> s.hasPermission(2)).then(Commands.literal("border").then(Commands.argument("toggle", BoolArgumentType.bool()).executes(arguments -> {
			if (arguments.getSource().getEntity() instanceof Player player) {
				boolean enabled = BoolArgumentType.getBool(arguments, "toggle");
				BorderUtils.toggleBorder(player, enabled);
				return 15;
			}
			return 0;
		}))).then(Commands.literal("reset").executes(arguments -> {
			if (arguments.getSource().getEntity() instanceof Player player) {
				BorderUtils.removeAllChunks(player);
				return 15;
			}
			return 0;
		})).then(Commands.literal("manager").executes(arguments -> {
			if (arguments.getSource().getEntity() instanceof ServerPlayer player) {
				BorderUtils.openManager(player);
				return 15;
			}
			return 0;
		})).then(Commands.literal("add").then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(arguments -> {
			if (arguments.getSource().getEntity() instanceof Player player) {
				BlockPos pos = BlockPosArgument.getBlockPos(arguments, "pos");
				BorderUtils.addChunk(player, pos.getX(), pos.getZ());
				return 15;
			}
			return 0;
		}))).then(Commands.literal("remove").then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(arguments -> {
			if (arguments.getSource().getEntity() instanceof Player player) {
				BlockPos pos = BlockPosArgument.getBlockPos(arguments, "pos");
				BorderUtils.removeChunk(player, pos.getX(), pos.getZ());
				return 15;
			}
			return 0;
		}))));
	}
}