package playasmob;

import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.fluids.FluidType;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class GolemData extends EntityTypeData implements ZombieAttack, SkeletonAttack, SpiderAttack, IllagerAttack {
   	public int attackAnimationTick;

   	@Override
   	public List<MobAttribute> getPros(EntityType type) {
   		ArrayList<MobAttribute> pros = new ArrayList();
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.sprint")).fullOnly());
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.health")));
   		return pros;
   	}

   	@Override
   	public List<MobAttribute> getInfo(EntityType type) {
   		ArrayList<MobAttribute> info = new ArrayList();
   		info.add(MobAttribute.info(Component.translatable("playasmob.info.no_diet")));
   		return info;
   	}

   	@Override
   	public List<MobAttribute> getCons(EntityType type) {
   		ArrayList<MobAttribute> cons = new ArrayList();
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.jump")));
   		return cons;
   	}

	public GolemData(MobData data) {
		super(data);
	}

   	@Override
	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
		compound.putInt("attackAnimationTick", this.attackAnimationTick);
		return compound;
	}

   	@Override
	public void load(CompoundTag compound) {
		this.attackAnimationTick = compound.getInt("attackAnimationTick");
	}

   	@Override
	public void activate(boolean firstSpawn, EntityType mob) {
		if(!firstSpawn || mob != EntityType.SNOW_GOLEM)
			return;
		GlobalUtils.dropItem(data.player, EquipmentSlot.HEAD);
		data.player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.CARVED_PUMPKIN));
	}

   	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return data.mob == EntityType.IRON_GOLEM ? SoundEvents.IRON_GOLEM_HURT : SoundEvents.SNOW_GOLEM_HURT;
	}

   	@Override
	public SoundEvent getDeathSound() {
		return data.mob == EntityType.IRON_GOLEM ? SoundEvents.IRON_GOLEM_DEATH : SoundEvents.SNOW_GOLEM_DEATH;
	}

   	@Override
	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder golemBuilder = data.mob == EntityType.IRON_GOLEM ? IronGolem.createAttributes() : SnowGolem.createAttributes();
		if(data.mob == EntityType.IRON_GOLEM)
			golemBuilder.add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.5);
		playerBuilder.combine(golemBuilder);
		return playerBuilder;
	}

   	@Override
	public EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		return data.renderAs == EntityType.IRON_GOLEM ? new IronGolemRenderer(context) : new SnowGolemRenderer(context);
	}

   	@Override
	public ModelLayerLocation getModelLayer(boolean outer) {
		return data.renderAs == EntityType.IRON_GOLEM ? ModelLayers.IRON_GOLEM : ModelLayers.SNOW_GOLEM;
	}
	
   	@Override
	public HandData getHand(EntityRendererProvider.Context context) {
		if(data.renderAs == EntityType.IRON_GOLEM) {
			HandData hand = new HandData(context, this, new IronGolemHand(context.bakeLayer(this.getModelLayer(false)), false), false);
			this.setupHand(hand);
			return hand;
		}

		return super.getHand(context);
	}

   	@Override
	public void setupHand(HandData hand) {
		if(data.renderAs == EntityType.IRON_GOLEM) {
			hand.texture = new ResourceLocation("playasmob:textures/entities/iron_golem.png");
			hand.setScale(0.8f, 0.8f, 0.8f);
			hand.setPosition(-3.3f, 20f, 0.5f);
			hand.setRotation(3.1f, -3.15f, 0.13f);
		}
	}

   	@Override
	public boolean zombieAttack(EntityType type) {
		return data.mob == EntityType.IRON_GOLEM;
	}

   	@Override
	public boolean skeletonAttack(EntityType type) {
		return data.mob == EntityType.IRON_GOLEM;
	}

   	@Override
	public boolean spiderAttack(EntityType type) {
		return data.mob == EntityType.IRON_GOLEM;
	}

   	@Override
	public boolean illagerAttack(EntityType type) {
		return data.mob == EntityType.IRON_GOLEM;
	}

   	@Override
	public double speedMultiplier() {
		return data.isSprinting ? 0.2 : 0.1;
	}
	
   	@Override
	public boolean canSprint() {
		return data.mob == EntityType.IRON_GOLEM;
	}

   	@Override
	public boolean isSensitiveToWater() {
		return data.mob == EntityType.SNOW_GOLEM;
	}
	
   	@Override
	public float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return dimensions.height * (data.mob == EntityType.IRON_GOLEM ? 0.85f : 0.894f);
	}

   	@Override
	public void attackEntity(LivingAttackEvent event) {
		if(data.mob != EntityType.IRON_GOLEM)
			return;
		
      	this.attackAnimationTick = 10;
      	Entity target = event.getEntity();
     	float f = (float)data.player.getAttributeValue(Attributes.ATTACK_DAMAGE);
      	float f1 = (int)f > 0 ? f / 2.0F + (float)data.player.getRandom().nextInt((int)f) : f;
      	//boolean flag = target.hurt(data.player.damageSources().mobAttack(data.player), f1);
      	//if (flag) {
         	double d2;
         	if (target instanceof LivingEntity) {
            	LivingEntity livingentity = (LivingEntity)target;
            	d2 = livingentity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
         	} else {
            	d2 = 0.0D;
         	}

         	double d0 = d2;
         	double d1 = Math.max(0.0D, 1.0D - d0);
         	target.setDeltaMovement(target.getDeltaMovement().add(0.0D, (double)0.4F * d1, 0.0D));
         	data.player.doEnchantDamageEffects(data.player, target);
     	//}

      	data.player.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1, 1);
      	//if(!flag)
      		//event.setCanceled(true);
	}
	
   	@Override
	public void causeFallDamage(LivingFallEvent event) {
		if(data.mob == EntityType.IRON_GOLEM)
			event.setCanceled(true);
	}

   	@Override
	public void modifyBreathing(LivingBreatheEvent event) {
		if(data.mob == EntityType.IRON_GOLEM) {
			event.setCanBreathe(true);
			event.setCanRefillAir(true);
		}
	}

   	@Override
	public boolean canJump() {
		return data.mob != EntityType.IRON_GOLEM;
	}

   	@Override
	public boolean jumpInFluid(FluidType type) {
		return data.mob != EntityType.IRON_GOLEM || type != ForgeMod.WATER_TYPE.get();
	}

   	@Override
	public void preTick() {
		super.preTick();
		if(this.attackAnimationTick > 0)
			this.attackAnimationTick--;
		if(data.mob != EntityType.SNOW_GOLEM)
			return;
			
        if(data.world.getBiome(data.player.blockPosition()).is(BiomeTags.SNOW_GOLEM_MELTS))
            data.player.hurt(data.player.damageSources().onFire(), 1);
        if(!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(data.world, data.player))
        	return;

        BlockState state = Blocks.SNOW.defaultBlockState();
      	for(int i = 0; i < 4; ++i) {
            int j = Mth.floor(data.player.getX() + (double)((float)(i % 2 * 2 - 1) * 0.25F));
            int k = Mth.floor(data.player.getY());
            int l = Mth.floor(data.player.getZ() + (double)((float)(i / 2 % 2 * 2 - 1) * 0.25F));
            BlockPos pos = new BlockPos(j, k, l);
            if(data.world.isEmptyBlock(pos) && state.canSurvive(data.world, pos)) {
               	data.world.setBlockAndUpdate(pos, state);
            	data.world.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(data.player, state));
        	}
    	}
	}

   	@Override
   	public int maximumFoodLevel() {
   		if(data.mob != EntityType.IRON_GOLEM)
   			return super.maximumFoodLevel();
   		return this.minimumFoodLevel();
   	}

   	@Override
   	public int minimumFoodLevel() {
   		if(data.mob != EntityType.IRON_GOLEM)
   			return super.minimumFoodLevel();
   		float HungerPoints = (data.player.getHealth() / data.player.getMaxHealth()) * 20f;
   		return (int)Math.floor(HungerPoints);
   	}

   	@Override
   	public RegenModifer modifyRegen() {
   		return RegenModifer.Disabled;
   	}

   	@Override
   	public Diet getDiet() {
   		return Diet.None;
   	}

   	@Override
   	public void buildFoodInfo() {
   		super.buildFoodInfo();
   		this.addFoodInfo(Items.IRON_NUGGET, FoodMapper.create(false, true), null);
   		this.addFoodInfo(Items.IRON_INGOT, FoodMapper.create(false), null);
   		this.addFoodInfo(Items.IRON_BLOCK, FoodMapper.create(false), null);
   	}

   	@Override
	public void ate(ItemStack stack) {
		if(stack.getItem() == Items.IRON_NUGGET)
			data.player.heal(1);
		if(stack.getItem() == Items.IRON_INGOT)
			data.player.heal(9);
		if(stack.getItem() == Items.IRON_BLOCK)
			data.player.heal(81);
   		super.ate(stack);
	}

   	@Override
	public double selectionSizeMultiplier() {
		return data.renderAs == EntityType.IRON_GOLEM ? 1.8 : 1;
	}

   	public boolean hasPumpkin() {
      	return data.player.getItemBySlot(EquipmentSlot.HEAD).getItem() == Items.CARVED_PUMPKIN;
   	}

   	public int getAttackAnimationTick() {
      	return this.attackAnimationTick;
   	}

   	public IronGolem.Crackiness getCrackiness() {
      	return IronGolem.Crackiness.byFraction(data.player.getHealth() / data.player.getMaxHealth());
   	}

   	@Override
	public boolean canSwim() {
		return data.mob != EntityType.IRON_GOLEM;
	}

   	@Override
	public boolean canGlide() {
		return data.mob != EntityType.IRON_GOLEM;
	}
}
