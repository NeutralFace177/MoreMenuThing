package io.bluestaggo.voxelthing.renderer.world;

import io.bluestaggo.voxelthing.math.MathUtil;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.vertices.Bindings;
import io.bluestaggo.voxelthing.renderer.vertices.VertexLayout;
import io.bluestaggo.voxelthing.window.Window;
import io.bluestaggo.voxelthing.world.Chunk;
import io.bluestaggo.voxelthing.world.ChunkCache;
import io.bluestaggo.voxelthing.world.IBlockAccess;
import io.bluestaggo.voxelthing.world.World;
import org.joml.FrustumIntersection;

public class ChunkRenderer {
	private final MainRenderer renderer;
	private final World world;
	private int x, y, z;
	private boolean needsUpdate;
	private boolean empty;
	private double firstAppearance;

	private Bindings bindings;

	public ChunkRenderer(MainRenderer renderer, World world, int x, int y, int z) {
		this.renderer = renderer;
		this.world = world;
		this.setPosition(x, y, z);
	}

	public void setPosition(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		needsUpdate = true;
		empty = true;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void render() {
		boolean wasEmpty = empty;

		if (needsUpdate) {
			empty = true;
			Chunk chunk = world.getOrLoadChunkAt(x, y, z);

			if (chunk == null || chunk.isEmpty()) {
				needsUpdate = false;
				return;
			}

			if (bindings == null) {
				bindings = new Bindings(VertexLayout.WORLD);
			}

			IBlockAccess cache = new ChunkCache(world, x, y, z);
			for (int xx = 0; xx < Chunk.LENGTH; xx++) {
				for (int yy = 0; yy < Chunk.LENGTH; yy++) {
					for (int zz = 0; zz < Chunk.LENGTH; zz++) {
						 empty &= !renderer.blockRenderer.render(bindings, cache, chunk, xx, yy, zz);
					}
				}
			}

			if (!empty) {
				bindings.upload(true);

				if (wasEmpty) {
					firstAppearance = Window.getTimeElapsed();
				}
			}
			needsUpdate = false;
		}
	}

	public void draw() {
		if (!empty && bindings != null) {
			bindings.draw();
		}
	}

	public void queueUpdate() {
		needsUpdate = true;
	}

	public boolean needsUpdate() {
		return needsUpdate;
	}

	public boolean inFrustum(FrustumIntersection frustum) {
		return frustum.testAab(getX() * Chunk.LENGTH, getY() * Chunk.LENGTH, getZ() * Chunk.LENGTH,
				(getX() + 1) * Chunk.LENGTH, (getY() + 1) * Chunk.LENGTH, (getZ() + 1) * Chunk.LENGTH);
	}

	public double getFadeAmount(double time) {
		return MathUtil.clamp(1.0 - (time - firstAppearance));
	}

	public void unload() {
		if (bindings != null) {
			bindings.unload();
			bindings = null;
		}
	}
}
