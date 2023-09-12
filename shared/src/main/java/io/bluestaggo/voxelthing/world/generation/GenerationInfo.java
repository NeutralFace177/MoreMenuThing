package io.bluestaggo.voxelthing.world.generation;

import io.bluestaggo.voxelthing.math.MathUtil;
import io.bluestaggo.voxelthing.math.OpenSimplex2Octaves;
import io.bluestaggo.voxelthing.world.Chunk;

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

	public final float offset = -9/11f;

	private final float[] height = new float[Chunk.AREA];
	private final float[] caveInfo = new float[LERP_MAP_SIZE];
	private int lastQueryLayer = Integer.MAX_VALUE;

	private boolean hasGenerated;

	public GenerationInfo(long salt, int cx, int cz) {
		randSeed = salt;

		baseSeed = splitMix();
		hillSeed = splitMix();
		cliffSeed = splitMix();
		cliffHeightSeed = splitMix();
		caveSeed = splitMix();

		chunkX = cx;
		chunkZ = cz;
	}

	public void generate() {
		if (hasGenerated) {
			return;
		}
		hasGenerated = true;

		final int baseOctaves = 4;
		final double baseScale = 250.0;
		final float baseHeightScale = 8.0f;

		final int hillOctaves = 3;
		final double hillScale = 250.0;
		final float hillHeightScale = 16.0f;
		final float hillHeightScaleMod = 4.0f;
		final float hillThresholdMin = -0.5f;
		final float hillThresholdMax = 1.0f;

		final int cliffOctaves = 2;
		final double cliffScale = 250.0;
		final float cliffThreshold = 0.5f;

		final int cliffHeightOctaves = 1;
		final double cliffHeightScale = 100.0;
		final float cliffHeightMin = 2.0f;
		final float cliffHeightMax = 8.0f;



		for (int x = 0; x < Chunk.LENGTH; x++) {
			for (int z = 0; z < Chunk.LENGTH; z++) {
				int xx = (chunkX * Chunk.LENGTH + x);
				int zz = (chunkZ * Chunk.LENGTH + z);

				float baseHeight = OpenSimplex2Octaves.noise2(baseSeed, baseOctaves, (xx + offset) / baseScale, (zz + offset) / baseScale);
				float hill = OpenSimplex2Octaves.noise2(hillSeed, hillOctaves, (xx + offset) / hillScale, (zz + offset) / hillScale);
				hill = 1.0f - (float) Math.cos(MathUtil.threshold(hill, hillThresholdMin, hillThresholdMax) * MathUtil.PI_F / 2.0f);

				float addedBaseHeight = baseHeightScale * MathUtil.lerp(1.0f, hillHeightScaleMod, hill);
				baseHeight = baseHeight * addedBaseHeight + hill * hillHeightScale;

				float cliff = OpenSimplex2Octaves.noise2(cliffSeed, cliffOctaves, (xx + offset) / cliffScale, (zz + offset) / cliffScale);
				float cliffHeight = OpenSimplex2Octaves.noise2(cliffHeightSeed, cliffHeightOctaves, (xx + offset) / cliffHeightScale, (zz + offset) / cliffHeightScale);
				cliffHeight = MathUtil.lerp(cliffHeightMin, cliffHeightMax, cliffHeight / 2.0f + 0.5f) * (1.0f - hill * 5.0f);

				if (cliff > cliffThreshold) {
					baseHeight += cliffHeight;
				}

				height[x + z * Chunk.LENGTH] = baseHeight;
			}
		}
	}

	private long splitMix() {
		long z = (randSeed += 0x9e3779b97f4a7c15L);
	    z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
        z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
		return z ^ z >>> 31;
	}

	public float getHeight(int x, int z) {
		return height[x + z * Chunk.LENGTH];
	}

	public boolean getCave(int x, int y, int z) {
		if (lastQueryLayer != y >> Chunk.SIZE_POW2) {
			generateCaves(y >> Chunk.SIZE_POW2);
		}

		final float cheeseMinDensity = -1.0f;
		final float cheeseMaxDensity = -0.3f;
		final float cheeseDensitySpread = 100.0f;
		final float cheeseDensitySurface = -0.5f;

		int xx = x / 4;
		int yy = (y & Chunk.LENGTH_MASK) / 4;
		int zz = z / 4;

		float c000 = caveInfo[MathUtil.index3D(xx, yy, zz, LERP_MAP_LENGTH)];
		float c001 = caveInfo[MathUtil.index3D(xx, yy, zz + 1, LERP_MAP_LENGTH)];
		float c010 = caveInfo[MathUtil.index3D(xx, yy + 1, zz, LERP_MAP_LENGTH)];
		float c011 = caveInfo[MathUtil.index3D(xx, yy + 1, zz + 1, LERP_MAP_LENGTH)];
		float c100 = caveInfo[MathUtil.index3D(xx + 1, yy, zz, LERP_MAP_LENGTH)];
		float c101 = caveInfo[MathUtil.index3D(xx + 1, yy, zz + 1, LERP_MAP_LENGTH)];
		float c110 = caveInfo[MathUtil.index3D(xx + 1, yy + 1, zz, LERP_MAP_LENGTH)];
		float c111 = caveInfo[MathUtil.index3D(xx + 1, yy + 1, zz + 1, LERP_MAP_LENGTH)];
		float caveInfo = MathUtil.trilinear(c000, c001, c010, c011, c100, c101, c110, c111, (x & 3) / 4.0f, (y & 3) / 4.0f, (z & 3) / 4.0f);
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
					int xx = (x << 2) + (chunkX << Chunk.SIZE_POW2);
					int yy = (y << 2) + (layer << Chunk.SIZE_POW2);
					int zz = (z << 2) + (chunkZ << Chunk.SIZE_POW2);

					float cheese = OpenSimplex2Octaves.noise3_ImproveXZ(caveSeed, cheeseOctaves, (xx + (int)offset) / cheeseScaleXZ, (yy + (int)offset) / cheeseScaleY, (zz + (int)offset) / cheeseScaleXZ);
					caveInfo[MathUtil.index3D(x, y, z, LERP_MAP_LENGTH)] = cheese;
				}
			}
		}
	}
}
