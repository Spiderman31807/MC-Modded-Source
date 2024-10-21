package playasmob;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public enum IllagerSpell {
    NONE(0, 0, 0, 0, -1, -1, -1, null),
    SUMMON_VEX(1, 0.7, 0.7, 0.8, 20, 100, 340, SoundEvents.EVOKER_PREPARE_SUMMON),
    FANGS(2, 0.4, 0.3, 0.35, 20, 40, 100, SoundEvents.EVOKER_PREPARE_ATTACK),
    WOLOLO(3, 0.7, 0.5, 0, 40, 60, 140, SoundEvents.EVOKER_PREPARE_WOLOLO),
    DISAPPEAR(4, 0.3, 0.3, 0.8, 20, 20, 340, SoundEvents.ILLUSIONER_PREPARE_BLINDNESS),
    BLINDNESS(5, 0.1, 0.1, 0.2, 20, 20, 180, SoundEvents.ILLUSIONER_PREPARE_MIRROR);

    private static final IntFunction<IllagerSpell> BY_ID = ByIdMap.continuous((spell) -> { return spell.id; }, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public final int id;
    public final double[] spellColor;
    public final SoundEvent prepareSound;
    public final int globalCooldown;
    public final int warmupTime;
    public final int castTime;
    public final int cooldown;

    private IllagerSpell(int id, double r, double g, double b, int globalCooldown, int castTime, int cooldown, SoundEvent prepareSound) {
 	    this.id = id;
        this.spellColor = new double[]{r, g, b};
        this.prepareSound = prepareSound;
        this.globalCooldown = globalCooldown;
        this.warmupTime = globalCooldown;
        this.castTime = castTime;
        this.cooldown = cooldown;
    }

    public static IllagerSpell byId(int id) {
   		return BY_ID.apply(id);
	}
}