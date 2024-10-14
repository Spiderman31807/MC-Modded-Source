package playasmob;

import net.minecraft.world.entity.EntityType;

public interface GolemAttack {
	default boolean golemAttack(EntityType type) {
		return true;
	}
}
