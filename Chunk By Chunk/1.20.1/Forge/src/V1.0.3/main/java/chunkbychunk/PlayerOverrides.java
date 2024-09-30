package chunkbychunk;

import chunkbychunk.configuration.ServerConfiConfiguration;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.TickEvent;

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
public class PlayerOverrides {
	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingTickEvent event) {
		Entity entity = event.getEntity();
		if (entity == null)
			return;
			
		CompoundTag data = entity.getPersistentData();
		CompoundTag speedData = new CompoundTag();
		Vec3 Pos = new Vec3(entity.getX(), entity.getY(), entity.getZ());
		if(data.contains("SpeedData")) {
			CompoundTag SpeedData = data.getCompound("SpeedData");
			Vec3 pastPos = new Vec3(SpeedData.getDouble("X"), SpeedData.getDouble("Y"), SpeedData.getDouble("Z"));
			float Distance = (float)pastPos.distanceTo(Pos);
			speedData.putFloat("speed", Distance);
		}

		speedData.putDouble("X", entity.getX());
		speedData.putDouble("Y", entity.getY());
		speedData.putDouble("Z", entity.getZ());
		data.put("SpeedData", speedData);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPlayerXPChange(PlayerXpEvent.PickupXp event) {
		Player player = event.getEntity();
		BorderData border = getBorderData(player);
		border.addXP(event.getOrb().getValue());
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			Player player = event.player;
			PlayerVariables.Variables variables = player.getCapability(PlayerVariables.CAPABILITY, null).orElse(new PlayerVariables.Variables());
			if(variables.borderData == null || variables.borderData.Chunks.size() == 0)
				variables.borderData = new BorderData(player);
			variables.borderData.player = player;

			if(variables.lastChunk.isPresent() && !variables.borderData.hasChunk(variables.lastChunk.get()))
				variables.lastChunk = Optional.empty();

			if(player.isPassenger()) {
				Entity mount = player.getVehicle();
				if(variables.borderData.isWithinBounds(mount)) {
					variables.lastChunk = Optional.of(new ChunkPos(mount.blockPosition()));
				} else {
					variables.borderData.putWithinBounds(player, mount);
				}
			} else {
				if(variables.borderData.isWithinBounds(player)) {
					variables.lastChunk = Optional.of(new ChunkPos(player.blockPosition()));
				} else if(!player.isSpectator()) {
					variables.borderData.putWithinBounds(player, player);
				}
			}
			
			variables.syncVariables(player);
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
		if(isWithinRange("Entity-RightClick", event))
			return;
		clickEvent(event);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if(isWithinRange("Item-RightClick", event))
			return;
		clickEvent(event);
	}

	public static boolean isWithinRange(String type, Event event) {
		if(event instanceof PlayerInteractEvent playerEvent) {
			BorderData border = getBorderData(playerEvent.getEntity());
			BlockPos pos = playerEvent.getPos();
			int maxRange = ConfiUtils.getRange(type);
			return maxRange == -1 ? true : border.getDistance(pos) <= maxRange;
		} else if(event instanceof LivingAttackEvent attackEvent) {
			DamageSource source = attackEvent.getSource();
			boolean isDirect = !source.isIndirect();
			String attackType = isDirect ? "Entity-DirectAttack" : "Entity-IndirectAttack";
			Entity attacker = isDirect ? source.getEntity() : source.getDirectEntity();
			if(attacker instanceof Player player) {
				int maxRange = ConfiUtils.getRange(attackType);
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
		event.setCanceled(!border.isWithinBounds(event.getPos()));
	}

	public static BorderData getBorderData(Player player) {
		PlayerVariables.Variables variables = player.getCapability(PlayerVariables.CAPABILITY, null).orElse(new PlayerVariables.Variables());
		if(variables.borderData == null || variables.borderData.Chunks.size() == 0)
			variables.borderData = new BorderData(player);
		variables.borderData.player = player;
		variables.syncVariables(player);
		return variables.borderData;
	}
}
