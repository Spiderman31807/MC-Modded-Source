package chunkbychunk;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Position;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;

public class BorderData {
	public Map<ResourceKey<Level>, ArrayList<ChunkPos>> Chunks = new HashMap();
	public Map<ResourceKey<Level>, Integer> WorldLevel = new HashMap();
	public Map<ResourceKey<Level>, Integer> WorldXP = new HashMap();
	public RandomSource randomSource = RandomSource.create();
	public boolean enabled = false;
	public Player player;
	
	public BorderData() {
	}

	public BorderData(Player player) {
		this.player = player;
		this.addChunk(new ChunkPos(player.blockPosition()));
	}

	public BorderData(BorderData copy) {
		this.Chunks = copy.Chunks;
		this.WorldLevel = copy.WorldLevel;
		this.WorldXP = copy.WorldXP;
		this.enabled = copy.enabled;
	}

	public BorderData(CompoundTag tag) {
		if(tag.contains("toggle")) {
			this.enabled = tag.getBoolean("toggle");
			tag.remove("toggle");
		}
			
		for(String key : tag.getAllKeys()) {
			if(key.startsWith("Bounds:")) {
				this.loadBounds(key, tag.getCompound(key));
			} else if(key.startsWith("XP:")) {
				this.loadInt("XP:", key, tag.getInt(key));
			} else if(key.startsWith("Level:")) {
				this.loadInt("Level:", key, tag.getInt(key));
			}
		}
	}

	public CompoundTag saveTag() {
		CompoundTag tag = new CompoundTag();
		for(ResourceKey<Level> dimension : this.Chunks.keySet()) {
			tag.put("Bounds:" + dimension.location().getPath(), this.saveChunks(this.Chunks.get(dimension)));
		}
		
		for(ResourceKey<Level> dimension : this.WorldXP.keySet()) {
			tag.putInt("XP:" + dimension.location().getPath(), this.WorldXP.get(dimension));
		}
		
		for(ResourceKey<Level> dimension : this.WorldLevel.keySet()) {
			tag.putInt("Level:" + dimension.location().getPath(), this.WorldLevel.get(dimension));
		}
		
		tag.putBoolean("toggle", this.enabled);
		return tag;
	}

	public CompoundTag saveChunks(ArrayList<ChunkPos> chunks) {
		CompoundTag compound = new CompoundTag();
		for(int idx = 0; idx < chunks.size(); idx++) {
			int[] XZ = new int[] { chunks.get(idx).x, chunks.get(idx).z };
			compound.putIntArray("Chunk" + idx, XZ);
		}
		return compound;
	}

	public void loadBounds(String path, CompoundTag ChunksTag) {
		ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(path.replace("Bounds:", "")));
		ArrayList<ChunkPos> chunks = new ArrayList();
		for(String key : ChunksTag.getAllKeys()) {
			int[] XZ = ChunksTag.getIntArray(key);
			chunks.add(new ChunkPos(XZ[0], XZ[1]));
		}

		this.Chunks.put(dimension, chunks);
	}

	public void loadInt(String prefix, String path, int Int) {
		ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(path.replace(prefix, "")));
		Map addInt = prefix == "XP:" ? this.WorldXP : this.WorldLevel;
		addInt.put(dimension, Int);
	}

	public int random(int min, int max) {
		return Mth.nextInt(this.randomSource, min, max);
	}

	public int getCost() {
		ResourceKey<Level> dimension = this.player.level().dimension();
		return ConfiUtils.getCost(dimension.location().getPath());
	}

	public void checkForLevelUp() {
		ResourceKey<Level> dimension = this.player.level().dimension();
		int levelCost = ConfiUtils.getCost(dimension.location().getPath());
		if(this.getXP() >= levelCost) {
			this.addXP(-levelCost);
			int level = this.getLevels();
			level++;
			this.WorldLevel.put(dimension, level);
		}
	}

	public void reduceLevel() {
		ResourceKey<Level> dimension = this.player.level().dimension();
		int level = this.getLevels();
		level--;
		this.WorldLevel.put(dimension, level);
	}

	public void setXP(int amount) {
		ResourceKey<Level> dimension = this.player.level().dimension();
		this.WorldXP.put(dimension, amount);
		this.checkForLevelUp();
	}

	public void addXP(int amount) {
		ResourceKey<Level> dimension = this.player.level().dimension();
		this.setXP(this.WorldXP.containsKey(dimension) ? this.getXP() + amount : amount);
	}

	public int getXP() {
		ResourceKey<Level> dimension = this.player.level().dimension();
		if(!this.WorldXP.containsKey(dimension))
			return 0;
		return this.WorldXP.get(dimension);
	}

	public int getLevels() {
		ResourceKey<Level> dimension = this.player.level().dimension();
		if(!this.WorldLevel.containsKey(dimension))
			return 0;
		return this.WorldLevel.get(dimension);
	}

	public void setChunks(ArrayList<ChunkPos> chunks) {
		if(chunks == null)
			return;
		ResourceKey<Level> dimension = this.player.level().dimension();
		this.Chunks.put(dimension, chunks);
	}

	public void addChunk(ChunkPos chunk) {
		if(chunk == null)
			return;
		ArrayList<ChunkPos> chunks = this.getChunks();
		if(!chunks.contains(chunk))
			chunks.add(chunk);
		this.setChunks(chunks);
	}

	public void removeChunk(ChunkPos chunk) {
		if(chunk == null)
			return;
		ArrayList<ChunkPos> chunks = this.getChunks();
		if(chunks.contains(chunk))
			chunks.remove(chunk);
		this.setChunks(chunks);
	}

	public void removeAllChunks() {
		this.Chunks = new HashMap();
		this.addChunk(new ChunkPos(this.player.blockPosition()));
	}

	public ArrayList<ChunkPos> getChunks() {
		ResourceKey<Level> dimension = this.player.level().dimension();
		if(!this.Chunks.containsKey(dimension))
			return new ArrayList();
		return this.Chunks.get(dimension);
	}

	public boolean hasChunk(ChunkPos chunk) {
		return this.getChunks().contains(chunk);
	}

	public boolean isWithinBounds(Entity entity) {
		return this.isWithinBounds(new ChunkPos(entity.blockPosition()));
	}

	public boolean isWithinBounds(BlockPos pos) {
		return this.isWithinBounds(new ChunkPos(pos));
	}

	public boolean isWithinBounds(ChunkPos chunk) {
		if(this.enabled == false)
			return true;
		if(chunk == null)
			return false;
		return this.getChunks().contains(chunk);
	}

	public void removeBorderEdgeMotion(Entity target, double xPos, double zPos) {
		Vec3 delta = target.getDeltaMovement();
		double x = delta.x;
		double z = delta.z;
		
		if(target.getX() != xPos)
			x = xPos > target.getX() ? Math.min(delta.x, 0) : Math.max(delta.x, 0);
		if(target.getZ() != zPos)
			z = zPos > target.getZ() ? Math.min(delta.z, 0) : Math.max(delta.z, 0);
			
		Vec3 restrictedDelta = new Vec3(x, delta.y, z);
		target.setDeltaMovement(restrictedDelta);
	}

	public void putWithinChunk(Entity target, ChunkPos chunk, boolean chunkChange) {
		ChunkPos playerChunk = new ChunkPos(player.blockPosition());
		double xPos = Math.min(chunk.getMaxBlockX() + 0.999, Math.max(player.getX(), chunk.getMinBlockX() + 0.001));
		double zPos = Math.min(chunk.getMaxBlockZ() + 0.999, Math.max(player.getZ(), chunk.getMinBlockZ() + 0.001));
		this.removeBorderEdgeMotion(target, xPos, zPos);
		double yPos = target.getY();
		while(!chunkChange && target.level().getBlockState(BlockPos.containing(xPos, yPos, zPos)).isSuffocating(target.level(), BlockPos.containing(xPos, yPos, zPos))) {
			yPos++;
		}
		
		if(!chunkChange)
			target.moveTo(xPos, yPos, zPos);
		target.setPos(xPos, yPos, zPos);
	}

	public void putWithinBounds(Player player, Entity target, boolean random) {
		this.player = player;
		ResourceKey<Level> dimension = player.level().dimension();
		if(!this.Chunks.containsKey(dimension)) {
			this.addChunk(new ChunkPos(player.blockPosition()));
			return;
		}

		ChunkPos chunk = this.closestChunk(player.position());
		if(chunk == null || random) {
			if(chunk == null) {
				ArrayList<ChunkPos> chunks = this.getChunks();
				chunk = chunks.get(random(0, chunks.size() - 1));
			}
			
			BlockPos block = chunk.getWorldPosition();
			block = BlockPos.containing(block.getX() + random(0, 15), 0, block.getZ() + random(0, 15));
			int y = player.level().getHeight(Heightmap.Types.MOTION_BLOCKING, block.getX(), block.getZ());
			target.teleportTo(block.getX(), y, block.getZ());
			return;
		}
		
		this.putWithinChunk(target, chunk, false);
	}

	public double getSpeed(Entity entity) {
		CompoundTag data = entity.getPersistentData();
		if(data.contains("SpeedData")) {
        	CompoundTag speedData = data.getCompound("SpeedData");
        	float speed = speedData.getFloat("speed") * 1000;
        	return speed;
        }
        return 0;
	}

	public ChunkPos closestChunk(Position pos) {
		Map<Double, ChunkPos> ChunkMap = new HashMap<>();
		Map<ChunkPos, Double> DistanceMap = new HashMap<>();
		for(ChunkPos chunk : this.getChunks()) {
			double distance = getDistance(pos, chunk);
			DistanceMap.put(chunk, distance);
			ChunkMap.put(distance, chunk);
		}
		
		if(DistanceMap.isEmpty())
			return null;
			
		List<Double> distances = new ArrayList(DistanceMap.values());
		distances.sort(Double::compareTo);
		return ChunkMap.get(distances.get(0));
	}

	public double getDistance(Entity entity) {
		if(this.enabled == false)
			return 0;
		
		ChunkPos closestChunk = this.closestChunk(entity.position());
		int minX = closestChunk.getMinBlockX();
		int minZ = closestChunk.getMinBlockZ();
		int maxX = closestChunk.getMaxBlockX();
		int maxZ = closestChunk.getMaxBlockZ();
		double X = entity.getX();
		double Z = entity.getZ();
		double XDis = X < minX ? minX - X : (X > maxX ? X - maxX : 0);
		double ZDis = Z < minZ ? minZ - Z : (Z > maxZ ? Z - maxZ : 0);
		return XDis + ZDis;
	}

	public double getDistance(BlockPos pos) {
		if(this.enabled == false)
			return 0;
			
		ChunkPos closestChunk = this.closestChunk(pos.getCenter());
		int minX = closestChunk.getMinBlockX();
		int minZ = closestChunk.getMinBlockZ();
		int maxX = closestChunk.getMaxBlockX();
		int maxZ = closestChunk.getMaxBlockZ();
		int X = pos.getX();
		int Z = pos.getZ();
		double XDis = X < minX ? minX - X : (X > maxX ? X - maxX : 0);
		double ZDis = Z < minZ ? minZ - Z : (Z > maxZ ? Z - maxZ : 0);
		return XDis + ZDis;
	}
	
	public double getDistance(Position pos, ChunkPos chunk) {
		double distance1 = distance(pos.x(), pos.z(), chunk.getMinBlockX(), chunk.getMinBlockZ());
		double distance2 = distance(pos.x(), pos.z(), chunk.getMaxBlockX(), chunk.getMaxBlockZ());
		double distance3 = distance(pos.x(), pos.z(), chunk.getMaxBlockX(), chunk.getMinBlockZ());
		double distance4 = distance(pos.x(), pos.z(), chunk.getMinBlockX(), chunk.getMaxBlockZ());
		return Math.max(distance1, Math.max(distance2, Math.max(distance3, distance4)));
	}

	public double distance(double x1, double z1, double x2, double z2) {
		double x = x1 - x2;
		double z = z1 - z2;
		return (x * x) + (z * z);
	}
}