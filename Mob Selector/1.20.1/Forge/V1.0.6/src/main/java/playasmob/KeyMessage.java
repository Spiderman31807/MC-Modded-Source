package playasmob;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkHooks;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyMessage {
	int ability;
	boolean pressed;

	public KeyMessage(int ability, boolean pressed) {
		this.ability = ability;
		this.pressed = pressed;
	}

	public KeyMessage(FriendlyByteBuf buffer) {
		this.ability = buffer.readInt();
		this.pressed = buffer.readBoolean();
	}

	public static void buffer(KeyMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.ability);
		buffer.writeBoolean(message.pressed);
	}

	public static void handler(KeyMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			pressAction(context.getSender(), message.ability, message.pressed);
		});
		context.setPacketHandled(true);
	}

	public static void pressAction(Player entity, int ability, boolean pressed) {
		MobData.get(entity).press(ability, pressed);
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		PlayasmobMod.addNetworkMessage(KeyMessage.class, KeyMessage::buffer, KeyMessage::new, KeyMessage::handler);
	}
}