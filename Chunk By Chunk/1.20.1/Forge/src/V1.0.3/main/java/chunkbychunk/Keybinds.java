package chunkbychunk;

import org.lwjgl.glfw.GLFW;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class Keybinds {
	public static final KeyMapping MANAGER = new KeyMapping("chunkbychunk.manager", GLFW.GLFW_KEY_F6, "key.categories.ui"){
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				ChunkbychunkMod.PACKET_HANDLER.sendToServer(new KeyMessage(0, 0));
				BorderUtils.openManager(Minecraft.getInstance().player);
			}
			isDownOld = isDown;
		}
	};
	
	public static final KeyMapping MAP = new KeyMapping("chunkbychunk.mini_map", GLFW.GLFW_KEY_F7, "key.categories.ui") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				ChunkbychunkMod.PACKET_HANDLER.sendToServer(new KeyMessage(1, 0));
				BorderUtils.toggleMap(Minecraft.getInstance().player);
			}
			isDownOld = isDown;
		}
	};
	
	public static final KeyMapping TOGGLE = new KeyMapping("chunkbychunk.toggle", GLFW.GLFW_KEY_F8, "key.categories.gameplay") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				ChunkbychunkMod.PACKET_HANDLER.sendToServer(new KeyMessage(2, 0));
				BorderUtils.toggleBorder(Minecraft.getInstance().player);
			}
			isDownOld = isDown;
		}
	};

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(MANAGER);
		event.register(MAP);
		event.register(TOGGLE);
	}

	@Mod.EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (Minecraft.getInstance().screen == null) {
				MANAGER.consumeClick();
				MAP.consumeClick();
				TOGGLE.consumeClick();
			}
		}
	}
}
