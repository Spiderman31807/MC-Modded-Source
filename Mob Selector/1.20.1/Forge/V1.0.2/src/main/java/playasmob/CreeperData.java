package playasmob;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.Level;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EntityType;

public class CreeperData implements EntityTypeData {
	public MobData data;
   	public boolean powered = false;
   	public boolean ignited = false;
   	public boolean exploded = false;
   	public int explosionRadius = 3;
   	public int droppedSkulls = 0;
   	public int maxSwell = 30;
   	public int swellDir = -1;
   	public int oldSwell = 0;
   	public int swell = 0;

   	public List<MobAttribute> getAbilities(EntityType type) {
   		ArrayList<MobAttribute> abilities = new ArrayList();
   		abilities.add(MobAttribute.ability(1, Component.translatable("playasmob.ability.explode").withStyle(ChatFormatting.GRAY)));
   		return abilities;
   	}

   	public List<MobAttribute> getInfo(EntityType type) {
   		ArrayList<MobAttribute> info = new ArrayList();
   		info.add(MobAttribute.info(Component.translatable("playasmob.info.chargeable")));
   		return info;
   	}

   	public List<MobAttribute> getCons(EntityType type) {
   		ArrayList<MobAttribute> cons = new ArrayList();
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.hand")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.pickup")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.sprint")));
   		cons.add(MobAttribute.con(Component.translatable("playasmob.con.crouch")));
   		return cons;
   	}

	public CreeperData(MobData data) {
		this.data = data;
	}

	public void load(CompoundTag compound) {
      	this.powered = compound.getBoolean("powered");
      	this.oldSwell = compound.getInt("oldSwell");
      	this.swell = compound.getInt("swell");
      	if (compound.contains("Fuse", 99))
         	this.maxSwell = compound.getShort("Fuse");

      	if (compound.contains("ExplosionRadius", 99))
         	this.explosionRadius = compound.getByte("ExplosionRadius");

      	if (compound.getBoolean("ignited"))
         	this.ignite();
	}

	public CompoundTag save() {
		CompoundTag compound = new CompoundTag();
      	if (this.powered)
         	compound.putBoolean("powered", true);

      	compound.putShort("Fuse", (short)this.maxSwell);
      	compound.putByte("ExplosionRadius", (byte)this.explosionRadius);
     	compound.putBoolean("ignited", this.isIgnited());
     	compound.putInt("oldSwell", this.oldSwell);
     	compound.putInt("swell", this.swell);
		return compound;
	}

	public EntityRenderer getRenderer(EntityRendererProvider.Context context) {
		return new CreeperRenderer(context);
	}
	
	public AttributeSupplier.Builder modifyAttributes(AttributeSupplier.Builder playerBuilder) {
		AttributeSupplier.Builder creeperBuilder = Creeper.createAttributes();
		playerBuilder.combine(creeperBuilder);
		return playerBuilder;
	}

	public double speedMultiplier() {
		return 0.3;
	}

	public void respawn() {
		data.player.setInvisible(false);
		this.load(new CompoundTag());
	}

	public void press(int ability, boolean pressed) {
		if(ability != 1)
			return;
		this.setSwellDir(pressed ? 1 : -1);
	}

	public void preTick() {
      	if (data.player.isAlive()) {
         	this.oldSwell = this.swell;
         	if (this.isIgnited())
            	this.setSwellDir(1);

         	int i = this.getSwellDir();
         	if (i > 0 && this.swell == 0) {
            	data.player.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
            	data.player.gameEvent(GameEvent.PRIME_FUSE);
         	}

         	this.swell += i;
         	if (this.swell < 0) {
            	this.swell = 0;
         	}

         	if (this.swell >= this.maxSwell) {
            	this.swell = this.maxSwell;
            	this.explodeCreeper();
         	}
      	}
   	}

   	public boolean isPowered() {
      	return this.powered;
   	}

   	public void causeFallDamage(LivingFallEvent event) {
      	this.swell += (int)(event.getDistance() * 1.5F);
      	if (this.swell > this.maxSwell - 5)
         	this.swell = this.maxSwell - 5;
   	}

   	public float getSwelling(float value) {
      	return Mth.lerp(value, (float)this.oldSwell, (float)this.swell) / (float)(this.maxSwell - 2);
   	}

   	public int getSwellDir() {
      	return this.swellDir;
   	}

   	public void setSwellDir(int dir) {
      	this.swellDir = dir;
   	}

   	public boolean isIgnited() {
      	return this.ignited;
   	}

   	public void ignite() {
      	this.ignited = true;
   	}

   	public boolean canDropMobsSkull() {
      	return this.isPowered() && this.droppedSkulls < 1;
   	}

   	public void increaseDroppedSkulls() {
      	++this.droppedSkulls;
   	}

   	public void thunderHit(ServerLevel server, LightningBolt bolt) {
      	this.powered = true;
      	data.sync(false, false);
   	}

   	public void explodeCreeper() {
      	if (!data.world.isClientSide) {
         	float f = this.isPowered() ? 2.0F : 1.0F;
			data.player.setInvisible(true);
         	data.player.setHealth(0);
         	data.world.explode(data.player, data.player.getX(), data.player.getY(), data.player.getZ(), (float)this.explosionRadius * f, Level.ExplosionInteraction.MOB);
         	this.spawnLingeringCloud();
      	}
   	}

   	public void spawnLingeringCloud() {
      	Collection<MobEffectInstance> collection = data.player.getActiveEffects();
      	if (!collection.isEmpty()) {
         	AreaEffectCloud areaeffectcloud = new AreaEffectCloud(data.world, data.player.getX(), data.player.getY(), data.player.getZ());
         	areaeffectcloud.setRadius(2.5F);
         	areaeffectcloud.setRadiusOnUse(-0.5F);
         	areaeffectcloud.setWaitTime(10);
         	areaeffectcloud.setDuration(areaeffectcloud.getDuration() / 2);
         	areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float)areaeffectcloud.getDuration());

         	for(MobEffectInstance mobeffectinstance : collection) {
            	areaeffectcloud.addEffect(new MobEffectInstance(mobeffectinstance));
         	}

         	data.world.addFreshEntity(areaeffectcloud);
      	}
   	}
}
