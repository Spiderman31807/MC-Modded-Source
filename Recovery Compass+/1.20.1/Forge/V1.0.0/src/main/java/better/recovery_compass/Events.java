package better.recovery_compass;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;

import better.recovery_compass.init.BetterRecoverycompassModGameRules;
import better.recovery_compass.block.entity.RecoveryStoneBlockEntity;

@Mod.EventBusSubscriber
public class Events {
	@SubscribeEvent
	public static void dropItems(LivingDropsEvent event) {
		ItemStack recoveryCompass = null;
		ArrayList<ItemEntity> items = new ArrayList();
		for (ItemEntity entity : event.getDrops()) {
			items.add(entity);
			ItemStack stack = entity.getItem();
			if (stack.getItem() == Items.RECOVERY_COMPASS) {
				if (stack.getOrCreateTag().getBoolean("RecoverystoneTracked") && Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, stack.getOrCreateTag().get("RecoverystoneDimension")).result().isPresent())
					recoveryCompass = stack;
			}
		}
		if (recoveryCompass == null)
			return;
		final ItemStack compass = recoveryCompass;
		CompoundTag stoneData = recoveryCompass.getTag();
		ResourceKey<Level> dimension = Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, stoneData.get("RecoverystoneDimension")).result().get();
		ServerLevel world = event.getEntity().level().getServer().getLevel(dimension);
		BlockPos pos = NbtUtils.readBlockPos(stoneData.getCompound("RecoverystonePos"));
		event.getEntity().level().playSound(null, event.getEntity().blockPosition(), SoundEvents.TOTEM_USE, event.getEntity().getSoundSource(), 0.5f, 2f);
		boolean consumeOnUse = world.getGameRules().getBoolean(BetterRecoverycompassModGameRules.CONSUME_RECOVERY_COMPASS);
		if (world.getBlockEntity(pos) instanceof RecoveryStoneBlockEntity recoveryStone) {
			world.playSound(null, pos, SoundEvents.TOTEM_USE, SoundSource.BLOCKS, 0.5f, 2f);
			items.forEach((entity) -> recoveryStone.addItem(entity, consumeOnUse ? compass : null, world, pos));
		}
	}
}
