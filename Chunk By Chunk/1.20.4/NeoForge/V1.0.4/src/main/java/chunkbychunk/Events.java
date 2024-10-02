package chunkbychunk;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.Event;

import java.util.Optional;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

@Mod.EventBusSubscriber
public class Events {
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPlayerXPChange(PlayerXpEvent.PickupXp event) {
		Player player = event.getEntity();
		BorderData border = getBorderData(player);
		if(border.enabled)
			border.addXP(event.getOrb().getValue());
	}

	@SubscribeEvent
	public static void respawn(PlayerEvent.PlayerRespawnEvent event) {
		Player player = event.getEntity();
		BorderData border = getBorderData(player);
		ChunkPos chunk = new ChunkPos(player.blockPosition());
		if(!border.isWithinBounds(chunk))
			border.putWithinBounds(player, player, true);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void chunkChange(EntityEvent.EnteringSection event) {
		Entity entity = event.getEntity();
		if(event.didChunkChange() && !entity.isPassenger()) {
			if(entity instanceof Player player) {
				BorderData border = getBorderData(player);
				ChunkPos oldChunk = event.getOldPos().chunk();
				ChunkPos newChunk = event.getNewPos().chunk();
				if(!border.isWithinBounds(newChunk) && border.isWithinBounds(oldChunk))
					border.putWithinChunk(player, oldChunk, true);
				if(!border.isWithinBounds(newChunk) && !border.isWithinBounds(oldChunk))
					border.putWithinBounds(player, player, false);
			} else if(entity.isVehicle()) {
				for(Entity rider : entity.getPassengers()) {
					if(rider instanceof Player player) {
						BorderData border = getBorderData(player);
						ChunkPos oldChunk = event.getOldPos().chunk();
						ChunkPos newChunk = event.getNewPos().chunk();
						if(!border.isWithinBounds(newChunk) && border.isWithinBounds(oldChunk))
							border.putWithinChunk(entity, oldChunk, true);
						if(!border.isWithinBounds(newChunk) && !border.isWithinBounds(oldChunk))
							border.putWithinBounds(player, entity, false);
						break;
					}
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if(isWithinRange("Block-RightClick", event))
			return;
		clickEvent(event);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		if(isWithinRange("Block-LeftClick", event))
			return;
		clickEvent(event);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onEntityAttacked(LivingAttackEvent event) {
		DamageSource source = event.getSource();
		if(isWithinRange("Entity-LeftClick", event))
			return;
			
		if(source.getDirectEntity() instanceof Player player) {
			if(source.isIndirect())
				return;
			BorderData border = getBorderData(player);
			BlockPos pos = event.getEntity().blockPosition();
			event.setCanceled(!border.isWithinBounds(pos));
		}
	}

	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
		if(isWithinRange("Entity-RightClick", event)) {
			final Player player = event.getEntity();
			final Entity mount = player.getVehicle();
			ChunkbychunkMod.queueServerWork(1, () -> {
				BorderUtils.checkMounted(player, mount);
			});
			return;
		}
		
		clickEvent(event);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if(isWithinRange("Item-RightClick", event))
			return;
		clickEvent(event);
	}

	public static boolean isAdjacentChunk(Player player, BlockPos pos) {
		ChunkPos playerChunk = new ChunkPos(player.blockPosition());
		ChunkPos blockChunk = new ChunkPos(pos);
		return playerChunk.x == blockChunk.x || playerChunk.z == blockChunk.z;
	}

	public static boolean isWithinRange(String type, Event event) {
		if(event instanceof PlayerInteractEvent playerEvent) {
			Player player = playerEvent.getEntity();
			BorderData border = getBorderData(player);
			BlockPos pos = playerEvent.getPos();
			double maxRange = ConfiUtils.getRange(type);
			double distance = border.getDistance(pos);
			if(!isAdjacentChunk(player, pos))
				distance /= 2;
			return maxRange == -1 ? true : Math.ceil(distance) <= maxRange;
		} else if(event instanceof LivingAttackEvent attackEvent) {
			DamageSource source = attackEvent.getSource();
			boolean isDirect = !source.isIndirect();
			String attackType = isDirect ? "Entity-DirectAttack" : "Entity-IndirectAttack";
			Entity attacker = isDirect ? source.getEntity() : source.getDirectEntity();
			if(attacker instanceof Player player) {
				double maxRange = ConfiUtils.getRange(attackType);
				BorderData border = getBorderData(player);
				return maxRange == -1 ? true : border.getDistance(attackEvent.getEntity()) <= maxRange;
			}
			return true;
		}

		return false;
	}

	public static void clickEvent(PlayerInteractEvent event) {
		Player player = event.getEntity();
		BorderData border = getBorderData(player);
		((ICancellableEvent) event).setCanceled(!border.isWithinBounds(event.getPos()));
	}

	public static BorderData getBorderData(Player player) {
		PlayerVariables.Variables variables = player.getData(PlayerVariables.VARIABLES);
		if(variables.borderData == null || variables.borderData.Chunks.size() == 0)
			variables.borderData = new BorderData(player);
		variables.borderData.player = player;
		variables.syncVariables(player);
		return variables.borderData;
	}
}