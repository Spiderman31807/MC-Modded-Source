package playasmob;

import net.minecraft.world.entity.EntityType;

public interface IllagerAttack {
	default boolean illagerAttack(EntityType type) {
		return true;
	}
}
