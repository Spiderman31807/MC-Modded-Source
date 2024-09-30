package chunkbychunk;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

public class ChunkByChunkMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ChunkbychunkMod.MODID);
	public static final RegistryObject<MenuType<ManagerMenu>> MANAGER = REGISTRY.register("manager", () -> IForgeMenuType.create(ManagerMenu::new));
}
