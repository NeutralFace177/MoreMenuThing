package io.bluestaggo.voxelthing.world.generation;

import io.bluestaggo.voxelthing.math.MathUtil;
import io.bluestaggo.voxelthing.math.OpenSimplex2Octaves;
import io.bluestaggo.voxelthing.world.Chunk;

import java.util.ArrayList;
import java.util.Arrays;

public class GenerationInfo {
	private static final int LERP_MAP_LENGTH = (Chunk.LENGTH >> 2) + 1;
	private static final int LERP_MAP_SIZE = LERP_MAP_LENGTH * LERP_MAP_LENGTH * LERP_MAP_LENGTH;

	public final int chunkX, chunkZ;
	private long randSeed;

	private final long baseSeed;
	private final long hillSeed;
	private final long cliffSeed;
	private final long cliffHeightSeed;
	private final long caveSeed;
	private final long biomeSeed;
	private final long secondBiomeSeed;
	private final long treeSeed;
	private final long treeSeed2;

	public ArrayList<ArrayList<Double>> voronoiSeeds;
	public ArrayList<ArrayList<Double>> unModVSeeds;

	public int waterLevel;
	public int snowLevel = 23;
	public int blendSnowLevel = snowLevel - 4;

	public WorldType worldType;

	public int dist = 100;
	public int gridDist = 50;

	private final float[] height = new float[Chunk.AREA];

	private final Biomes[] biomes = new Biomes[Chunk.AREA];

	private final float[] trees = new float[Chunk.AREA];
	private final float[] caveInfo = new float[LERP_MAP_SIZE];
	private int lastQueryLayer = Integer.MAX_VALUE;

	private boolean hasGenerated;

	public GenerationInfo(long salt, int cx, int cz, WorldType type) {
		randSeed = salt;

		baseSeed = splitMix();
		hillSeed = splitMix();
		cliffSeed = splitMix();
		cliffHeightSeed = splitMix();
		caveSeed = splitMix();
		biomeSeed = splitMix();
		secondBiomeSeed = splitMix();
		treeSeed = splitMix();
		treeSeed2 = splitMix();

		worldType = type;
		waterLevel = worldType == WorldType.Normal ? 0 : 2;

		

		chunkX = cx;
		chunkZ = cz;
	}

	public void generate() {
		if (hasGenerated) {
			return;
		}
		hasGenerated = true;

		final int baseOctaves = 6;
		final double baseScale = 300.0;
		final float baseHeightScale = 8.0f;

		final int hillOctaves = 3;
		final double hillScale = 275.0;
		final float hillHeightScale = 16.0f;
		final float hillHeightScaleMod = 4.0f;
		final float hillThresholdMin = -0.5f;
		final float hillThresholdMax = 1.0f;

		final int cliffOctaves = 2;
		final double cliffScale = 300.0;
		final float cliffThreshold = 0.5f;

		final int cliffHeightOctaves = 1;
		final double cliffHeightScale = 100.0;
		final float cliffHeightMin = 2.0f;
		final float cliffHeightMax = 8.0f;


		final long seed = splitMix();

		for (int x = 0; x < Chunk.LENGTH; x++) {
			for (int z = 0; z < Chunk.LENGTH; z++) {
				int xx = (chunkX * Chunk.LENGTH + x);
				int zz = (chunkZ * Chunk.LENGTH + z);

				float baseHeight = OpenSimplex2Octaves.noise2(baseSeed, baseOctaves, xx / baseScale, zz / baseScale);
				float hill = OpenSimplex2Octaves.noise2(hillSeed, hillOctaves, xx / hillScale, zz / hillScale);
				hill = 1.0f - (float) Math.sin(MathUtil.threshold(hill, hillThresholdMin, hillThresholdMax) * MathUtil.PI_F / 2.0f);

				float s = OpenSimplex2Octaves.noise2(seed, 2, xx / baseScale, zz / baseScale);

				if (worldType == WorldType.Chaotic) {
					baseHeight = MathUtil.floorMod(baseHeight, s);
					hill = MathUtil.floorMod(hill, s);
				}

				float mod = worldType == WorldType.Normal ? 2.5f : 5.5f;

				float addedBaseHeight = baseHeightScale * MathUtil.lerp(mod, hillHeightScaleMod, hill);
				baseHeight = baseHeight * addedBaseHeight + hill * hillHeightScale;

				float cliff = OpenSimplex2Octaves.noise2(cliffSeed, cliffOctaves, xx / cliffScale, zz / cliffScale);
				float cliffHeight = OpenSimplex2Octaves.noise2(cliffHeightSeed, cliffHeightOctaves, xx / cliffHeightScale, zz / cliffHeightScale);
				cliffHeight = MathUtil.lerp(cliffHeightMin, cliffHeightMax, cliffHeight / 2.0f + 0.5f) * (1.0f - hill * 5.0f);

				if (worldType == WorldType.Chaotic) {
					cliff = MathUtil.floorMod(cliff, s);
					cliff = MathUtil.floorMod(cliffHeight, s);
				}
				
				

				if (cliff > cliffThreshold) {
					baseHeight += cliffHeight;
				}

				float exp = worldType == WorldType.Normal ? 0.08f : 0.15f;
				baseHeight = baseHeight > 20 ? baseHeight + (float)Math.pow(Math.exp(baseHeight-20),exp)-1 : baseHeight;
				baseHeight = baseHeight < waterLevel-3 ? -(float)Math.log(Math.pow(Math.abs(baseHeight), 7)) + 6 : baseHeight;
				

				height[x + z * Chunk.LENGTH] = baseHeight;
			}
		}      
	}

	public void treeGen() {
		for (int x = 0; x < Chunk.LENGTH; x++) {
			for (int z = 0; z < Chunk.LENGTH; z++) {
				int xx = (chunkX * Chunk.LENGTH + x);
				int zz = (chunkZ * Chunk.LENGTH + z);
				Biomes biome = getBiome(x, z);

				double baseScale = 1;
				double threshold = biome == Biomes.Forest ? 0.7 : 0.5;
				threshold = (biome == Biomes.Desert || biome == Biomes.Plains) ? 8008135 : threshold;

				double largeThreshold = biome == Biomes.Forest ? 0.8 : 0.8;
				largeThreshold = (biome == Biomes.Desert || biome == Biomes.Plains) ? 102496 : largeThreshold;

				float tree = OpenSimplex2Octaves.noise2(treeSeed, 5, xx / baseScale, zz / baseScale);

				int n = largeThreshold > tree && tree > threshold ? 1 : 0;
				n = tree >= largeThreshold ? 2 : n;

				trees[x + z * Chunk.LENGTH] = n;
			}
		}
	}

	public void biomeGen() {
		for (int x = 0; x < Chunk.LENGTH; x++) {
			for (int z = 0; z < Chunk.LENGTH; z++) {

				int xx = (chunkX * Chunk.LENGTH + x);
				int zz = (chunkZ * Chunk.LENGTH + z);

				float scale = 500;
				float heat = OpenSimplex2Octaves.noise2(biomeSeed, 2, xx / scale, zz / scale);
				float moist = OpenSimplex2Octaves.noise2(secondBiomeSeed, 2, xx / scale, zz / scale);

				if (heat > 0 && moist > 0) {
					biomes[x + z * Chunk.LENGTH] =  Biomes.Jungle;
				} else if (heat > 0 && moist < 0) {
					biomes[x + z * Chunk.LENGTH] =  Biomes.Desert;
				} else if (heat < 0 && moist > 0) {
					biomes[x + z * Chunk.LENGTH] =  Biomes.Forest;
				} else if (heat < 0 && moist < 0) {
					biomes[x + z * Chunk.LENGTH] =  Biomes.Plains;
				} else {
					biomes[x + z * Chunk.LENGTH] =  Biomes.Plains;
				}
			}
		}
	}

	//im not going to use this for biomes cuz math is too much mathing
	/*
	public void voronoiSeedsGen(int x,int z) {
		
		ArrayList<ArrayList<Double>> seedArray = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> array = new ArrayList<ArrayList<Double>>();
		int distance = dist + x;
		int distance2  = dist + z;
		for (int xx = -distance; xx <= distance; xx += gridDist) {
			for (int zz = -distance2; zz <= distance2; zz += gridDist) {
				float seedX = xx + (OpenSimplex2Octaves.noise2(biomeSeed, 1, xx, zz) * (gridDist /2));
				float seedZ = zz + (OpenSimplex2Octaves.noise2(secondBiomeSeed, 1, xx, zz) * (gridDist/2));
				seedArray.add(new ArrayList<Double>(Arrays.asList((double)seedX,(double)seedZ)));
				array.add(new ArrayList<Double>(Arrays.asList((double)xx,(double)zz)));

			}
		}
		voronoiSeeds = seedArray;
		unModVSeeds = array;
		//System.out.println(voronoiSeeds);
	}
*/
	private long splitMix() {
		long z = (randSeed += 0x9e3779b97f4a7c15L);
	    z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
        z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
		return z ^ z >>> 31;
	}

	public float getHeight(int x, int z) {
		return height[x + z * Chunk.LENGTH];
	}

	public Biomes getBiome(int x, int z) {
		return biomes[x + z * Chunk.LENGTH];
	}

	public String returnStringBiome(double x, double z) {
		int xx = (int) x / Chunk.LENGTH;
		int zz = (int) z / Chunk.LENGTH;
		try {
			return getBiome(xx, zz).toString();
		} catch (IndexOutOfBoundsException e) {
			return "Depths Of Hell";
		}

	}

	public float getTree(int x, int z) {
		return trees[x + z * Chunk.LENGTH];
	}


	public boolean getCave(int x, int y, int z) {
		if (lastQueryLayer != y >> Chunk.SIZE_POW2) {
			generateCaves(y >> Chunk.SIZE_POW2);
		}

		final float cheeseMinDensity = -1.0f;
		final float cheeseMaxDensity = -0.3f;
		final float cheeseDensitySpread = 100.0f;
		final float cheeseDensitySurface = -0.5f;

		int shiftPow2 = Chunk.SIZE_POW2 - 3;
		int shiftMask = 3;
		int shiftDiv = 4;
		int xx = x >> shiftPow2;
		int yy = (y & Chunk.LENGTH_MASK) >> shiftPow2;
		int zz = z >> shiftPow2;

		float c000 = caveInfo[MathUtil.index3D(xx, yy, zz, LERP_MAP_LENGTH)];
		float c001 = caveInfo[MathUtil.index3D(xx, yy, zz + 1, LERP_MAP_LENGTH)];
		float c010 = caveInfo[MathUtil.index3D(xx, yy + 1, zz, LERP_MAP_LENGTH)];
		float c011 = caveInfo[MathUtil.index3D(xx, yy + 1, zz + 1, LERP_MAP_LENGTH)];
		float c100 = caveInfo[MathUtil.index3D(xx + 1, yy, zz, LERP_MAP_LENGTH)];
		float c101 = caveInfo[MathUtil.index3D(xx + 1, yy, zz + 1, LERP_MAP_LENGTH)];
		float c110 = caveInfo[MathUtil.index3D(xx + 1, yy + 1, zz, LERP_MAP_LENGTH)];
		float c111 = caveInfo[MathUtil.index3D(xx + 1, yy + 1, zz + 1, LERP_MAP_LENGTH)];
		float caveInfo = MathUtil.trilinear(c000, c001, c010, c011, c100, c101, c110, c111,
					(x & shiftMask) / (float) shiftDiv, (y & shiftMask) / (float) shiftDiv, (z & shiftMask) / (float) shiftDiv);
		float cheeseThreshold = MathUtil.clamp(-y / cheeseDensitySpread + cheeseDensitySurface, cheeseMinDensity, cheeseMaxDensity);
		return caveInfo < cheeseThreshold;
	}

	private void generateCaves(int layer) {
		lastQueryLayer = layer;
		Arrays.fill(caveInfo, 0);

		final int cheeseOctaves = 4;
		final double cheeseScaleXZ = 100.0;
		final double cheeseScaleY = 50.0;


		for (int x = 0; x < LERP_MAP_LENGTH; x++) {
			for (int y = 0; y < LERP_MAP_LENGTH; y++) {
				for (int z = 0; z < LERP_MAP_LENGTH; z++) {
					int xx = (x << (Chunk.SIZE_POW2 - 3)) + (chunkX << Chunk.SIZE_POW2);
					int yy = (y << (Chunk.SIZE_POW2 - 3)) + (layer << Chunk.SIZE_POW2);
					int zz = (z << (Chunk.SIZE_POW2 - 3)) + (chunkZ << Chunk.SIZE_POW2);

					float cheese = OpenSimplex2Octaves.noise3_ImproveXZ(caveSeed, cheeseOctaves, xx / cheeseScaleXZ, yy / cheeseScaleY, zz / cheeseScaleXZ);
					caveInfo[MathUtil.index3D(x, y, z, LERP_MAP_LENGTH)] = cheese;
				}
			}
		}
	}
}
