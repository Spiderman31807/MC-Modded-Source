package spiderman.adaptive_difficulty.procedures;

import spiderman.adaptive_difficulty.Adaptive;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.world.entity.player.Player;

@Mod.EventBusSubscriber
public class SetupAdaptiveProcedure {
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		Adaptive.load(event.getEntity());
	}
}
