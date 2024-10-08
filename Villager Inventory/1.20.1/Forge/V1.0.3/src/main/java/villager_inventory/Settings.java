package villager_inventory;

import net.minecraftforge.common.ForgeConfigSpec;

public class Settings {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec.ConfigValue<Boolean> AllowBoth;
	public static final ForgeConfigSpec SPEC;
	static {
		BUILDER.push("Villager Inventory");
		AllowBoth = BUILDER.comment("If enabled then the Shift Right Click interaction will not be disabled when the interaction Key is binded").define("AllowBoth", false);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}
}