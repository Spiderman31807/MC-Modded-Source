package chunkbychunk;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

import java.util.function.Supplier;
import java.util.HashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ManagerButton {
	private final int buttonID;
	private final int[] chunk;

	public ManagerButton(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.chunk = buffer.readVarIntArray();
	}

	public ManagerButton(int buttonID, int[] chunk) {
		this.buttonID = buttonID;
		this.chunk = chunk;
	}

	public static void buffer(ManagerButton message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeVarIntArray(message.chunk);
	}

	public static void handler(ManagerButton message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player entity = context.getSender();
			int buttonID = message.buttonID;
			ChunkPos chunk = new ChunkPos(message.chunk[0], message.chunk[1]);
			handleButtonAction(entity, buttonID, chunk);
		});
		context.setPacketHandled(true);
	}

	public static void handleButtonAction(Player entity, int buttonID, ChunkPos chunk) {
		Level world = entity.level();
		HashMap guistate = ManagerMenu.guistate;
		BorderUtils.buyChunk(entity, chunk);
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		ChunkbychunkMod.addNetworkMessage(ManagerButton.class, ManagerButton::buffer, ManagerButton::new, ManagerButton::handler);
	}
}
