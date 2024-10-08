package villager_inventory;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.registries.Registries;

public class Menus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, VillagerInventoryMod.MODID);
	public static final DeferredHolder<MenuType<?>, MenuType<Menu>> Inventory = REGISTRY.register("villager_inventory", () -> IMenuTypeExtension.create(Menu::new));
}
