package spiderman.adaptive_difficulty.procedures;

import spiderman.adaptive_difficulty.Adaptive;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

@Mod.EventBusSubscriber
public class DamageTakenProcedure {
	@SubscribeEvent
	public static void onEntityAttacked(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		boolean scale = source.scalesWithDifficulty();
		float damage = event.getAmount();

		if (event.getEntity() instanceof Player player) {
			Difficulty adaptive = Adaptive.getDifficulty(player);
			Difficulty difficulty = player.level().getDifficulty();
			int difficultyId = Adaptive.getDifficultyId(player);
			float health = player.getHealth();

			if(Adaptive.isSameDifficulty(difficultyId, player.level()))
				return;
			
			if(source.is(DamageTypes.STARVE)) {
				if(difficultyId == 0)
					damage = 0;
				if(difficultyId == 1 && health <= 10)
					damage = 0;
				if(difficultyId == 2 && health <= 1)
					damage = 0;
			} else if(scale && damage > 1) {
				if(difficulty == Difficulty.EASY)
					damage = (damage - 1) * 2;
				if(difficulty == Difficulty.HARD)
					damage /= 1.5f;
				
				if(difficultyId == 0)
					damage = 0;
				if(difficultyId == 1)
					damage = Math.min(damage / 2 + 1, damage);
				if(difficultyId == 3)
					damage = damage * 3 / 2;
			}

			if(damage <= 0) {
				if (event != null && event.isCancelable())
					event.setCanceled(true);
				if (event != null && event.hasResult())
					event.setResult(Event.Result.DENY);
			}

			event.setAmount(damage);
		}
	}
}
