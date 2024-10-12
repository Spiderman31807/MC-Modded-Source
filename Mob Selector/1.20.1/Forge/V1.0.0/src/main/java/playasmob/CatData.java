package playasmob;

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

public class CatData implements EntityTypeData<CatRenderer>, TamableData {
	public MobData data = null;
	public TamedData tamedData = new TamedData(this);
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

	public CatData(MobData data) {
		this.data = data;
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

		CatVariant variant = BuiltInRegistries.CAT_VARIANT.get(ResourceLocation.tryParse(compound.getString("variant")));
      	if (variant != null)
         	this.setVariant(variant);
	}

	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
		compound.put("TameData", this.tameSave());
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
		
		if(ability == 3)
			this.hiss();
		
		if(ability == 1) {
			if(!this.isInSittingPose() && this.isLying())
				this.setLying(false);
			this.setInSittingPose(!this.isInSittingPose());
		}
		
		if(ability == 2) {
			if(!this.isLying() && this.isInSittingPose())
				this.setInSittingPose(false);
			this.setLying(!this.isLying());
		}
	}

	public CatRenderer getRenderer(EntityRendererProvider.Context context) {
		return new CatRenderer(context);
	}

	public double speedMultiplier() {
		return 0.3;
	}

	public boolean canPickup(ItemStack stack) {
		return false;
	}

	public boolean preventSleeping() {
		return true;
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

   	public void hiss() {
   		if(this.hissCooldown == 0) {
      		data.player.playSound(SoundEvents.CAT_HISS);
      		this.hissCooldown = 20;
   		}
   	}

   	public void tick() {
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
