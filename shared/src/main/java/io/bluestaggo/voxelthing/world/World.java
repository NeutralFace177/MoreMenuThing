package io.bluestaggo.voxelthing.world;

import io.bluestaggo.pds.CompoundItem;
import io.bluestaggo.voxelthing.math.AABB;
import io.bluestaggo.voxelthing.math.MathUtil;
import io.bluestaggo.voxelthing.world.block.Block;
import io.bluestaggo.voxelthing.world.generation.GenCache;
import io.bluestaggo.voxelthing.world.generation.GenerationInfo;
import io.bluestaggo.voxelthing.world.generation.WorldType;
import io.bluestaggo.voxelthing.world.storage.ChunkStorage;
import io.bluestaggo.voxelthing.world.storage.EmptySaveHandler;
import io.bluestaggo.voxelthing.world.storage.ISaveHandler;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World implements IBlockAccess {
	protected final ChunkStorage chunkStorage;
	private final GenCache genCache;
	public final ISaveHandler saveHandler;
	public final WorldType worldType;

	public final Random random = new Random();
	public final WorldInfo info = new WorldInfo();

	public double partialTick;

	public World() {
		this(null, WorldType.Normal);
	}

	public World(ISaveHandler saveHandler, WorldType type) {
		if (saveHandler == null) {
			saveHandler = new EmptySaveHandler();
		}

		chunkStorage = new ChunkStorage(this);
		genCache = new GenCache(this);
		this.saveHandler = saveHandler;

		this.worldType = type;

		info.seed = random.nextLong();
		info.type = type;

		CompoundItem data = saveHandler.loadWorldData();
		if (data != null) {
			info.deserialize(data);
		}
	}

	public Chunk getChunkAt(int x, int y, int z) {
		return chunkStorage.getChunkAt(x, y, z);
	}

	public boolean chunkExists(int x, int y, int z) {
		return getChunkAt(x, y, z) != null;
	}

	public Chunk getChunkAtBlock(int x, int y, int z) {
		return getChunkAt(
				Math.floorDiv(x, Chunk.LENGTH),
				Math.floorDiv(y, Chunk.LENGTH),
				Math.floorDiv(z, Chunk.LENGTH)
		);
	}

	public boolean chunkExistsAtBlock(int x, int y, int z) {
		return getChunkAtBlock(x, y, z) != null;
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		Chunk chunk = getChunkAtBlock(x, y, z);
		if (chunk == null) {
			return null;
		}
		return chunk.getBlock(
				Math.floorMod(x, Chunk.LENGTH),
				Math.floorMod(y, Chunk.LENGTH),
				Math.floorMod(z, Chunk.LENGTH)
		);
	}

	public void setBlock(int x, int y, int z, Block block) {
		Chunk chunk = getChunkAtBlock(x, y, z);
		if (chunk == null) {
			return;
		}
		chunk.setBlock(
				Math.floorMod(x, Chunk.LENGTH),
				Math.floorMod(y, Chunk.LENGTH),
				Math.floorMod(z, Chunk.LENGTH),
				block
		);
		onBlockUpdate(x, y, z);
	}

	public void loadChunkAt(int cx, int cy, int cz) {
		if (chunkStorage.getChunkAt(cx, cy, cz) != null) {
			return;
		}

		CompoundItem data = saveHandler.loadChunkData(cx, cy, cz);
		if (data != null) {
			chunkStorage.deserializeChunkAt(cx, cy, cz, data);
			return;
		}

		Chunk chunk = chunkStorage.newChunkAt(cx, cy, cz);
		GenerationInfo genInfo = genCache.getGenerationAt(cx, cz, worldType);

		genInfo.generate();

		for (int x = 0; x < Chunk.LENGTH; x++) {
			for (int z = 0; z < Chunk.LENGTH; z++) {
				float height = genInfo.getHeight(x, z);

				for (int y = 0; y < Chunk.LENGTH; y++) {
					int yy = cy * Chunk.LENGTH + y;
					boolean cave = yy < height && genInfo.getCave(x, yy, z);
					Block block = null;

					if (!cave) {
						if (yy < height - 4) {
							block = Block.STONE;
						} else if (yy < height - 1) {
							block = Block.DIRT;
						} else if (yy < height) {
							block = Block.GRASS;
						}
					}

					if (block != null) {
						chunk.setBlock(x, y, z, block);
					}
				}
			}
		}

		chunk.dontSave();
		onChunkAdded(cx, cy, cz);
	}

	public void loadSurroundingChunks(int cx, int cy, int cz, int radius) {
		List<Vector3i> points = MathUtil.getSpherePoints(radius);

		int loaded = 0;

		for (Vector3i point : points) {
			int x = point.x + cx;
			int y = point.y + cy;
			int z = point.z + cz;

			if (!chunkExists(x, y, z)) {
				loadChunkAt(x, y, z);

				if (++loaded >= 25) {
					return;
				}
			}
		}
	}

	public Chunk getOrLoadChunkAt(int x, int y, int z) {
		Chunk chunk = getChunkAt(x, y, z);
		if (chunk == null) {
			loadChunkAt(x, y, z);
			chunk = getChunkAt(x, y, z);
		}
		return chunk;
	}

	public List<AABB> getSurroundingCollision(AABB box) {
		List<AABB> boxes = new ArrayList<>();

		int minX = (int) Math.floor(box.minX);
		int minY = (int) Math.floor(box.minY);
		int minZ = (int) Math.floor(box.minZ);
		int maxX = (int) Math.floor(box.maxX + 1.0);
		int maxY = (int) Math.floor(box.maxY + 1.0);
		int maxZ = (int) Math.floor(box.maxZ + 1.0);

		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				for (int z = minZ; z < maxZ; z++) {
					Block block = getBlock(x, y, z);
					if (block != null) {
						boxes.add(block.getCollisionBox(x, y, z));
					}
				}
			}
		}

		return boxes;
	}

	public boolean doRaycast(BlockRaycast raycast) {
		Vector3d pos = new Vector3d(raycast.position);
		Vector3d dir = raycast.direction;
		float dist = 0.0f;

		while (dist < raycast.length) {
			int x = (int) Math.floor(pos.x());
			int y = (int) Math.floor(pos.y());
			int z = (int) Math.floor(pos.z());

			Block block = getBlock(x, y, z);
			if (block != null) {
				AABB collision = block.getCollisionBox(x, y, z);
				if (collision.contains(pos.x, pos.y, pos.z)) {
					raycast.setResult(x, y, z, collision.getClosestFace(pos, dir));
					return true;
				}
			}

			pos.add(dir);
			dist += BlockRaycast.STEP_DISTANCE;
		}

		return false;
	}

	public double scaleToTick(double a, double b) {
		return MathUtil.lerp(a, b, partialTick);
	}

	public void onBlockUpdate(int x, int y, int z) {
	}

	public void onChunkAdded(int x, int y, int z) {
	}

	public void close() {
		saveHandler.saveWorldData(info.serialize());
		chunkStorage.unloadAllChunks();
	}
}
