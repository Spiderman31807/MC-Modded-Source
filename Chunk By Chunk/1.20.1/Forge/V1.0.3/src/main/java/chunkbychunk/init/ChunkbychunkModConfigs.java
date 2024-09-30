package chunkbychunk.init;

import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import chunkbychunk.configuration.ServerConfiConfiguration;

import chunkbychunk.ChunkbychunkMod;

@Mod.EventBusSubscriber(modid = ChunkbychunkMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChunkbychunkModConfigs {
	@SubscribeEvent
	public static void register(FMLConstructModEvent event) {
		event.enqueueWork(() -> {
			ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfiConfiguration.SPEC, "ChunkByChunk.toml");
		});
	}
}
