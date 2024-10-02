package chunkbychunk;

import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.HashMap;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public record ManagerButton(int buttonID, int[] chunk) implements CustomPacketPayload {

	public static final ResourceLocation ID = new ResourceLocation(ChunkbychunkMod.MODID, "manager_buttons");
	public ManagerButton(FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readVarIntArray());
	}

	@Override
	public void write(final FriendlyByteBuf buffer) {
		buffer.writeInt(buttonID);
		buffer.writeVarIntArray(chunk);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public static void handleData(final ManagerButton message, final PlayPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.workHandler().submitAsync(() -> {
				Player entity = context.player().get();
				int buttonID = message.buttonID;
				int[] chunk = message.chunk;
				handleButtonAction(entity, buttonID, new ChunkPos(chunk[0], chunk[1]));
			}).exceptionally(e -> {
				context.packetHandler().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	public static void handleButtonAction(Player entity, int buttonID, ChunkPos chunk) {
		System.out.println("Handle Chunk Button");
		BorderUtils.buyChunk(entity, chunk);
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		ChunkbychunkMod.addNetworkMessage(ManagerButton.ID, ManagerButton::new, ManagerButton::handleData);
	}
}