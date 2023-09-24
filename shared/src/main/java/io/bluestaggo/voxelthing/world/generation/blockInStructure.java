package io.bluestaggo.voxelthing.world.generation;

import io.bluestaggo.voxelthing.world.block.Block;

public class blockInStructure {
    public final Block block;
    public final int x;
    public final int y;
    public final int z;

    public blockInStructure(Block block, int x, int y, int z) {
        this.block = block;
        this.x =  x;
        this.y = y;
        this.z = z;
    }
}
