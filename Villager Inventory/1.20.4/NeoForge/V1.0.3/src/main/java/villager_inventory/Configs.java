package villager_inventory;

import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.bus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = VillagerInventoryMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Configs {
	@SubscribeEvent
	public static void register(FMLConstructModEvent event) {
		event.enqueueWork(() -> {
			ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Settings.SPEC, "VillagerInventory.toml");
		});
	}
}