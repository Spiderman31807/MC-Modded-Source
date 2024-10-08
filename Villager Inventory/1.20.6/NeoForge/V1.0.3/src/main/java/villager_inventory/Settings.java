package villager_inventory;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Settings {
	public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	public static final ModConfigSpec.ConfigValue<Boolean> AllowBoth;
	public static final ModConfigSpec SPEC;
	static {
		BUILDER.push("Villager Inventory");
		AllowBoth = BUILDER.comment("If enabled then the Shift Right Click interaction will not be disabled when the interaction Key is binded").define("AllowBoth", false);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}
}