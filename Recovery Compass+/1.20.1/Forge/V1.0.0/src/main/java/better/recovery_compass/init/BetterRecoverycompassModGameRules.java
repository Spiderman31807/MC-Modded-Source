
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package better.recovery_compass.init;

import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.level.GameRules;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BetterRecoverycompassModGameRules {
	public static final GameRules.Key<GameRules.BooleanValue> CONSUME_RECOVERY_COMPASS = GameRules.register("consumeRecoveryCompass", GameRules.Category.MISC, GameRules.BooleanValue.create(true));
}
