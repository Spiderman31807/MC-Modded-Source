package playasmob;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.effect.MobEffect;
import java.util.List;

public class Effects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, PlayasmobMod.MODID);
	public static final RegistryObject<MobEffect> Frenzy = REGISTRY.register("frenzy", () -> new FrenzyEffect());
	public static final RegistryObject<MobEffect> Fear = REGISTRY.register("fear", () -> new FearEffect());
}