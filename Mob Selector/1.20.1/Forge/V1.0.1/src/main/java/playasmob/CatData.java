package playasmob;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.CatVariantTags;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

public class CatData implements EntityTypeData, TamableData, AgeableData {
	public MobData data = null;
	public TamedData tamedData;
	public AgeData ageData;
	public boolean isLying = false;
	public boolean relaxStateOne = false;
	public CatVariant variant = BuiltInRegistries.CAT_VARIANT.getOrThrow(CatVariant.BLACK);
   	public float lieDownAmount;
   	public float lieDownAmountO;
   	public float lieDownAmountTail;
   	public float lieDownAmountOTail;
   	public float relaxStateOneAmount;
   	public float relaxStateOneAmountO;
   	public int hissCooldown = 0;

   	public List<MobAttribute> getAbilities(EntityType type) {
   		ArrayList<MobAttribute> abilities = new ArrayList();
   		abilities.add(MobAttribute.ability(1, Component.translatable("playasmob.ability.sit")));
   		abilities.add(MobAttribute.ability(2, Component.translatable("playasmob.ability.lay")));
   		abilities.add(MobAttribute.ability(3, Component.translatable("playasmob.ability.hiss")));
   		return abilities;
   	}

   	public List<MobAttribute> getPros(EntityType type) {
   		ArrayList<MobAttribute> pros = new ArrayList();
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.fall_immune")));
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.creeper_scared")));
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.sprint")));
   		pros.add(MobAttribute.pro(Component.translatable("playasmob.pro.crouch")));
   		return pros;
   	}

   	public List<MobAttribute> getInfo(EntityType type) {
   		ArrayList<MobAttribute> info = new ArrayList();
   		info.add(MobAttribute.info(Component.translatable("playasmob.info.tamable")));
   		info.add(MobAttribute.info(Component.translatable("playasmob.info.ageable")));
   		info.add(MobAttribute.info(Component.translatable("playasmob.info.variants")));
   		return info;
   	}

   	public List<MobAttribute> getCons(EntityType type) {
   		ArrayList<MobAttribute> cons = new ArrayList();
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.health")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.hand")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.pickup")));
   		return cons;
   	}

	public CatData(MobData data) {
		this.data = data;
		this.tamedData = new TamedData(this, data);
		this.ageData = new AgeData(this, data);
	}

	public CatData randomiseVariant(boolean randomise, Level world) {
		if(!randomise)
			return this;
      	boolean flag = world.getMoonBrightness() > 0.9F;
      	TagKey<CatVariant> tagkey = flag ? CatVariantTags.FULL_MOON_SPAWNS : CatVariantTags.DEFAULT_SPAWNS;
      	BuiltInRegistries.CAT_VARIANT.getTag(tagkey).flatMap((p_289435_) -> { return p_289435_.getRandomElement(world.getRandom()); }).ifPresent((variant) -> {
         	this.setVariant(variant.value());
      	});

      	return this;
	}

	public void load(CompoundTag compound) {
		if(compound.contains("TameData"))
			this.tameLoad(compound.getCompound("TameData"));
		if(compound.contains("AgeData"))
			this.ageLoad(compound.getCompound("AgeData"));

		this.hissCooldown = compound.getInt("hissCooldown");
		this.lieDownAmount = compound.getFloat("lieDownAmount");
		this.lieDownAmountO = compound.getFloat("lieDownAmountO");
		this.lieDownAmountTail = compound.getFloat("lieDownAmountTail");
		this.lieDownAmountOTail = compound.getFloat("lieDownAmountOTail");
		this.relaxStateOneAmount = compound.getFloat("relaxStateOneAmount");
		this.relaxStateOneAmountO = compound.getFloat("relaxStateOneAmountO");
		this.relaxStateOne = compound.getBoolean("relaxStateOne");
		this.isLying = compound.getBoolean("isLying");
		
		CatVariant variant = BuiltInRegistries.CAT_VARIANT.get(ResourceLocation.tryParse(compound.getString("variant")));
      	if (variant != null)
         	this.setVariant(variant);
	}

	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
		compound.put("TameData", this.tameSave());
		compound.put("AgeData", this.ageSave());
		
		compound.putInt("hissCooldown", this.hissCooldown);
		compound.putFloat("lieDownAmount", this.lieDownAmount);
		compound.putFloat("lieDownAmountO", this.lieDownAmountO);
		compound.putFloat("lieDownAmountTail", this.lieDownAmountTail);
		compound.putFloat("lieDownAmountOTail", this.lieDownAmountOTail);
		compound.putFloat("relaxStateOneAmount", this.relaxStateOneAmount);
		compound.putFloat("relaxStateOneAmountO", this.relaxStateOneAmountO);
		compound.putBoolean("relaxStateOne", this.relaxStateOne);
		compound.putBoolean("isLying", this.isLying);
		
      	compound.putString("variant", BuiltInRegistries.CAT_VARIANT.getKey(this.getVariant()).toString());
		return compound;
	}
	
	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder catBuilder = Cat.createAttributes();
		playerBuilder.combine(catBuilder);
		return playerBuilder;
	}

	public void press(int ability, boolean pressed) {
		if(pressed == false)
			return;
		
		if(ability == 1) {
			if(!this.isInSittingPose() && this.isLying())
				this.setLying(false);
			this.setInSittingPose(!this.isInSittingPose());
      		this.data.updateSpeed();
		}
		
		if(ability == 2) {
			if(!this.isLying() && this.isInSittingPose())
				this.setInSittingPose(false);
			this.setLying(!this.isLying());
      		this.data.updateSpeed();
		}
		
		if(ability == 3)
			this.hiss();
	}

	public void setInSittingPose(boolean sitting) {
		this.tamedData.setInSittingPose(sitting);
		if(sitting == true) {
	   		this.lieDownAmount = 0;
	   		this.lieDownAmountO = 0;
	   		this.lieDownAmountTail = 0;
	   		this.lieDownAmountOTail = 0;
	   		this.relaxStateOneAmount = 0;
	   		this.relaxStateOneAmountO = 0;
		}
	}

	public EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		return new CatRenderer(context);
	}
	
	public void causeFallDamage(LivingFallEvent event) {
		event.setCanceled(true);
	}

	public double speedMultiplier() {
		if(this.isLying() || this.isInSittingPose())
			return 0;
		if(data.isSprinting)
			return 0.5;
		if(data.isCrouching)
			return 0.4;
		return 0.28;
	}

	public boolean canPickup(ItemStack stack) {
		return false;
	}

	public boolean canSprint() {
		return !this.isInSittingPose() && !this.isLying();
	}

	public boolean canCrouch() {
		return !this.isInSittingPose() && !this.isLying();
	}

   	public ResourceLocation getResourceLocation() {
      	return this.getVariant().texture();
   	}

   	public CatVariant getVariant() {
      	return this.variant;
   	}

   	public void setVariant(CatVariant variant) {
      	this.variant = variant;
   	}

   	public void setLying(boolean lying) {
      	this.isLying = lying;
   	}

   	public boolean isLying() {
      	return this.isLying;
   	}

   	public void setRelaxStateOne(boolean relax) {
      	this.relaxStateOne = relax;
  	}

   	public boolean isRelaxStateOne() {
      	return this.relaxStateOne;
   	}

	public TamedData tameData() {
		return this.tamedData;
	}

	public AgeData ageData() {
		return this.ageData;
	}

   	public void hiss() {
   		if(this.hissCooldown == 0) {
      		data.player.playSound(SoundEvents.CAT_HISS);
      		this.hissCooldown = 20;
   		}
   	}

   	public void preTick() {
   		if(this.ageData != null)
   			this.ageData.tick();
   		if(this.hissCooldown > 0)
   			this.hissCooldown--;
      	this.handleLieDown();
   	}

   	public void handleLieDown() {
      	if ((this.isLying() || this.isRelaxStateOne()) && data.player.tickCount % 5 == 0)
         	data.player.playSound(SoundEvents.CAT_PURR, 0.6F + 0.4F * (data.world.random.nextFloat() - data.world.random.nextFloat()), 1.0F);
      	this.updateLieDownAmount();
      	this.updateRelaxStateOneAmount();
   	}

   	public void updateLieDownAmount() {
      	this.lieDownAmountO = this.lieDownAmount;
      	this.lieDownAmountOTail = this.lieDownAmountTail;
      	if (this.isLying()) {
         	this.lieDownAmount = Math.min(1.0F, this.lieDownAmount + 0.15F);
         	this.lieDownAmountTail = Math.min(1.0F, this.lieDownAmountTail + 0.08F);
      	} else {
         	this.lieDownAmount = Math.max(0.0F, this.lieDownAmount - 0.22F);
         	this.lieDownAmountTail = Math.max(0.0F, this.lieDownAmountTail - 0.13F);
      	}
   	}

   	public void updateRelaxStateOneAmount() {
      	this.relaxStateOneAmountO = this.relaxStateOneAmount;
      	if (this.isRelaxStateOne()) {
         	this.relaxStateOneAmount = Math.min(1.0F, this.relaxStateOneAmount + 0.1F);
      	} else {
         	this.relaxStateOneAmount = Math.max(0.0F, this.relaxStateOneAmount - 0.13F);
      	}
   	}
	
   	public float getLieDownAmount(float p_28184_) {
      	return Mth.lerp(p_28184_, this.lieDownAmountO, this.lieDownAmount);
   	}

   	public float getLieDownAmountTail(float p_28188_) {
      	return Mth.lerp(p_28188_, this.lieDownAmountOTail, this.lieDownAmountTail);
   	}

   	public float getRelaxStateOneAmount(float p_28117_) {
      	return Mth.lerp(p_28117_, this.relaxStateOneAmountO, this.relaxStateOneAmount);
   	}
}
