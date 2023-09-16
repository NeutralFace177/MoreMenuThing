package io.bluestaggo.voxelthing.world.generation;

import io.bluestaggo.voxelthing.world.World;
import io.bluestaggo.voxelthing.world.storage.ChunkStorage;

public class GenCache {
	public static final int RADIUS_POW2 = ChunkStorage.RADIUS_POW2;
	public static final int AREA = 1 << RADIUS_POW2 * 2;
	public static final int POS_MASK = (1 << RADIUS_POW2) - 1;

	private final World world;
	private final GenerationInfo[] cache = new GenerationInfo[AREA];

	public GenCache(World world) {
		this.world = world;
	}

	public static int cacheCoords(int x, int z) {
		x &= POS_MASK;
		z &= POS_MASK;
		return (x << RADIUS_POW2 | z);
	}

	public GenerationInfo getGenerationAt(int x, int z, WorldType type) {
		GenerationInfo entry = cache[cacheCoords(x, z)];
		if (entry != null && entry.chunkX == x && entry.chunkZ == z) {
			return entry;
		}

		entry = new GenerationInfo(world.info.seed, x, z, type);
		cache[cacheCoords(x, z)] = entry;
		return entry;
	}
}
