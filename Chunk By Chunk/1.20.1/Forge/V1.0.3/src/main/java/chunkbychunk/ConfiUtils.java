package chunkbychunk;

import chunkbychunk.configuration.ServerConfiConfiguration;

import java.util.List;

public class ConfiUtils {
	public static int getCost(String dimension) {
		List<? extends String> dimensions = ServerConfiConfiguration.DIMENSIONS.get();
		List<? extends Integer> costs = ServerConfiConfiguration.DIMENSION_COST.get();
		if(dimensions.contains(dimension))
			return costs.get(dimensions.indexOf(dimension));
		return ServerConfiConfiguration.DEFAULT_COST.get();
	}

	public static int getRange(String type) {
		return switch(type) {
			default -> 0;
			case "Item-RightClick" -> ServerConfiConfiguration.BLOCK_PLACE.get();
			case "Entity-RightClick" -> ServerConfiConfiguration.ENTITY_INTERACT.get();
			case "Entity-IndirectAttack" -> ServerConfiConfiguration.ENTITY_ATTACK.get();
			case "Entity-DirectAttack" -> ServerConfiConfiguration.ENTITY_ATTACKDIRECT.get();
			case "Block-LeftClick" -> ServerConfiConfiguration.BLOCK_BREAK.get();
			case "Block-RightClick" -> ServerConfiConfiguration.BLOCK_INTERACT.get();
		};
	}
}
