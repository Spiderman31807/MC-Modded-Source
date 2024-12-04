
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package better.recovery_compass.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;

import better.recovery_compass.block.entity.RecoveryStoneBlockEntity;
import better.recovery_compass.BetterRecoverycompassMod;

public class BetterRecoverycompassModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BetterRecoverycompassMod.MODID);
	public static final RegistryObject<BlockEntityType<?>> RECOVERY_STONE = register("recovery_stone", BetterRecoverycompassModBlocks.RECOVERY_STONE, RecoveryStoneBlockEntity::new);

	// Start of user code block custom block entities
	// End of user code block custom block entities
	private static RegistryObject<BlockEntityType<?>> register(String registryname, RegistryObject<Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}
}
