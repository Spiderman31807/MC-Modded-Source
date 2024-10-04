package mobfactions;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.platform.DisplayData;
import net.minecraft.ChatFormatting;
import com.mojang.brigadier.context.ParsedCommandNode;
import java.util.List;

public class Factions implements SuggestionProvider<String> {
	public static ArrayList<String> factions = new ArrayList();
	public static Map<String, ArrayList<EntityType>> entities = new HashMap();
	public static Map<String, ArrayList<String>> enemies = new HashMap();
	public static Map<String, ArrayList<String>> allies = new HashMap();
	public static boolean isLoaded = false;

	public Factions() {
	}

	public static void load(FactionData.MapVariables data) {
		isLoaded = true;
		factions = new ArrayList();
		for(String faction : data.Factions.split(", ")) {
			factions.add(faction);
		}
		
		Map<String, ArrayList<String>> entityData = toArray(factions, data.Entities);
		entities = toTypes(factions, entityData);
		enemies = toArray(factions, data.Enemies);
		allies = toArray(factions, data.Allies);
	}

	public static void save(FactionData.MapVariables data) {
		String factionsData = "";
		boolean firstFaction = true;
		ArrayList<String> entityData = new ArrayList();
		ArrayList<String> enemiesData = new ArrayList();
		ArrayList<String> alliesData = new ArrayList();
		for(String faction : factions) {
			if(!firstFaction)
				factionsData += ", ";
			factionsData += faction;
			if(entities.containsKey(faction))
				entityData.add(toArray(getTypes(entities.get(faction)), true));
			if(enemies.containsKey(faction))
				enemiesData.add(toArray(enemies.get(faction), true));
			if(allies.containsKey(faction))
				alliesData.add(toArray(allies.get(faction), true));
			firstFaction = false;
		}

		data.Factions = factionsData;
		data.Entities = toArray(entityData, false);
		data.Enemies = toArray(enemiesData, false);
		data.Allies = toArray(alliesData, false);
	}

	public static Map<String, ArrayList<EntityType>> toTypes(ArrayList<String> factionData, Map<String, ArrayList<String>> arrayData) {
		Map<String, ArrayList<EntityType>> typeData = new HashMap();
		for(String faction : factionData) {
			ArrayList<EntityType> compiledData = new ArrayList();
			for(String value : arrayData.get(faction)) {
				EntityType.byString(value).ifPresent((type) -> {
					compiledData.add(type);
				});
			}
			typeData.put(faction, compiledData);
		}
		return typeData;
	}

	public static ArrayList<String> getTypes(ArrayList<EntityType> entityTypes) {
		ArrayList<String> types = new ArrayList();
		for(EntityType type : entityTypes) {
			types.add(getType(type));
		}
		return types;
	}

	public static String getType(EntityType type) {
		String typeId = type.toString();
		int idx = typeId.lastIndexOf('.');
		return typeId.substring(idx + 1);
	}

	public static Map<String, ArrayList<String>> toArray(ArrayList<String> factionData, String longData) {
		Map<String, ArrayList<String>> arrayData = new HashMap();
		int factionIdx = 0;
		for(String data : longData.split("], ")) {
			ArrayList<String> compiledData = new ArrayList();
			for(String value : data.replace("[", "").split(", ")) {
				compiledData.add(value.replace("]", ""));
			}
			arrayData.put(factionData.get(factionIdx), compiledData);
			factionIdx++;
		}
		return arrayData;
	}

	public static String toArray(ArrayList<String> arrayData, boolean inCase) {
		String longArray = "";
		if(inCase)
			longArray += "[";
		boolean firstData = true;
		for(String data : arrayData) {
			if(!firstData)
				longArray += ", ";
			longArray += data;
			firstData = false;
		}
		if(inCase)
			longArray += "]";
		return longArray;
	}

	public static SuggestionProvider<CommandSourceStack> suggest() {
		return (SuggestionProvider) new Factions();
	}

	public CompletableFuture<Suggestions> getSuggestions(CommandContext<String> context, SuggestionsBuilder builder) {
		List<ParsedCommandNode<String>> nodes = context.getNodes();
		System.out.println("\nNodes");
		for(ParsedCommandNode node : nodes) {
			System.out.println("Node" + node.getNode().getCommand());
		}
		System.out.println("\n");
		
		for(String faction : factions) {
			builder.suggest(faction);
		}
		return builder.buildFuture();
	}

	public static boolean has(Mob mob) {
		return get(mob).size() > 0;
	}
	
	public static ArrayList<String> get(Mob mob) {
		ArrayList<String> MobFactions = new ArrayList();
		for(String faction : factions) {
			if(!entities.containsKey(faction))
				continue;
			if(entities.get(faction).contains(mob.getType()))
				MobFactions.add(faction);
		}
		return MobFactions;
	}

	public static boolean isEnemy(ArrayList<String> factions1, ArrayList<String> factions2) {
		if(isAlly(factions1, factions2))
			return false;
		for(String faction : factions1) {
			for(String check : factions2) {
				if(!enemies.containsKey(faction))
					continue;
				if(enemies.get(faction).contains(check))
					return true;
			}
		}
		return false;
	}

	public static boolean isAlly(ArrayList<String> factions1, ArrayList<String> factions2) {
		for(String faction : factions1) {
			for(String check : factions2) {
				if(!allies.containsKey(faction))
					continue;
				if(allies.get(faction).contains(check) || faction == check)
					return true;
			}
		}
		return false;
	}
	
	public static boolean isEnemy(Mob attacker, Mob target) {
		if(!has(attacker) || !has(target))
			return false;
		return isEnemy(get(attacker), get(target));
	}
	
	public static boolean isAlly(Mob attacker, Mob target) {
		if(!has(attacker) || !has(target))
			return false;
		return isAlly(get(attacker), get(target));
	}

	public static Component create(String faction) {
		if(factions.contains(faction))
			return Component.translatable("faction.dupe").withStyle(ChatFormatting.RED);
		
		factions.add(faction);
		entities.put(faction, new ArrayList());
		enemies.put(faction, new ArrayList());
		allies.put(faction, new ArrayList());
		return Component.translatable("faction.feedback.create").append(faction);
	}

	public static Component remove(String faction) {
		if(!factions.contains(faction))
			return Component.translatable("faction.missing").withStyle(ChatFormatting.RED);
		
		factions.remove(faction);
		entities.remove(faction);
		enemies.remove(faction);
		allies.remove(faction);
		return Component.translatable("faction.feedback.remove").append(faction);
	}

	public static Component addEntity(String faction, EntityType type) {
		if(!factions.contains(faction))
			return Component.translatable("faction.missing").withStyle(ChatFormatting.RED);
		ArrayList<EntityType> entityData = entities.get(faction);
		if(entityData.contains(type))
			return Component.translatable("entity.dupe").append(getType(type)).withStyle(ChatFormatting.RED);
			
		entityData.add(type);
		entities.put(faction, entityData);
		return Component.translatable("faction.feedback.addEntity").append(getType(type));
	}

	public static Component removeEntity(String faction, EntityType type) {
		if(!factions.contains(faction))
			return Component.translatable("faction.missing").withStyle(ChatFormatting.RED);
		ArrayList<EntityType> entityData = entities.get(faction);
		if(!entityData.contains(type))
			return Component.translatable("entity.missing").append(getType(type)).withStyle(ChatFormatting.RED);
			
		entityData.remove(type);
		entities.put(faction, entityData);
		return Component.translatable("faction.feedback.removeEntity").append(getType(type));
	}

	public static Component addEnemy(String faction, String enemy) {
		if(!factions.contains(faction))
			return Component.translatable("faction.missing").withStyle(ChatFormatting.RED);
		ArrayList<String> EnemyData = enemies.get(faction);
		if(EnemyData.contains(enemy))
			return Component.translatable("enemy.dupe").append(enemy).withStyle(ChatFormatting.RED);
		if(allies.get(faction).contains(enemy))
			return Component.translatable("faction.already_ally").withStyle(ChatFormatting.RED);
			
		EnemyData.add(enemy);
		enemies.put(faction, EnemyData);
		return Component.translatable("faction.feedback.addEnemy").append(enemy);
	}

	public static Component removeEnemy(String faction, String enemy) {
		if(!factions.contains(faction))
			return Component.translatable("faction.missing").withStyle(ChatFormatting.RED);
		ArrayList<String> EnemyData = enemies.get(faction);
		if(!EnemyData.contains(enemy))
			return Component.translatable("enemy.missing").append(enemy).withStyle(ChatFormatting.RED);
			
		EnemyData.remove(enemy);
		enemies.put(faction, EnemyData);
		return Component.translatable("faction.feedback.removeEnemy").append(enemy);
	}

	public static Component addAlly(String faction, String ally) {
		if(!factions.contains(faction))
			return Component.translatable("faction.missing").withStyle(ChatFormatting.RED);
		ArrayList<String> AllyData = allies.get(faction);
		if(AllyData.contains(ally))
			return Component.translatable("ally.dupe").append(ally).withStyle(ChatFormatting.RED);
		if(enemies.get(faction).contains(ally))
			return Component.translatable("faction.already_enemy").withStyle(ChatFormatting.RED);
			
		AllyData.add(ally);
		allies.put(faction, AllyData);
		return Component.translatable("faction.feedback.addAlly").append(ally);
	}

	public static Component removeAlly(String faction, String ally) {
		if(!factions.contains(faction))
			return Component.translatable("faction.missing").withStyle(ChatFormatting.RED);
		ArrayList<String> AllyData = allies.get(faction);
		if(!AllyData.contains(ally))
			return Component.translatable("ally.missing").append(ally).withStyle(ChatFormatting.RED);
			
		AllyData.remove(ally);
		allies.put(faction, AllyData);
		return Component.translatable("faction.feedback.removeAlly").append(ally);
	}

	public static Component displayData() {
		MutableComponent factionData = Component.translatable("faction.display.all");
		boolean firstFaction = true;
		for(String faction : factions) {
			if(!firstFaction)
				factionData.append("\n\n");
			factionData.append("\n\n");
			factionData.append(displayData(faction));
			firstFaction = true;
		}

		return factionData;
	}

	public static Component displayData(String faction) {
		MutableComponent factionData = Component.translatable("faction.display");
		factionData.append(faction + "\n");
		factionData.append(displayEntities(faction, false));
		factionData.append("\n");
		factionData.append(displayEnemies(faction, false));
		factionData.append("\n");
		factionData.append(displayAllies(faction, false));

		return factionData;
	}

	public static Component displayEntities(String faction, boolean title) {
		MutableComponent factionData = Component.translatable("faction.display.entities");
		if(title) {
			factionData = Component.translatable("faction.display");
			factionData.append(faction + "\n");
			factionData.append(Component.translatable("faction.display.entities"));
		}

		if(!entities.containsKey(faction) || entities.get(faction).size() == 0)
			return factionData;
			
		boolean firstEntry = true;
		for(EntityType type : entities.get(faction)) {
			if(!firstEntry)
				factionData.append(", ");
			factionData.append(Component.translatable(type.toString()));
			firstEntry = false;
		}

		return factionData;
	}

	public static Component displayEnemies(String faction, boolean title) {
		MutableComponent factionData = Component.translatable("faction.display.enemies");
		if(title) {
			factionData = Component.translatable("faction.display");
			factionData.append(faction + "\n");
			factionData.append(Component.translatable("faction.display.enemies"));
		}

		if(!enemies.containsKey(faction) || enemies.get(faction).size() == 0)
			return factionData;
		
		boolean firstEntry = true;
		for(String enemy : enemies.get(faction)) {
			if(!firstEntry)
				factionData.append(", ");
			factionData.append(Component.translatable(enemy));
			firstEntry = false;
		}

		return factionData;
	}

	public static Component displayAllies(String faction, boolean title) {
		MutableComponent factionData = Component.translatable("faction.display.allies");
		if(title) {
			factionData = Component.translatable("faction.display");
			factionData.append(faction + "\n");
			factionData.append(Component.translatable("faction.display.allies"));
		}

		if(!allies.containsKey(faction) || allies.get(faction).size() == 0)
			return factionData;
		
		boolean firstEntry = true;
		for(String ally : allies.get(faction)) {
			if(!firstEntry)
				factionData.append(", ");
			factionData.append(ally);
			firstEntry = false;
		}

		return factionData;
	}
}
