package playasmob;

import net.minecraft.world.entity.EntityType;

public interface SpiderAttack {
	default boolean spiderAttack(EntityType type) {
		return true;
	}
}
