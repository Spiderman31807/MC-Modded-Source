package villager_inventory;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.MenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import io.netty.buffer.Unpooled;

@Mod.EventBusSubscriber
public class Events {
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
		Entity target = event.getTarget();
		Entity user = event.getEntity();
		if(!target.isRemoved() && user instanceof ServerPlayer player && target instanceof Villager villager) {
			if (player.isShiftKeyDown()) {
				player.openMenu(new MenuProvider() {
					@Override
					public Component getDisplayName() {
						return Component.literal("Villager");
					}

					@Override
					public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
						FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
						packetBuffer.writeBlockPos(player.blockPosition());
						packetBuffer.writeByte(0);
						packetBuffer.writeVarInt(villager.getId());
						return new Menu(id, inventory, packetBuffer);
					}
				}, buf -> {
					buf.writeBlockPos(player.blockPosition());
					buf.writeByte(0);
					buf.writeVarInt(villager.getId());
				});

				player.swing(event.getHand(), true);
				event.setCanceled(true);
			}
		}
	}
}