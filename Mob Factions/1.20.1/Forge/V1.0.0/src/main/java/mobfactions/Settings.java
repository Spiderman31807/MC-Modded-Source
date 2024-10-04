package mobfactions;

import net.minecraftforge.common.ForgeConfigSpec;

public class Settings {
	public static final ForgeConfigSpec.Builder Builder = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec.ConfigValue<String> Factions;
	public static final ForgeConfigSpec.ConfigValue<String> Entities;
	public static final ForgeConfigSpec.ConfigValue<String> Enemies;
	public static final ForgeConfigSpec.ConfigValue<String> Allies;
	public static final ForgeConfigSpec Spec;
	static {
		Builder.push("Default");
		Factions = Builder.define("Factions", "zombie, skeleton, illager, piglin");
		Entities = Builder.define("Entities", "[zombie, zombie_villager, husk, drowned, zombified_piglin, zoglin], [skeleton, stray, wither_skeleton], [pillager, vindicator, ravager, evoker, vex], [piglin, piglin_brute, zombified_piglin]");
		Enemies = Builder.define("Enemies", "[illager, piglin], [illager, piglin], [zombie, skeleton, piglin], [zombie, skeleton, illager]");
		Allies = Builder.define("Allies", "[skeleton], [zombie], [], []");
		Builder.pop();

		Spec = Builder.build();
	}

}