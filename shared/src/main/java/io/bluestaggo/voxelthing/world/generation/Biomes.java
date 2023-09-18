package io.bluestaggo.voxelthing.world.generation;

import io.bluestaggo.voxelthing.world.block.Block;

public enum Biomes {
    Plains,
    Forest,
    Desert,
    Jungle;

    public static Block BiomeBlock(Biomes biome) {
        if (biome == Plains) {
            return Block.WOOL[3];
        } else if (biome == Forest) {
            return Block.WOOL[8];
        } else if (biome == Desert) {
            return Block.WOOL[4];
        } else if (biome == Jungle) {
            return Block.WOOL[6];
        } else {
            return Block.BRICKS;
        }
    }
    
}
