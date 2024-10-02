package chunkbychunk;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class Settings {
	public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	public static final ModConfigSpec SPEC;
	public static final ModConfigSpec.ConfigValue<Double> EntityInteraction;
	public static final ModConfigSpec.ConfigValue<Double> EntityAttackDirect;
	public static final ModConfigSpec.ConfigValue<Double> EntityAttack;
	public static final ModConfigSpec.ConfigValue<Double> BlockBreak;
	public static final ModConfigSpec.ConfigValue<Double> BlockPlace;
	public static final ModConfigSpec.ConfigValue<Double> BlockInteraction;
	public static final ModConfigSpec.ConfigValue<List<? extends String>> Dimensions;
	public static final ModConfigSpec.ConfigValue<List<? extends Integer>> DimensionCost;
	public static final ModConfigSpec.ConfigValue<Integer> DefaultCost;
	
	static {
		BUILDER.push("Outside Chunk Area, Interaction buffer");
		EntityInteraction = BUILDER.comment("The number of blocks outside the Chunk Area that the Player can interact with Entities (-1 for no limit)").define("Entity Right Click", 2d);
		EntityAttackDirect = BUILDER.comment("The number of blocks outside the Chunk Area that the Player can melee Entities (-1 for no limit)").define("Entity Left Click (Direct)", 2d);
		EntityAttack = BUILDER.comment("The number of blocks outside the Chunk Area that the Player can attack Entities (-1 for no limit)").define("Entity Left Click (Indirect)", -1d);
		BlockBreak = BUILDER.comment("The number of blocks outside the Chunk Area that the Player can break Blocks (-1 for no limit)").define("Block Destroy", 1d);
		BlockPlace = BUILDER.comment("The number of blocks outside the Chunk Area that the Player can place Blocks (-1 for no limit)").define("Block Place", 1d);
		BlockInteraction = BUILDER.comment("The number of blocks outside the Chunk Area that the Player can interact with Blocks (-1 for no limit)").define("Block Interact", 1d);
		BUILDER.pop();
		
		BUILDER.push("Chunk Cost");
		Dimensions = BUILDER.comment("Please add any modded Dimensions entries to this list!").defineList("Dimensions", List.of("overworld", "the_nether", "the_end"), entry -> true);
		DimensionCost = BUILDER.comment("Make sure the amount of entries in this List are the same as the Dimensions List").defineList("Cost Value", List.of(55, 160, 316), entry -> true);
		DefaultCost = BUILDER.comment("The Cost of any Dimension not found on the List above").define("Default Cost", 160);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}
}