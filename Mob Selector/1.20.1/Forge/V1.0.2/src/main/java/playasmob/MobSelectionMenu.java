package playasmob;

import io.netty.buffer.Unpooled;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.world.MenuProvider;
import net.minecraft.server.level.ServerPlayer;

public class MobSelectionMenu extends AbstractContainerMenu {
	public final Level world;
	public final Player entity;

	public static void openSelector(Player player) {
		if (player instanceof ServerPlayer serverPlayer) {
			NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
				@Override
				public Component getDisplayName() {
					return Component.literal("Selector");
				}

				@Override
				public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
					return new MobSelectionMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()));
				}
			});
		}
	}
	
	public MobSelectionMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(Menus.MobSelector.get(), id);
		this.entity = inv.player;
		this.world = inv.player.level();
	}

	public boolean stillValid(Player player) {
		return true;
	}

	public ItemStack quickMoveStack(Player player, int slot) {
		return ItemStack.EMPTY;
	}
}
