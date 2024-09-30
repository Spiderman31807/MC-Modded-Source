package spiderman.adaptive_difficulty.procedures;

import spiderman.adaptive_difficulty.Adaptive;
import spiderman.adaptive_difficulty.AdaptiveDifficultyMod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

@Mod.EventBusSubscriber
public class RespawnProcedure {
	@SubscribeEvent
	public static void onPlayerRespawned(PlayerEvent.PlayerRespawnEvent event) {
		Player player = event.getEntity();
		Difficulty adaptive = Adaptive.getDifficulty(player);
		Difficulty difficulty = player.level().getDifficulty();
		int difficultyId = Adaptive.getDifficultyId(player);

		if(Adaptive.isSameDifficulty(difficultyId, player.level()))
			return;
		if(difficultyId != 4 && player.level().getServer().isHardcore()) {
			AdaptiveDifficultyMod.queueServerWork(1, () -> {
				if(player instanceof ServerPlayer serverPlayer && serverPlayer.isSpectator())
					serverPlayer.setGameMode(GameType.SURVIVAL);
			});
		} else if(difficultyId == 4 && !player.level().getServer().isHardcore()) {
			if(player instanceof ServerPlayer serverPlayer && !serverPlayer.isSpectator())
				serverPlayer.setGameMode(GameType.SPECTATOR);
		}
	}
}
