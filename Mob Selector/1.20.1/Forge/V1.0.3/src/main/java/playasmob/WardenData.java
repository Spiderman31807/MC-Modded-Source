package playasmob;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.sounds.SoundEvent;

public class WardenData implements VibrationSystem.User, VibrationSystem, EntityTypeData, GolemAttack {
	public static final MobEffectInstance darknessEffect = new MobEffectInstance(MobEffects.DARKNESS, -1, 0, false, false, false);

	public MobData data;
   	public AnimationState roarAnimationState = new AnimationState();
   	public AnimationState sniffAnimationState = new AnimationState();
   	public AnimationState emergeAnimationState = new AnimationState();
   	public AnimationState diggingAnimationState = new AnimationState();
   	public AnimationState attackAnimationState = new AnimationState();
   	public AnimationState sonicBoomAnimationState = new AnimationState();
  	public final DynamicGameEventListener<VibrationSystem.Listener> dynamicGameEventListener;
  	public ArrayList<UUID> highlightMobs = new ArrayList();
   	public VibrationSystem.Data vibrationData;
   	public int tendrilAnimation = 0;
   	public int tendrilAnimationO = 0;
   	public int heartAnimation = 0;
   	public int heartAnimationO = 0;
   	public int highlightTicks = 0;
   	public int sniffAnimation = 0;
   	public int attackAnimation = 0;
   	public int roarAnimation = 0;
   	public int sonicAnimation = 0;
   	public int sniffCooldown = 0;
   	public int roarCooldown = 0;
   	public int sonicCooldown = 0;
   	public int attackCooldown = 0;
   	public UUID sonicTarget;

   	public List<MobAttribute> getAbilities(EntityType type) {
   		ArrayList<MobAttribute> abilities = new ArrayList();
   		abilities.add(MobAttribute.ability(1, Component.translatable("playasmob.ability.sniff")));
   		abilities.add(MobAttribute.ability(2, Component.translatable("playasmob.ability.roar")));
   		abilities.add(MobAttribute.ability(3, Component.translatable("playasmob.ability.sonicboom")));
   		return abilities;
   	}

   	public List<MobAttribute> getPros(EntityType type) {
   		ArrayList<MobAttribute> pros = new ArrayList();
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.health")));
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.fire_immune")));
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.sprint")));
   		return pros;
   	}

   	public List<MobAttribute> getInfo(EntityType type) {
   		ArrayList<MobAttribute> info = new ArrayList();
   		return info;
   	}

   	public List<MobAttribute> getCons(EntityType type) {
   		ArrayList<MobAttribute> cons = new ArrayList();
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.blind")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.crouch")));
   		return cons;
   	}
	
	public WardenData(MobData data) {
		this.data = data;
      	this.vibrationData = new VibrationSystem.Data();
      	this.dynamicGameEventListener = new DynamicGameEventListener<>(new VibrationSystem.Listener(this));
	}

	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
		int tick = data.player == null ? 0 : data.player.tickCount;
		
		if(this.tendrilAnimation > 0)
			compound.putInt("tendrilAnimation", this.tendrilAnimation);
		if(this.tendrilAnimationO > 0)
			compound.putInt("tendrilAnimationO", this.tendrilAnimationO);
		if(this.heartAnimation > 0)
			compound.putInt("heartAnimation", this.heartAnimation);
		if(this.heartAnimationO > 0)
			compound.putInt("heartAnimationO", this.heartAnimationO);
		if(this.sniffCooldown > 0)
			compound.putInt("sniffCooldown", this.sniffCooldown);
		if(this.roarCooldown > 0)
			compound.putInt("roarCooldown", this.roarCooldown);
		if(this.sonicCooldown > 0)
			compound.putInt("sonicCooldown", this.sonicCooldown);
		if(this.attackCooldown > 0)
			compound.putInt("attackCooldown", this.attackCooldown);

		if(this.sniffAnimation > 0)
			compound.putInt("sniffAnimation", this.sniffAnimation);
		if(this.roarAnimation > 0)
			compound.putInt("roarAnimation", this.roarAnimation);
		if(this.attackAnimation > 0)
			compound.putInt("attackAnimation", this.attackAnimation);
		if(this.sonicAnimation > 0) {
			compound.putInt("sonicAnimation", this.sonicAnimation);
			if(this.sonicTarget != null)
				compound.putUUID("sonicTarget", this.sonicTarget);
		}

		if(this.highlightTicks > 0)
			compound.put("highlightData", this.saveHighlight());

		return compound;
	}

	public CompoundTag saveHighlight() {
		int index = 1;
		CompoundTag compound = new CompoundTag();
		compound.putInt("highlightTicks", this.highlightTicks);
		for(UUID uuid : this.highlightMobs) {
			compound.putUUID("entity" + index, uuid);
			index++;
		}
		return compound;
	}

	public void load(CompoundTag compound) {
		int tick = data.player == null ? 0 : data.player.tickCount;
		this.tendrilAnimation = compound.getInt("tendrilAnimation");
		this.tendrilAnimationO = compound.getInt("tendrilAnimationO");
		this.heartAnimation = compound.getInt("heartAnimation");
		this.heartAnimationO = compound.getInt("heartAnimationO");
		this.sniffCooldown = compound.getInt("sniffCooldown");
		this.roarCooldown = compound.getInt("roarCooldown");
		this.sonicCooldown = compound.getInt("sonicCooldown");
		this.attackCooldown = compound.getInt("attackCooldown");
		
		this.sniffAnimation = compound.getInt("sniffAnimation");
		this.roarAnimation = compound.getInt("roarAnimation");
		this.sonicAnimation = compound.getInt("sonicAnimation");
		this.attackAnimation = compound.getInt("attackAnimation");

		if(compound.contains("sonicTarget"))
			this.sonicTarget = compound.getUUID("sonicTarget");

		if(this.sniffAnimation > 0)
			this.sniffAnimationState.start(this.sniffAnimation);
		if(this.roarAnimation > 0)
			this.roarAnimationState.start(this.roarAnimation);
		if(this.sonicAnimation > 0)
			this.sonicBoomAnimationState.start(this.sonicAnimation);
		if(this.attackAnimation > 0)
			this.attackAnimationState.start(this.attackAnimation);
			
		if(compound.contains("highlightData"))
			this.loadHighlight(compound.getCompound("highlightData"));
	}

	public void loadHighlight(CompoundTag compound) {
		this.highlightTicks = compound.getInt("highlightTicks");
		
		this.highlightMobs = new ArrayList();
		for(int idx = 1; idx < compound.size(); idx++) {
			this.highlightMobs.add(compound.getUUID("entity" + idx));
		}
	}

	public boolean allowTargeting(LivingEntity entity) {
		return !(entity instanceof Warden);
	}

	public boolean canSprint() {
		return true;
	}

	public void renderInfo(GuiGraphics graphics, int width, int height) {
		boolean useable = !(this.sniffAnimation > 0 || this.roarAnimation > 0 || this.sonicAnimation > 0 || this.attackAnimation > 0);
		RenderUtils.abilityIcon(graphics, "warden", useable, width - 30, height - 22, this.sniffCooldown, Keybinds.Ability1);
		RenderUtils.abilityIcon(graphics, "warden_roar", useable, width - 30, height - 40, this.roarCooldown, Keybinds.Ability2);
		RenderUtils.abilityIcon(graphics, "sonic", useable, width - 30, height - 58, this.sonicCooldown, Keybinds.Ability3);
	}

	public void login() {
		this.sniffAnimation = 0;
		this.roarAnimation = 0;
		this.sonicAnimation = 0;
		data.updateReach();
		data.sync(false, false);
	}

	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.WARDEN_HURT;
	}

	public SoundEvent getDeathSound() {
		return SoundEvents.WARDEN_DEATH;
	}

	public SoundEvent getStepSound() {
		return SoundEvents.WARDEN_STEP;
	}

	public float stepVolume() {
		return 10f;
	}

	public void activate() {
		if(data.player == null)
			return;
		data.player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, -1, 0, false, false, false));
	}

	public void deactivate() {
		if(data.player == null)
			return;
		data.player.removeEffect(MobEffects.DARKNESS);
	}

	public double entityReach() {
		return this.sniffAnimation > 0 || this.roarAnimation > 0 || this.sonicAnimation > 0 || this.attackAnimation > 0 ? 0 : 3;
	}

	public double blockReach() {
		return this.sniffAnimation > 0 || this.roarAnimation > 0 || this.sonicAnimation > 0 || this.attackAnimation > 0 ? 0 : 4.5;
	}

	public void attackEntity(LivingAttackEvent event) {
		if(this.attackCooldown > 0 && !event.getSource().isIndirect() && event.getEntity().getUUID() != this.sonicTarget) {
			event.setCanceled(true);
			return;
		}
		
		this.attackAnimation = data.player.tickCount;
		this.attackCooldown = 18;
	}

	public void press(int ability, boolean pressed) {
		if(!pressed)
			return;

		boolean useable = !(this.sniffAnimation > 0 || this.roarAnimation > 0 || this.sonicAnimation > 0 || this.attackAnimation > 0);
		if(ability == 1 && this.sniffCooldown == 0 && useable)
			this.startSniffing();
		if(ability == 2 && this.roarCooldown == 0 && useable)
			this.startRoaring();
		if(ability == 3 && this.sonicCooldown == 0 && useable)
			this.startCharging();
	}

	public void startSniffing() {
		this.sniffAnimation = data.player.tickCount;
		this.sniffAnimationState.start(data.player.tickCount);
		data.player.playSound(SoundEvents.WARDEN_SNIFF, 5, 1);
		data.updateReach();
	}

	public void startRoaring() {
		this.roarAnimation = data.player.tickCount;
		this.roarAnimationState.start(data.player.tickCount);
		data.player.playSound(SoundEvents.WARDEN_ROAR, 3, 1);
		data.updateReach();
    }

	public void startCharging() {
		this.sonicAnimation = data.player.tickCount;
		this.sonicBoomAnimationState.start(data.player.tickCount);
		data.player.playSound(SoundEvents.WARDEN_SONIC_CHARGE, 3, 1);
		data.updateReach();
		
   		if(GlobalUtils.getClosestTarget(data.player, 600) instanceof LivingEntity entity)
   			this.sonicTarget = entity.getUUID();
    }

	public void finishSniffing() {
		if(this.sniffCooldown > 0)
			return;
			
		this.sniffCooldown = 200;
		if(data.world != null)
      		this.refreshHighlights();
    }

	public void finishRoaring() {
		if(this.roarCooldown > 0)
			return;
			
		this.roarCooldown = 300;
		List<Entity> nearby = GlobalUtils.getNearby(data.player, 300, (entity) -> entity instanceof LivingEntity);
		for(Entity entity : nearby) {
			LivingEntity living = (LivingEntity)entity;
			living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 300));
			living.addEffect(new MobEffectInstance(Effects.Fear.get(), 100));
		}
    }

	public void finishSonicBoom() {
		if(this.sonicCooldown > 0)
			return;
			
		this.attackCooldown = 26;
		this.sonicCooldown = 40;
   		LivingEntity target = null;
   		if(GlobalUtils.getClosestTarget(data.player, 600) instanceof LivingEntity entity)
   			target = entity;

		if(data.world instanceof ServerLevel server) {
			if(target == null && this.sonicTarget != null)
				target = (LivingEntity)server.getEntity(this.sonicTarget);
			if(target == null || !data.player.closerThan(target, 15, 20))
				return;
			this.sonicTarget = target.getUUID();
			
            Vec3 vec3 = data.player.position().add(0.0D, (double)1.6F, 0.0D);
            Vec3 vec31 = target.getEyePosition().subtract(vec3);
            Vec3 vec32 = vec31.normalize();

            for(int i = 1; i < Mth.floor(vec31.length()) + 7; ++i) {
               Vec3 vec33 = vec3.add(vec32.scale((double)i));
               server.sendParticles(ParticleTypes.SONIC_BOOM, vec33.x, vec33.y, vec33.z, 1, 0, 0, 0, 0);
            }

            data.player.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3, 1);
            target.hurt(server.damageSources().sonicBoom(data.player), 10);
            double d1 = 0.5D * (1.0D - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            double d0 = 2.5D * (1.0D - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            target.push(vec32.x() * d0, vec32.y() * d1, vec32.z() * d0);
		}

		this.sonicTarget = null;
    }

	public void refreshHighlights() {
		this.highlightTicks = 200;
		this.highlightMobs = new ArrayList();
		for(LivingEntity entity : data.world.getEntitiesOfClass(LivingEntity.class, AABB.unitCubeFromLowerCorner(data.player.position()).inflate(100, 50, 100), EntitySelector.NO_SPECTATORS)) {
			if(entity == data.player)
				continue;
			this.highlightMobs.add(entity.getUUID());
		}
	}

	public boolean renderEntity(LivingEntity entity) {
		return entity == data.player || data.player.closerThan(entity, 5) || this.forceGlow(entity);
	}

	public boolean forceGlow(LivingEntity entity) {
		return !data.player.closerThan(entity, 5) && data.player.closerThan(entity, 30) && this.highlightMobs.contains(entity.getUUID());
	}

	public void attacked(LivingAttackEvent event) {
		DamageSource source = event.getSource();
		
		if(source.is(DamageTypeTags.IS_FIRE)) {
			event.setCanceled(true);
			return;
		}
		
		if(this.isDiggingOrEmerging()) {
			event.setCanceled(true);
			return;
		}
	}

	public void preTick() {
      	if (data.world instanceof ServerLevel serverlevel)
         	VibrationSystem.Ticker.tick(serverlevel, this.vibrationData, this);

        if (data.player.tickCount % this.getHeartBeatDelay() == 0) {
            this.heartAnimation = 10;
            data.player.playSound(SoundEvents.WARDEN_HEARTBEAT, 5, data.player.getVoicePitch());
        }

        this.tendrilAnimationO = this.tendrilAnimation;
        if (this.tendrilAnimation > 0)
        	--this.tendrilAnimation;
        this.heartAnimationO = this.heartAnimation;
        if (this.heartAnimation > 0)
            --this.heartAnimation;
            
        if(this.highlightTicks > 0) {
        	--this.highlightTicks;
        } else if(this.highlightMobs.size() > 0) {
        	this.highlightMobs = new ArrayList();
        }

        if(this.sniffCooldown > 0)
        	--this.sniffCooldown;
        if(this.roarCooldown > 0)
        	--this.roarCooldown;
        if(this.sonicCooldown > 0)
        	--this.sonicCooldown;
        if(this.attackCooldown > 0)
        	--this.attackCooldown;

		if(!data.player.hasEffect(MobEffects.DARKNESS))
			data.player.addEffect(darknessEffect);

        switch (data.player.getPose()) {
        	case EMERGING:
               	this.clientDiggingParticles(this.emergeAnimationState);
               	break;
           	case DIGGING:
               	this.clientDiggingParticles(this.diggingAnimationState);
        }

		int tick = data.player.tickCount;
        if(this.sniffAnimation > 0 && (this.sniffAnimation - tick) == -50)
			this.finishSniffing();
		if(this.roarAnimation > 0 && (this.roarAnimation - tick) == -60)
			this.finishRoaring();
		if(this.sonicAnimation > 0 && (this.sonicAnimation - tick) == -34)
			this.finishSonicBoom();
			
		if(this.sniffAnimation > 0 && (this.sniffAnimation - tick) <= -60) {
			this.sniffAnimation = 0;
			data.updateReach();
		}
		if(this.roarAnimation > 0 && (this.roarAnimation - tick) <= -80) {
			this.roarAnimation = 0;
			data.updateReach();
		}
		if(this.sonicAnimation > 0 && (this.sonicAnimation - tick) <= -50) {
			this.sonicAnimation = 0;
			data.updateReach();
		}
		if(this.attackAnimation > 0 && (this.attackAnimation - tick) <= -30) {
			this.attackAnimation = 0;
			data.updateReach();
		}
   	}

   	public void respawn() {
   		this.sniffAnimation = 0;
   		this.roarAnimation = 0;
   		this.sonicAnimation = 0;
   		this.sonicTarget = null;
   		this.sniffCooldown = 0;
   		this.roarCooldown = 0;
   		this.sonicCooldown = 0;
   	}
	
	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder wardenBuilder = Warden.createAttributes();
		playerBuilder.combine(wardenBuilder);
		return playerBuilder;
	}

	public double speedMultiplier() {
		if(this.sniffAnimation > 0 || this.roarAnimation > 0)
			return 0;
		if(this.sonicAnimation > 0)
			return data.isSprinting ? 0.28 : 0.08;
		return data.isSprinting ? 0.32 : 0.1;
	}

	public double fovMultiplier() {
		return this.speedMultiplier() / (data.isSprinting ? 22 : 20);
	}

	public EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		return new WardenRenderer(context);
	}

	public ModelLayerLocation getModelLayer(boolean outer) {
		return ModelLayers.WARDEN;
	}
	
	public HandData getHand(EntityRendererProvider.Context context) {
		HandData hand = new HandData(context, this, new WardenHand(context.bakeLayer(ModelLayers.WARDEN), false), false);
		this.setupHand(hand);
		return hand;
	}

	public void setupHand(HandData hand) {
		hand.texture = new ResourceLocation("playasmob:textures/entities/warden.png");
		hand.setScale(0.7f, 0.7f, 0.7f);
		hand.setPosition(3.6f, 25f, 0.6f);
		hand.setRotation(3.2f, -1.6f, 0.05f);
	}

   	public boolean isDiggingOrEmerging() {
      	return data.player.hasPose(Pose.DIGGING) || data.player.hasPose(Pose.EMERGING);
   	}

   	public boolean dampensVibrations() {
      	return true;
   	}
   	
   	public int getHeartBeatDelay() {
      	return 40;
   	}

   	public float getTendrilAnimation(float p_219468_) {
      	return Mth.lerp(p_219468_, (float)this.tendrilAnimationO, (float)this.tendrilAnimation) / 10.0F;
   	}

   	public float getHeartAnimation(float p_219470_) {
      	return Mth.lerp(p_219470_, (float)this.heartAnimationO, (float)this.heartAnimation) / 10.0F;
   	}

   	public void clientDiggingParticles(AnimationState state) {
      	if ((float)state.getAccumulatedTime() < 4500.0F) {
         	RandomSource randomsource = data.player.getRandom();
         	BlockState blockstate = data.player.getBlockStateOn();
         	if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            	for(int i = 0; i < 30; ++i) {
               		double d0 = data.player.getX() + (double)Mth.randomBetween(randomsource, -0.7F, 0.7F);
               		double d1 = data.player.getY();
               		double d2 = data.player.getZ() + (double)Mth.randomBetween(randomsource, -0.7F, 0.7F);
               		data.world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), d0, d1, d2, 0.0D, 0.0D, 0.0D);
            	}
         	}
      	}
   	}

   	public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> dynamicEvent) {
      	if (data.world instanceof ServerLevel serverlevel)
         	dynamicEvent.accept(this.dynamicGameEventListener, serverlevel);
   	}
   	
   	public EntityDimensions getDimensions(Pose pose) {
      	EntityDimensions entitydimensions = data.renderAs.getDimensions();
      	return this.isDiggingOrEmerging() ? EntityDimensions.fixed(entitydimensions.width, 1.0F) : entitydimensions;
   	}

   	public VibrationSystem.Data getVibrationData() {
      	return this.vibrationData;
   	}

   	public VibrationSystem.User getVibrationUser() {
      	return this;
   	}

   	public int getListenerRadius() {
		return 16;
 	}
	
	public PositionSource getPositionSource() {
		return new EntityPositionSource(data.player, data.player.getEyeHeight());
	}
	
	public TagKey<GameEvent> getListenableEvents() {
	    return GameEventTags.WARDEN_CAN_LISTEN;
	}

	public boolean canReceiveVibration(ServerLevel server, BlockPos pos, GameEvent event, GameEvent.Context context) {
		return true;
	}

	public void onReceiveVibration(ServerLevel server, BlockPos pos, GameEvent event, @Nullable Entity entity1, @Nullable Entity entity2, float value) {
		PlayasmobMod.LOGGER.info("received vibration");
		if (!data.player.isDeadOrDying()) {
	    	this.tendrilAnimation = 10;
	        data.player.playSound(SoundEvents.WARDEN_TENDRIL_CLICKS, 5, data.player.getVoicePitch());
	    }
	}
}