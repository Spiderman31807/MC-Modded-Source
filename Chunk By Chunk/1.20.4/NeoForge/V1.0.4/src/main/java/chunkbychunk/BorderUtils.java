package chunkbychunk;

import io.netty.buffer.Unpooled;
import java.util.Optional;

import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class BorderUtils {
	public static PlayerVariables.Variables getVariables(Player player) {
		return player.getData(PlayerVariables.VARIABLES);
	}

	public static void toggleBorder(Player player) {
		PlayerVariables.Variables variables = getVariables(player);
		variables.borderData.player = player;
		variables.borderData.enabled = !variables.borderData.enabled;
		displayToggleMessage(player, variables.borderData.enabled);
		variables.syncVariables(player);
	}

	public static void toggleBorder(Player player, boolean toggle) {
		PlayerVariables.Variables variables = getVariables(player);
		variables.borderData.player = player;
		variables.borderData.enabled = toggle;
		displayToggleMessage(player, variables.borderData.enabled);
		variables.syncVariables(player);
	}

	public static void displayToggleMessage(Player player, boolean enabled) {
		if(player.level().isClientSide())
			return;
		
		Component message = Component.translatable("chunkbychunk.toggled");
		Component toggle = Component.translatable("chunkbychunk." + (enabled ? "on" : "off"));
		player.displayClientMessage(Component.literal(message.getString() + toggle.getString()), false);
	}

	public static void toggleMap(Player player) {
		PlayerVariables.Variables variables = getVariables(player);
		variables.miniMap = !variables.miniMap;
		variables.syncVariables(player);
	}

	public static boolean useMap(Player player) {
		PlayerVariables.Variables variables = getVariables(player);
		return variables.miniMap;
	}

	public static void reduceLevel(Player player) {
		PlayerVariables.Variables variables = getVariables(player);
		variables.borderData.player = player;
		variables.borderData.reduceLevel();
		variables.syncVariables(player);
	}

	public static int getLevel(Player player) {
		PlayerVariables.Variables variables = getVariables(player);
		variables.borderData.player = player;
		return variables.borderData.getLevels();
	}

	public static int getXP(Player player) {
		PlayerVariables.Variables variables = getVariables(player);
		variables.borderData.player = player;
		return variables.borderData.getXP();
	}

	public static int getLevelCost(Player player) {
		PlayerVariables.Variables variables = getVariables(player);
		variables.borderData.player = player;
		return variables.borderData.getCost();
	}

	public static void openManager(Player player) {
		if (player instanceof ServerPlayer serverPlayer) {
			player.openMenu(new MenuProvider() {
				@Override
				public Component getDisplayName() {
					return Component.literal("Manager");
				}

				@Override
				public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
					FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
					packetBuffer.writeBlockPos(player.blockPosition());
					return new ManagerMenu(id, inventory, packetBuffer);
				}
			}, buf -> {
				buf.writeBlockPos(player.blockPosition());
			});
		}
	}

	public static void buyChunk(Player player, ChunkPos chunk) {
		System.out.println("Buy Chunk: " + chunk);
		if(getLevel(player) >= 1 || player.isCreative()) {
			addChunk(player, chunk.getMiddleBlockX(), chunk.getMiddleBlockZ());
			if(!player.isCreative())
				reduceLevel(player);
		}
	}

	public static void addChunk(Player player, int x, int y) {
		PlayerVariables.Variables variables = getVariables(player);
		variables.borderData.player = player;
		variables.borderData.addChunk(new ChunkPos(BlockPos.containing(x, 0, y)));
		variables.syncVariables(player);
	}

	public static void removeChunk(Player player, int x, int y) {
		PlayerVariables.Variables variables = getVariables(player);
		variables.borderData.player = player;
		variables.borderData.removeChunk(new ChunkPos(BlockPos.containing(x, 0, y)));
		variables.syncVariables(player);
	}

	public static void removeAllChunks(Player player) {
		PlayerVariables.Variables variables = getVariables(player);
		variables.borderData.player = player;
		variables.borderData.removeAllChunks();
		variables.syncVariables(player);
	}

	public static void checkMounted(Player player, Entity mount) {
		Entity vehicle = player.getVehicle();
		if(vehicle == null)
			return;
		if(vehicle == mount)
			return;

		PlayerVariables.Variables variables = getVariables(player);
		variables.borderData.player = player;
		variables.borderData.putWithinBounds(player, vehicle, false);
	}

	public static int getChunkSize(Player player) {
		PlayerVariables.Variables variables = getVariables(player);
		variables.borderData.player = player;
		return variables.borderData.getChunks().size();
	}

	public static boolean hasChunk(Player player, ChunkPos chunk) {
		PlayerVariables.Variables variables = getVariables(player);
		variables.borderData.player = player;
		return variables.borderData.hasChunk(chunk);
	}

	public static ChunkPos getRelitiveChunk(Player player, int x, int z) {
		ChunkPos playerChunk = new ChunkPos(player.blockPosition());
		Direction direction = Direction.fromYRot(player.getYRot());
		int relitiveX = x;
		int relitiveZ = z;
		switch(direction) {
			case SOUTH:
				relitiveX *= -1;
				relitiveZ *= -1;
				break;
			case EAST:
				relitiveX = z * -1;
				relitiveZ = x;
				break;
			case WEST:
				relitiveX = z;
				relitiveZ = x * -1;
				break;
		}
		
		return new ChunkPos(playerChunk.x + relitiveX, playerChunk.z + relitiveZ);
	}

	public static boolean hasChunkRelitive(Player player, int x, int z) {
		return hasChunk(player, getRelitiveChunk(player, x, z));
	}

	public static boolean isAdjacentRelitive(Player player, int x, int z) {
		if(hasChunkRelitive(player, x + 1, z + 1))
			return true;
		if(hasChunkRelitive(player, x - 1, z - 1))
			return true;
		if(hasChunkRelitive(player, x + 1, z - 1))
			return true;
		if(hasChunkRelitive(player, x - 1, z + 1))
			return true;
		if(hasChunkRelitive(player, x, z + 1))
			return true;
		if(hasChunkRelitive(player, x, z - 1))
			return true;
		if(hasChunkRelitive(player, x + 1, z))
			return true;
		if(hasChunkRelitive(player, x - 1, z))
			return true;
		return false;
	}
}