package playasmob;

import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability;

import java.util.function.Supplier;
import java.util.Optional;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ChunkPos;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MobVars {
	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		PlayasmobMod.addNetworkMessage(Sync.class, Sync::buffer, Sync::new, Sync::handler);
	}

	@SubscribeEvent
	public static void init(RegisterCapabilitiesEvent event) {
		event.register(Data.class);
	}

	@Mod.EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void login(PlayerEvent.PlayerLoggedInEvent event) {
			boolean spawnedAlready = true;
			if (!event.getEntity().level().isClientSide()) {
				Data data = event.getEntity().getCapability(MobData, null).orElse(new Data());
				spawnedAlready = data.hasSpawned;
				data.sync(event.getEntity());
			}

			final boolean spawned = spawnedAlready;
			PlayasmobMod.queueServerWork(1, () -> {
				if(spawned) {
					event.getEntity().refreshDimensions();
					playasmob.MobData.get(event.getEntity()).updateAttributes();
				} else {
					MobSelectionMenu.openSelector(event.getEntity());
				}
			});
		}

		@SubscribeEvent
		public static void respawn(PlayerEvent.PlayerRespawnEvent event) {
			if (!event.getEntity().level().isClientSide())
				((Data) event.getEntity().getCapability(MobData, null).orElse(new Data())).mobData.respawn();
			if(event.isEndConquered()) {
				PlayasmobMod.queueServerWork(1, () -> {
					MobSelectionMenu.openSelector(event.getEntity());
				});
			}
		}

		@SubscribeEvent
		public static void worldChange(PlayerEvent.PlayerChangedDimensionEvent event) {
			final Player player = event.getEntity();
			if (!player.level().isClientSide())
				((Data) player.getCapability(MobData, null).orElse(new Data())).sync(player);
			PlayasmobMod.queueServerWork(1, () -> {
				playasmob.MobData.get(player).worldChange(event);
			});
		}

		@SubscribeEvent
		public static void clone(PlayerEvent.Clone event) {
			event.getOriginal().revive();
			Data original = ((Data) event.getOriginal().getCapability(MobData, null).orElse(new Data()));
			Data clone = ((Data) event.getEntity().getCapability(MobData, null).orElse(new Data()));
			clone.mobData = original.mobData;
			clone.hasSpawned = original.hasSpawned;
		}
	}

	public static final Capability<Data> MobData = CapabilityManager.get(new CapabilityToken<Data>() {
	});

	@Mod.EventBusSubscriber
	private static class Provider implements ICapabilitySerializable<Tag> {
		@SubscribeEvent
		public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
			if (event.getObject() instanceof Player && !(event.getObject() instanceof FakePlayer))
				event.addCapability(new ResourceLocation("mob_selector", "data"), new Provider());
		}

		private final Data data = new Data();
		private final LazyOptional<Data> instance = LazyOptional.of(() -> data);

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			return cap == MobData ? instance.cast() : LazyOptional.empty();
		}

		@Override
		public Tag serializeNBT() {
			return data.writeNBT();
		}

		@Override
		public void deserializeNBT(Tag nbt) {
			data.readNBT(nbt);
		}
	}

	public static class Data {
		public MobData mobData = new MobData();
		public boolean hasSpawned = false;

		public void sync(Entity entity) {
			if (entity instanceof ServerPlayer serverPlayer)
				PlayasmobMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new Sync(this));
		}

		public Tag writeNBT() {
			CompoundTag nbt = new CompoundTag();
			nbt.put("info", mobData.save());
			return nbt;
		}

		public void readNBT(Tag tag) {
			CompoundTag nbt = (CompoundTag) tag;
			if(nbt.contains("info")) {
				mobData = new MobData(nbt.getCompound("info"));
				hasSpawned = true;
			}
		}
	}

	public static class Sync {
		private final Data data;

		public Sync(FriendlyByteBuf buffer) {
			this.data = new Data();
			this.data.readNBT(buffer.readNbt());
		}

		public Sync(Data data) {
			this.data = data;
		}

		public static void buffer(Sync message, FriendlyByteBuf buffer) {
			buffer.writeNbt((CompoundTag) message.data.writeNBT());
		}

		public static void handler(Sync message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				if (!context.getDirection().getReceptionSide().isServer()) {
					Data data = ((Data) Minecraft.getInstance().player.getCapability(MobData, null).orElse(new Data()));
					data.mobData = message.data.mobData;
					data.hasSpawned = message.data.hasSpawned;
				}
			});
			context.setPacketHandled(true);
		}
	}
}