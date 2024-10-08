package zombiemode;

import javax.annotation.Nullable;
import java.util.UUID;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.EntityType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public class ZombieData {
	public Player player;
	public Level world;
	public EntityType type = EntityType.PLAYER;
	public boolean isUnderWaterConverting = false;
	public int conversionTime = 0;
	public int inWaterTime = -1;
	
   	@Nullable
   	public UUID conversionStarter;
	public boolean isCureConverting = false;
   	public int cureConversionTime = 0;

	public ZombieData(@Nullable Player player) {
		this.player = Minecraft.getInstance().player;
		if(player != null)
			this.player = player;
			
		if(this.player != null) {
			this.world = this.player.level();
			/*if(world.getBiome(this.player.blockPosition()).is(Biomes.DESERT))
				this.type = EntityType.HUSK;*/
			this.sync();
		}
	}

	public void updatePlayer(Player player) {
		if(this.player == null)
			this.player = player;
		this.world = this.player.level();
		this.sync();
	}

	public ZombieData(CompoundTag data, Level world) {
		if(world != null) {
			this.world = world;
			if(data.contains("player"))
				this.player = world.getPlayerByUUID(data.getUUID("player"));
		}
		
		if(data.contains("isConverting"))
			this.isUnderWaterConverting = data.getBoolean("isConverting");
		if(data.contains("convertTime"))
			this.conversionTime = data.getInt("convertTime");
		if(data.contains("inWater"))
			this.inWaterTime = data.getInt("inWater");
		if(data.contains("isCureConverting"))
			this.isCureConverting = data.getBoolean("isCureConverting");
		if(data.contains("cureConvertTime"))
			this.cureConversionTime = data.getInt("cureConvertTime");
		if(data.contains("cureStarter"))
			this.conversionStarter = data.getUUID("cureStarter");

		if(data.contains("type")) {
			EntityType.byString(data.getString("type")).ifPresent((type) -> {
				this.type = type;
			});
		}
		
		this.sync();
	}

	public CompoundTag saveTag() {
		CompoundTag data = new CompoundTag();
		if(this.player != null)
			data.putUUID("player", this.player.getUUID());
		if(this.conversionStarter != null)
			data.putUUID("cureStarter", this.conversionStarter);
		data.putString("type", EntityType.getKey(this.type).toString());
		data.putBoolean("isConverting", this.isUnderWaterConverting);
		data.putInt("convertTime", this.conversionTime);
		data.putInt("inWater", this.inWaterTime);
		data.putBoolean("isCureConverting", this.isCureConverting);
		data.putInt("cureConvertTime", this.cureConversionTime);
		return data;
	}

	public void sync() {
		ZombieUtils.syncData(this.player, this);
	}

	public boolean isZombie() {
		return this.type != EntityType.PLAYER;
	}

	public void setup(EntityType type) {
		this.type = type == EntityType.ZOMBIE_VILLAGER ? EntityType.ZOMBIE : type;
		loadAttributes(true);
		this.sync();
	}

	public void loadAttributes(boolean fullheal) {
		AttributeSupplier attributes = getAttributes().build();
		AttributeUtils.replace(this.player, attributes);
		if(fullheal)
			this.player.setHealth(this.player.getMaxHealth());
		
		if(isZombie()) {
			double speed = attributes.getBaseValue(Attributes.MOVEMENT_SPEED);
			AttributeUtils.replace(this.player, speed / 4, Attributes.MOVEMENT_SPEED);
		}
	}

	public AttributeSupplier.Builder getAttributes() {
		if(this.isZombie())
			return Zombie.createAttributes();
		return Player.createAttributes();
	}

	public AbstractZombieRenderer getRenderer(EntityRendererProvider.Context context) {
		if(this.type == EntityType.DROWNED)
			return new DrownedRenderer(context);
		if(this.type == EntityType.HUSK)
			return new HuskRenderer(context);
		return new ZombieRenderer(context);
	}

	public EntityDimensions getDimensions() {
		return this.type.getDimensions();
	}

	public boolean isAggressive() {
		return player.getCombatTracker().getCombatDuration() > 0;
	}
	
	public boolean isUnderWaterConverting() {
		return this.isUnderWaterConverting;
	}

   	public void startUnderWaterConversion(int time) {
      	this.conversionTime = time;
      	this.isUnderWaterConverting = true;
      	this.sync();
   	}

   	public void doUnderWaterConversion() {
        this.inWaterTime = 0;
        this.conversionTime = 0;
        this.isUnderWaterConverting = false;
      	this.convertToZombieType(drownInto());
        this.world.levelEvent((Player)null, 1040, this.player.blockPosition(), 0);
   	}

   	public void convertToZombieType(EntityType<? extends Zombie> type) {
      	this.type = type;
      	this.sync();
   	}

   	public EntityType drownInto() {
   		if(this.type == EntityType.HUSK)
   			return EntityType.ZOMBIE;
   		return EntityType.DROWNED;
   	}

   	public boolean canSprint() {
   		if(this.player.isUnderWater())
   			return this.type == EntityType.DROWNED;
   		return false;
   	}

   	public boolean canCrouch() {
   		return this.player.isPassenger();
   	}

   	public boolean canJump() {
   		if(!this.player.isUnderWater())
   			return true;
   		if(this.type == EntityType.DROWNED)
   			return true;
   			
   		BlockPos underPlayer = this.player.blockPosition().below();
   		Block block = this.world.getBlockState(underPlayer).getBlock();
   		return !(block instanceof LiquidBlock);
   	}

   	public boolean convertsInWater() {
      	return this.type != EntityType.DROWNED;
   	}

   	public boolean isSunSensitive() {
      	return this.type != EntityType.HUSK;
   	}

   	public boolean isSunBurnTick() {
      	if (this.world.isDay() && !this.world.isClientSide) {
         	float f = this.player.getLightLevelDependentMagicValue();
         	boolean flag = this.player.isInWaterRainOrBubble() || this.player.isInPowderSnow || this.player.wasInPowderSnow;
         	if (f > 0.5F && this.world.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !flag && this.world.canSeeSky(this.player.blockPosition()))
            	return true;
      	}

      	return false;
   	}

   	public boolean isConverting() {
      	return this.isCureConverting;
   	}

   	public int getConversionProgress() {
      	int i = 1;
      	if (this.world.random.nextFloat() < 0.01F) {
         	int j = 0;
         	BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         	for(int k = (int)this.player.getX() - 4; k < (int)this.player.getX() + 4 && j < 14; ++k) {
            	for(int l = (int)this.player.getY() - 4; l < (int)this.player.getY() + 4 && j < 14; ++l) {
               		for(int i1 = (int)this.player.getZ() - 4; i1 < (int)this.player.getZ() + 4 && j < 14; ++i1) {
                  		BlockState blockstate = this.world.getBlockState(blockpos$mutableblockpos.set(k, l, i1));
                  		if (blockstate.is(Blocks.IRON_BARS) || blockstate.getBlock() instanceof BedBlock) {
                     		if (this.world.random.nextFloat() < 0.3F) {
                        		++i;
                     		}

                     		++j;
                  		}
               		}
            	}
         	}
      	}

      	return i;
   	}

   	public void startConverting(@Nullable UUID uuid, int time) {
      	this.conversionStarter = uuid;
      	this.cureConversionTime = time;
      	this.isCureConverting = true;
      	this.player.removeEffect(MobEffects.WEAKNESS);
      	this.player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, time, Math.min(this.world.getDifficulty().getId() - 1, 0)));
      	this.world.playLocalSound(this.player.getX(), this.player.getEyeY(), this.player.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, this.player.getSoundSource(), 1.0F + this.world.random.nextFloat(), this.world.random.nextFloat() * 0.7F + 0.3F, false);
    	this.sync();
   	}

   	public void finishConversion(ServerLevel server) {
      	if (this.conversionStarter != null) {
         	Player player = server.getPlayerByUUID(this.conversionStarter);
         	if (player instanceof ServerPlayer serverPlayer) {
            	Advancement advancement = serverPlayer.server.getAdvancements().getAdvancement(new ResourceLocation("minecraft:story/cure_zombie_villager"));
				serverPlayer.getAdvancements().award(advancement, "cured_zombie_villager");
      		}
      	}

      	this.player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
    	server.levelEvent((Player)null, 1027, this.player.blockPosition(), 0);
    	this.type = EntityType.PLAYER;
    	loadAttributes(false);
    	this.sync();
   	}
}
