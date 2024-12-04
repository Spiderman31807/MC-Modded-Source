
package better.recovery_compass.block;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.Containers;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import com.mojang.logging.LogUtils;

import better.recovery_compass.block.entity.RecoveryStoneBlockEntity;

public class RecoveryStoneBlock extends Block implements EntityBlock {
	public RecoveryStoneBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.LODESTONE).pushReaction(PushReaction.BLOCK));
	}

	@Override
	public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
		InteractionResult result = InteractionResult.PASS;
		ItemStack stack = entity.getItemInHand(hand);
		if (!entity.isCrouching() && stack.getItem() == Items.RECOVERY_COMPASS) {
			CompoundTag data = stack.getOrCreateTag();
			world.playSound(null, pos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1, 1);
			data.put("RecoverystonePos", NbtUtils.writeBlockPos(pos));
			Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, world.dimension()).resultOrPartial(LogUtils.getLogger()::error).ifPresent((dimension) -> {
				data.put("RecoverystoneDimension", dimension);
			});
			data.putBoolean("RecoverystoneTracked", true);
			result = InteractionResult.SUCCESS;
		}
		if (result == InteractionResult.PASS && world.getBlockEntity(pos) instanceof RecoveryStoneBlockEntity stoneEntity) {
			if (stoneEntity.hasLoot == true) {
				world.playSound(null, pos, SoundEvents.BEEHIVE_EXIT, SoundSource.PLAYERS, 1f, 0.6f);
				stoneEntity.ejectContents(world, pos);
				result = InteractionResult.SUCCESS;
			}
		}
		return result == InteractionResult.PASS ? super.use(blockstate, world, pos, entity, hand, hit) : result;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new RecoveryStoneBlockEntity(pos, state);
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof RecoveryStoneBlockEntity be) {
				Containers.dropContents(world, pos, be);
				world.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos) {
		BlockEntity tileentity = world.getBlockEntity(pos);
		if (tileentity instanceof RecoveryStoneBlockEntity be)
			return AbstractContainerMenu.getRedstoneSignalFromContainer(be);
		else
			return 0;
	}
}
