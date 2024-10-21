package playasmob;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

@Mod.EventBusSubscriber
public class ClientSettings {
	public static boolean replaceStepSounds;
	public static float stepSoundMultiplier;

	public static CompoundTag save() {
		CompoundTag compound = new CompoundTag();
		compound.putBoolean("replaceStepSounds", replaceStepSounds);
		compound.putFloat("stepSoundMultiplier", stepSoundMultiplier);
		return compound;
	}

	public static void load(CompoundTag compound) {
		if(compound == null)
			return;
		
		replaceStepSounds = compound.getBoolean("replaceStepSounds");
		stepSoundMultiplier = compound.getFloat("stepSoundMultiplier");
	}

	@SubscribeEvent
	public static void saveFile(PlayerEvent.SaveToFile event) {
		try { NbtIo.write(save(), event.getPlayerFile("mobData-Settings")); } catch(Exception e) {};
	}

	@SubscribeEvent
	public static void loadFile(PlayerEvent.LoadFromFile event) {
		try { load(NbtIo.read(event.getPlayerFile("mobData-Settings"))); } catch(Exception e) {};
	}
}
