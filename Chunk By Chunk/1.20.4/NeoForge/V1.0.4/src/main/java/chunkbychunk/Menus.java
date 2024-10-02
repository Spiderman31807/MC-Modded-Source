package chunkbychunk;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.registries.Registries;

public class Menus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, ChunkbychunkMod.MODID);
	public static final DeferredHolder<MenuType<?>, MenuType<ManagerMenu>> Manager = REGISTRY.register("manager", () -> IMenuTypeExtension.create(ManagerMenu::new));

}
