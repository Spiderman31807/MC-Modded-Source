package spiderman.villager_inventory.procedures;

import spiderman.villager_inventory.world.inventory.VillagerInventoryMenu;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;

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

import javax.annotation.Nullable;
import io.netty.buffer.Unpooled;

@Mod.EventBusSubscriber
public class RightClickProcedure {
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
		Entity target = event.getTarget();
		Entity user = event.getEntity();
	
		if(!target.isRemoved() && user instanceof ServerPlayer player && target instanceof Villager villager) {
			if (player.isShiftKeyDown()) {
				NetworkHooks.openScreen(player, new MenuProvider() {
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
						return new VillagerInventoryMenu(id, inventory, packetBuffer);
					}
				}, buf -> {
					buf.writeBlockPos(player.blockPosition());
					buf.writeByte(0);
					buf.writeVarInt(villager.getId());
				});

				player.swing(event.getHand(), true);
				if (event != null && event.isCancelable())
					event.setCanceled(true);
			}
		}
	}
}
