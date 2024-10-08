package villager_inventory;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Supplier;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.client.Minecraft;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyMessage {

	public KeyMessage() {
	}

	public KeyMessage(FriendlyByteBuf buffer) {
	}

	public static void buffer(KeyMessage message, FriendlyByteBuf buffer) {
	}

	public static void handler(KeyMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			pressAction(context.getSender());
		});
		context.setPacketHandled(true);
	}

	public static void pressAction(Player entity) {
		if(Minecraft.getInstance().crosshairPickEntity instanceof Villager villager && entity instanceof ServerPlayer player)
			Events.openInventory(villager, player);
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		VillagerInventoryMod.addNetworkMessage(KeyMessage.class, KeyMessage::buffer, KeyMessage::new, KeyMessage::handler);
	}
}