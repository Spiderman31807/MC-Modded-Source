
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package better.recovery_compass.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import better.recovery_compass.block.RecoveryStoneBlock;
import better.recovery_compass.BetterRecoverycompassMod;

public class BetterRecoverycompassModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, BetterRecoverycompassMod.MODID);
	public static final RegistryObject<Block> RECOVERY_STONE = REGISTRY.register("recovery_stone", () -> new RecoveryStoneBlock());
	// Start of user code block custom blocks
	// End of user code block custom blocks
}
