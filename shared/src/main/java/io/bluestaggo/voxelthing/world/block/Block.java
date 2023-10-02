package io.bluestaggo.voxelthing.world.block;

import io.bluestaggo.voxelthing.Identifier;
import io.bluestaggo.voxelthing.math.AABB;
import io.bluestaggo.voxelthing.world.Direction;
import io.bluestaggo.voxelthing.world.IBlockAccess;
import io.bluestaggo.voxelthing.world.block.texture.AllSidesTexture;
import io.bluestaggo.voxelthing.world.block.texture.BlockTexture;
import io.bluestaggo.voxelthing.world.block.texture.ColumnTexture;
import io.bluestaggo.voxelthing.world.block.texture.GrassTexture;

import java.util.*;
import java.util.stream.IntStream;

import org.joml.Vector2i;

public class Block {
	public static final int TEXTURE_RES = 512;
	public static final int TEXTURE_ROWS = TEXTURE_RES / 16;
	public static final float TEXTURE_WIDTH = 1.0f / TEXTURE_ROWS;

	private static final List<Block> REGISTERED_BLOCKS_ORDERED_MUTABLE = new ArrayList<>();
	private static final Map<Identifier, Block> REGISTERED_BLOCKS_MUTABLE = new HashMap<>();
	public static final List<Block> REGISTERED_BLOCKS_ORDERED = Collections.unmodifiableList(REGISTERED_BLOCKS_ORDERED_MUTABLE);
	public static final Map<Identifier, Block> REGISTERED_BLOCKS = Collections.unmodifiableMap(REGISTERED_BLOCKS_MUTABLE);
	public static final List<Block> REGISTERED_SLABS_O = new ArrayList<>();
	public static final Map<Identifier, Block> REGISTERED_SLABS_M = new HashMap<>();
	public static final List<Block> REGISTERED_SLABS_ORDERED = Collections.unmodifiableList(REGISTERED_SLABS_O);

	public final BlockType type;
	public String[] blockStates = {};
	//how long in multiples of 5 ticks it takes to mine a block 
	public float hardness = 5;

	public static final String[] WOOL_NAMES = {
			"black",
			"dark_gray",
			"gray",
			"light_gray",
			"yellow",
			"orange",
			"green",
			"teal",
			"turquoise",
			"cyan",
			"blue",
			"navy",
			"red",
			"purple",
			"brown",
			"white"
	};

	public static final Identifier ID_AIR = new Identifier("air");
	public static final Block STONE = new Block("stone", 10).withTex(1, 0);
	public static final Block GRASS = new Block("grass", 2.5f).withTex(new GrassTexture(0, 1, 0, 0, 0, 2));
	public static final Block DIRT = new Block("dirt", 2).withTex(0, 2);
	public static final Block COBBLESTONE = new Block("cobblestone", 8).withTex(1, 1);
	public static final Block BRICKS = new Block("bricks", 8).withTex(3, 2);
	public static final Block PLANKS = new Block("planks", 4).withTex(3, 0);
	public static final Block LOG = new Block("log", 4.25f).withTex(new ColumnTexture(3, 1, 4, 1));
	public static final Block LEAVES = new Block("leaves", 1.5f).withTex(4, 0).transparency(BlockTransparency.THICK);
	public static final Block GLASS = new Block("glass", 1.5f).withTex(4, 2).transparency(BlockTransparency.FULL);
	public static final Block SAND = new Block("sand", 2).withTex(2, 0);
	public static final Block GRAVEL = new Block("gravel", 2).withTex(2, 1);
	public static final Block STONE_BRICKS = new Block("stone_bricks", 8).withTex(2, 2);
	public static final Block POLISHED_STONE = new Block("polished_stone", 10).withTex(1, 2);
	public static final Block WATER = new Block("water", 2/5).withTex(4,3).transparency(BlockTransparency.FULL);
	public static final Block SNOW = new Block("snow", 2).withTex(4,4);
	public static final Block[] WOOL = IntStream.range(0, WOOL_NAMES.length)
			.mapToObj(i -> new Block("wool_" + WOOL_NAMES[i], 3).withTex(i % 4, i / 4 + 3))
			.toArray(Block[]::new);
	public static final Block[] SLABS = IntStream.range(0, REGISTERED_BLOCKS_ORDERED_MUTABLE.size())
			.mapToObj(i -> new Block(REGISTERED_BLOCKS_ORDERED_MUTABLE.get(i).getId().name + "_slab",BlockType.slab, new String[]{"Bottom"}, REGISTERED_BLOCKS_ORDERED_MUTABLE.get(i).hardness).withTex(REGISTERED_BLOCKS_ORDERED_MUTABLE.get(i).texture))
			.toArray(Block[]::new);

	public final Identifier id;
	protected BlockTexture texture;
	protected BlockTransparency transparency = BlockTransparency.NONE;

	static {
		REGISTERED_BLOCKS_MUTABLE.put(ID_AIR, null);
	}

	public Block(String id) {
		this(new Identifier(id), BlockType.Normal, new String[]{}, 2147483647);
	}

	public Block(String id, float hardness) {
		this(new Identifier(id), BlockType.Normal, new String[]{}, hardness);
	}

	public Block(String id, BlockType type, String[] states) {
		this(new Identifier(id), type, states, 2147483647);
	}

	public Block(String id, BlockType type, String[] states, float hardness) {
		this(new Identifier(id), type, states, hardness);
	}

	public Block(String namespace, String name) {
		this(new Identifier(namespace, name), BlockType.Normal, new String[]{}, 2147483647);
	}

	public Block(String namespace, String name, float hardness) {
		this(new Identifier(namespace, name), BlockType.Normal, new String[]{}, hardness);
	}

	public Block(Identifier id, BlockType type, String[] states, float hardness) {
		this.hardness  = hardness;
		if (REGISTERED_BLOCKS.containsKey(id)) {
			throw new IllegalArgumentException("Block \"" + id + "\" already exists");
		}

		this.type = type;
		this.blockStates = states;

		this.id = id;
		REGISTERED_BLOCKS_ORDERED_MUTABLE.add(this);
		REGISTERED_BLOCKS_MUTABLE.put(id, this);
		if (type == BlockType.slab) {
			REGISTERED_SLABS_O.add(this);
			REGISTERED_SLABS_M.put(id, this);
		}
	}

	public static Block fromId(Identifier id) {
		if (REGISTERED_BLOCKS_MUTABLE.containsKey(id)) {
			return REGISTERED_BLOCKS_MUTABLE.get(id);
		}
		return null;
	}

	public static String getCode(Block block) {
		if (block.id.name.contains("WOOL") || block.id.name.contains("wool")) {
			for (int i = 0; i < WOOL_NAMES.length; i++) {
				String name = block.id.name.substring(5, block.id.name.length());
				if (WOOL_NAMES[i].equals(name)) {
					return "Block.WOOL[" + i + "]";
					
				}
			}
		}
		return "Block." +  block.id.name.toUpperCase();


	}

	public final Identifier getId() {
		return id;
	}

	@Override
	public String toString() {
		return id.toString();
	}

	public int hardnessTick() {
		return (int)Math.ceil((double)hardness * 5d) > 0 ? (int)Math.ceil((double)hardness * 5d) : 1;
	}

	protected Block withTex(int x, int y) {
		return withTex(new AllSidesTexture(x, y));
	}

	protected Block withTex(BlockTexture texture) {
		this.texture = texture;
		return this;
	}

	protected Block transparency(BlockTransparency transparency) {
		this.transparency = transparency;
		return this;
	}

	public BlockTexture getTexture() {
		return texture;
	}

	public boolean isTransparent() {
		return transparency.transparent;
	}

	public boolean isFaceDrawn(IBlockAccess blockAccess, int x, int y, int z, Direction face, Block ogBlock) {
		Block block = blockAccess.getBlock(x, y, z);
		try {
			if (block.type == BlockType.slab && ogBlock.type == BlockType.slab && (face == Direction.BOTTOM || face == Direction.TOP)) {
				return true;
			}
		} catch (NullPointerException e) {

		}

		if (block == null || (block.type == BlockType.slab && ogBlock.type != BlockType.slab ) ) {
			return true;
		}

		if (transparency.transparent) {
			if (!transparency.drawSameFaces) {
				return block != this;
			}
		} else {
			return block.isTransparent();
		}

		return true;
	}

	public AABB getCollisionBox(int x, int y, int z) {
		return new AABB(x, y, z, x + this.type.shape().x, y + this.type.shape().y, z + this.type.shape().z);
	}
}
