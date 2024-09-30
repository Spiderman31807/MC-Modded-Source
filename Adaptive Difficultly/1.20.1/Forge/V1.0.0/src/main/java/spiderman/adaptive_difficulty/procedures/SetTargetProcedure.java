package spiderman.adaptive_difficulty.procedures;

import spiderman.adaptive_difficulty.Adaptive;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;

@Mod.EventBusSubscriber
public class SetTargetProcedure {
	@SubscribeEvent
	public static void onEntitySetsAttackTarget(LivingChangeTargetEvent event) {
		if(event.getNewTarget() instanceof Player player) {
			Difficulty adaptive = Adaptive.getDifficulty(player);
			Difficulty difficulty = player.level().getDifficulty();
			int difficultyId = Adaptive.getDifficultyId(player);

			if(Adaptive.isSameDifficulty(difficultyId, player.level()))
				return;
					
			if(difficultyId == 0 && !event.getEntity().getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("adaptive:peaceful_targeting")))) {
				if (event != null && event.isCancelable())
					event.setCanceled(true);
				if (event != null && event.hasResult())
					event.setResult(Event.Result.DENY);
			}
		}
	}
}
