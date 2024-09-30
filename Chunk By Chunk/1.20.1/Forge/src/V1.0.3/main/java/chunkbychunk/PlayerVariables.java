package chunkbychunk;

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

import java.util.function.Supplier;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ChunkPos;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlayerVariables {
	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		ChunkbychunkMod.addNetworkMessage(VariablesSync.class, VariablesSync::buffer, VariablesSync::new, VariablesSync::handler);
	}

	@SubscribeEvent
	public static void init(RegisterCapabilitiesEvent event) {
		event.register(Variables.class);
	}

	@Mod.EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
			if (!event.getEntity().level().isClientSide())
				((Variables) event.getEntity().getCapability(CAPABILITY, null).orElse(new Variables())).syncVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
			if (!event.getEntity().level().isClientSide())
				((Variables) event.getEntity().getCapability(CAPABILITY, null).orElse(new Variables())).syncVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (!event.getEntity().level().isClientSide())
				((Variables) event.getEntity().getCapability(CAPABILITY, null).orElse(new Variables())).syncVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void clonePlayer(PlayerEvent.Clone event) {
			event.getOriginal().revive();
			Variables original = ((Variables) event.getOriginal().getCapability(CAPABILITY, null).orElse(new Variables()));
			Variables clone = ((Variables) event.getEntity().getCapability(CAPABILITY, null).orElse(new Variables()));
			clone.borderData = original.borderData;
			clone.miniMap = original.miniMap;
		}
	}

	public static final Capability<Variables> CAPABILITY = CapabilityManager.get(new CapabilityToken<Variables>() {
	});

	@Mod.EventBusSubscriber
	private static class VariablesProvider implements ICapabilitySerializable<Tag> {
		@SubscribeEvent
		public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
			if (event.getObject() instanceof Player && !(event.getObject() instanceof FakePlayer))
				event.addCapability(new ResourceLocation("chunkbychunk", "variables"), new VariablesProvider());
		}

		private final Variables variables = new Variables();
		private final LazyOptional<Variables> instance = LazyOptional.of(() -> variables);

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			return cap == CAPABILITY ? instance.cast() : LazyOptional.empty();
		}

		@Override
		public Tag serializeNBT() {
			return variables.writeNBT();
		}

		@Override
		public void deserializeNBT(Tag nbt) {
			variables.readNBT(nbt);
		}
	}

	public static class Variables {
		public BorderData borderData = new BorderData();
		public Optional<ChunkPos> lastChunk = Optional.empty();
		public boolean miniMap = false;

		public void syncVariables(Entity entity) {
			if (entity instanceof ServerPlayer serverPlayer)
				ChunkbychunkMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new VariablesSync(this));
		}

		public Tag writeNBT() {
			CompoundTag nbt = new CompoundTag();
			nbt.put("borderData", borderData.saveTag());
			nbt.putBoolean("miniMap", miniMap);

			lastChunk.ifPresent((chunk) -> {
				nbt.putIntArray("lastChunk", new int[] { chunk.x, chunk.z });
			});	
			
			return nbt;
		}

		public void readNBT(Tag tag) {
			CompoundTag nbt = (CompoundTag) tag;
			if(nbt.contains("borderData"))
				borderData = new BorderData(nbt.getCompound("borderData"));
			if(nbt.contains("miniMap"))
				miniMap = nbt.getBoolean("miniMap");
			
			if(nbt.contains("lastChunk")) {
				int[] XZ = nbt.getIntArray("lastChunk");
				lastChunk = Optional.of(new ChunkPos(XZ[0], XZ[1]));
			}
		}
	}

	public static class VariablesSync {
		private final Variables data;

		public VariablesSync(FriendlyByteBuf buffer) {
			this.data = new Variables();
			this.data.readNBT(buffer.readNbt());
		}

		public VariablesSync(Variables data) {
			this.data = data;
		}

		public static void buffer(VariablesSync message, FriendlyByteBuf buffer) {
			buffer.writeNbt((CompoundTag) message.data.writeNBT());
		}

		public static void handler(VariablesSync message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				if (!context.getDirection().getReceptionSide().isServer()) {
					Variables variables = ((Variables) Minecraft.getInstance().player.getCapability(CAPABILITY, null).orElse(new Variables()));
					variables.borderData = message.data.borderData;
					variables.lastChunk = message.data.lastChunk;
					variables.miniMap = message.data.miniMap;
				}
			});
			context.setPacketHandled(true);
		}
	}
}