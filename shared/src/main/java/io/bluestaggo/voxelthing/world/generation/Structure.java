package io.bluestaggo.voxelthing.world.generation;

import io.bluestaggo.voxelthing.world.block.Block;

public class Structure {
    public final Block block;
    public final int x;
    public final int y;
    public final int z;

    public Structure(Block block, int x, int y, int z) {
        this.block = block;
        this.x =  x;
        this.y = y;
        this.z = z;
    }
}
