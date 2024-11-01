package playasmob;

import net.minecraft.world.entity.EntityType;

public interface SkeletonAttack {
	default boolean skeletonAttack(EntityType type) {
		return true;
	}
}
