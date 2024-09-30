
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package spidermna318.bonehandles.init;

import spidermna318.bonehandles.BoneHandlesMod;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.registries.Registries;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BoneHandlesModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BoneHandlesMod.MODID);

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.COMBAT) {
			tabData.accept(BoneHandlesModItems.WOODEN_AXE.get());
			tabData.accept(BoneHandlesModItems.STONE_AXE.get());
			tabData.accept(BoneHandlesModItems.IRON_AXE.get());
			tabData.accept(BoneHandlesModItems.GOLDEN_AXE.get());
			tabData.accept(BoneHandlesModItems.DIAMOND_AXE.get());
			tabData.accept(BoneHandlesModItems.NETHERITE_AXE.get());
		} else if (tabData.getTabKey() == CreativeModeTabs.INGREDIENTS) {
			tabData.accept(BoneHandlesModItems.TEMPLATE.get());
		} else if (tabData.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			tabData.accept(BoneHandlesModItems.WOODEN_AXE.get());
			tabData.accept(BoneHandlesModItems.STONE_AXE.get());
			tabData.accept(BoneHandlesModItems.IRON_AXE.get());
			tabData.accept(BoneHandlesModItems.GOLDEN_AXE.get());
			tabData.accept(BoneHandlesModItems.DIAMOND_AXE.get());
			tabData.accept(BoneHandlesModItems.NETHERITE_AXE.get());
			tabData.accept(BoneHandlesModItems.WOODEN_PICKAXE.get());
			tabData.accept(BoneHandlesModItems.STONE_PICKAXE.get());
			tabData.accept(BoneHandlesModItems.IRON_PICKAXE.get());
			tabData.accept(BoneHandlesModItems.GOLDEN_PICKAXE.get());
			tabData.accept(BoneHandlesModItems.DIAMOND_PICKAXE.get());
			tabData.accept(BoneHandlesModItems.NETHERITE_PICKAXE.get());
			tabData.accept(BoneHandlesModItems.WOODEN_SHOVEL.get());
			tabData.accept(BoneHandlesModItems.STONE_SHOVEL.get());
			tabData.accept(BoneHandlesModItems.IRON_SHOVEL.get());
			tabData.accept(BoneHandlesModItems.GOLDEN_SHOVEL.get());
			tabData.accept(BoneHandlesModItems.DIAMOND_SHOVEL.get());
			tabData.accept(BoneHandlesModItems.NETHERITE_SHOVEL.get());
			tabData.accept(BoneHandlesModItems.WOODEN_SWORD.get());
			tabData.accept(BoneHandlesModItems.STONE_SWORD.get());
			tabData.accept(BoneHandlesModItems.IRON_SWORD.get());
			tabData.accept(BoneHandlesModItems.GOLDEN_SWORD.get());
			tabData.accept(BoneHandlesModItems.DIAMOND_SWORD.get());
			tabData.accept(BoneHandlesModItems.NETHERITE_SWORD.get());
			tabData.accept(BoneHandlesModItems.WOODEN_HOE.get());
			tabData.accept(BoneHandlesModItems.STONE_HOE.get());
			tabData.accept(BoneHandlesModItems.IRON_HOE.get());
			tabData.accept(BoneHandlesModItems.GOLDEN_HOE.get());
			tabData.accept(BoneHandlesModItems.DIAMOND_HOE.get());
			tabData.accept(BoneHandlesModItems.NETHERITE_HOE.get());
		}
	}
}
