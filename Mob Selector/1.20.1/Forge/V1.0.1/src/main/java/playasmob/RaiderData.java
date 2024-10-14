package playasmob;

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

public class RaiderData implements EntityTypeData, GolemAttack {
	public static final List<Potion> drinkPotions = List.of(Potions.HEALING, Potions.SWIFTNESS, Potions.WATER_BREATHING, Potions.FIRE_RESISTANCE);
	public static final List<Potion> potions = List.of(Potions.HARMING, Potions.HEALING, Potions.POISON, Potions.REGENERATION, Potions.SLOWNESS, Potions.SWIFTNESS, Potions.WATER_BREATHING, Potions.FIRE_RESISTANCE);
	public static final List<String> textures = List.of("harming", "healing", "poison", "regeneration", "slowness", "swiftness", "water_breathing", "fire_resistance");
	
   	public MobData data;
	public Potion selectedPotion = Potions.HEALING;
	public Potion drinkingPotion = null;
	public int potionCooldown = 0;
	public int drinkingTime = 0;

   	public List<MobAttribute> getAbilities(EntityType type) {
   		ArrayList<MobAttribute> abilities = new ArrayList();
   		if(type == EntityType.WITCH) {
   			abilities.add(MobAttribute.ability(1, Component.translatable("playasmob.ability.use_potion")));
   			abilities.add(MobAttribute.ability(2, Component.translatable("playasmob.cycle.next").append(Component.translatable("playasmob.ability.potion"))));
   			abilities.add(MobAttribute.ability(3, Component.translatable("playasmob.cycle.previous").append(Component.translatable("playasmob.ability.potion"))));
   		}
   		return abilities;
   	}

   	public List<MobAttribute> getPros(EntityType type) {
   		ArrayList<MobAttribute> pros = new ArrayList();
   		if(type == EntityType.WITCH) {
   			pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.magic_resistance")));
   			pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.health")));
   		}
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
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.sprint")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.crouch")));
   		return cons;
   	}

	public RaiderData(MobData data) {
		this.data = data;
	}

	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
		compound.putInt("potion", Math.max(potions.indexOf(this.selectedPotion), 0));
		compound.putInt("potionCooldown", this.potionCooldown);
		compound.putInt("drinkingTime", this.drinkingTime);

		if(this.isDrinkingPotion())
			compound.putInt("drinkingPotion", potions.indexOf(this.drinkingPotion));
		return compound;
	}

	public void load(CompoundTag compound) {
		this.selectedPotion = potions.get(compound.getInt("potion"));
		this.potionCooldown = compound.getInt("potionCooldown");
		this.drinkingTime = compound.getInt("drinkingTime");

		if(compound.contains("drinkingPotion"))
			this.drinkingPotion = potions.get(compound.getInt("drinkingPotion"));
	}

	public boolean canSprint() {
		return false;
	}

	public boolean canCrouch() {
		return false;
	}

	public boolean hasHand() {
		return true;
	}

	public void press(int ability, boolean pressed) {
		if(data.mob == EntityType.WITCH) {
			if(ability == 1 && this.potionCooldown == 0)
				this.usePotion(pressed);
			if(ability == 2 && pressed)
				this.cyclePotion(false);
			if(ability == 3 && pressed)
				this.cyclePotion(true);
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

   		data.updateSpeed();
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
			graphics.blit(new ResourceLocation("playasmob:textures/screens/potion_" + textures.get(Math.max(potions.indexOf(this.selectedPotion), 0)) + ".png"), width - 30, height - 30, 0, 0, 16, 16, 16, 16);
			if(this.potionCooldown > 0)
				graphics.drawCenteredString(Minecraft.getInstance().font, Component.literal("" + ((this.potionCooldown / 20) + 1)), width - 21, height - 12, -1);
			if(this.potionCooldown == 0)
				graphics.drawCenteredString(Minecraft.getInstance().font, Keybinds.Ability1.getTranslatedKeyMessage(), width - 21, height - 12, -1);
		}
	}

	public void hurt(LivingHurtEvent event) {
		event.setAmount(this.getDamageAfterMagicAbsorb(event.getSource(), event.getAmount()));
	}

   	public float getDamageAfterMagicAbsorb(DamageSource source, float amount) {
      	if (source.getEntity() == data.player)
         	amount = 0;
      	if (source.is(DamageTypeTags.WITCH_RESISTANT_TO))
        	amount *= 0.15;
      	return amount;
   	}

	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder raiderBuilder = switch(GlobalUtils.getString(data.mob)) {
			default -> null;
			case "minecraft:witch" -> Witch.createAttributes();
		};

		if(raiderBuilder != null)
			playerBuilder.combine(raiderBuilder);
		return playerBuilder;
	}

	public double speedMultiplier() {
		return switch(GlobalUtils.getString(data.mob)) {
			default -> 1;
			case "minecraft:witch" -> this.isDrinkingPotion() ? 0.35 : 0.4;
		};
	}

	public EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		return switch(GlobalUtils.getString(data.renderAs)) {
			default -> null;
			case "minecraft:witch" -> new WitchRenderer(context);
		};
	}

	public float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return switch(GlobalUtils.getString(data.mob)) {
			default -> dimensions.height * 0.85F;
			case "minecraft:witch" -> 1.62F;
		};
	}
	
	public void preTick() {
		if(data.mob == EntityType.WITCH)
			this.witchTick();
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
}
