package io.bluestaggo.voxelthing.renderer.world;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.renderer.vertices.Bindings;
import io.bluestaggo.voxelthing.world.BlockRaycast;
import io.bluestaggo.voxelthing.world.Chunk;
import io.bluestaggo.voxelthing.world.Direction;
import io.bluestaggo.voxelthing.world.IBlockAccess;
import io.bluestaggo.voxelthing.world.block.Block;
import io.bluestaggo.voxelthing.world.block.BlockType;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class BlockRenderer {
	private static final float SHADE_FACTOR = 0.15f;
	private static float xOffSet = 1;
	private static float yOffSet = 1.25f;
	private static float zOffSet = 1;
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
		void render(Bindings bindings, IBlockAccess blockAccess, Block block, int x, int y, int z);
	}

	private float getShade(int amount) {
		return 1.0f - SHADE_FACTOR * amount;
	}

	public boolean render(Bindings bindings, IBlockAccess blockAccess, Chunk chunk, int x, int y, int z) {
		Block block = chunk.getBlock(x, y, z);

		if (block == null) {
			return false;
		}
		
		xOffSet = block.type.shape().x;
		yOffSet = block.type.shape().y;
		zOffSet = block.type.shape().z;

		int xx = x + Chunk.LENGTH * chunk.x;
		int yy = y + Chunk.LENGTH * chunk.y;
		int zz = z + Chunk.LENGTH * chunk.z;
		
		for (Direction dir : Direction.ALL) {
			if (block.isFaceDrawn(blockAccess, xx + dir.X, yy + dir.Y, zz + dir.Z, dir, block)) {
				SIDE_RENDERERS[dir.ordinal()].render(bindings, blockAccess, block, xx, yy, zz);
			}
		}
		return true;
	}

	private void renderNorthFace(Bindings bindings, IBlockAccess blockAccess, Block block, int x, int y, int z) {
		Vector2i texture = block.getTexture().get(Direction.NORTH, blockAccess, x, y, z);
		BlockType type = blockAccess.getBlock(x, y, z).type;
		float texX = texture.x * Block.TEXTURE_WIDTH;
		float texY = texture.y * Block.TEXTURE_WIDTH;
		float texXp = texX + Block.TEXTURE_WIDTH;
		float texYp = texY +  (type == BlockType.slab ? Block.TEXTURE_WIDTH/2 : Block.TEXTURE_WIDTH);
		float shade = getShade(1);

		bindings.addVertices(   x +  +xOffSet,  y + yOffSet,  z,  shade,  shade,  shade,  texX,   texY    );
		bindings.addVertices(   x +  +xOffSet,  y,            z,  shade,  shade,  shade,  texX,   texYp   );
		bindings.addVertices(   x,              y,            z,  shade,  shade,  shade,  texXp,  texYp   );
		bindings.addVertices(   x,              y + yOffSet,  z,  shade,  shade,  shade,  texXp,  texY    );
		bindings.addIndices(0, 1, 2, 2, 3, 0);
	}

	private void renderSouthFace(Bindings bindings, IBlockAccess blockAccess, Block block, int x, int y, int z) {
		Vector2i texture = block.getTexture().get(Direction.SOUTH, blockAccess, x, y, z);
		BlockType type = blockAccess.getBlock(x, y, z).type;
		float texX = texture.x * Block.TEXTURE_WIDTH;
		float texY = texture.y * Block.TEXTURE_WIDTH;
		float texXp = texX + Block.TEXTURE_WIDTH;
		float texYp = texY +  (type == BlockType.slab ? Block.TEXTURE_WIDTH/2 : Block.TEXTURE_WIDTH);
		float shade = getShade(3);

		bindings.addVertices(   x,            y + yOffSet,  z + zOffSet,  shade,  shade,  shade,  texX,   texY    );
		bindings.addVertices(   x,            y,            z + zOffSet,  shade,  shade,  shade,  texX,   texYp   );
		bindings.addVertices(   x + xOffSet,  y,            z + zOffSet,  shade,  shade,  shade,  texXp,  texYp   );
		bindings.addVertices(   x + xOffSet,  y + yOffSet,  z + zOffSet,  shade,  shade,  shade,  texXp,  texY    );
		bindings.addIndices(0, 1, 2, 2, 3, 0);
	}

	private void renderWestFace(Bindings bindings, IBlockAccess blockAccess, Block block, int x, int y, int z) {
		Vector2i texture = block.getTexture().get(Direction.WEST, blockAccess, x, y, z);
		BlockType type = blockAccess.getBlock(x, y, z).type;
		float texX = texture.x * Block.TEXTURE_WIDTH;
		float texY = texture.y * Block.TEXTURE_WIDTH;
		float texXp = texX + Block.TEXTURE_WIDTH;
		float texYp = texY +  (type == BlockType.slab ? Block.TEXTURE_WIDTH/2 : Block.TEXTURE_WIDTH);
		float shade = getShade(2);

		bindings.addVertices(   x,  y + yOffSet,  z,            shade,  shade,  shade,  texX,   texY    );
		bindings.addVertices(   x,  y,            z,            shade,  shade,  shade,  texX,   texYp   );
		bindings.addVertices(   x,  y,            z + zOffSet,  shade,  shade,  shade,  texXp,  texYp   );
		bindings.addVertices(   x,  y + yOffSet,  z + zOffSet,  shade,  shade,  shade,  texXp,  texY    );
		bindings.addIndices(0, 1, 2, 2, 3, 0);
	}

	private void renderEastFace(Bindings bindings, IBlockAccess blockAccess, Block block, int x, int y, int z) {
		Vector2i texture = block.getTexture().get(Direction.EAST, blockAccess, x, y, z);
		BlockType type = blockAccess.getBlock(x, y, z).type;
		float texX = texture.x * Block.TEXTURE_WIDTH;
		float texY = texture.y * Block.TEXTURE_WIDTH;
		float texXp = texX + Block.TEXTURE_WIDTH;
		float texYp = texY +  (type == BlockType.slab ? Block.TEXTURE_WIDTH/2 : Block.TEXTURE_WIDTH);
		float shade = getShade(2);

		bindings.addVertices(   x + xOffSet,  y + yOffSet,  z + zOffSet,  shade,  shade,  shade,  texX,   texY    );
		bindings.addVertices(   x + xOffSet,  y,            z + zOffSet,  shade,  shade,  shade,  texX,   texYp   );
		bindings.addVertices(   x + xOffSet,  y,            z,            shade,  shade,  shade,  texXp,  texYp   );
		bindings.addVertices(   x + xOffSet,  y + yOffSet,  z,            shade,  shade,  shade,  texXp,  texY    );
		bindings.addIndices(0, 1, 2, 2, 3, 0);
	}

	private void renderBottomFace(Bindings bindings, IBlockAccess blockAccess, Block block, int x, int y, int z) {
		Vector2i texture = block.getTexture().get(Direction.BOTTOM, blockAccess, x, y, z);
		BlockType type = blockAccess.getBlock(x, y, z).type;
		float texX = texture.x * Block.TEXTURE_WIDTH;
		float texY = texture.y * Block.TEXTURE_WIDTH;
		float texXp = texX + Block.TEXTURE_WIDTH;
		float texYp = texY + Block.TEXTURE_WIDTH;
		float shade = getShade(4);

		bindings.addVertices(   x + xOffSet,  y,  z,      shade,  shade,  shade,  texX,   texY    );
		bindings.addVertices(   x + xOffSet,  y,  z + zOffSet,  shade,  shade,  shade,  texX,   texYp   );
		bindings.addVertices(   x,            y,  z + zOffSet,  shade,  shade,  shade,  texXp,  texYp   );
		bindings.addVertices(   x,            y,  z,      shade,  shade,  shade,  texXp,  texY    );
		bindings.addIndices(0, 1, 2, 2, 3, 0);
	}

	private void renderTopFace(Bindings bindings, IBlockAccess blockAccess, Block block, int x, int y, int z) {
		Vector2i texture = block.getTexture().get(Direction.TOP, blockAccess, x, y, z);
		BlockType type = blockAccess.getBlock(x, y, z).type;
		float texX = texture.x * Block.TEXTURE_WIDTH;
		float texY = texture.y * Block.TEXTURE_WIDTH;
		float texXp = texX + Block.TEXTURE_WIDTH;
		float texYp = texY + Block.TEXTURE_WIDTH;
		float shade = getShade(0);

		bindings.addVertices(   x + xOffSet,  y + yOffSet,  z + zOffSet,  shade,  shade,  shade,  texX,   texY    );
		bindings.addVertices(   x + xOffSet,  y + yOffSet,  z,            shade,  shade,  shade,  texX,   texYp   );
		bindings.addVertices(   x,            y + yOffSet,  z,            shade,  shade,  shade,  texXp,  texYp   );
		bindings.addVertices(   x,            y + yOffSet,  z + zOffSet,  shade,  shade,  shade,  texXp,  texY    );
		bindings.addIndices(0, 1, 2, 2, 3, 0);
	}
}
