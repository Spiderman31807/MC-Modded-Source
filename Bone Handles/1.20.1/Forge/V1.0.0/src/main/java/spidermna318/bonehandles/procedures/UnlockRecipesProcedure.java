package spidermna318.bonehandles.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;

@Mod.EventBusSubscriber
public class UnlockRecipesProcedure {
	public static final String[] Tools = new String[] { "sword", "pickaxe", "axe", "shovel", "hoe" };
	public static final String[] Grades = new String[] { "wooden", "stone", "iron", "golden", "diamond", "netherite" };

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if(event.getEntity() instanceof ServerPlayer player) {
			for(String tool : Tools) {
				for(String grade : Grades) {
					player.awardRecipesByKey(new ResourceLocation[]{new ResourceLocation("bone_handles:" + tool + "_" + grade)});
					player.awardRecipesByKey(new ResourceLocation[]{new ResourceLocation("bone_handles:" + tool + "_" + grade + "_left")});
					//player.awardRecipesByKey(new ResourceLocation[]{new ResourceLocation("bone_handles:" + tool + "_" + grade + "_right")});
				}
			}
		}
	}
}
