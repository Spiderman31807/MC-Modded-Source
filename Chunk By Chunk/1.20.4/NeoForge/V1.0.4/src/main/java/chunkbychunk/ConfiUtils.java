package chunkbychunk;

import java.util.List;

public class ConfiUtils {
	public static int getCost(String dimension) {
		List<? extends String> dimensions = Settings.Dimensions.get();
		List<? extends Integer> costs = Settings.DimensionCost.get();
		if(dimensions.contains(dimension))
			return costs.get(dimensions.indexOf(dimension));
		return Settings.DefaultCost.get();
	}

	public static double getRange(String type) {
		return switch(type) {
			default -> 0;
			case "Item-RightClick" -> Settings.BlockPlace.get();
			case "Entity-RightClick" -> Settings.EntityInteraction.get();
			case "Entity-IndirectAttack" -> Settings.EntityAttack.get();
			case "Entity-DirectAttack" -> Settings.EntityAttackDirect.get();
			case "Block-LeftClick" -> Settings.BlockBreak.get();
			case "Block-RightClick" -> Settings.BlockInteraction.get();
		};
	}
}