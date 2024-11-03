package playasmob;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

public class Menus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, PlayasmobMod.MODID);
	public static final RegistryObject<MenuType<MobSelectionMenu>> MobSelector = REGISTRY.register("mob_selector", () -> IForgeMenuType.create(MobSelectionMenu::new));
}