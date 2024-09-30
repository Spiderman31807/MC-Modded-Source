package chunkbychunk.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ServerConfiConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;
	public static final ForgeConfigSpec.ConfigValue<Integer> ENTITY_INTERACT;
	public static final ForgeConfigSpec.ConfigValue<Integer> ENTITY_ATTACKDIRECT;
	public static final ForgeConfigSpec.ConfigValue<Integer> ENTITY_ATTACK;
	public static final ForgeConfigSpec.ConfigValue<Integer> BLOCK_BREAK;
	public static final ForgeConfigSpec.ConfigValue<Integer> BLOCK_PLACE;
	public static final ForgeConfigSpec.ConfigValue<Integer> BLOCK_INTERACT;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DIMENSIONS;
	public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> DIMENSION_COST;
	public static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_COST;
	static {
		BUILDER.push("Outside Chunk Area, Interaction buffer");
		ENTITY_INTERACT = BUILDER.comment("The number of blocks outside the Chunk Area that the Player can interact with Entities (-1 for no limit)").define("Entity Right Click", 2);
		ENTITY_ATTACKDIRECT = BUILDER.comment("The number of blocks outside the Chunk Area that the Player can melee Entities (-1 for no limit)").define("Entity Left Click (Direct)", 2);
		ENTITY_ATTACK = BUILDER.comment("The number of blocks outside the Chunk Area that the Player can attack Entities (-1 for no limit)").define("Entity Left Click (Indirect)", -1);
		BLOCK_BREAK = BUILDER.comment("The number of blocks outside the Chunk Area that the Player can break Blocks (-1 for no limit)").define("Block Destroy", 1);
		BLOCK_PLACE = BUILDER.comment("The number of blocks outside the Chunk Area that the Player can place Blocks (-1 for no limit)").define("Block Place", 1);
		BLOCK_INTERACT = BUILDER.comment("The number of blocks outside the Chunk Area that the Player can interact with Blocks (-1 for no limit)").define("Block Interact", 1);
		BUILDER.pop();
		BUILDER.push("Chunk Cost");
		DIMENSIONS = BUILDER.comment("Please add any modded Dimensions entries to this list!").defineList("Dimensions", List.of("overworld", "the_nether", "the_end"), entry -> true);
		DIMENSION_COST = BUILDER.comment("Make sure the amount of entries in this List are the same as the Dimensions List").defineList("Cost Value", List.of(55, 160, 316), entry -> true);
		DEFAULT_COST = BUILDER.comment("The Cost of any Dimension not found on the List above").define("Default Cost", 160);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}
