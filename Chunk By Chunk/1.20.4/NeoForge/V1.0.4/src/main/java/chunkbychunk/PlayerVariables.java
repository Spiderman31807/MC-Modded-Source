package chunkbychunk;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.function.Supplier;
import java.util.Optional;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.Minecraft;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlayerVariables {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ChunkbychunkMod.MODID);
	public static final Supplier<AttachmentType<Variables>> VARIABLES = ATTACHMENT_TYPES.register("player_variables", () -> AttachmentType.serializable(() -> new Variables()).build());

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		ChunkbychunkMod.addNetworkMessage(syncVariables.ID, syncVariables::new, syncVariables::handleData);
	}

	@Mod.EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				player.getData(VARIABLES).syncVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				player.getData(VARIABLES).syncVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				player.getData(VARIABLES).syncVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void clonePlayer(PlayerEvent.Clone event) {
			Variables original = event.getOriginal().getData(VARIABLES);
			Variables clone = new Variables();
			clone.borderData = original.borderData;
			clone.miniMap = original.miniMap;
			
			event.getEntity().setData(VARIABLES, clone);
		}
	}

	public static class Variables implements INBTSerializable<CompoundTag> {
		public BorderData borderData = new BorderData();
		public boolean miniMap = false;

		public CompoundTag serializeNBT() {
			CompoundTag nbt = new CompoundTag();
			nbt.put("borderData", borderData.saveTag());
			nbt.putBoolean("miniMap", miniMap);
			return nbt;
		}

		public void deserializeNBT(CompoundTag nbt) {
			if(nbt.contains("borderData"))
				borderData = new BorderData(nbt.getCompound("borderData"));
			if(nbt.contains("miniMap"))
				miniMap = nbt.getBoolean("miniMap");
		}

		public void syncVariables(Entity entity) {
			if (entity instanceof ServerPlayer serverPlayer)
				PacketDistributor.PLAYER.with(serverPlayer).send(new syncVariables(this));
		}
	}

	public record syncVariables(Variables data) implements CustomPacketPayload {
		public static final ResourceLocation ID = new ResourceLocation(ChunkbychunkMod.MODID, "player_variables_sync");

		public syncVariables(FriendlyByteBuf buffer) {
			this(new Variables());
			this.data.deserializeNBT(buffer.readNbt());
		}

		@Override
		public void write(final FriendlyByteBuf buffer) {
			buffer.writeNbt(data.serializeNBT());
		}

		@Override
		public ResourceLocation id() {
			return ID;
		}

		public static void handleData(final syncVariables message, final PlayPayloadContext context) {
			if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
				context.workHandler().submitAsync(() -> Minecraft.getInstance().player.getData(VARIABLES).deserializeNBT(message.data.serializeNBT())).exceptionally(e -> {
					context.packetHandler().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}
	}
}