package playasmob;

import net.minecraftforge.common.ForgeMod;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.context.CommandContext;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.effect.MobEffect;

public class GlobalData implements SuggestionProvider<String> {
	public static final List<EntityType> supported = List.of(EntityType.VILLAGER, EntityType.WITCH, EntityType.ZOMBIE, EntityType.HUSK,
		EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.SPIDER,
		EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON, EntityType.COW, EntityType.SHEEP,
		EntityType.PIG, EntityType.CHICKEN, EntityType.CAT, EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.EVOKER, EntityType.ILLUSIONER,
		EntityType.WARDEN, /*EntityType.RAVAGER, EntityType.IRON_GOLEM,*/ EntityType.ENDERMAN);

	public static final List<EntityType> rareType = List.of(EntityType.PLAYER, EntityType.WARDEN, EntityType.WITHER, EntityType.ENDER_DRAGON,
		EntityType.PIGLIN_BRUTE, EntityType.ELDER_GUARDIAN, EntityType.ILLUSIONER, EntityType.EVOKER);

	public static final List<EntityType> disabled = List.of(EntityType.RAVAGER, EntityType.IRON_GOLEM);

	public GlobalData() {
	}

	public CompletableFuture<Suggestions> getSuggestions(CommandContext<String> context, SuggestionsBuilder builder) {
		for(EntityType mob : supported) {
			builder.suggest(EntityType.getKey(mob).toString());
		}
		return builder.buildFuture();
	}

	public static SuggestionProvider<CommandSourceStack> suggest() {
		return (SuggestionProvider) new GlobalData();
	}

	public static List<Attribute> getAttributes() {
		ArrayList<Attribute> attributes = new ArrayList();
		attributes.add(Attributes.MAX_HEALTH);
		attributes.add(Attributes.FOLLOW_RANGE);
		attributes.add(Attributes.KNOCKBACK_RESISTANCE);
		attributes.add(Attributes.MOVEMENT_SPEED);
		attributes.add(Attributes.FLYING_SPEED);
		attributes.add(Attributes.ATTACK_DAMAGE);
		attributes.add(Attributes.ATTACK_KNOCKBACK);
		attributes.add(Attributes.ATTACK_SPEED);
		attributes.add(Attributes.ARMOR);
		attributes.add(Attributes.ARMOR_TOUGHNESS);
		attributes.add(Attributes.LUCK);
		attributes.add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
		attributes.add(Attributes.JUMP_STRENGTH);
		attributes.add(ForgeMod.SWIM_SPEED.get());
		attributes.add(ForgeMod.NAMETAG_DISTANCE.get());
		attributes.add(ForgeMod.ENTITY_GRAVITY.get());
		attributes.add(ForgeMod.BLOCK_REACH.get());
		attributes.add(ForgeMod.ENTITY_REACH.get());
		attributes.add(ForgeMod.STEP_HEIGHT_ADDITION.get());
		return attributes;
	}

	public static List<ResourceKey<Biome>> allBiomes() {
		ArrayList<ResourceKey<Biome>> biomes = new ArrayList();
		biomes.add(Biomes.THE_VOID);
        biomes.add(Biomes.PLAINS);
        biomes.add(Biomes.SUNFLOWER_PLAINS);
        biomes.add(Biomes.SNOWY_PLAINS);
        biomes.add(Biomes.ICE_SPIKES);
        biomes.add(Biomes.DESERT);
        biomes.add(Biomes.SWAMP);
        biomes.add(Biomes.MANGROVE_SWAMP);
        biomes.add(Biomes.FOREST);
        biomes.add(Biomes.FLOWER_FOREST);
        biomes.add(Biomes.BIRCH_FOREST);
        biomes.add(Biomes.DARK_FOREST);
        biomes.add(Biomes.OLD_GROWTH_BIRCH_FOREST);
        biomes.add(Biomes.OLD_GROWTH_PINE_TAIGA);
        biomes.add(Biomes.OLD_GROWTH_SPRUCE_TAIGA);
        biomes.add(Biomes.TAIGA);
        biomes.add(Biomes.SNOWY_TAIGA);
        biomes.add(Biomes.SAVANNA);
        biomes.add(Biomes.SAVANNA_PLATEAU);
        biomes.add(Biomes.WINDSWEPT_HILLS);
        biomes.add(Biomes.WINDSWEPT_GRAVELLY_HILLS);
        biomes.add(Biomes.WINDSWEPT_FOREST);
        biomes.add(Biomes.WINDSWEPT_SAVANNA);
        biomes.add(Biomes.JUNGLE);
        biomes.add(Biomes.SPARSE_JUNGLE);
        biomes.add(Biomes.BAMBOO_JUNGLE);
        biomes.add(Biomes.BADLANDS);
        biomes.add(Biomes.ERODED_BADLANDS);
        biomes.add(Biomes.WOODED_BADLANDS);
        biomes.add(Biomes.MEADOW);
        biomes.add(Biomes.CHERRY_GROVE);
        biomes.add(Biomes.GROVE);
        biomes.add(Biomes.SNOWY_SLOPES);
        biomes.add(Biomes.FROZEN_PEAKS);
        biomes.add(Biomes.JAGGED_PEAKS);
        biomes.add(Biomes.STONY_PEAKS);
        biomes.add(Biomes.RIVER);
        biomes.add(Biomes.FROZEN_RIVER);
        biomes.add(Biomes.BEACH);
        biomes.add(Biomes.SNOWY_BEACH);
        biomes.add(Biomes.STONY_SHORE);
        biomes.add(Biomes.WARM_OCEAN);
        biomes.add(Biomes.LUKEWARM_OCEAN);
        biomes.add(Biomes.DEEP_LUKEWARM_OCEAN);
        biomes.add(Biomes.OCEAN);
        biomes.add(Biomes.DEEP_OCEAN);
        biomes.add(Biomes.COLD_OCEAN);
        biomes.add(Biomes.DEEP_COLD_OCEAN);
        biomes.add(Biomes.FROZEN_OCEAN);
        biomes.add(Biomes.DEEP_FROZEN_OCEAN);
        biomes.add(Biomes.MUSHROOM_FIELDS);
        biomes.add(Biomes.DRIPSTONE_CAVES);
        biomes.add(Biomes.LUSH_CAVES);
        biomes.add(Biomes.DEEP_DARK);
        biomes.add(Biomes.NETHER_WASTES);
        biomes.add(Biomes.WARPED_FOREST);
        biomes.add(Biomes.CRIMSON_FOREST);
        biomes.add(Biomes.SOUL_SAND_VALLEY);
        biomes.add(Biomes.BASALT_DELTAS);
        biomes.add(Biomes.THE_END);
        biomes.add(Biomes.END_HIGHLANDS);
        biomes.add(Biomes.END_MIDLANDS);
        biomes.add(Biomes.SMALL_END_ISLANDS);
    	return biomes;
	}

	public static List<ResourceKey<Biome>> getBiomesExclude(List<ResourceKey<Biome>> excluded) {
		List<ResourceKey<Biome>> allBiomes = allBiomes();
		ArrayList<ResourceKey<Biome>> vaildBiomes = new ArrayList(allBiomes);
		for(ResourceKey<Biome> biome : allBiomes) {
			if(excluded.contains(biome))
				vaildBiomes.remove(biome);
		}

		return vaildBiomes;
	}

	public static EntityType getCureZombie(EntityType type) {
		return switch(GlobalUtils.getString(type)) {
			default -> null;
			case "minecraft:zombie" -> EntityType.PLAYER;
			case "minecraft:husk" -> EntityType.PLAYER;
			case "minecraft:drowned" -> EntityType.PLAYER;
			case "minecraft:zombie_villager" -> EntityType.VILLAGER;
			case "minecraft:zombified_piglin" -> EntityType.PIGLIN;
		};
	}

	public static EntityTypeData getData(EntityType type, MobData link, boolean isNew) {
		EntityType cureInto = getCureZombie(type);
		if(cureInto != null)
			return new ZombieData(link).setCureInto(cureInto);
		
		if(SelectionScreenData.withinGroup(type, "animal"))
			return new AnimalData(link);
		if(SelectionScreenData.withinGroup(type, "illager"))
			return new RaiderData(link);
		if(SelectionScreenData.withinGroup(type, "undead"))
			return new SkeletonData(link);
		if(type == EntityType.CREEPER)
			return new CreeperData(link);
		if(type == EntityType.VILLAGER)
			return new VillagerData(link);
		if(SelectionScreenData.withinGroup(type, "arthropod"))
			return new SpiderData(link);
		if(type == EntityType.CAT)
			return new CatData(link).randomiseVariant(isNew, link.world);
		if(SelectionScreenData.withinGroup(type, "piglin"))
			return new PiglinData(link);
		if(type == EntityType.WARDEN)
			return new WardenData(link);
		if(type == EntityType.ENDERMAN)
			return new EndermanData(link);
		return null;
	}

	public static boolean canPlayerHaveEffect(MobEffect effect) {
		if(effect instanceof FrenzyEffect || effect instanceof FearEffect)
			return false;
		return true;
	}
}
