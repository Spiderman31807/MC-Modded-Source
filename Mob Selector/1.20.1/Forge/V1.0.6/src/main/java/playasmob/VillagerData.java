package playasmob;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Items;

public class VillagerData extends EntityTypeData implements VillagerDataHolder, AgeableData, ZombieAttack, IllagerAttack {
	public AgeData ageData;
	public net.minecraft.world.entity.npc.VillagerData villagerData = new net.minecraft.world.entity.npc.VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1);
	
   	@Override
   	public List<MobAttribute> getAbilities(EntityType type) {
   		ArrayList<MobAttribute> abilities = new ArrayList();
   		abilities.add(MobAttribute.ability(1, Component.translatable("playasmob.ability.set_workspace")));
   		return abilities;
   	}

   	@Override
   	public List<MobAttribute> getInfo(EntityType type) {
   		ArrayList<MobAttribute> info = new ArrayList();
   		info.add(MobAttribute.info(Component.translatable("playasmob.info.villager_diet")).tooltip(Component.translatable("playasmob.tooltip.villager_diet")));
   		info.add(MobAttribute.info(Component.translatable("playasmob.info.transform_villager")).fullOnly());
   		return info;
   	}

   	@Override
   	public List<MobAttribute> getCons(EntityType type) {
   		ArrayList<MobAttribute> cons = new ArrayList();
   		return cons;
   	}
	
	public VillagerData(MobData data) {
		super(data);
		this.ageData = new AgeData(this, data);
	}

   	@Override
	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
		compound.put("AgeData", this.ageSave());
		compound.putBoolean("IsBaby", this.isBaby());
      	net.minecraft.world.entity.npc.VillagerData.CODEC.encodeStart(NbtOps.INSTANCE, this.getVillagerData()).resultOrPartial(PlayasmobMod.LOGGER::error).ifPresent((data) -> {
         	compound.put("VillagerData", data);
      	});

      	return compound;
	}
	
   	@Override
	public void load(CompoundTag compound) {
		if(compound.contains("AgeData"))
			this.ageLoad(compound.getCompound("AgeData"));
      	if(compound.getBoolean("IsBaby") && !compound.contains("AgeData"))
      		this.setBabyAge(true);
			
      	if (compound.contains("VillagerData", 10)) {
         	DataResult<net.minecraft.world.entity.npc.VillagerData> dataresult = net.minecraft.world.entity.npc.VillagerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, compound.get("VillagerData")));
         	dataresult.resultOrPartial(PlayasmobMod.LOGGER::error).ifPresent(this::setVillagerData);
      	}
      	
	}

	public AgeData ageData() {
		return this.ageData;
	}

   	@Override
   	public void thunderHit(ServerLevel server, LightningBolt bolt) {
   		if(data.fullyMobMode)
      		data.changeMob(EntityType.WITCH, null, false);
   	}

	public net.minecraft.world.entity.npc.VillagerData getVillagerData() {
		return this.villagerData;
	}

	public void setVillagerData(net.minecraft.world.entity.npc.VillagerData data) {
      	if (this.villagerData.getProfession() != data.getProfession()) {
      		
      	}
         	
        this.villagerData = data;
	}

   	@Override
	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder villagerBuilder = Villager.createAttributes();
		playerBuilder.combine(villagerBuilder);
		return playerBuilder;
	}

   	@Override
   	public SoundEvent getAmbientSound() {
		return SoundEvents.VILLAGER_AMBIENT;
   	}

   	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.VILLAGER_HURT;
	}

   	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.VILLAGER_DEATH;
	}

   	@Override
	public double speedMultiplier() {
		return 0.2;
	}

   	@Override
	public EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		return new VillagerRenderer(context);
	}
	
   	@Override
	public float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.83f;
	}

   	@Override
	public void press(int ability, boolean pressed) {
		if(ability == 1 && pressed)
			this.getNewWorkspace();
	}

	public void getNewWorkspace() {
		BlockHitResult result = data.world.clip(new ClipContext(data.player.getEyePosition(1f), data.player.getEyePosition(1f).add(data.player.getViewVector(1f).scale(5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, data.player));
		if(result.getType() != HitResult.Type.BLOCK)
			return;
			
		if(data.world instanceof ServerLevel server) {
	        Optional.ofNullable(server).flatMap((p_22467_) -> {
	        	return p_22467_.getPoiManager().getType(result.getBlockPos());
	        }).flatMap((p_258313_) -> {
	        	return BuiltInRegistries.VILLAGER_PROFESSION.stream().filter((p_217125_) -> {
	        		return p_217125_.heldJobSite().test(p_258313_);
	        	}).findFirst();
	        }).ifPresent((p_22464_) -> {
	        	this.setVillagerData(this.getVillagerData().setProfession(p_22464_));
	        	data.sync(false, false);
	        });
		}
	}
	
   	public void playWorkSound() {
      	SoundEvent soundevent = this.getVillagerData().getProfession().workSound();
      	if (soundevent != null)
         	data.player.playSound(soundevent);
   	}

   	@Override
	public String dietName() {
		return "villager";
	}

   	@Override
   	public Diet getDiet() {
   		return Diet.Custom;
   	}

   	@Override
   	public void buildFoodInfo() {
   		super.buildFoodInfo();
   		this.addFoodInfo(Items.BREAD, null, null);
   		this.addFoodInfo(Items.CARROT, null, null);
   		this.addFoodInfo(Items.POTATO, null, null);
   		this.addFoodInfo(Items.BEETROOT, null, null);
   	}

   	@Override
	public boolean hasHand() {
		return true;
	}

   	@Override
	public boolean preventSleeping() {
		return false;
	}
}
