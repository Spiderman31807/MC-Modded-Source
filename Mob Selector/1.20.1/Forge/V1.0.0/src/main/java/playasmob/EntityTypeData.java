package playasmob;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import java.util.List;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.entity.MobType;

public interface EntityTypeData<R extends EntityRenderer> {
	MobData data = null;

	default MobType getMobType() {
		return MobType.UNDEFINED;
	}
	
	default void load(CompoundTag compound) {
	}

	default CompoundTag save() {
		return new CompoundTag();
	}

	default void tick() {
	}
	
   	default void thunderHit(ServerLevel server, LightningBolt bolt) {
   	}
	
	default void press(int ability, boolean pressed) {
	}

	default void respawn() {
	}

	default R getRenderer(EntityRendererProvider.Context context) {
		return null;
	}

	default float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return pose == Pose.SLEEPING ? 0.2f : dimensions.height * 0.85f;
	}

	default HandData getHand(EntityRendererProvider.Context context) {
		HandData hand = new HandData(context);
		this.setupHand(hand);
		return hand;
	}

	default HandData getHand() {
		HandData hand = new HandData();
		this.setupHand(hand);
		return hand;
	}

	default double attackReach() {
		return 3;
	}

	default boolean hasHand() {
		return false;
	}

	default void setupHand(HandData hand) {
	}

	default boolean canPickup(ItemStack stack) {
		return true;
	}

	default boolean isShaking() {
		return false;
	}

	default boolean canSprint() {
		return true;
	}

	default boolean canCrouch() {
		return true;
	}

	default boolean canJump() {
		return true;
	}

	default boolean preventSleeping() {
		return false;
	}

	default AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder attributes) {
		return attributes;
	}

	default double speedMultiplier() {
		return 1;
	}
	
   	default List<ResourceKey<Level>> spawnDimension() {
   		return List.of(Level.OVERWORLD);
   	}
	
   	default List<ResourceKey<Biome>> spawnBiomes() {
   		return GlobalData.allBiomes();
   	}
	
   	default List<ResourceKey<Structure>> spawnStructures() {
   		return List.of();
   	}

	default void usedItem(LivingEntityUseItemEvent.Finish event) {
	}

	default void useItem(LivingEntityUseItemEvent.Start event) {
	}

	default void itemInteraction(PlayerInteractEvent.RightClickItem event) {
	}

	default void entityInteraction(PlayerInteractEvent.EntityInteract event) {
	}

	default void killedEntity(LivingDeathEvent event) {
	}

	default void hurtEntity(LivingHurtEvent event) {
	}

	default void killed(LivingDeathEvent event) {
	}

	default void hurt(LivingHurtEvent event) {
	}

	default void attacked(LivingAttackEvent event) {
	}
	
	default void causeFallDamage(LivingFallEvent event) {
	}

	default void getProjectile(LivingGetProjectileEvent event) {
	}

	default boolean allowEffect(MobEffectInstance effect) {
		return true;
	}
}
