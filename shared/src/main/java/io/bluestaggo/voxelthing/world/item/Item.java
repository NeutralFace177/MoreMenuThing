package io.bluestaggo.voxelthing.world.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import io.bluestaggo.voxelthing.Identifier;
import io.bluestaggo.voxelthing.world.Direction;
import io.bluestaggo.voxelthing.world.block.Block;
import io.bluestaggo.voxelthing.world.block.BlockType;
import io.bluestaggo.voxelthing.world.item.texture.ItemTexture;
import org.joml.Vector2i;


public class Item {
    public static final int TEXTURE_RES = 512;
	public static final int TEXTURE_ROWS = TEXTURE_RES / 16;
	public static final float TEXTURE_WIDTH = 1.0f / TEXTURE_ROWS;

	private static final List<Item> REGISTERED_ITEMS_ORDERED_MUTABLE = new ArrayList<>();
	private static final Map<Identifier, Item> REGISTERED_ITEMS_MUTABLE = new HashMap<>();
	public static final List<Item> REGISTERED_ITEMS_ORDERED = Collections.unmodifiableList(REGISTERED_ITEMS_ORDERED_MUTABLE);
	public static final Map<Identifier, Item> REGISTERED_ITEMS = Collections.unmodifiableMap(REGISTERED_ITEMS_MUTABLE);

    private static final List<Item> REGISTERED_BLOCK_ITEMS_ORDERED_MUTABLE = new ArrayList<>();
	private static final Map<Identifier, Item> REGISTERED_BLOCK_ITEMS_MUTABLE = new HashMap<>();
	public static final List<Item> REGISTERED_BLOCK_ITEMS_ORDERED = Collections.unmodifiableList(REGISTERED_BLOCK_ITEMS_ORDERED_MUTABLE);
	public static final Map<Identifier, Item> REGISTERED_BLOCK_ITEMS = Collections.unmodifiableMap(REGISTERED_BLOCK_ITEMS_MUTABLE);

    private static final List<Item> REGISTERED_SLAB_ITEMS_ORDERED_MUTABLE = new ArrayList<>();
	private static final Map<Identifier, Item> REGISTERED_SLAB_ITEMS_MUTABLE = new HashMap<>();
	public static final List<Item> REGISTERED_SLAB_ITEMS_ORDERED = Collections.unmodifiableList(REGISTERED_SLAB_ITEMS_ORDERED_MUTABLE);
	public static final Map<Identifier, Item> REGISTERED_SLAB_ITEMS = Collections.unmodifiableMap(REGISTERED_SLAB_ITEMS_MUTABLE);

    private static final List<Item> REGISTERED_ITEMS_ONLY_ORDERED_MUTABLE = new ArrayList<>();
	private static final Map<Identifier, Item> REGISTERED_ITEMS_ONLY_MUTABLE = new HashMap<>();
	public static final List<Item> REGISTERED_ITEMS_ONLY_ORDERED = Collections.unmodifiableList(REGISTERED_ITEMS_ONLY_ORDERED_MUTABLE);
	public static final Map<Identifier, Item> REGISTERED_ITEMS_ONLY = Collections.unmodifiableMap(REGISTERED_ITEMS_ONLY_MUTABLE);

    public static final Item STICK = new Item("stick").withTex(0, 0);
    public static final Item COAL = new Item("coal").withTex(0, 1);
    public static final Item IRON = new Item("iron").withTex(1, 1);
    public static final Item GOLD = new Item("gold").withTex(2, 1);
    public static final Item WOODEN_PICKAXE = new Item("wooden_pickaxe").withTex(0, 2); 
    public static final Item IRON_PICKAXE = new Item("iron_pickaxe").withTex(1, 2); 
    public static final Item GOLD_PICKAXE = new Item("gold_pickaxe").withTex(2, 2); 
    public static final Item[] BLOCK_ITEMS = IntStream.range(0, Block.REGISTERED_BLOCKS_ORDERED.size())
			.mapToObj(i -> new Item(Block.REGISTERED_BLOCKS_ORDERED.get(i).getId().name + "_item", true).withTex(Block.REGISTERED_BLOCKS_ORDERED.get(i).getTexture().get(Direction.NORTH).x,Block.REGISTERED_BLOCKS_ORDERED.get(i).getTexture().get(Direction.NORTH).y))
			.toArray(Item[]::new);

    public final Identifier id;
    protected ItemTexture tex;
    public boolean blockItem;

    public Item(String id) {
        this(new Identifier(id), false);
    }
    
    public Item(String id, boolean blockItem) {
        this(new Identifier(id), blockItem);
    }

    public Item(Identifier id, boolean blockItem) {
		if (REGISTERED_ITEMS.containsKey(id)) {
			throw new IllegalArgumentException("Item \"" + id + "\" already exists");
		}

		this.id = id;
        this.blockItem = blockItem;
		REGISTERED_ITEMS_ORDERED_MUTABLE.add(this);
		REGISTERED_ITEMS_MUTABLE.put(id, this);
        if (blockItem) {
            REGISTERED_BLOCK_ITEMS_ORDERED_MUTABLE.add(this);
            REGISTERED_BLOCK_ITEMS_MUTABLE.put(id, this);
            if (itemToBlock(this).type == BlockType.slab) {
                REGISTERED_SLAB_ITEMS_ORDERED_MUTABLE.add(this);
                REGISTERED_SLAB_ITEMS_MUTABLE.put(id, this);
            }
        } else {
            REGISTERED_ITEMS_ONLY_ORDERED_MUTABLE.add(this);
            REGISTERED_ITEMS_ONLY_MUTABLE.put(id, this);
  
        }
	}

    protected Item withTex(int x, int y) {
        this.tex = new ItemTexture(x, y);
        return this;
    } 

    public ItemTexture getTex() {
        return tex;
    }

    public static Item fromId(Identifier id) {
		if (REGISTERED_ITEMS_MUTABLE.containsKey(id)) {
			return REGISTERED_ITEMS_MUTABLE.get(id);
		}
		return null;
	}


    public static Item blockToItem(Block block) {
        return fromId(new Identifier(block.getId().name + "_item"));
    }

    public static Block itemToBlock(Item item) {
        return Block.fromId(new Identifier(item.id.name.replace("_item", "")));
    }

}
