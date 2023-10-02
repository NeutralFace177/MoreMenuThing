package io.bluestaggo.voxelthing.renderer.world;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.renderer.GLState;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.util.Primitives;
import io.bluestaggo.voxelthing.renderer.vertices.Bindings;
import io.bluestaggo.voxelthing.renderer.vertices.VertexLayout;
import io.bluestaggo.voxelthing.renderer.vertices.VertexType;
import io.bluestaggo.voxelthing.window.Window;
import io.bluestaggo.voxelthing.world.BlockRaycast;
import io.bluestaggo.voxelthing.world.Chunk;
import io.bluestaggo.voxelthing.world.World;
import io.bluestaggo.voxelthing.world.block.texture.AllSidesTexture;

import org.joml.FrustumIntersection;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11C.GL_BACK;
import static org.lwjgl.opengl.GL11C.GL_BLEND;
import static org.lwjgl.opengl.GL11C.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.GL_FRONT;
import static org.lwjgl.opengl.GL11C.glCullFace;
import static org.lwjgl.opengl.GL33C.*;

public class WorldRenderer {
	private final MainRenderer renderer;
	private final Bindings background;
	private final Bindings clouds;

	private World world;
	private ChunkRenderer[] chunkRenderers;
	private List<ChunkRenderer> sortedChunkRenderers;
	private int minX, minY, minZ;
	private int maxX, maxY, maxZ;
	private int renderRange;
	public boolean mining;
	public int mineProgress;

	public int renderDistance = 16;

	public WorldRenderer(MainRenderer renderer) {
		this.renderer = renderer;

		background = Primitives.inWorld().generateSphere(null, 1.0f, 16, 16);
		clouds = Primitives.ofVector3f().generatePlane(null);
	}

	public int chunkRendererCoord(int x, int y, int z) {
		x = Math.floorMod(x + renderDistance, renderRange);
		y = Math.floorMod(y + renderDistance, renderRange);
		z = Math.floorMod(z + renderDistance, renderRange);
		return (x * renderRange + z) * renderRange + y;
	}

	public void setWorld(World world) {
		this.world = world;
		this.loadRenderers();
	}

	public void loadRenderers() {
		minX = minY = minZ = -renderDistance;
		maxX = maxY = maxZ = renderDistance;
		renderRange = renderDistance * 2 + 1;

		if (chunkRenderers != null) {
			for (ChunkRenderer chunkRenderer : chunkRenderers) {
				chunkRenderer.unload();
			}
		}

		chunkRenderers = new ChunkRenderer[renderRange * renderRange * renderRange];
		for (int x = -renderDistance; x <= renderDistance; x++) {
			for (int z = -renderDistance; z <= renderDistance; z++) {
				for (int y = -renderDistance; y <= renderDistance; y++) {
					int i = chunkRendererCoord(x, y, z);
					chunkRenderers[i] = new ChunkRenderer(renderer, world, x, y, z);
				}
			}
		}

		moveRenderers();
	}

	public void draw() {
		int updates = 0;
		int maxUpdates = 1;

		FrustumIntersection frustum = this.renderer.camera.getFrustum();
		double currentTime = Window.getTimeElapsed();

		for (ChunkRenderer chunkRenderer : sortedChunkRenderers) {
			if (!chunkRenderer.inFrustum(frustum)) continue;

			if (updates < maxUpdates) {
				boolean neededUpdate = chunkRenderer.needsUpdate();
				chunkRenderer.render();
				if (neededUpdate) {
					updates++;
				}
			}

			renderer.worldShader.fade.set((float)chunkRenderer.getFadeAmount(currentTime));
			chunkRenderer.draw();
		}
		Game game = Game.getInstance();
		BlockRaycast raycast = game.getBlockRaycast();
		Bindings bindings = new Bindings(VertexLayout.WORLD);
		if (raycast.blockHit()) {
			if (mining) {
				renderer.blockOverlayRenderer.render(bindings, raycast.getHitX(), raycast.getHitY(), raycast.getHitZ(), raycast.getHitFace(), new AllSidesTexture(28-mineProgress+2, 31), true);
				System.out.println(mineProgress);
			}
			renderer.blockOverlayRenderer.render(bindings, raycast.getHitX(), raycast.getHitY(), raycast.getHitZ(), raycast.getHitFace());
		}
		if (bindings != null) {
			bindings.upload(true);
			bindings.draw();
		}


		renderer.worldShader.fade.set(0.0f);
	}

	public void drawSky() {
		try (GLState state = new GLState()) {
			renderer.skyShader.use();
			state.disable(GL_DEPTH_TEST);
			glCullFace(GL_FRONT);
			background.draw();
			glCullFace(GL_BACK);
		}
	}

	public void drawClouds() {
		try (GLState state = new GLState()) {
			renderer.cloudShader.use();
			state.enable(GL_BLEND);
			state.disable(GL_CULL_FACE);
			clouds.draw();
		}
	}

	public void moveRenderers() {
		Vector3f cameraPos = renderer.camera.getPosition();
		int x = (int)Math.floor(cameraPos.x / Chunk.LENGTH);
		int y = (int)Math.floor(cameraPos.y / Chunk.LENGTH);
		int z = (int)Math.floor(cameraPos.z / Chunk.LENGTH);

		minX = x - renderDistance;
		minY = y - renderDistance;
		minZ = z - renderDistance;
		maxX = x + renderDistance;
		maxY = y + renderDistance;
		maxZ = z + renderDistance;

		for (int ax = 0; ax < renderRange; ax++) {
			for (int ay = 0; ay < renderRange; ay++) {
				for (int az = 0; az < renderRange; az++) {
					int cx = ax + x - renderDistance;
					int cy = ay + y - renderDistance;
					int cz = az + z - renderDistance;

					ChunkRenderer renderer = chunkRenderers[chunkRendererCoord(cx, cy, cz)];
					if (renderer.getX() != cx || renderer.getY() != cy || renderer.getZ() != cz) {
						renderer.setPosition(cx, cy, cz);
					}
				}
			}
		}

		sortedChunkRenderers = Arrays.stream(chunkRenderers)
				.sorted(this::compareChunks)
				.toList();
	}

	private int compareChunks(ChunkRenderer a, ChunkRenderer b) {
		Vector3f cameraPos = renderer.camera.getPosition();

		int ax = (a.getX() - (int)(cameraPos.x / Chunk.LENGTH));
		int ay = (a.getY() - (int)(cameraPos.y / Chunk.LENGTH));
		int az = (a.getZ() - (int)(cameraPos.z / Chunk.LENGTH));

		int bx = (b.getX() - (int)(cameraPos.x / Chunk.LENGTH));
		int by = (b.getY() - (int)(cameraPos.y / Chunk.LENGTH));
		int bz = (b.getZ() - (int)(cameraPos.z / Chunk.LENGTH));

		int aDist = ax * ax + ay * ay + az * az;
		int bDist = bx * bx + by * by + bz * bz;
		return Integer.compare(aDist, bDist);
	}

	public void markNeighbourUpdateAt(int x, int y, int z) {
		markUpdateAt(x, y, z);
		markUpdateAt(x - 1, y, z);
		markUpdateAt(x + 1, y, z);
		markUpdateAt(x, y - 1, z);
		markUpdateAt(x, y + 1, z);
		markUpdateAt(x, y, z - 1);
		markUpdateAt(x, y, z + 1);
	}

	private void markUpdateAt(int x, int y, int z) {
		int cx = Math.floorDiv(x, Chunk.LENGTH);
		int cy = Math.floorDiv(y, Chunk.LENGTH);
		int cz = Math.floorDiv(z, Chunk.LENGTH);
		markChunkUpdateAt(cx, cy, cz);
	}

	public void markNeighbourChunkUpdateAt(int x, int y, int z) {
		markChunkUpdateAt(x, y, z);
		markChunkUpdateAt(x - 1, y, z);
		markChunkUpdateAt(x + 1, y, z);
		markChunkUpdateAt(x, y - 1, z);
		markChunkUpdateAt(x, y + 1, z);
		markChunkUpdateAt(x, y, z - 1);
		markChunkUpdateAt(x, y, z + 1);
	}

	private void markChunkUpdateAt(int x, int y, int z) {
		if (x < minX || x > maxX || y < minY || y > maxY || z < minZ || z > maxZ) {
			return;
		}

		ChunkRenderer renderer = chunkRenderers[chunkRendererCoord(x, y, z)];
		if (renderer.getX() == x && renderer.getY() == y && renderer.getZ() == z) {
			renderer.queueUpdate();
		}
	}

	public void unload() {
		background.unload();

		if (chunkRenderers != null) {
			for (ChunkRenderer chunkRenderer : chunkRenderers) {
				chunkRenderer.unload();
			}
		}
	}
}
