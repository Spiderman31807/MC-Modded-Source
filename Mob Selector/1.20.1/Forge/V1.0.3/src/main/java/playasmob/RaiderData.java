package playasmob;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.util.FormattedCharSequence;
import org.openjdk.nashorn.api.tree.IfTree;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class RaiderData implements EntityTypeData, GolemAttack {
	public static final List<Potion> drinkPotions = List.of(Potions.HEALING, Potions.SWIFTNESS, Potions.WATER_BREATHING, Potions.FIRE_RESISTANCE);
	public static final List<Potion> potions = List.of(Potions.HARMING, Potions.HEALING, Potions.POISON, Potions.REGENERATION, Potions.SLOWNESS, Potions.SWIFTNESS, Potions.WATER_BREATHING, Potions.FIRE_RESISTANCE);
	
   	public MobData data;
	public UUID spellTarget;
   	public final Vec3[][] IllusionOffsets;
   	public IllagerSpell usingSpell = IllagerSpell.NONE;
	public Potion selectedPotion = Potions.HEALING;
	public Potion drinkingPotion = null;
	public boolean isCelebrating = false;
	public boolean castedSpell = false;
   	public int spellCastingWarmUp;
   	public int wololoSpellCooldown;
   	public int attackSpellCooldown;
   	public int summonSpellCooldown;
   	public int mirrorSpellCooldown;
   	public int blindnessSpellCooldown;
   	public int spellCastingTime = -1;
   	public int globalSpellCooldown;
	public int potionCooldown;
	public int drinkingTime;
   	public int IllusionTicks;
   	public int attackTick;
   	public int stunnedTick;
   	public int roarTick;

   	public List<MobAttribute> getAbilities(EntityType type) {
   		ArrayList<MobAttribute> abilities = new ArrayList();
   		if(type == EntityType.WITCH) {
   			abilities.add(MobAttribute.ability(1, Component.translatable("playasmob.ability.use_potion")));
   			abilities.add(MobAttribute.ability(2, Component.translatable("playasmob.cycle.next").append(Component.translatable("playasmob.ability.potion"))));
   			abilities.add(MobAttribute.ability(3, Component.translatable("playasmob.cycle.previous").append(Component.translatable("playasmob.ability.potion"))));
   		} else if(type == EntityType.EVOKER) {
   			abilities.add(MobAttribute.ability(1, Component.translatable("playasmob.ability.spell_attack")));
   			abilities.add(MobAttribute.ability(2, Component.translatable("playasmob.ability.spell_summon")));
   			abilities.add(MobAttribute.ability(3, Component.translatable("playasmob.ability.spell_wololo")));
   		} else if(type == EntityType.ILLUSIONER) {
   			abilities.add(MobAttribute.ability(1, Component.translatable("playasmob.ability.spell_mirror")));
   			abilities.add(MobAttribute.ability(2, Component.translatable("playasmob.ability.spell_blindness")));
   		}
   		return abilities;
   	}

   	public List<MobAttribute> getPros(EntityType type) {
   		ArrayList<MobAttribute> pros = new ArrayList();
   		if(type == EntityType.WITCH)
   			pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.magic_resistance")));
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.health")));
   		if(type != EntityType.WITCH && type != EntityType.RAVAGER)
   			pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.sprint")));
   		return pros;
   	}

   	public List<MobAttribute> getInfo(EntityType type) {
   		ArrayList<MobAttribute> info = new ArrayList();
   		if(type == EntityType.WITCH)
   			info.add(MobAttribute.info(Component.translatable("playasmob.info.self_magic_immune")));
   		return info;
   	}

   	public List<MobAttribute> getCons(EntityType type) {
   		ArrayList<MobAttribute> cons = new ArrayList();
   		if(type == EntityType.WITCH)
   			cons.add(MobAttribute.con(Component.translatable("playasmob.con.sprint")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.crouch")));
   		return cons;
   	}

	public RaiderData(MobData data) {
		this.data = data;
      	this.IllusionOffsets = new Vec3[2][4];
      	for(int i = 0; i < 4; ++i) {
         	this.IllusionOffsets[0][i] = Vec3.ZERO;
         	this.IllusionOffsets[1][i] = Vec3.ZERO;
      	}
	}

	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();

		if(data.mob == EntityType.WITCH) {
			if(potions.indexOf(this.selectedPotion) > 0)
				compound.putInt("potion", potions.indexOf(this.selectedPotion));
			if(this.potionCooldown > 0)
				compound.putInt("potionCooldown", this.potionCooldown);
			if(this.drinkingTime > 0)
				compound.putInt("drinkingTime", this.drinkingTime);
			if(this.isDrinkingPotion())
				compound.putInt("drinkingPotion", potions.indexOf(this.drinkingPotion));
		} else if(data.mob == EntityType.EVOKER) {
			if(this.attackSpellCooldown > 0)
				compound.putInt("attackSpellCooldown", this.attackSpellCooldown);
			if(this.summonSpellCooldown > 0)
				compound.putInt("summonSpellCooldown", this.summonSpellCooldown);
			if(this.wololoSpellCooldown > 0)
				compound.putInt("wololoSpellCooldown", this.wololoSpellCooldown);
		} else if(data.mob == EntityType.ILLUSIONER) {
			if(this.mirrorSpellCooldown > 0)
				compound.putInt("mirrorSpellCooldown", this.mirrorSpellCooldown);
			if(this.blindnessSpellCooldown > 0)
				compound.putInt("blindnessSpellCooldown", this.blindnessSpellCooldown);
			if(this.IllusionTicks > 0)
				compound.putInt("IllusionTicks", this.IllusionTicks);
			this.saveIllusionOffsets(compound);
		}

		if(data.mob == EntityType.EVOKER || data.mob == EntityType.ILLUSIONER) {
			if(this.globalSpellCooldown > 0)
				compound.putInt("globalSpellCooldown", this.globalSpellCooldown);
	
			if(this.usingSpell != IllagerSpell.NONE) {
				if(this.spellCastingWarmUp > 0)
					compound.putInt("spellCastingWarmUp", this.spellCastingWarmUp);
				if(this.spellCastingTime != -1)
					compound.putInt("spellTime", this.spellCastingTime);
				if(this.spellTarget != null)
					compound.putUUID("spellTarget", this.spellTarget);
				compound.putBoolean("castedSpell", this.castedSpell);
				compound.putInt("spellID", this.usingSpell.id);
			}
		}

		return compound;
	}

	public void saveIllusionOffsets(CompoundTag compound) {
	    ListTag list = new ListTag();
	    for (Vec3[] offsets : this.IllusionOffsets) {
	        ListTag innerList = new ListTag();
	        for (Vec3 offset : offsets) {
	            CompoundTag vecTag = new CompoundTag();
	            vecTag.putDouble("x", offset.x);
	            vecTag.putDouble("y", offset.y);
	            vecTag.putDouble("z", offset.z);
	            innerList.add(vecTag);
	        }
	        list.add(innerList);
	    }
	    compound.put("IllusionOffsets", list);
	}

	public void load(CompoundTag compound) {
		this.selectedPotion = potions.get(compound.getInt("potion"));
		this.potionCooldown = compound.getInt("potionCooldown");
		this.drinkingTime = compound.getInt("drinkingTime");
		if(compound.contains("drinkingPotion"))
			this.drinkingPotion = potions.get(compound.getInt("drinkingPotion"));

		if(data.mob == EntityType.ILLUSIONER)
			this.loadIllusionOffsets(compound);
		this.IllusionTicks = compound.getInt("IllusionTicks");
		if(compound.contains("spellTime"))
			this.spellCastingTime = compound.getInt("spellTime");
		this.spellCastingWarmUp = compound.getInt("spellCastingWarmUp");
		this.attackSpellCooldown = compound.getInt("attackSpellCooldown");
		this.summonSpellCooldown = compound.getInt("summonSpellCooldown");
		this.wololoSpellCooldown = compound.getInt("wololoSpellCooldown");
		this.mirrorSpellCooldown = compound.getInt("mirrorSpellCooldown");
		this.blindnessSpellCooldown = compound.getInt("blindnessSpellCooldown");
		this.globalSpellCooldown = compound.getInt("globalSpellCooldown");
		this.usingSpell = IllagerSpell.byId(compound.getInt("spellID"));
		this.castedSpell = compound.getBoolean("castedSpell");
		if(compound.contains("spellTarget"))
			this.spellTarget = compound.getUUID("spellTarget");
	}

	public void loadIllusionOffsets(CompoundTag compound) {
	    if (compound.contains("IllusionOffsets")) {
	        ListTag list = compound.getList("IllusionOffsets", Tag.TAG_LIST);
	        for (int i = 0; i < list.size(); i++) {
	            ListTag innerList = list.getList(i);
	            for (int j = 0; j < innerList.size(); j++) {
	                CompoundTag vecTag = innerList.getCompound(j);
	                double x = vecTag.getDouble("x");
	                double y = vecTag.getDouble("y");
	                double z = vecTag.getDouble("z");
	                this.IllusionOffsets[i][j] = new Vec3(x, y, z);
	            }
	        }
	    }
	}

	public void login() {
		int cooldown = this.globalSpellCooldown;
   		if(this.isCastingSpell())
   			this.stopCastingSpell();
   		this.globalSpellCooldown = cooldown;
   		this.drinkingPotion = null;
   		this.drinkingTime = 0;
	}

   	public void respawn() {
   		if(this.isCastingSpell())
   			this.stopCastingSpell();
   		this.attackSpellCooldown = 0;
   		this.summonSpellCooldown = 0;
   		this.wololoSpellCooldown = 0;
   		this.mirrorSpellCooldown = 0;
   		this.blindnessSpellCooldown = 0;
   		this.globalSpellCooldown = 0;
   		this.spellTarget = null;
   		this.drinkingPotion = null;
   		this.potionCooldown = 0;
   		this.drinkingTime = 0;
   		this.attackTick = 0;
   		this.stunnedTick = 0;
   		this.roarTick = 0;
   	}

	public SoundEvent getHurtSound(DamageSource source) {
		return switch(GlobalUtils.getString(data.mob)) {
			default -> null;
			case "minecraft:witch" -> SoundEvents.WITCH_HURT;
			case "minecraft:pillager" -> SoundEvents.PILLAGER_HURT;
			case "minecraft:vindicator" -> SoundEvents.VINDICATOR_HURT;
			case "minecraft:evoker" -> SoundEvents.EVOKER_HURT;
			case "minecraft:illusioner" -> SoundEvents.ILLUSIONER_HURT;
			case "minecraft:ravager" -> SoundEvents.RAVAGER_HURT;
		};
	}

	public SoundEvent getDeathSound() {
		return switch(GlobalUtils.getString(data.mob)) {
			default -> null;
			case "minecraft:witch" -> SoundEvents.WITCH_DEATH;
			case "minecraft:pillager" -> SoundEvents.PILLAGER_DEATH;
			case "minecraft:vindicator" -> SoundEvents.VINDICATOR_DEATH;
			case "minecraft:evoker" -> SoundEvents.EVOKER_DEATH;
			case "minecraft:illusioner" -> SoundEvents.ILLUSIONER_DEATH;
			case "minecraft:ravager" -> SoundEvents.RAVAGER_DEATH;
		};
	}

	public SoundEvent getStepSound() {
		return switch(GlobalUtils.getString(data.mob)) {
			default -> null;
			case "minecraft:ravager" -> SoundEvents.RAVAGER_STEP;
		};
	}

   	public float stepVolume() {
   		return 0.15f;
   	}

	public SoundEvent getCastingSoundEvent() {
      	return data.mob == EntityType.EVOKER ? SoundEvents.EVOKER_CAST_SPELL : SoundEvents.ILLUSIONER_CAST_SPELL;
   	}

	public double entityReach() {
		return this.isCastingSpell() ? 0 : 3;
	}

	public double blockReach() {
		if(this.hasHand())
			return this.isCastingSpell() ? 0 : 4.5;
		return 0;
	}

	public boolean canSprint() {
		return data.mob != EntityType.WITCH;
	}

	public boolean hasHand() {
		return data.mob != EntityType.RAVAGER;
	}

	public double selectionSizeMultiplier() {
		return data.renderAs == EntityType.RAVAGER ? 2 : 1;
	}

	public boolean allowTargeting(LivingEntity entity) {
		if(entity instanceof Ravager || entity instanceof Witch)
			return false;
		return entity.getMobType() != MobType.ILLAGER || this.getMobType() != MobType.ILLAGER;
	}

	public boolean canRideRavager() {
		if(data.mob == EntityType.WITCH)
			return false;
		if(data.mob == EntityType.RAVAGER)
			return false;
		return true;
	}

	public void entityInteraction(PlayerInteractEvent.EntityInteract event) {
		if(!this.hasHand()) {
			event.setCanceled(true);
			return;
		}
		
		if(!this.canRideRavager())
			return;
		
		Entity entity = event.getTarget();
		if(entity instanceof Ravager ravager && !ravager.isVehicle()) {
            ItemStack stack = data.player.getItemInHand(event.getHand());
            if(stack == ItemStack.EMPTY)
            	data.player.startRiding(ravager);
		}
	}

   	public Vec3[] getIllusionOffsets(float p_32940_) {
      	if (this.IllusionTicks <= 0) {
         	return this.IllusionOffsets[1];
      	} else {
         	double d0 = (double)(((float)this.IllusionTicks - p_32940_) / 3.0F);
         	d0 = Math.pow(d0, 0.25D);
         	Vec3[] avec3 = new Vec3[4];
	
         	for(int i = 0; i < 4; ++i) {
            	avec3[i] = this.IllusionOffsets[1][i].scale(1.0D - d0).add(this.IllusionOffsets[0][i].scale(d0));
         	}

         	return avec3;
      	}
   	}

	public void press(int ability, boolean pressed) {
		if(data.mob == EntityType.WITCH) {
			if(ability == 1 && this.potionCooldown == 0)
				this.usePotion(pressed);
			if(ability == 2 && pressed)
				this.cyclePotion(false);
			if(ability == 3 && pressed)
				this.cyclePotion(true);
		} else if(data.mob == EntityType.EVOKER && pressed && !this.isCastingSpell() && this.globalSpellCooldown == 0) {
			IllagerSpell spell = switch(ability) {
				default -> IllagerSpell.NONE;
				case 1 -> IllagerSpell.FANGS;
				case 2 -> IllagerSpell.SUMMON_VEX;
				case 3 -> IllagerSpell.WOLOLO;
			};
				
			if(this.getSpellCooldown(spell) == 0)
				this.startCastingSpell(spell);
		} else if(data.mob == EntityType.ILLUSIONER && pressed && !this.isCastingSpell() && this.globalSpellCooldown == 0) {
			IllagerSpell spell = switch(ability) {
				default -> IllagerSpell.NONE;
				case 1 -> IllagerSpell.DISAPPEAR;
				case 2 -> IllagerSpell.BLINDNESS;
			};
				
			if(this.getSpellCooldown(spell) == 0 && (spell != IllagerSpell.DISAPPEAR || !data.player.hasEffect(MobEffects.INVISIBILITY)))
				this.startCastingSpell(spell);
		}
	}

   	public void setDrinkingPotion(boolean drinking) {
   		if(!drinking) {
   			this.drinkingPotion = null;
   			this.drinkingTime = 0;
   		} else {
   			data.player.playSound(SoundEvents.WITCH_DRINK, 1, 0.8F + data.world.random.nextFloat() * 0.4F);
   			this.drinkingPotion = this.selectedPotion;
   			this.drinkingTime = 32;
   		}
   	}

   	public boolean isDrinkingPotion() {
     	return this.drinkingPotion != null;
   	}

   	public ItemStack getPotion() {
   		boolean drink = drinkPotions.contains(this.selectedPotion);
   		ItemStack stack = new ItemStack(drink ? Items.POTION : Items.SPLASH_POTION);
   		PotionUtils.setPotion(stack, this.selectedPotion);
   		return stack;
   	}

	public void usePotion(boolean pressed) {
		if(this.selectedPotion == this.drinkingPotion && !pressed) {
			this.setDrinkingPotion(false);
			return;
		} else if(drinkPotions.contains(this.selectedPotion) && pressed) {
			this.setDrinkingPotion(true);
			return;
		}

		if(!pressed)
			return;

        ThrownPotion thrownpotion = new ThrownPotion(data.world, data.player);
        thrownpotion.setItem(this.getPotion());
		thrownpotion.shootFromRotation(data.player, data.player.getXRot(), data.player.getYRot(), -20.0F, 0.5F, 1.0F);
        data.world.playSound((Player)null, data.player.getX(), data.player.getY(), data.player.getZ(), SoundEvents.WITCH_THROW, data.player.getSoundSource(), 1.0F, 0.8F + data.world.random.nextFloat() * 0.4F);
        data.world.addFreshEntity(thrownpotion);
        this.potionCooldown = 60;
	}

	public void cyclePotion(boolean direction) {
		int currentIdx = potions.indexOf(this.selectedPotion);
		if(currentIdx == -1) {
			this.selectedPotion = potions.get(direction ? 0 : potions.size() - 1);
		} else {
			int selectIdx = currentIdx + (direction ? 1 : -1);
			if(selectIdx < 0)
				selectIdx = potions.size() - 1;
			if(selectIdx > potions.size() - 1)
				selectIdx = 0;
			this.selectedPotion = potions.get(selectIdx);
		}

		data.sync(false, false);
	}

	public void renderInfo(GuiGraphics graphics, int width, int height) {
		if(data.mob == EntityType.WITCH) {
			RenderUtils.abilityIcon(graphics, this.selectedPotion.getName("") + "_potion", !this.isDrinkingPotion(), width - 30, height - 22, this.potionCooldown, Keybinds.Ability1);
		} else if(data.mob == EntityType.EVOKER) {
			RenderUtils.abilityIcon(graphics, "attack_spell", !this.isCastingSpell(), width - 30, height - 22, Math.max(this.globalSpellCooldown, this.attackSpellCooldown), Keybinds.Ability1);
			RenderUtils.abilityIcon(graphics, "summon_spell", !this.isCastingSpell(), width - 30, height - 40, Math.max(this.globalSpellCooldown, this.summonSpellCooldown), Keybinds.Ability2);
			RenderUtils.abilityIcon(graphics, "wololo_spell", !this.isCastingSpell(), width - 30, height - 58, Math.max(this.globalSpellCooldown, this.wololoSpellCooldown), Keybinds.Ability3);
		} else if(data.mob == EntityType.ILLUSIONER) {
			RenderUtils.abilityIcon(graphics, "mirror_spell", (!this.isCastingSpell() && !data.player.hasEffect(MobEffects.INVISIBILITY)), width - 30, height - 22, Math.max(this.globalSpellCooldown, this.mirrorSpellCooldown), Keybinds.Ability1);
			RenderUtils.abilityIcon(graphics, "blindness_spell", !this.isCastingSpell(), width - 30, height - 40, Math.max(this.globalSpellCooldown, this.blindnessSpellCooldown), Keybinds.Ability2);
		}
	}

	public void attacked(LivingAttackEvent event) {
   		if(data.mob == EntityType.WITCH) { 
   			DamageSource source = event.getSource();
   			if(source.getEntity() == data.player && source.is(DamageTypeTags.WITCH_RESISTANT_TO))
   				event.setCanceled(true);
   		}
	}

	public void hurt(LivingHurtEvent event) {
   		if(data.mob == EntityType.WITCH && event.getSource().is(DamageTypeTags.WITCH_RESISTANT_TO))
			event.setAmount(event.getAmount() * 0.15f);
	}

	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder raiderBuilder = switch(GlobalUtils.getString(data.mob)) {
			default -> null;
			case "minecraft:witch" -> Witch.createAttributes();
			case "minecraft:pillager" -> Pillager.createAttributes();
			case "minecraft:vindicator" -> Vindicator.createAttributes();
			case "minecraft:evoker" -> Evoker.createAttributes();
			case "minecraft:illusioner" -> Illusioner.createAttributes();
			case "minecraft:ravager" -> Ravager.createAttributes();
		};

		if(raiderBuilder != null)
			playerBuilder.combine(raiderBuilder);
		return playerBuilder;
	}

	public double speedMultiplier() {
		boolean sprinting = data.player.isSprinting();
		if(data.mob == EntityType.RAVAGER && (this.attackTick > 0 || this.stunnedTick > 0 || this.roarTick > 0))
			return 0;
		
		return switch(GlobalUtils.getString(data.mob)) {
			default -> 1;
			case "minecraft:witch" -> this.isDrinkingPotion() ? 0.35 : 0.4;
			case "minecraft:pillager" -> sprinting ? 0.28 : 0.15;
			case "minecraft:vindicator" -> sprinting ? 0.28 : 0.15;
			case "minecraft:evoker" -> sprinting ? 0.35 : 0.2;
			case "minecraft:illusioner" -> sprinting ? 0.35 : 0.2;
			case "minecraft:ravager" -> sprinting ? 0.3 : 0.09;
		};
	}

	public EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		return switch(GlobalUtils.getString(data.renderAs)) {
			default -> null;
			case "minecraft:witch" -> new WitchRenderer(context);
			case "minecraft:pillager" -> new PillagerRenderer(context);
			case "minecraft:vindicator" -> new VindicatorRenderer(context);
			case "minecraft:evoker" -> new EvokerRenderer(context);
			case "minecraft:illusioner" -> new IllusionerRenderer(context);
			case "minecraft:ravager" -> new RavagerRenderer(context);
		};
	}

	public ModelLayerLocation getModelLayer(boolean outer) {
		return switch(GlobalUtils.getString(data.renderAs)) {
			default -> ModelLayers.PILLAGER;
			case "minecraft:witch" -> ModelLayers.WITCH;
			case "minecraft:vindicator" -> ModelLayers.VINDICATOR;
			case "minecraft:evoker" -> ModelLayers.EVOKER;
			case "minecraft:illusioner" -> ModelLayers.ILLUSIONER;
			case "minecraft:ravager" -> ModelLayers.RAVAGER;
		};
	}

	public boolean renderHand() {
		return this.hasHand() && !this.isCastingSpell();
	}
	
	public HandData getHand(EntityRendererProvider.Context context) {
		if(data.mob == EntityType.WITCH)
			return new HandData();
		HandData hand = new HandData(context, this, new IllagerHand(context.bakeLayer(this.getModelLayer(false)), false), false);
		this.setupHand(hand);
		return hand;
	}

	public void setupHand(HandData hand) {
		if(data.mob == EntityType.RAVAGER || data.mob == EntityType.WITCH)
			return;
		
		String texture = switch(GlobalUtils.getString(data.mob)) {
			default -> "playasmob:textures/entities/pillager.png";
			case "minecraft:vindicator" -> "playasmob:textures/entities/vindicator.png";
			case "minecraft:evoker" -> "playasmob:textures/entities/evoker.png";
			case "minecraft:illusioner" -> "playasmob:textures/entities/illusioner.png";
		};
		
		hand.texture = new ResourceLocation(texture);
		hand.setScale(1f, 1f, 1f);
		hand.setPosition(5.25f, 21.5f, -1.1f);
		hand.setRotation(3.2f, 1.6f, 0.15f);
	}

	public float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return switch(GlobalUtils.getString(data.mob)) {
			default -> dimensions.height * 0.85F;
			case "minecraft:witch" -> 1.62F;
		};
	}

	public MobType getMobType() {
		if(data.mob == EntityType.RAVAGER || data.mob == EntityType.WITCH)
			return MobType.UNDEFINED;
		return MobType.ILLAGER;
	}
	
	public void preTick() {
		if(data.mob == EntityType.WITCH)
			this.witchTick();
		if(data.mob == EntityType.ILLUSIONER)
			this.illusionTick();
			
		if(this.spellCastingWarmUp > 0)
			this.spellCastingWarmUp--;
		if(this.spellCastingTime > 0)
			this.spellCastingTime--;
			
		if(this.attackSpellCooldown > 0)
			this.attackSpellCooldown--;
		if(this.summonSpellCooldown > 0)
			this.summonSpellCooldown--;
		if(this.wololoSpellCooldown > 0)
			this.wololoSpellCooldown--;
		if(this.mirrorSpellCooldown > 0)
			this.mirrorSpellCooldown--;
		if(this.blindnessSpellCooldown > 0)
			this.blindnessSpellCooldown--;
		if(this.globalSpellCooldown > 0)
			this.globalSpellCooldown--;

      	if(this.isCastingSpell() && this.spellCastingTime == 0 && this.castedSpell)
			this.stopCastingSpell();

		if(this.isCastingSpell()) {
         	IllagerSpell spell = this.getCurrentSpell();
			if(this.spellCastingWarmUp == 0 && !this.castedSpell) {
            	data.player.playSound(this.getCastingSoundEvent(), 1, 1);
				this.performSpell(spell);
			}
         	
         	double red = spell.spellColor[0];
         	double green = spell.spellColor[1];
         	double blue = spell.spellColor[2];
         	float f = data.player.yBodyRot * ((float)Math.PI / 180F) + Mth.cos((float)data.player.tickCount * 0.6662F) * 0.25F;
         	float f1 = Mth.cos(f);
         	float f2 = Mth.sin(f);
         	data.world.addParticle(ParticleTypes.ENTITY_EFFECT, data.player.getX() + (double)f1 * 0.6D, data.player.getY() + 1.8D, data.player.getZ() + (double)f2 * 0.6D, red, green, blue);
         	data.world.addParticle(ParticleTypes.ENTITY_EFFECT, data.player.getX() - (double)f1 * 0.6D, data.player.getY() + 1.8D, data.player.getZ() - (double)f2 * 0.6D, red, green, blue);
      	}
	}

   	public int getSpellCooldown(IllagerSpell spell) {
   		return switch(spell) {
   			default -> 0;
   			case FANGS -> this.attackSpellCooldown;
   			case SUMMON_VEX -> this.summonSpellCooldown;
   			case WOLOLO -> this.wololoSpellCooldown;
   			case DISAPPEAR -> this.mirrorSpellCooldown;
   			case BLINDNESS -> this.blindnessSpellCooldown;
   		};
   	}

   	public void illusionTick() {
      	if (data.player.isInvisible()) {
         	--this.IllusionTicks;
         	if(this.IllusionTicks < 0)
            	this.IllusionTicks = 0;

         	if(data.player.hurtTime != 1 && data.player.tickCount % 1200 != 0) {
            	if(data.player.hurtTime == data.player.hurtDuration - 1) {
               		this.IllusionTicks = 3;

               		for(int k = 0; k < 4; ++k) {
                  		this.IllusionOffsets[0][k] = this.IllusionOffsets[1][k];
                  		this.IllusionOffsets[1][k] = new Vec3(0.0D, 0.0D, 0.0D);
              		}
            	}
         	} else {
            	this.IllusionTicks = 3;
            	float f = -6.0F;
            	int i = 13;

            	for(int j = 0; j < 4; ++j) {
               		this.IllusionOffsets[0][j] = this.IllusionOffsets[1][j];
               		this.IllusionOffsets[1][j] = new Vec3((double)(-6.0F + (float)data.world.random.nextInt(13)) * 0.5D, (double)Math.max(0, data.world.random.nextInt(6) - 4), (double)(-6.0F + (float)data.world.random.nextInt(13)) * 0.5D);
            	}

            	for(int l = 0; l < 16; ++l) {
               		data.world.addParticle(ParticleTypes.CLOUD, data.player.getRandomX(0.5D), data.player.getRandomY(), data.player.getZ(0.5D), 0.0D, 0.0D, 0.0D);
            	}

            	data.world.playLocalSound(data.player.getX(), data.player.getY(), data.player.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, data.player.getSoundSource(), 1.0F, 1.0F, false);
        	}
    	}
   	}

	public void witchTick() {
		if(this.potionCooldown > 0)
			this.potionCooldown--;

		if (this.isDrinkingPotion()) {
        	if (this.drinkingTime-- <= 0) {
                List<MobEffectInstance> effects = PotionUtils.getMobEffects(this.getPotion());
                if (effects != null) {
                    for(MobEffectInstance effect : effects) {
                    	data.player.addEffect(new MobEffectInstance(effect));
                	}
                }

				this.potionCooldown = 60;
            	this.setDrinkingPotion(false);
      			data.player.gameEvent(GameEvent.DRINK);
        	}
		}
		
    	if(data.world.random.nextFloat() < 7.5E-4F) {    
         	for(int i = 0; i < data.world.random.nextInt(35) + 10; ++i) {
            	data.world.addParticle(ParticleTypes.WITCH, data.player.getX() + data.world.random.nextGaussian() * (double)0.13F, data.player.getBoundingBox().maxY + 0.5D + data.world.random.nextGaussian() * (double)0.13F, data.player.getZ() + data.world.random.nextGaussian() * (double)0.13F, 0.0D, 0.0D, 0.0D);
         	}
    	}
	}

   	public boolean isCelebrating() {
      	return this.isCelebrating;
   	}
   	
   	public SoundEvent getCelebrateSound() {
   		return switch(GlobalUtils.getString(data.mob)) {
   			default -> SoundEvents.PILLAGER_CELEBRATE;
			case "minecraft:witch" -> SoundEvents.WITCH_CELEBRATE;
			case "minecraft:vindicator" -> SoundEvents.VINDICATOR_CELEBRATE;
			case "minecraft:evoker" -> SoundEvents.EVOKER_CELEBRATE;
			case "minecraft:illusioner" -> SoundEvents.ILLUSIONER_AMBIENT;
		};
   	}

   	public boolean isCastingSpell() {
    	return (this.spellCastingTime != -1 || !this.castedSpell) && this.usingSpell != IllagerSpell.NONE;
   	}

   	public void stopCastingSpell() {
   		this.globalSpellCooldown = this.usingSpell.globalCooldown;
   		this.usingSpell = IllagerSpell.NONE;
   		this.spellCastingWarmUp = 0;
   		this.spellCastingTime = -1;
   		this.castedSpell = false;
		data.updateReach();
   	}

   	public void startCastingSpell(IllagerSpell spell) {
		if(spell == IllagerSpell.NONE)
			return;
			
   		this.castedSpell = false;
   		if(GlobalUtils.getClosestTarget(data.player, 300) instanceof LivingEntity entity)
   			this.spellTarget = entity.getUUID();
   		this.usingSpell = spell;
   		this.spellCastingWarmUp = spell.warmupTime;
   		this.spellCastingTime = spell.castTime;
   		data.player.playSound(spell.prepareSound);
		data.updateReach();
   	}

   	public IllagerSpell getCurrentSpell() {
      	return this.usingSpell;
   	}

   	public int getAttackTick() {
      	return this.attackTick;
   	}

   	public int getStunnedTick() {
      	return this.stunnedTick;
   	}

   	public int getRoarTick() {
      	return this.roarTick;
   	}

	public AbstractIllager.IllagerArmPose getArmPose() {
		if(this.data.typeData != this)
			return AbstractIllager.IllagerArmPose.NEUTRAL;
		
		ItemStack mainStack = data.player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack offStack = data.player.getItemInHand(InteractionHand.MAIN_HAND);
		boolean holdingItem = mainStack.getCount() > 0 || offStack.getCount() > 0;
      	if(mainStack.getItem() == Items.BOW && data.player.isUsingItem())
      		return AbstractIllager.IllagerArmPose.BOW_AND_ARROW;
      	if(mainStack.getItem() == Items.CROSSBOW)
      		return data.player.isUsingItem() ? AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE : AbstractIllager.IllagerArmPose.CROSSBOW_HOLD;
      	if(this.isCastingSpell() && (data.renderAs == EntityType.EVOKER || data.renderAs == EntityType.ILLUSIONER))
      		return AbstractIllager.IllagerArmPose.SPELLCASTING;
      	if(holdingItem || data.renderAs == EntityType.PILLAGER)
      		return data.isAggressive() && holdingItem ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.NEUTRAL;
      	if(this.isCelebrating())
      		return AbstractIllager.IllagerArmPose.CELEBRATING;
      	return AbstractIllager.IllagerArmPose.CROSSED;
   	}

   	public void performSpell(IllagerSpell spell) {
   		this.globalSpellCooldown = spell.globalCooldown;
   		this.castedSpell = true;
   		LivingEntity target = null;
   		if(GlobalUtils.getClosestTarget(data.player, 500) instanceof LivingEntity entity)
   			target = entity;
		if(target == null && this.spellTarget != null && data.world instanceof ServerLevel server)
			target = (LivingEntity)server.getEntity(this.spellTarget);
   		
   		if(spell == IllagerSpell.FANGS) {
         	double distance = -1;
   			Vec3 lookPos = data.player.position().add(data.player.getLookAngle().scale(10));
         	double d0 = Math.min(lookPos.y, data.player.getY());
         	double d1 = Math.max(lookPos.y, data.player.getY()) + 1;
         	float f = (float)Mth.atan2(lookPos.z - data.player.getZ(), lookPos.x - data.player.getX());
         	int nearbyEntities = GlobalUtils.getNearby(data.player, 9, (entity) -> entity instanceof LivingEntity).size();
         	if(target != null) {
	         	d0 = Math.min(target.getY(), data.player.getY());
	         	d1 = Math.max(target.getY(), data.player.getY()) + 1;
	         	f = (float)Mth.atan2(target.getZ() - data.player.getZ(), target.getX() - data.player.getX());
	         	distance = data.player.distanceToSqr(target);
         	}
         	
         	if(distance == -1 ? nearbyEntities > 1 : distance < 9) {
            	for(int i = 0; i < 5; ++i) {
               		float f1 = f + (float)i * (float)Math.PI * 0.4F;
               		this.createFang(data.player.getX() + (double)Mth.cos(f1) * 1.5D, data.player.getZ() + (double)Mth.sin(f1) * 1.5D, d0, d1, f1, 0);
            	}

            	for(int k = 0; k < 8; ++k) {
               		float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
               		this.createFang(data.player.getX() + (double)Mth.cos(f2) * 2.5D, data.player.getZ() + (double)Mth.sin(f2) * 2.5D, d0, d1, f2, 3);
            	}
         	} else {
            	for(int l = 0; l < 16; ++l) {
               		double d2 = 1.25D * (double)(l + 1);
               		int j = 1 * l;
               		this.createFang(data.player.getX() + (double)Mth.cos(f) * d2, data.player.getZ() + (double)Mth.sin(f) * d2, d0, d1, f, j);
            	}
         	}
   			this.attackSpellCooldown = spell.cooldown;
   		} else if(spell == IllagerSpell.SUMMON_VEX && data.world instanceof ServerLevel server) {
         	for(int i = 0; i < 3; ++i) {
            	BlockPos blockpos = data.player.blockPosition().offset(-2 + server.random.nextInt(5), 1, -2 + server.random.nextInt(5));
            	Vex vex = EntityType.VEX.create(server);
            	if (vex != null) {
               		vex.moveTo(blockpos, 0, 0);
               		vex.finalizeSpawn(server, server.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
               		vex.setBoundOrigin(blockpos);
               		vex.setLimitedLife(20 * (30 + server.random.nextInt(90)));
               		server.addFreshEntityWithPassengers(vex);
               		if(target != null)
               			vex.setTarget(target);
            	}
         	}
   			this.summonSpellCooldown = spell.cooldown;
   		} else if(spell == IllagerSpell.WOLOLO && target != null) {
   			if(target instanceof Sheep sheep) {
   				sheep.setColor(DyeColor.RED);
   			} else if(target instanceof TamableAnimal tameable && !tameable.isTame()) {
   				tameable.tame(data.player);
   			} else if(target instanceof Villager villager) {
   				AbstractIllager illager = villager.convertTo(EntityType.PILLAGER, false);
   				if(illager != null) {
   					SimpleContainer villagerInventory = villager.getInventory();
   					for(int idx = 0; idx < villagerInventory.getContainerSize(); idx++) {
   						ItemStack stack = villagerInventory.getItem(idx);
   						if(stack.getCount() > 0)
   							villager.spawnAtLocation(stack);
   					}
					for(ItemStack stack : villager.getAllSlots()) {
						if(stack.getCount() > 0)
   							villager.spawnAtLocation(stack);
					}
   					net.minecraftforge.event.ForgeEventFactory.onLivingConvert(villager, illager);
   				}
   			} else if(target instanceof Allay allay) {
   				Vex vex = allay.convertTo(EntityType.VEX, false);
   				if(vex != null) {
   					SimpleContainer allayInventory = allay.getInventory();
   					for(int idx = 0; idx < allayInventory.getContainerSize(); idx++) {
   						ItemStack stack = allayInventory.getItem(idx);
   						if(stack.getCount() > 0)
   							allay.spawnAtLocation(stack);
   					}
					for(ItemStack stack : allay.getAllSlots()) {
						if(stack.getCount() > 0)
   							allay.spawnAtLocation(stack);
					}
   					net.minecraftforge.event.ForgeEventFactory.onLivingConvert(allay, vex);
   				}
   			} else {
   				target.addEffect(new MobEffectInstance(Effects.Frenzy.get(), 400));
   			}
   			
   			this.wololoSpellCooldown = spell.cooldown;
   		} else if(spell == IllagerSpell.DISAPPEAR) {
   			data.player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1200));
   			this.mirrorSpellCooldown = spell.cooldown;
   		} else if(spell == IllagerSpell.BLINDNESS && target != null) {
   			target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400), data.player);
   			this.blindnessSpellCooldown = spell.cooldown;
   		}

   		this.spellTarget = null;
   	}

    public void createFang(double p_32673_, double p_32674_, double p_32675_, double p_32676_, float p_32677_, int p_32678_) {
    	BlockPos blockpos = BlockPos.containing(p_32673_, p_32676_, p_32674_);
        boolean flag = false;
        double d0 = 0.0D;

        do{
        	BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = data.world.getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(data.world, blockpos1, Direction.UP)) {
               	if (!data.world.isEmptyBlock(blockpos)) {
                  	BlockState blockstate1 = data.world.getBlockState(blockpos);
                  	VoxelShape voxelshape = blockstate1.getCollisionShape(data.world, blockpos);
                  	if (!voxelshape.isEmpty()) {
                    	d0 = voxelshape.max(Direction.Axis.Y);
                	}
                }

                flag = true;
            	break;
            }

        	blockpos = blockpos.below();
        } while(blockpos.getY() >= Mth.floor(p_32675_) - 1);

        if (flag) {
    	    data.world.addFreshEntity(new EvokerFangs(data.world, p_32673_, (double)blockpos.getY() + d0, p_32674_, p_32677_, p_32678_, data.player));
    	}
	}
}