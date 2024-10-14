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

public class VillagerData implements VillagerDataHolder, EntityTypeData, ZombieAttack, IllagerAttack {
	public MobData data = null;
	public net.minecraft.world.entity.npc.VillagerData villagerData = new net.minecraft.world.entity.npc.VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1);

   	public List<MobAttribute> getAbilities(EntityType type) {
   		ArrayList<MobAttribute> abilities = new ArrayList();
   		abilities.add(MobAttribute.ability(1, Component.translatable("playasmob.ability.set_workspace")));
   		return abilities;
   	}

   	public List<MobAttribute> getInfo(EntityType type) {
   		ArrayList<MobAttribute> info = new ArrayList();
   		info.add(MobAttribute.info(Component.translatable("playasmob.info.transform_villager")));
   		return info;
   	}

   	public List<MobAttribute> getCons(EntityType type) {
   		ArrayList<MobAttribute> cons = new ArrayList();
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.crouch")));
   		return cons;
   	}
	
	public VillagerData(MobData data) {
		this.data = data;
	}

	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
      	net.minecraft.world.entity.npc.VillagerData.CODEC.encodeStart(NbtOps.INSTANCE, this.getVillagerData()).resultOrPartial(data.LOGGER::error).ifPresent((data) -> {
         	compound.put("VillagerData", data);
      	});

      	return compound;
	}
	
	public void load(CompoundTag compound) {
      	if (compound.contains("VillagerData", 10)) {
         	DataResult<net.minecraft.world.entity.npc.VillagerData> dataresult = net.minecraft.world.entity.npc.VillagerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, compound.get("VillagerData")));
         	dataresult.resultOrPartial(data.LOGGER::error).ifPresent(this::setVillagerData);
      	}
	}

   	public void thunderHit(ServerLevel server, LightningBolt bolt) {
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

	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder villagerBuilder = Villager.createAttributes();
		playerBuilder.combine(villagerBuilder);
		return playerBuilder;
	}

	public double speedMultiplier() {
		return 0.2;
	}

	public EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		return new VillagerRenderer(context);
	}
	
	public float getEyeHeight(Pose pose, EntityDimensions dimensions) {
		return this.isBaby() ? 0.81F : 1.62F;
	}

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

	public boolean hasHand() {
		return true;
	}

	public boolean canCrouch() {
		return false;
	}

	public boolean isBaby() {
		return false;
	}
}
