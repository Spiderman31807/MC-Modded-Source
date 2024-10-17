package playasmob;

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
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class Keybinds {
	public static final KeyMapping Ability1 = new KeyMapping("playasmob.ability1", GLFW.GLFW_KEY_UNKNOWN, "key.categories.gameplay"){
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown) {
				PlayasmobMod.PACKET_HANDLER.sendToServer(new KeyMessage(1, isDown));
				MobData.get(Minecraft.getInstance().player).press(1, isDown);
			}
			isDownOld = isDown;
		}
	};
	
	public static final KeyMapping Ability2 = new KeyMapping("playasmob.ability2", GLFW.GLFW_KEY_UNKNOWN, "key.categories.gameplay"){
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown) {
				PlayasmobMod.PACKET_HANDLER.sendToServer(new KeyMessage(2, isDown));
				MobData.get(Minecraft.getInstance().player).press(2, isDown);
			}
			isDownOld = isDown;
		}
	};
	
	public static final KeyMapping Ability3 = new KeyMapping("playasmob.ability3", GLFW.GLFW_KEY_UNKNOWN, "key.categories.gameplay"){
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown) {
				PlayasmobMod.PACKET_HANDLER.sendToServer(new KeyMessage(3, isDown));
				MobData.get(Minecraft.getInstance().player).press(3, isDown);
			}
			isDownOld = isDown;
		}
	};

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(Ability1);
		event.register(Ability2);
		event.register(Ability3);
	}

	@Mod.EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (Minecraft.getInstance().screen == null) {
				Ability1.consumeClick();
				Ability2.consumeClick();
				Ability3.consumeClick();
			}
		}
	}
}