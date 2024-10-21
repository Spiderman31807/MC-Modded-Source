package playasmob;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;
import java.util.HashMap;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import java.util.Optional;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SelectorButton {
	private final EntityType mob;
	private final String type;

	public SelectorButton(FriendlyByteBuf buffer) {
		Optional<EntityType<?>> entityType = EntityType.byString(buffer.readUtf());
		this.mob = entityType.isPresent() ? entityType.get() : EntityType.PLAYER;
		this.type = buffer.readUtf();
	}

	public SelectorButton(EntityType mob, String type) {
		this.mob = mob;
		this.type = type;
	}

	public static void buffer(SelectorButton message, FriendlyByteBuf buffer) {
		buffer.writeUtf((EntityType.getKey(message.mob).toString()));
		buffer.writeUtf(message.type);
	}

	public static void handler(SelectorButton message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			handleButtonAction(context.getSender(), message.mob, message.type);
		});
		context.setPacketHandled(true);
	}

	public static void handleButtonAction(Player entity, EntityType mob, String type) {
		switch(type) {
			case "select":
				MobData.get(entity).changeMob(mob, null, false);
				MobData.get(entity).menuOpen(false);
				entity.closeContainer();
				break;
			case "preview":
				MobData.get(entity).setPreview(mob);
				break;
			case "back":
				MobData.get(entity).menuBack();
				break;
			case "open":
				MobData.get(entity).menuOpen(true);
				break;
			case "openGroup":
				MobData.get(entity).openGroup(SelectionScreenData.getGroup(mob, true));
				break;
		};
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		PlayasmobMod.addNetworkMessage(SelectorButton.class, SelectorButton::buffer, SelectorButton::new, SelectorButton::handler);
	}
}