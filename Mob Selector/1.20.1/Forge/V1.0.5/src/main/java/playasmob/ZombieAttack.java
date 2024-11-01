package playasmob;

import net.minecraft.world.entity.EntityType;

public interface ZombieAttack {
	default boolean zombieAttack(EntityType type) {
		return true;
	}
}
