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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.tags.TagKey;
import java.util.stream.Collectors;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Holder;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;

public class GlobalData implements SuggestionProvider<String> {
	public static final List<EntityType> supported = List.of(EntityType.VILLAGER, EntityType.WITCH, EntityType.ZOMBIE, EntityType.HUSK,
		EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.SPIDER,
		EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON, EntityType.COW, EntityType.SHEEP,
		EntityType.PIG, EntityType.CHICKEN, EntityType.CAT, EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.EVOKER, EntityType.ILLUSIONER,
		EntityType.WARDEN, EntityType.VEX, EntityType.RAVAGER, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.ENDERMAN);

	public static final List<EntityType> rareType = List.of(EntityType.PLAYER, EntityType.WARDEN, EntityType.WITHER, EntityType.ENDER_DRAGON,
		EntityType.PIGLIN_BRUTE, EntityType.ELDER_GUARDIAN, EntityType.ILLUSIONER, EntityType.EVOKER);

	public static final List<EntityType> disabled = List.of(EntityType.SNOW_GOLEM);

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
	
	public static List<Item> getItemTagged(TagKey<Item> tag) {
    	return BuiltInRegistries.ITEM.getTag(tag).stream().flatMap(holder -> holder.stream()).map(Holder::value).collect(Collectors.toList());
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
		//if(type == EntityType.WANDERING_TRADER)
			//return new WarderingTraderData(link);
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
		if(SelectionScreenData.withinGroup(type, "human"))
			return new GolemData(link);
		return null;
	}

	public static boolean canPlayerHaveEffect(MobEffect effect) {
		if(effect instanceof FrenzyEffect || effect instanceof FearEffect)
			return false;
		return true;
	}
}