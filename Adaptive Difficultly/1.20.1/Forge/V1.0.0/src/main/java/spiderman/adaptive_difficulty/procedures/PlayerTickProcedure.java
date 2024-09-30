package spiderman.adaptive_difficulty.procedures;

import spiderman.adaptive_difficulty.Adaptive;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodData;

@Mod.EventBusSubscriber
public class PlayerTickProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			Player player = event.player;
			Difficulty adaptive = Adaptive.getDifficulty(player);
			Difficulty difficulty = player.level().getDifficulty();
			int difficultyId = Adaptive.getDifficultyId(player);
			float health = player.getHealth();

			if(Adaptive.isSameDifficulty(difficultyId, player.level()))
				return;

			FoodData foodData = player.getFoodData();
			int foodLevel = foodData.getFoodLevel();
			int foodTick = Adaptive.getFoodTick(foodData);
			if(difficultyId == 0) {
				foodData.setSaturation(1);
				if(foodTick == 0)
					foodData.setFoodLevel(Math.min(foodData.getFoodLevel() + 1, 20));
			}

			if(foodLevel <= 0 && foodTick == 0) {
				float difficultyStopAt = switch(difficulty) {
					default -> player.getMaxHealth();
					case EASY -> 10;
					case NORMAL -> 1;
					case HARD -> 0;
				};
				
				float adaptiveStopAt = switch(adaptive) {
					default -> player.getMaxHealth();
					case EASY -> 10;
					case NORMAL -> 1;
					case HARD -> 0;
				};

				if(adaptiveStopAt < health && difficultyStopAt >= health) {
					player.hurt(player.damageSources().starve(), 1);
				}
			}
		}
	}
}
