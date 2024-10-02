package chunkbychunk;

import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public record KeyMessage(int type, int pressedms) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(ChunkbychunkMod.MODID, "key");

	public KeyMessage(FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readInt());
	}

	@Override
	public void write(final FriendlyByteBuf buffer) {
		buffer.writeInt(type);
		buffer.writeInt(pressedms);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public static void handleData(final KeyMessage message, final PlayPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.workHandler().submitAsync(() -> {
				pressAction(context.player().get(), message.type, message.pressedms);
			}).exceptionally(e -> {
				context.packetHandler().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	public static void pressAction(Player entity, int type, int pressedms) {
		switch(type) {
			case 0 -> BorderUtils.openManager(entity);
			case 1 -> BorderUtils.toggleMap(entity);
			case 2 -> BorderUtils.toggleBorder(entity);
		};
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		ChunkbychunkMod.addNetworkMessage(KeyMessage.ID, KeyMessage::new, KeyMessage::handleData);
	}
}
