package playasmob;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.List;

import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.Holder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.food.FoodData;

public class MobData {
	public static final Logger LOGGER = LogUtils.getLogger();
	public EntityType mob = EntityType.PLAYER;
	public EntityType renderAs = EntityType.PLAYER;
	public EntityTypeData typeData = null;
	public EntityTypeData typeRender = null;
	public MobPreview previewMob = null;
	public SelectionGroup viewGroup = null;
	public boolean respawnDimensionSwap = false;
	public boolean usingSelector = false;
	public boolean isCrouching = false;
	public boolean isSprinting = false;
	public double baseSpeed = 1;
	public int inCombat = 0;
	public Player player;
	public Level world;
	public boolean debug = false;
	public float xScale = 1, yScale = 1, zScale = 1;
	public float xRot = 0, yRot = 0, zRot = 0;
	public float xPos = 0, yPos = 0, zPos = 0;

	public MobData() {
	}

	public MobData(CompoundTag data) {
		this.inCombat = data.getInt("inCombat");
		this.baseSpeed = data.getDouble("baseSpeed");
		this.isCrouching = data.getBoolean("isCrouching");
		this.isSprinting = data.getBoolean("isSprinting");
		this.usingSelector = data.getBoolean("usingSelector");
		if(data.contains("viewGroup"))
			this.viewGroup = SelectionScreenData.groups.get(data.getInt("viewGroup"));
		
		if(data.contains("type")) {
			EntityType.byString(data.getString("type")).ifPresent((type) -> {
				this.mob = type;
				this.renderAs = type;
			});
		}

		if(data.contains("hand")) {
			CompoundTag hand = data.getCompound("hand");
			CompoundTag rot = hand.getCompound("rotation");
			CompoundTag pos = hand.getCompound("position");
			CompoundTag scale = hand.getCompound("scale");
			this.xRot = rot.getFloat("x");
			this.yRot = rot.getFloat("y");
			this.zRot = rot.getFloat("z");
			this.xPos = pos.getFloat("x");
			this.yPos = pos.getFloat("y");
			this.zPos = pos.getFloat("z");
			this.xScale = scale.getFloat("x");
			this.yScale = scale.getFloat("y");
			this.zScale = scale.getFloat("z");
			this.debug = hand.getBoolean("debug");
		}

		this.typeData = GlobalData.getData(this.mob, this, false);
		this.typeRender = typeData;
		this.respawnDimensionSwap = data.getBoolean("respawnDimensionSwap");
		if(data.contains("typeData") && this.typeData != null)
			this.typeData.load(data.getCompound("typeData"));
		
		if(data.contains("renderType")) {
			EntityType.byString(data.getString("renderType")).ifPresent((type) -> {
				this.renderAs = type;
				this.typeRender = GlobalData.getData(this.renderAs, this, false);
			});
		}

		if(data.contains("preview"))
			this.previewMob = new MobPreview(data.getCompound("preview"));
	}

	public CompoundTag save() {
		CompoundTag data = new CompoundTag();
		if(this.debug) {
			CompoundTag hand = new CompoundTag();
			CompoundTag rot = new CompoundTag();
			CompoundTag pos = new CompoundTag();
			CompoundTag scale = new CompoundTag();
			rot.putFloat("x", this.xRot);
			rot.putFloat("y", this.yRot);
			rot.putFloat("z", this.zRot);
			hand.put("rotation", rot);
			pos.putFloat("x", this.xPos);
			pos.putFloat("y", this.yPos);
			pos.putFloat("z", this.zPos);
			hand.put("position", pos);
			scale.putFloat("x", this.xScale);
			scale.putFloat("y", this.yScale);
			scale.putFloat("z", this.zScale);
			hand.put("scale", scale);
			hand.putBoolean("debug", this.debug);
			data.put("hand", hand);
		}

		if(this.viewGroup != null) 
			data.putInt("viewGroup", SelectionScreenData.groups.indexOf(this.viewGroup));

		data.putInt("inCombat", this.inCombat);
		data.putDouble("baseSpeed", this.baseSpeed);
		if(this.isCrouching)
			data.putBoolean("isCrouching", true);
		if(this.isSprinting)
			data.putBoolean("isSprinting", true);
		if(this.usingSelector)
			data.putBoolean("usingSelector", true);
		if(this.respawnDimensionSwap)
			data.putBoolean("respawnDimensionSwap", true);
		data.putString("type", EntityType.getKey(this.mob).toString());
		
		if(this.renderAs != this.mob)
			data.putString("renderType", EntityType.getKey(this.renderAs).toString());
		if(this.typeData != null)
			data.put("typeData", this.typeData.save());
		if(this.previewMob != null)
			data.put("preview", this.previewMob.save());
			
		return data;
	}

	public void openGroup(SelectionGroup group) {
		this.viewGroup = group;
	}

	public SelectionGroup getGroup() {
		return this.viewGroup;
	}

	public MobPreview getPreview() {
		if(this.previewMob == null)
			return null;
		return this.previewMob;
	}

	public void setPreview(EntityType mob) {
		if(mob == EntityType.PLAYER) {
			this.previewMob = SelectionScreenData.previewPlayer();
			return;
		}
			
		EntityTypeData data = GlobalData.getData(mob, this, false);
		this.previewMob = data == null ? null : new MobPreview(data, mob);
		data.addAttributeInfo(this.previewMob);
	}

	public void menuOpen(boolean isOpen) {
		this.usingSelector = isOpen;
		if(!isOpen) {
			this.previewMob = null;
			this.viewGroup = null;
		}
		
		this.sync(false, false);
	}

	public void menuBack() {
		 if(this.previewMob != null) {
			this.previewMob = null;
		} else if(this.viewGroup != null) {
			this.viewGroup = null;
		}
	}

	public static MobData get(Player player) {
		MobData data = player.getCapability(MobVars.MobData, null).orElse(new MobVars.Data()).mobData;
		data.player = player;
		data.world = player.level();
		if(data.typeData == null)
			data.typeData = GlobalData.getData(data.mob, data, false);
		return data;
	}

	public void changeMob(EntityType mob, @Nullable CompoundTag withData, boolean refillHP) {
		EntityTypeData typeData = GlobalData.getData(mob, this, true);
		if(typeData != null || mob == EntityType.PLAYER) {
			if(this.typeData != null)
				this.typeData.deactivate();
			if(typeData != null)
				typeData.activate();
				
			this.mob = mob;
			this.renderAs = mob;
			this.typeData = typeData;
			this.typeRender = typeData;
			GlobalUtils.resetAttackers(this.player);
			if(this.typeData != null && withData != null)
				this.typeData.load(withData);
			this.sync(true, refillHP);
		}
	}

	public void changeRenderer(EntityType mob) {
		this.renderAs = mob;
		this.typeRender = GlobalData.getData(mob, this, false);
		this.sync(false, false);
	}

	public void resetRenderer() {
		this.changeRenderer(this.mob);
	}

	public void sync(boolean updateStuff, boolean refillHP) {
		if(this.player == null)
			return;
			
		MobVars.Data data = this.player.getCapability(MobVars.MobData, null).orElse(new MobVars.Data());
		data.mobData = this;
		data.sync(this.player);
		if(!updateStuff)
			return;
		
		this.player.refreshDimensions();
		updateAttributes(refillHP);
		if(refillHP)
			this.player.setAirSupply(this.player.getMaxAirSupply());
	}

	public void updateAttributes(boolean refillHP) {
		AttributeSupplier.Builder builder = this.player.createAttributes();
		if(this.typeData != null) {
			builder = this.typeData.modifyAttributes(builder);
			if(!this.typeData.hasHand())
				builder.add(ForgeMod.BLOCK_REACH.get(), 0);
			builder.add(ForgeMod.ENTITY_REACH.get(), this.typeData.attackReach());
		}

		float HP = this.player.getHealth() / this.player.getMaxHealth();
		this.player.setHealth(1);
		
		AttributeSupplier attributes = builder.build();
		for(Attribute attribute : GlobalData.getAttributes()) {
			if(!attributes.hasAttribute(attribute))
				continue;
			AttributeInstance instance = this.player.getAttributes().getInstance(attribute);
			if(instance != null) {
				instance.setBaseValue(attributes.getBaseValue(attribute));
				if(attribute == Attributes.MOVEMENT_SPEED)
					this.baseSpeed = attributes.getBaseValue(attribute);
			}
		}

		HP = refillHP ? this.player.getMaxHealth() : this.player.getMaxHealth() * HP;
		this.player.setHealth(HP);
		this.updateSpeed();
	}

	public void updateSpeed() {
		double speed = this.typeData == null ? 1 : this.typeData.speedMultiplier();
		AttributeInstance instance = this.player.getAttributes().getInstance(Attributes.MOVEMENT_SPEED);
		instance.setBaseValue(this.baseSpeed * speed);
	}

	public boolean isSunBurnTick() {
		if (this.world.isDay() && !this.world.isClientSide) {
         	float f = this.player.getLightLevelDependentMagicValue();
         	BlockPos blockpos = BlockPos.containing(this.player.getX(), this.player.getEyeY(), this.player.getZ());
         	boolean flag = this.player.isInWaterRainOrBubble() || this.player.isInPowderSnow || this.player.wasInPowderSnow;
         	if (f > 0.5F && this.world.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !flag && this.world.canSeeSky(blockpos))
            	return true;
      	}
      	return false;
	}

	public EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		if(this.typeRender == null)
			return null;
		return this.typeRender.getRenderer(context);
	}

	public boolean isAggressive() {
		return this.inCombat > 0;
	}

	public boolean isShaking() {
		if(this.typeData == null)
			return false;
		return this.typeData.isShaking();
	}

	public boolean allowEffect(MobEffectInstance effect) {
		if(this.typeData != null)
			return this.typeData.allowEffect(effect);
		return true;
	}

	public void preTick() {
		if(this.inCombat > 0)
			this.inCombat--;
			
		if(this.typeData != null)
			this.typeData.preTick();

		if(this.player.isCrouching() != this.isCrouching) {
			this.isCrouching = this.player.isCrouching();
			this.updateSpeed();
		}
		
		if(this.player.isSprinting() != this.isSprinting) {
			this.isSprinting = this.player.isSprinting();
			this.updateSpeed();
		}
		
		this.sync(false, false);
	}

	public void postTick() {
		if(this.typeData != null)
			this.typeData.postTick();
	}

	public void attacked(LivingAttackEvent event) {
		if(this.usingSelector) {
			event.setCanceled(true);
			return;
		}
		
		if(this.mob == EntityType.PLAYER)
			this.playerAttacked(event);
		if(this.typeData == null)
			return;
			
		MobType type = this.typeData.getMobType();
		DamageSource source = event.getSource();
		if(type == MobType.UNDEAD && source.is(DamageTypes.DROWN)) {
			event.setCanceled(true);
			return;
		}
		
		this.typeData.attacked(event);
	}

	public void hurtEntity(LivingHurtEvent event) {
		if(this.typeData != null)
			this.typeData.hurtEntity(event);
			
		this.inCombat = 100;
		this.sync(false, false);
	}

	public void hurt(LivingHurtEvent event) {
		if(this.typeData == null)
			return;
			
		MobType type = this.typeData.getMobType();
		DamageSource source = event.getSource();
		if(!source.isIndirect()) {
			if(source.getDirectEntity() instanceof LivingEntity attacker) {
				ItemStack stack = attacker.getMainHandItem();
				if(!(stack.getItem() instanceof SwordItem) && !(stack.getItem() instanceof AxeItem))
					return;
			
				int smiteLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SMITE, stack);
				int baneLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS, stack);
				if(type == MobType.ARTHROPOD ? baneLevel == 0 : (type == MobType.UNDEAD ? smiteLevel == 0 : true))
					return;

				if(type == MobType.ARTHROPOD) {
        			int time = 20 + this.player.getRandom().nextInt(10 * baneLevel);
        			this.player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, time, 3), attacker);
				}
			
				event.setAmount(event.getAmount() + ((type == MobType.ARTHROPOD ? baneLevel : smiteLevel) * 2.5f));
			}
		}
		
		this.typeData.hurt(event);
	}

	public void playerAttacked(LivingAttackEvent event) {
		DamageSource source = event.getSource();
		Entity killer = source.isIndirect() ? source.getEntity() : source.getDirectEntity();
		if(player.getHealth() - event.getAmount() <= 0) {
			if(killer instanceof Zombie) {
               	this.world.levelEvent((Player)null, 1026, player.blockPosition(), 0);
				this.changeMob(killer.getType(), null, false);
				event.setCanceled(true);
			} else if(killer instanceof Player attacker && MobData.get(attacker).typeData instanceof ZombieData zombieData) {
				FoodData food = attacker.getFoodData();
				food.setFoodLevel(food.getFoodLevel() + 8);
				food.setSaturation(food.getSaturationLevel() + 0.8f);
               	this.world.levelEvent((Player)null, 1026, player.blockPosition(), 0);
				this.changeMob(zombieData.data.mob, null, false);
				event.setCanceled(true);
			}
		}
	}

	public void press(int ability, boolean pressed) {
		if(this.typeData != null)
			this.typeData.press(ability, pressed);
	}

	public void respawn() {
		this.respawnDimensionSwap = false;
		if(this.typeData != null)
			this.typeData.respawn();
		this.sync(false, true);

		PlayasmobMod.queueServerWork(1, () -> {
			this.player.refreshDimensions();
			updateAttributes(true);
		});
	}

	public void worldChange(PlayerEvent.PlayerChangedDimensionEvent event) {
		if(this.respawnDimensionSwap)
			this.respawn();
		if(!this.respawnDimensionSwap)
			this.sync(true, true);
	}

	public void swapDimension(List<ResourceKey<Level>> vaildDimensions, List<ResourceKey<Biome>> vaildBiomes) {
		this.respawnDimensionSwap = true;
		ResourceKey<Level> targetDimension = null;
		if(vaildDimensions.contains(Level.END))
			targetDimension = Level.END;
		if(vaildDimensions.contains(Level.NETHER))
			targetDimension = Level.NETHER;
		if(vaildDimensions.contains(Level.OVERWORLD))
			targetDimension = Level.OVERWORLD;
			
		ServerLevel dimension = this.world.getServer().getLevel(targetDimension);
		if(this.player instanceof ServerPlayer player) {
			player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
			player.teleportTo(dimension, player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
			player.connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
			for (MobEffectInstance _effectinstance : player.getActiveEffects())
				player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), _effectinstance));
			player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
		}
	}

	public boolean vaildBiome(Holder<Biome> biome, List<ResourceKey<Biome>> vaildBiomes) {
		for(ResourceKey<Biome> vaildBiome : vaildBiomes) {
			if(biome.is(vaildBiome))
				return true;
		}
		return false;
	}

	public void findVaildBiome(List<ResourceKey<Biome>> vaildBiomes) {
		if(this.world instanceof ServerLevel server) {
			Pair<BlockPos, Holder<Biome>> closestBiome = server.findClosestBiome3d((biome) -> { return vaildBiome(biome, vaildBiomes); }, this.player.blockPosition(), 6400, 32, 64);
			if(closestBiome == null)
				return;
			
			BlockPos pos = closestBiome.getFirst();
			player.teleportTo(pos.getX(), pos.getY(), pos.getZ());
		}
	}
}
