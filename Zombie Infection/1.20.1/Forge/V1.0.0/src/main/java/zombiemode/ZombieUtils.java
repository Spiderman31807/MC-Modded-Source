package zombiemode;

import java.util.UUID;
import java.util.List;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Abilities;

public class ZombieUtils {
	public static ZombieData getData(Player player) {
		if(player == null)
			return new ZombieVariables.Variables().zombieData;
		ZombieData zombieData = player.getCapability(ZombieVariables.CAPABILITY, null).orElse(new ZombieVariables.Variables()).zombieData;
		if(zombieData.player != player || zombieData.world != player.level())
			zombieData.updatePlayer(player);
		return zombieData;
	}

	public static void syncData(Player player, ZombieData data) {
		if(player == null || player.level().isClientSide())
			return;
		ZombieVariables.Variables variables = player.getCapability(ZombieVariables.CAPABILITY, null).orElse(new ZombieVariables.Variables());
		variables.zombieData = data;
		if(player instanceof ServerPlayer serverPlayer && serverPlayer.connection != null)
			variables.syncVariables(player);
	}

	public static void resetAttackers(Player player) {
		final Player victim = player;
		player.setInvulnerable(true);
		ZombieModeMod.queueServerWork(20, () -> {
			victim.setInvulnerable(false);
		});
	}

	public static void setup(Player player, EntityType type) {
		ZombieData data = new ZombieData(player);
		data.setup(type);
	}

	public static boolean isZombie(LivingEntity target) {
		if(target instanceof Player player)
			return getData(player).isZombie();
		return target instanceof Zombie;
	}
}