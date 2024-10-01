
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package spiderman.villager_inventory.init;

import spiderman.villager_inventory.world.inventory.VillagerInventoryMenu;
import spiderman.villager_inventory.VillagerInventoryMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

public class VillagerInventoryModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, VillagerInventoryMod.MODID);
	public static final RegistryObject<MenuType<VillagerInventoryMenu>> VILLAGER_INVENTORY = REGISTRY.register("villager_inventory", () -> IForgeMenuType.create(VillagerInventoryMenu::new));
}
