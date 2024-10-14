package playasmob;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SpiderData implements EntityTypeData, GolemAttack {
	public static final MobEffectInstance VisionEffect = new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false, false);

	public MobData data = null;
	public boolean climbing = false;
	public boolean nightVision = false;
	public int climbState = -1;

   	public List<MobAttribute> getAbilities(EntityType type) {
   		ArrayList<MobAttribute> abilities = new ArrayList();
   		if(type == EntityType.CAVE_SPIDER)
   			abilities.add(MobAttribute.ability(1, Component.translatable("playasmob.ability.night_vision")));
   		abilities.add(MobAttribute.ability(2, Component.translatable("playasmob.cycle.next").append(Component.translatable("playasmob.ability.climbing"))));
   		abilities.add(MobAttribute.ability(3, Component.translatable("playasmob.cycle.previous").append(Component.translatable("playasmob.ability.climbing"))));
   		return abilities;
   	}

   	public List<MobAttribute> getPros(EntityType type) {
   		ArrayList<MobAttribute> pros = new ArrayList();
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.poison_immune")));
   		if(type == EntityType.CAVE_SPIDER)
   			pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.poison_hit")));
   		return pros;
   	}

   	public List<MobAttribute> getCons(EntityType type) {
   		ArrayList<MobAttribute> cons = new ArrayList();
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.arthropod")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.health")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.hand")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.pickup")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.crouch")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.sprint")));
   		return cons;
   	}

	public SpiderData(MobData data) {
		this.data = data;
	}

	public void deactivate() {
		if(this.hasNightVision())
			this.setNightVision(false);
	}

	public void load(CompoundTag compound) {
    	this.setClimbing(compound.getBoolean("Climbing"));
    	this.setNightVision(compound.getBoolean("nightVision"));
    	this.climbState = compound.getInt("climbState");
	}

	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
		compound.putInt("climbState", this.climbState);
		if(this.isClimbing())
			compound.putBoolean("Climbing", true);
		if(this.hasNightVision())
			compound.putBoolean("nightVision", true);
		return compound;
	}

	public boolean hasNightVision() {
		return this.nightVision && data.mob == EntityType.CAVE_SPIDER;
	}

	public void setNightVision(boolean enabled) {
		this.nightVision = enabled;
		if(data.player == null)
			return;
		
		if(enabled) {
			data.player.forceAddEffect(VisionEffect, null);
		} else if(data.player.hasEffect(MobEffects.NIGHT_VISION)) {
			data.player.removeEffectNoUpdate(MobEffects.NIGHT_VISION);
		}
	}

	public void press(int ability, boolean pressed) {
		if(ability == 1 && pressed)
			this.setNightVision(!this.hasNightVision());
		if(ability == 2 && pressed)
			cycleClimbState(false);
		if(ability == 3 && pressed)
			cycleClimbState(true);
		data.sync(false, false);
	}

	public void renderInfo(GuiGraphics graphics, int width, int height) {
		graphics.blit(new ResourceLocation("textures/block/ladder.png"), width - 30, height - 30, 0, 0, 16, 16, 16, 16);
		int yOffset = this.climbState == 0 ? 30 : (this.climbState == 1 ? 34 : 26);
		graphics.blit(new ResourceLocation("playasmob:textures/screens/" + this.climbTexture() + ".png"), width - 30, height - yOffset, 0, 0, 16, 16, 16, 16);
	}

	public String climbTexture() {
		return switch(this.climbState) {
			default -> "neutral";
			case 1 -> "up";
			case -1 -> "down";
			case -2 -> "negitive";
		};
	}

	public void cycleClimbState(boolean direction) {
		if(this.climbState >= 1 && direction) {
			this.climbState = -1;
		} else if(this.climbState <= -2 && !direction) {
			this.climbState = 1;
		} else {
			this.climbState += direction ? 1 : -1;
		}
	}
	
	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder spiderBuilder = Spider.createAttributes();
		if(data.mob == EntityType.CAVE_SPIDER)
			spiderBuilder.add(Attributes.MAX_HEALTH, 12);
		
		playerBuilder.combine(spiderBuilder);
		return playerBuilder;
	}

	public double speedMultiplier() {
		return 0.3;
	}

	public void respawn() {
		this.data.changeMob(EntityType.SPIDER, null, true);
	}

	public EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		return new SpiderRenderer(context);
	}

   	public MobType getMobType() {
   		return MobType.ARTHROPOD;
   	}

   	public boolean nearbyWall() {
   		EntityDimensions dimensions = new EntityDimensions(data.player.getBbWidth(), data.player.getBbHeight(), false);
        AABB aabb = dimensions.makeBoundingBox(data.player.position()).inflate(0.4, 0, 0.4);
        return BlockPos.betweenClosedStream(aabb).anyMatch((pos) -> {
        	BlockState blockstate = data.world.getBlockState(pos);
           	return !blockstate.isAir() && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(data.world, pos).move((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()), Shapes.create(aabb), BooleanOp.AND);
    	});
   	}
	
   	public void preTick() {
        Vec3 delta = data.player.getDeltaMovement();
        boolean moving = delta.x != 0 || delta.z != 0;
       	boolean climb = this.isJumping() || (this.climbState == 1 && moving);
   		this.setClimbing(this.climbState >= -1 ? nearbyWall() : false);
   	}
	
   	public void postTick() {
        if (this.isClimbing()) {
			if(!nearbyWall())
				return;
        	Vec3 delta = data.player.getDeltaMovement();
       		boolean moving = delta.x != 0 || delta.z != 0;
       		boolean climb = this.isJumping() || (this.climbState == 1 && moving);
        	double yDelta = climb ? 0.2 : (this.climbState >= 0 ? 0 : -0.2);
    		delta = new Vec3(delta.x, Math.max(delta.y, yDelta), delta.z);
    		data.player.setDeltaMovement(delta);
        	data.player.resetFallDistance();
        }
   	}

   	public boolean isJumping() {
		return Minecraft.getInstance().options.keyJump.isDown();
   	}

   	public boolean isClimbing() {
      	return this.climbing;
   	}

   	public void setClimbing(boolean climbing) {
      	this.climbing = climbing;
   	}

	public float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return data.mob == EntityType.SPIDER ? 0.65F : 0.45F;
	}

	public boolean allowEffect(MobEffectInstance effect) {
		return effect.getEffect() != MobEffects.POISON;
	}
	
	public void hurtEntity(LivingHurtEvent event) {
		if(data.mob == EntityType.SPIDER)
			return;

        int time = switch(data.world.getDifficulty()) {
        	default -> 0;
        	case NORMAL -> 7;
        	case HARD -> 15;
        };

        if (time > 0)
 	       event.getEntity().addEffect(new MobEffectInstance(MobEffects.POISON, time * 20, 0), data.player);
	}

	public boolean canPickup(ItemStack stack) {
		return false;
	}

	public boolean canSprint() {
		return false;
	}

	public boolean canCrouch() {
		return false;
	}
}
