package playasmob;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.common.IPlantable;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class FoodMapper {
	public static Map<Item, FoodProperties> foodInfo = new HashMap();
	public static List<Item> notFolivoreFood = List.of(Items.CACTUS, Items.NETHER_WART, Items.AIR);
	
	@SubscribeEvent
	public static void modLoader(FMLCommonSetupEvent event) {
		init();
	}
	
	public static void init() {
		//Granivore Diet
		add(Items.COCOA_BEANS, create(1, true));
		add(Items.WHEAT_SEEDS, create(2));
		add(Items.BEETROOT_SEEDS, create(2));
		add(Items.MELON_SEEDS, create(3));
		add(Items.PUMPKIN_SEEDS, create(3));
		add(Items.TORCHFLOWER_SEEDS, create(4, new FoodEffect(MobEffects.FIRE_RESISTANCE, 600, 0, 1f)));
		add(Items.PITCHER_POD, create(6));

		//Insectivore Diet
		add(Items.SPIDER_EYE, create(3));
		add(Items.FERMENTED_SPIDER_EYE, create(5));

		//Nectarivore Diet
		add(Items.HONEYCOMB, create(2));
		add(Items.HONEYCOMB_BLOCK, create(6));
		add(Items.HONEY_BLOCK, create(4, new FoodEffect(MobEffects.MOVEMENT_SPEED, 600, 1, 1f)));

		//Herbivore Diet
		add(Items.WHEAT, create(2));
		add(Items.BROWN_MUSHROOM, create(2));
		add(Items.RED_MUSHROOM, create(2));
		add(Items.CACTUS, create(3));
		
		//Detritivore Diet
		add(Items.ROTTEN_FLESH, create(5));
		add(Items.SPIDER_EYE, create(3));
		add(Items.POISONOUS_POTATO, create(2));

		//Folivore Diet
		add(Items.WITHER_ROSE, create(1, true, new FoodEffect(MobEffects.WITHER, 40, 0, 1f)));
		for(Block block : BuiltInRegistries.BLOCK) {
			if(foodInfo.containsKey(block.asItem()))
				continue;
			if((block instanceof IPlantable || block instanceof LeavesBlock) && !notFolivoreFood.contains(block.asItem())) {
				int food = (int)Math.ceil(block.defaultBlockState().getDestroySpeed(null, null));
				if(block instanceof SuspiciousEffectHolder suspicious && suspicious.getSuspiciousEffect() != null) {
					add(block.asItem(), create(food == 0 ? 1 : food, food == 0, new FoodEffect(suspicious.getSuspiciousEffect(), suspicious.getEffectDuration(), 0, 0.1f)));
				} else {
					add(block.asItem(), create(food == 0 ? 1 : food, food == 0));
				}
			}
		}
	}

	public static FoodProperties getBasic() {
		return (new FoodProperties.Builder()).nutrition(2).saturationMod(0.4f).build();
	}

	public static FoodProperties create(int nutrition, float saturation, boolean alwaysEat, boolean fast, boolean meat, FoodEffect... effects) {
		FoodProperties.Builder builder = new FoodProperties.Builder();
		builder.nutrition(nutrition).saturationMod(saturation);
		if(alwaysEat)
			builder.alwaysEat();
		if(fast)
			builder.fast();
		if(meat)
			builder.meat();
			
		for(FoodEffect foodEffect : effects) {
			MobEffectInstance instance = new MobEffectInstance(foodEffect.effect, foodEffect.duration, foodEffect.amplifier);
			builder.effect(instance, foodEffect.chance);
		}

		return builder.build();
	}

	public static FoodProperties create(int nutrition, float saturation, FoodEffect... effects) {
		return create(nutrition, saturation, nutrition <= 0, false, false, effects);
	}

	public static FoodProperties create(int nutrition, float saturation, boolean fast, FoodEffect... effects) {
		return create(nutrition, saturation, nutrition <= 0, fast, false, effects);
	}

	public static FoodProperties create(int nutrition, boolean alwaysEat, boolean fast, FoodEffect... effects) {
		return create(nutrition, nutrition / 5, alwaysEat, fast, false, effects);
	}

	public static FoodProperties create(int nutrition, boolean fast, FoodEffect... effects) {
		return create(nutrition, nutrition / 5, nutrition <= 0, fast, false, effects);
	}

	public static FoodProperties create(int nutrition, FoodEffect... effects) {
		return create(nutrition, nutrition / 5, nutrition <= 0, false, false, effects);
	}

	public static FoodProperties create(boolean alwaysEat, boolean fast, FoodEffect... effects) {
		return create(0, 0, alwaysEat, fast, false, effects);
	}

	public static FoodProperties create(boolean alwaysEat, FoodEffect... effects) {
		return create(0, 0, alwaysEat, false, false, effects);
	}

	public static void add(Item item, FoodProperties info) {
		PlayasmobMod.LOGGER.info("adding item: " + item + ", to foodMap with info: " + debug(info));
		foodInfo.put(item, info);
	}

	public static String debug(FoodProperties info) {
		String debugInfo = "\nnutrition: " + info.getNutrition();
		debugInfo += ", saturation: " + info.getSaturationModifier();
		debugInfo += ", alwaysEat: " + info.canAlwaysEat();
		debugInfo += ", fast: " + info.isFastFood();
		debugInfo += ", meat: " + info.isMeat();
		if(info.getEffects().size() <= 0)
			return debugInfo;
			
		debugInfo += "\n  effects: [";
		boolean isFirstEffect = true;
		for(Pair<MobEffectInstance, Float> foodEffects : info.getEffects()) {
			debugInfo += isFirstEffect ? " " : ",\n";
			debugInfo += "effect: " + foodEffects.getFirst().getEffect().getDisplayName().getString();
			debugInfo += ", chance: " + (foodEffects.getSecond() * 100) + "%";
			isFirstEffect = false;
		}
		debugInfo += " ]";
		return debugInfo;
	}

	public static FoodProperties get(Item item) {
		if(foodInfo.containsKey(item))
			return foodInfo.get(item);
		return getBasic();
	}
}
