package io.bluestaggo.voxelthing.world;

import io.bluestaggo.pds.CompoundItem;
import io.bluestaggo.pds.ListItem;
import io.bluestaggo.voxelthing.Identifier;
import io.bluestaggo.voxelthing.world.block.Block;
import io.bluestaggo.voxelthing.world.storage.EmptyBlockStorage;
import io.bluestaggo.voxelthing.world.generation.blockInStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Chunk implements IBlockAccess {
	public static final int SIZE_POW2 = 5;
	public static final int LENGTH = 1 << SIZE_POW2;
	public static final int LENGTH_MASK = (1 << SIZE_POW2) - 1;
	public static final int AREA = 1 << SIZE_POW2 * 2;
	public static final int VOLUME = 1 << SIZE_POW2 * 3;

	public final World world;
	public final int x, y, z;

	private BlockStorage blockStorage;
	private boolean empty;
	private boolean hasChanged;

	public Chunk(World world, int x, int y, int z) {
		this(world, x, y, z, new EmptyBlockStorage());
	}

	public Chunk(World world, int x, int y, int z, BlockStorage blockStorage) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.blockStorage = blockStorage;
		empty = blockStorage.isEmpty();
	}

	public int toGlobalX(int x) {
		return x + (this.x << Chunk.SIZE_POW2);
	}

	public int toGlobalY(int y) {
		return y + (this.y << Chunk.SIZE_POW2);
	}

	public int toGlobalZ(int z) {
		return z + (this.z << Chunk.SIZE_POW2);
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		if (!containsLocal(x, y, z)) {
			return this.world.getBlock(toGlobalX(x), toGlobalY(y), toGlobalZ(z));
		}

		return blockStorage.getBlock(x, y, z);
	}

	public void setBlock(int x, int y, int z, Block block) {
		if (blockStorage.needsExpansion(block)) {
			blockStorage = blockStorage.expand();
		}
		blockStorage.setBlock(x, y, z, block);
		hasChanged = true;

		if (block != null) {
			empty = false;
		}
	}

	public boolean isEmpty() {
		return empty;
	}

	public boolean contains(int x, int y, int z) {
		x -= this.x * Chunk.LENGTH;
		y -= this.y * Chunk.LENGTH;
		z -= this.z * Chunk.LENGTH;
		return containsLocal(x, y, z);
	}

	public boolean containsLocal(int x, int y, int z) {
		return x >= 0 && x < Chunk.LENGTH
				&& y >= 0 && y < Chunk.LENGTH
				&& z >= 0 && z < Chunk.LENGTH;
	}

	public CompoundItem serialize() {
		var item = new CompoundItem();

		var paletteItem = new ListItem(blockStorage.palette.stream()
				.map(b -> (b == null ? Block.ID_AIR : b.id).serialize())
				.collect(Collectors.toList()));
		item.setItem("blockPalette", paletteItem);

		item.setByte("blockArrayType", blockStorage.getType());
		item.setByteArray("blocks", blockStorage.getBytes());

		return item;
	}

	public static Chunk deserialize(World world, int x, int y, int z, CompoundItem item) {
		var paletteItem = item.getItem("blockPalette");
		List<Block> palette = paletteItem.getList().stream()
				.map(Identifier::deserialize)
				.map(Block::fromId)
				.collect(Collectors.toCollection(ArrayList::new));

		byte[] blocks = item.getByteArray("blocks");
		byte blockArrayType = item.getByte("blockArrayType");

		BlockStorage storage = BlockStorage.decode(blockArrayType, palette, blocks);

		return new Chunk(world, x, y, z, storage);
	}

	public void dontSave() {
		hasChanged = false;
	}

	public void onUnload() {
		save();
	}

	public void save() {
		if (hasChanged) {
			world.saveHandler.saveChunkData(x, y, z, serialize());
		}
	}
}
