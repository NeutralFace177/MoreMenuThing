package io.bluestaggo.voxelthing.renderer.world;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.renderer.vertices.Bindings;
import io.bluestaggo.voxelthing.world.BlockRaycast;
import io.bluestaggo.voxelthing.world.Chunk;
import io.bluestaggo.voxelthing.world.Direction;
import io.bluestaggo.voxelthing.world.IBlockAccess;
import io.bluestaggo.voxelthing.world.block.Block;
import io.bluestaggo.voxelthing.world.block.BlockType;
import io.bluestaggo.voxelthing.world.block.texture.AllSidesTexture;
import io.bluestaggo.voxelthing.world.block.texture.BlockTexture;

import org.joml.Vector2i;
import org.joml.Vector3f;

public class BlockOverlayRenderer {
	private static final float SHADE_FACTOR = 0.15f;
	private static final float OFFSET = 0.0125f;
	private final SideRenderer[] SIDE_RENDERERS = {
			this::renderNorthFace,
			this::renderSouthFace,
			this::renderWestFace,
			this::renderEastFace,
			this::renderBottomFace,
			this::renderTopFace,
	};

	@FunctionalInterface
	private interface SideRenderer {
		void render(Bindings bindings, AllSidesTexture texture, int x, int y, int z);
	}

	private float getShade(int amount) {
		return 1.0f - SHADE_FACTOR * amount;
	}

	public boolean render(Bindings bindings, int x, int y, int z, Direction face) {	
		AllSidesTexture texture;
		for (Direction dir : Direction.ALL) {
			if (dir == face) {
				texture = new AllSidesTexture(30, 30);
			} else {
				texture = new AllSidesTexture(30, 31);
			}
			SIDE_RENDERERS[dir.ordinal()].render(bindings, texture, x, y, z);
		}
		return true;
	}

	private void renderNorthFace(Bindings bindings, AllSidesTexture texture, int x, int y, int z) {
		Vector2i tex = texture.get(Direction.NORTH, null, x, y, z);
		float texX = tex.x * Block.TEXTURE_WIDTH;
		float texY = tex.y * Block.TEXTURE_WIDTH;
		float texXp = texX + Block.TEXTURE_WIDTH;
		float texYp = texY + Block.TEXTURE_WIDTH;
		float shade = getShade(1);

		bindings.addVertices(   x+OFFSET+1,  y+OFFSET+1,  z - OFFSET,  shade,  shade,  shade,  texX,   texY    );
		bindings.addVertices(   x+OFFSET+1,  y - OFFSET,  z - OFFSET,  shade,  shade,  shade,  texX,   texYp   );
		bindings.addVertices(   x - OFFSET,  y - OFFSET,  z - OFFSET,  shade,  shade,  shade,  texXp,  texYp   );
		bindings.addVertices(   x - OFFSET,  y+OFFSET+1,  z - OFFSET,  shade,  shade,  shade,  texXp,  texY    );
		bindings.addIndices(0, 1, 2, 2, 3, 0);
	}

	private void renderSouthFace(Bindings bindings, AllSidesTexture texture, int x, int y, int z) {
		Vector2i tex = texture.get(Direction.NORTH, null, x, y, z);
		float texX = tex.x * Block.TEXTURE_WIDTH;
		float texY = tex.y * Block.TEXTURE_WIDTH;
		float texXp = texX + Block.TEXTURE_WIDTH;
		float texYp = texY + Block.TEXTURE_WIDTH;
		float shade = getShade(3);

		bindings.addVertices(   x - OFFSET,  y+OFFSET+1,  z+OFFSET+1,  shade,  shade,  shade,  texX,   texY    );
		bindings.addVertices(   x - OFFSET,  y - OFFSET,  z+OFFSET+1,  shade,  shade,  shade,  texX,   texYp   );
		bindings.addVertices(   x+OFFSET+1,  y - OFFSET,  z+OFFSET+1,  shade,  shade,  shade,  texXp,  texYp   );
		bindings.addVertices(   x+OFFSET+1,  y+OFFSET+1,  z+OFFSET+1,  shade,  shade,  shade,  texXp,  texY    );
		bindings.addIndices(0, 1, 2, 2, 3, 0);
	}

	private void renderWestFace(Bindings bindings, AllSidesTexture texture, int x, int y, int z) {
		Vector2i tex = texture.get(Direction.NORTH, null, x, y, z);
		float texX = tex.x * Block.TEXTURE_WIDTH;
		float texY = tex.y * Block.TEXTURE_WIDTH;
		float texXp = texX + Block.TEXTURE_WIDTH;
		float texYp = texY + Block.TEXTURE_WIDTH;
		float shade = getShade(2);

		bindings.addVertices(   x - OFFSET,  y+OFFSET+1,  z - OFFSET,  shade,  shade,  shade,  texX,   texY    );
		bindings.addVertices(   x - OFFSET,  y - OFFSET,  z - OFFSET,  shade,  shade,  shade,  texX,   texYp   );
		bindings.addVertices(   x - OFFSET,  y - OFFSET,  z+OFFSET+1,  shade,  shade,  shade,  texXp,  texYp   );
		bindings.addVertices(   x - OFFSET,  y+OFFSET+1,  z+OFFSET+1,  shade,  shade,  shade,  texXp,  texY    );
		bindings.addIndices(0, 1, 2, 2, 3, 0);
	}

	private void renderEastFace(Bindings bindings, AllSidesTexture texture, int x, int y, int z) {
		Vector2i tex = texture.get(Direction.NORTH, null, x, y, z);
		float texX = tex.x * Block.TEXTURE_WIDTH;
		float texY = tex.y * Block.TEXTURE_WIDTH;
		float texXp = texX + Block.TEXTURE_WIDTH;
		float texYp = texY + Block.TEXTURE_WIDTH;
		float shade = getShade(2);

		bindings.addVertices(   x+OFFSET+1,  y+OFFSET+1,  z+OFFSET+1,  shade,  shade,  shade,  texX,   texY    );
		bindings.addVertices(   x+OFFSET+1,  y - OFFSET,  z+OFFSET+1,  shade,  shade,  shade,  texX,   texYp   );
		bindings.addVertices(   x+OFFSET+1,  y - OFFSET,  z - OFFSET,  shade,  shade,  shade,  texXp,  texYp   );
		bindings.addVertices(   x+OFFSET+1,  y+OFFSET+1,  z - OFFSET,  shade,  shade,  shade,  texXp,  texY    );
		bindings.addIndices(0, 1, 2, 2, 3, 0);
	}

	private void renderBottomFace(Bindings bindings, AllSidesTexture texture, int x, int y, int z) {
		Vector2i tex = texture.get(Direction.NORTH, null, x, y, z);
		float texX = tex.x * Block.TEXTURE_WIDTH;
		float texY = tex.y * Block.TEXTURE_WIDTH;
		float texXp = texX + Block.TEXTURE_WIDTH;
		float texYp = texY + Block.TEXTURE_WIDTH;
		float shade = getShade(4);

		bindings.addVertices(   x+OFFSET+1,  y - OFFSET,  z - OFFSET,   shade,  shade,  shade,  texX,   texY    );
		bindings.addVertices(   x+OFFSET+1,  y - OFFSET,  z+OFFSET+1,  shade,  shade,  shade,  texX,   texYp   );
		bindings.addVertices(   x - OFFSET,  y - OFFSET,  z+OFFSET+1,  shade,  shade,  shade,  texXp,  texYp   );
		bindings.addVertices(   x - OFFSET,  y - OFFSET,  z - OFFSET,  shade,  shade,  shade,  texXp,  texY    );
		bindings.addIndices(0, 1, 2, 2, 3, 0);
	}

	private void renderTopFace(Bindings bindings, AllSidesTexture texture, int x, int y, int z) {
		Vector2i tex = texture.get(Direction.NORTH, null, x, y, z);
		float texX = tex.x * Block.TEXTURE_WIDTH;
		float texY = tex.y * Block.TEXTURE_WIDTH;
		float texXp = texX + Block.TEXTURE_WIDTH;
		float texYp = texY + Block.TEXTURE_WIDTH;
		float shade = getShade(0);

		bindings.addVertices(   x+OFFSET+1,  y+OFFSET+1,  z+OFFSET+1,  shade,  shade,  shade,  texX,   texY    );
		bindings.addVertices(   x+OFFSET+1,  y+OFFSET+1,  z - OFFSET,  shade,  shade,  shade,  texX,   texYp   );
		bindings.addVertices(   x - OFFSET,  y+OFFSET+1,  z - OFFSET,  shade,  shade,  shade,  texXp,  texYp   );
		bindings.addVertices(   x - OFFSET,  y+OFFSET+1,  z+OFFSET+1,  shade,  shade,  shade,  texXp,  texY    );
		bindings.addIndices(0, 1, 2, 2, 3, 0);
	}
}
