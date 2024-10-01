
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package spiderman.villager_inventory.init;

import spiderman.villager_inventory.client.gui.VillagerInventoryScreen;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.gui.screens.MenuScreens;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VillagerInventoryModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(VillagerInventoryModMenus.VILLAGER_INVENTORY.get(), VillagerInventoryScreen::new);
		});
	}
}
