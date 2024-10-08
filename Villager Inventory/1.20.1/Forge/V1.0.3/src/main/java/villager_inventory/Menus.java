package villager_inventory;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

public class Menus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, VillagerInventoryMod.MODID);
	public static final RegistryObject<MenuType<Menu>> Inventory = REGISTRY.register("villager_inventory", () -> IForgeMenuType.create(Menu::new));
}