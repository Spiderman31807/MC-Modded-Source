package spiderman.adaptive_difficulty;

import spiderman.adaptive_difficulty.network.AdaptiveDifficultyModVariables;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.food.FoodData;
import net.minecraft.nbt.CompoundTag;

public class Adaptive {
	public static final ArrayList<EntityType> canAttack = new ArrayList(List.of(
		EntityType.SHULKER, EntityType.SHULKER_BULLET, EntityType.HOGLIN, EntityType.ZOGLIN, 
		EntityType.PIGLIN_BRUTE, EntityType.ENDER_DRAGON, EntityType.LLAMA));
		
	public static final  ArrayList<String> vaildDifficulty = new ArrayList(List.of("peaceful", "easy", "normal", "hard", "hardcore"));

	public static void load(Player player) {
		if(!vaildDifficulty.contains(getDifficultyName(player))) {
			Difficulty difficulty = player.level().getDifficulty();
			if(player.level().getServer().isHardcore()) {
				setDifficulty(player, "hardcore");
			} else {
				setDifficulty(player, difficulty.getKey());
			}
		}
	}

	public static void setDifficulty(Player player, String mode) {
		player.getCapability(AdaptiveDifficultyModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
			capability.Difficulty = mode;
			capability.syncPlayerVariables(player);
		});
	}

	public static String getDifficultyName(Player player) {
		return (player.getCapability(AdaptiveDifficultyModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new AdaptiveDifficultyModVariables.PlayerVariables())).Difficulty;
	}

	public static int getDifficultyId(Player player) {
		String difficulty = getDifficultyName(player);
		return switch(difficulty) {
			default -> 2;
			case "peaceful" -> 0;
			case "easy" -> 1;
			case "hard" -> 3;
			case "hardcore" -> 4;
		};
	}

	public static Difficulty getDifficulty(Player player) {
		String difficulty = getDifficultyName(player);
		return switch(difficulty) {
			default -> Difficulty.NORMAL;
			case "peaceful" -> Difficulty.PEACEFUL;
			case "easy" -> Difficulty.EASY;
			case "hard" -> Difficulty.HARD;
			case "hardcore" -> Difficulty.HARD;
		};
	}

	public static int getFoodTick(FoodData data) {
		CompoundTag compound = new CompoundTag();
		data.addAdditionalSaveData(compound);
		if(compound.contains("foodTickTimer"))
			return compound.getInt("foodTickTimer");
		return 0;
	}

	public static boolean isSameDifficulty(int adaptive, Level world) {
		Difficulty difficulty = world.getDifficulty();
		MinecraftServer server = world.getServer();
		boolean isHarcore = server == null ? false : server.isHardcore();

		if(isHarcore && adaptive != 4)
			return false;
		if(!isHarcore && adaptive == 4)
			return false;
		return difficulty.getId() == adaptive;
	}
}
