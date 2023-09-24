package io.bluestaggo.voxelthing.world;

import io.bluestaggo.pds.CompoundItem;
import io.bluestaggo.voxelthing.math.AABB;
import io.bluestaggo.voxelthing.math.MathUtil;
import io.bluestaggo.voxelthing.world.block.Block;
import io.bluestaggo.voxelthing.world.generation.GenCache;
import io.bluestaggo.voxelthing.world.generation.GenerationInfo;
import io.bluestaggo.voxelthing.world.generation.Structures;
import io.bluestaggo.voxelthing.world.generation.WorldType;
import io.bluestaggo.voxelthing.world.storage.ChunkStorage;
import io.bluestaggo.voxelthing.world.storage.EmptySaveHandler;
import io.bluestaggo.voxelthing.world.storage.ISaveHandler;
import io.bluestaggo.voxelthing.world.generation.Biomes;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World implements IBlockAccess {
	protected final ChunkStorage chunkStorage;
	public final GenCache genCache;
	public final ISaveHandler saveHandler;
	public final WorldType worldType;

	
	public final Random random = new Random();
	public final WorldInfo info;

	public double partialTick;

	public World() {
		this(null,null, WorldType.Normal);
	}

	public World(ISaveHandler saveHandler, WorldInfo info) {
		this(saveHandler, info,WorldType.Normal);
	}


	public World(ISaveHandler saveHandler, WorldInfo info, WorldType type) {
		if (info == null) {
			this.info = new WorldInfo();
			CompoundItem data = saveHandler.loadData("world");
			if (data != null) {
				this.info.deserialize(data);
			}
		} else {
			this.info = info;
			this.info.type = type;
		}
		this.worldType = type;
		
		if (saveHandler == null) {
			saveHandler = new EmptySaveHandler();
		}

		chunkStorage = new ChunkStorage(this);
		genCache = new GenCache(this);
		this.saveHandler = saveHandler;
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

		Chunk chunk = chunkStorage.newChunkAt(cx, cy, cz);
		GenerationInfo genInfo = genCache.getGenerationAt(cx, cz, worldType);


		genInfo.generate();
		for (int x = 0; x < Chunk.LENGTH; x++) {
			for (int z = 0; z < Chunk.LENGTH; z++) {
				float height = genInfo.getHeight(x, z);

				
				for (int y = 0; y < Chunk.LENGTH; y++) {
					int yy = cy * Chunk.LENGTH + y;
					int xx = cx * Chunk.LENGTH + x;
					int zz = cz * Chunk.LENGTH + z;
					boolean cave = yy < height && genInfo.getCave(x, yy, z);
					Block block = null;
					Block topLayerBlock = Block.GRASS;
					Block lowerLayerBlock = Block.DIRT;
					Block snowLayerBlock = Block.SNOW;
					if (genInfo.biomeGen(xx, zz) == Biomes.Desert) {
						topLayerBlock = Block.SAND;
						lowerLayerBlock = Block.SAND;
						snowLayerBlock = Block.STONE;
					}

					if (genInfo.biomeGen(xx, zz) == Biomes.Jungle) {
						snowLayerBlock = Block.STONE;
					}
					

					//increase water level for chaotic world
					int waterLevel = genInfo.waterLevel;
					int snowHeight = genInfo.snowLevel;
					int blend = genInfo.blendSnowLevel;
					if (!cave) {
						if (yy < height - 4) {
							block = Block.STONE;
						} else if (yy < height - 1 && yy < snowHeight) {
							block = lowerLayerBlock;
							//blends grass -> snow
							if (yy > blend) {
								if (Math.random() > 0.5) {
									block = lowerLayerBlock;
								} else {
									block = snowLayerBlock;
								}
							}
						} else if (yy < height && yy > waterLevel && yy < snowHeight) {
							block = topLayerBlock;
							//blends grass -> snow
							if (yy > blend) {
								if (Math.random() > 0.5) {
									block = topLayerBlock;
								} else {
									block = snowLayerBlock;
								}
							}
						} else if (yy < height && yy < waterLevel) {
							block = Block.SAND;
						} else if (yy < height && yy > snowHeight-1) {
							block = snowLayerBlock;
						} else if (yy < waterLevel && worldType == WorldType.Normal) {
							block = Block.WATER;
						}

						
						
					if (genInfo.genTree(xx, zz, genInfo.biomeGen(xx, zz)) != 0 && yy == Math.round(height) && yy > waterLevel && yy < snowHeight) {
						loadChunkAt(cx, cy+1, cz);
						if (genInfo.genTree(xx, zz, genInfo.biomeGen(xx, zz)) == 1) {
							for (int i = 0; i < Structures.SmallTree.getStructure().length; i++) {
								setBlock(xx - Structures.SmallTree.getStructure()[i].x, yy - Structures.SmallTree.getStructure()[i].y, zz - Structures.SmallTree.getStructure()[i].z, Structures.SmallTree.getStructure()[i].block);
							}
						}
						if (genInfo.genTree(xx, zz, genInfo.biomeGen(xx, zz)) == 2) {
							for (int i = 0; i < Structures.LargeTree.getStructure().length; i++) {
								setBlock(xx - Structures.LargeTree.getStructure()[i].x, yy - Structures.LargeTree.getStructure()[i].y, zz - Structures.LargeTree.getStructure()[i].z, Structures.LargeTree.getStructure()[i].block);
							}
						}

						if (getBlock(xx, yy-1, zz) == null) {
							setBlock(xx, yy-1, zz, Block.LOG);
						}
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
		saveHandler.saveData("world", info.serialize());
		chunkStorage.unloadAllChunks();
	}
}
