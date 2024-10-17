package playasmob;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.entity.MobType;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderArmEvent;

public interface EntityTypeData {
	default MobType getMobType() {
		return MobType.UNDEFINED;
	}

	default void deactivate() {
	}

	default void activate() {
	}
	
	default void load(CompoundTag compound) {
	}

	default CompoundTag save() {
		return new CompoundTag();
	}

	default List<MobAttribute> getAbilities(EntityType type) {
		return new ArrayList();
	}

	default List<MobAttribute> getPros(EntityType type) {
		return new ArrayList();
	}

	default List<MobAttribute> getInfo(EntityType type) {
		return new ArrayList();
	}

	default List<MobAttribute> getCons(EntityType type) {
		return new ArrayList();
	}
	
	default void addAttributeInfo(MobPreview preview) {
		preview.abilityData = this.getAbilities(preview.mob);
		preview.proData = this.getPros(preview.mob);
		preview.infoData = this.getInfo(preview.mob);
		preview.conData = this.getCons(preview.mob);
		preview.updateAbilities();
	}

	default void preTick() {
	}

	default void postTick() {
	}
	
   	default void thunderHit(ServerLevel server, LightningBolt bolt) {
   	}
	
   	default void renderInfo(GuiGraphics graphics, int width, int height) {
   	}
	
	default void press(int ability, boolean pressed) {
	}

	default void login() {
	}

	default void respawn() {
	}

	default EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		return null;
	}

   	default EntityDimensions getDimensions(Pose pose) {
		return null;
	}

	default float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return pose == Pose.SLEEPING ? 0.2f : dimensions.height * 0.85f;
	}

	default ModelLayerLocation getModelLayer(boolean outer) {
		return ModelLayers.PLAYER;
	}

	default HandData getHand(EntityRendererProvider.Context context) {
		HandData hand = new HandData(context, this);
		this.setupHand(hand);
		return hand;
	}

	default HandData getHand() {
		HandData hand = new HandData();
		this.setupHand(hand);
		return hand;
	}

	default double entityReach() {
		return 3;
	}

	default double blockReach() {
		return !this.hasHand() ? 0 : 4.5;
	}

	default boolean hasHand() {
		return this.renderHand();
	}

	default boolean renderHand() {
		return this.getHand().texture != null;
	}

	default void setupHand(HandData hand) {
	}

	default boolean canPickup(ItemStack stack) {
		return this.hasHand();
	}

	default boolean allowTargeting(LivingEntity entity) {
		return true;
	}

	default boolean renderEntity(LivingEntity entity) {
		return true;
	}

	default boolean forceGlow(LivingEntity entity) {
		return false;
	}

	default boolean isShaking() {
		return false;
	}

	default boolean canSprint() {
		return false;
	}

	default boolean canCrouch() {
		return false;
	}

	default boolean canJump() {
		return this.speedMultiplier() > 0;
	}

	default boolean preventSleeping() {
		return true;
	}

	default AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder attributes) {
		return attributes;
	}

	default double speedMultiplier() {
		return 1;
	}

	default double fovMultiplier() {
		return this.speedMultiplier() / 8;
	}

	default double selectionSizeMultiplier() {
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

	default boolean isFood(ItemStack stack) {
		return true;
	}

	default void usedItem(LivingEntityUseItemEvent.Finish event) {
	}

	default void useItem(LivingEntityUseItemEvent.Start event) {
	}

	default void itemInteraction(PlayerInteractEvent.RightClickItem event) {
		this.canEatItem(event);
	}

	default void canEatItem(PlayerInteractEvent.RightClickItem event) {
		ItemStack stack = event.getEntity().getItemInHand(event.getHand());
		if(stack.getItem().isEdible())
			event.setCanceled(!this.isFood(stack));
	}

	default void entityInteraction(PlayerInteractEvent.EntityInteract event) {
		if(!this.hasHand())
			event.setCanceled(true);
	}

	default void mobInteract(Player player, InteractionHand hand) {
	}

	default void killedEntity(LivingDeathEvent event) {
	}

	default void hurtEntity(LivingHurtEvent event) {
	}

	default void killed(LivingDeathEvent event) {
	}

	default void hurt(LivingHurtEvent event) {
	}

	default void attackEntity(LivingAttackEvent event) {
	}

	default void attacked(LivingAttackEvent event) {
	}

	default void healed(LivingHealEvent event) {
	}
	
	default void causeFallDamage(LivingFallEvent event) {
	}

	default void getProjectile(LivingGetProjectileEvent event) {
	}

	default void addEffect(MobEffectEvent.Added effect) {
	}

	default boolean allowEffect(MobEffectInstance effect) {
		return true;
	}
}
