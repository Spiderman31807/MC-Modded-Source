package villager_inventory;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.client.Minecraft;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record KeyMessage() implements CustomPacketPayload {
	public static final Type<KeyMessage> TYPE = new Type<>(new ResourceLocation(VillagerInventoryMod.MODID, "key"));
	public static final StreamCodec<RegistryFriendlyByteBuf, KeyMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, KeyMessage message) -> {}, (RegistryFriendlyByteBuf buffer) -> new KeyMessage());

	@Override
	public Type<KeyMessage> type() {
		return TYPE;
	}

	public static void handleData(final KeyMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> {
				pressAction(context.player());
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	public static void pressAction(Player entity) {
		if(Minecraft.getInstance().crosshairPickEntity instanceof Villager villager && entity instanceof ServerPlayer player)
			Events.openInventory(villager, player);
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		VillagerInventoryMod.addNetworkMessage(KeyMessage.TYPE, KeyMessage.STREAM_CODEC, KeyMessage::handleData);
	}
}