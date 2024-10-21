package playasmob;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

public class EndermanData implements EntityTypeData, GolemAttack {
	public Optional<BlockState> carriedBlock = Optional.empty();
	public int teleportDirectCooldown = 0;
	public int teleportRandomCooldown = 0;
	public boolean creepy = false;
	public MobData data;

   	public List<MobAttribute> getAbilities(EntityType type) {
   		ArrayList<MobAttribute> abilities = new ArrayList();
   		abilities.add(MobAttribute.ability(1, Component.translatable("playasmob.ability.teleport")));
   		abilities.add(MobAttribute.ability(2, Component.translatable("playasmob.ability.teleport_random")));
   		return abilities;
   	}

   	public List<MobAttribute> getPros(EntityType type) {
   		ArrayList<MobAttribute> pros = new ArrayList();
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.health")));
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.sprint")));
   		return pros;
   	}

   	public List<MobAttribute> getCons(EntityType type) {
   		ArrayList<MobAttribute> cons = new ArrayList();
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.water")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.crouch")));
   		return cons;
   	}
	
	public EndermanData(MobData data) {
		this.data = data;
	}

	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
		if(this.teleportDirectCooldown > 0)
			compound.putInt("teleportDirectCooldown", this.teleportDirectCooldown);
		if(this.teleportRandomCooldown > 0)
			compound.putInt("teleportRandomCooldown", this.teleportRandomCooldown);
		return compound;
	}

	public void load(CompoundTag compound) {
		this.teleportDirectCooldown = compound.getInt("teleportDirectCooldown");
		this.teleportRandomCooldown = compound.getInt("teleportRandomCooldown");
	}
	
	public void renderInfo(GuiGraphics graphics, int width, int height) {
		RenderUtils.abilityIcon(graphics, "teleport", true, width - 30, height - 22, this.teleportDirectCooldown, Keybinds.Ability1);
		RenderUtils.abilityIcon(graphics, "teleport_random", true, width - 30, height - 40, this.teleportRandomCooldown, Keybinds.Ability2);
	}

	public void press(int ability, boolean pressed) {
		if(!pressed || ability == 3)
			return;

		if(ability == 1 && this.teleportDirectCooldown == 0 && this.teleportForward())
			this.teleportDirectCooldown = 40;
		if(ability == 2 && this.teleportRandomCooldown == 0 && this.teleportRandom())
			this.teleportRandomCooldown = 40;
		data.sync(false, false);
	}

	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENDERMAN_HURT;
	}

	public SoundEvent getDeathSound() {
		return SoundEvents.ENDERMAN_DEATH;
	}

	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder endermanBuilder = EnderMan.createAttributes();
		playerBuilder.combine(endermanBuilder);
		return playerBuilder;
	}

	public double speedMultiplier() {
		return data.isSprinting ? 0.5 : 0.3;
	}

	public boolean canSprint() {
		return true;
	}

	public EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		return new EndermanRenderer(context);
	}
	
	public HandData getHand(EntityRendererProvider.Context context) {
		HandData hand = new HandData(context, this, new EndermanHand(context.bakeLayer(ModelLayers.ENDERMAN), false), false);
		this.setupHand(hand);
		return hand;
	}

	public void setupHand(HandData hand) {
		hand.texture = new ResourceLocation("textures/entity/enderman/enderman.png");
		hand.setScale(1.33f, 1.33f, 1.33f);
		hand.setPosition(7.2f, 8f, -0.8f);
		hand.setRotation(0f, 0f, 0.1f);
	}
	
	public float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return 2.55F;
	}

	public boolean isSensitiveToWater() {
		return true;
	}

	public void preTick() {
		if(this.teleportDirectCooldown > 0)
			this.teleportDirectCooldown--;
		if(this.teleportRandomCooldown > 0)
			this.teleportRandomCooldown--;

        for(int i = 0; i < 2; ++i) {
        	data.world.addParticle(ParticleTypes.PORTAL, data.player.getRandomX(0.5D), data.player.getRandomY() - 0.25D, data.player.getRandomZ(0.5D), (data.world.random.nextDouble() - 0.5D) * 2.0D, -data.world.random.nextDouble(), (data.world.random.nextDouble() - 0.5D) * 2.0D);
    	}
	}
	
   	@Nullable
   	public BlockState getCarriedBlock() {
      	return this.carriedBlock.orElse(null);
   	}
   	
   	public boolean isCreepy() {
      	return this.creepy;
   	}

   	public boolean teleportForward() {
   		BlockHitResult result = data.world.clip(new ClipContext(data.player.getEyePosition(1f), data.player.getEyePosition(1f).add(data.player.getViewVector(1f).scale(32)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, data.player));
		EntityDimensions dimensions = EntityType.ENDERMAN.getDimensions();
		Vec3 hitPosition = result.getLocation();
		Vec3 playerPosition = data.player.getEyePosition(1f);
		Vec3 direction = playerPosition.subtract(hitPosition).normalize(); 
		Vec3 targetPosition = hitPosition.add(direction.scale(dimensions.width));
		return this.teleport(targetPosition.x, targetPosition.y, targetPosition.z, false);
   	}

   	public boolean teleportRandom() {
   		if(data.world.isClientSide())
   			return false;
   		for(int idx = 0; idx < 10; idx++) {
   			if(this.attemptTeleportRandom())
   				return true;
   		}
   		return false;
   	}

   	public boolean attemptTeleportRandom() {
    	if(!data.world.isClientSide() && data.player.isAlive()) {
        	double d0 = data.player.getX() + (data.world.random.nextDouble() - 0.5D) * 64.0D;
         	double d1 = data.player.getY() + (double)(data.world.random.nextInt(64) - 32);
         	double d2 = data.player.getZ() + (data.world.random.nextDouble() - 0.5D) * 64.0D;
         	return this.teleport(d0, d1, d2, true);
      	}
        return false;
   	}

   	public boolean teleport(double x, double y, double z, boolean wasRandom) {
      	BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
      	while(pos.getY() > data.world.getMinBuildHeight() && !data.world.getBlockState(pos).blocksMotion()) {
         	pos.move(Direction.DOWN);
      	}

      	BlockState blockstate = data.world.getBlockState(pos);
      	boolean flag = blockstate.blocksMotion();
      	boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
      	if (flag && !flag1) {
         	net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(data.player, x, y, z);
         	if (event.isCanceled()) return false;
         	Vec3 vec3 = data.player.position();
         	boolean flag2 = wasRandom ? data.player.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true) : true;
         	if(!wasRandom)
         		data.player.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
         	if (flag2) {
         		data.player.resetFallDistance();
            	data.world.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(data.player));
               	data.world.playSound(null, data.player.xo, data.player.yo, data.player.zo, SoundEvents.ENDERMAN_TELEPORT, data.player.getSoundSource(), 1.0F, 1.0F);
               	data.player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1, 1);
         	}	

         	return flag2;
      	}
        return false;
   	}
}