package villager_inventory;

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
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.client.Minecraft;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public record KeyMessage() implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(VillagerInventoryMod.MODID, "key");

	public KeyMessage(FriendlyByteBuf buffer) {
		this();
	}

	@Override
	public void write(final FriendlyByteBuf buffer) {
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public static void handleData(final KeyMessage message, final PlayPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.workHandler().submitAsync(() -> {
				pressAction(context.player().get());
			}).exceptionally(e -> {
				context.packetHandler().disconnect(Component.literal(e.getMessage()));
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
		VillagerInventoryMod.addNetworkMessage(KeyMessage.ID, KeyMessage::new, KeyMessage::handleData);
	}
}