
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package better.recovery_compass.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import better.recovery_compass.BetterRecoverycompassMod;

public class BetterRecoverycompassModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, BetterRecoverycompassMod.MODID);
	public static final RegistryObject<Item> RECOVERY_STONE = block(BetterRecoverycompassModBlocks.RECOVERY_STONE);

	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
