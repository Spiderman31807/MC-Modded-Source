package playasmob;

import net.minecraft.world.effect.MobEffect;

public class FoodEffect {
	public final MobEffect effect;
	public final int duration;
	public final int amplifier;
	public final float chance;
	
	public FoodEffect(MobEffect effect, int duration, int amplifier, float chance) {
		this.effect = effect;
		this.duration = duration;
		this.amplifier = amplifier;
		this.chance = chance;
	}
}
