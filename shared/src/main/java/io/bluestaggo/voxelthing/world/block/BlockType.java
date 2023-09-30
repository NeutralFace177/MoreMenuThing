package io.bluestaggo.voxelthing.world.block;

import org.joml.Vector3f;

public enum BlockType {
    Normal(new Vector3f(1, 1, 1)),
 // not implemented   Stair(new Vector3f(1, 1, 1)),
    slab(new Vector3f(1, 0.5f, 1)),
    verticalSlab(new Vector3f(0.5f, 1, 0.5f));
 // nit implemented   plant;

    private Vector3f shape;
    BlockType(Vector3f shape) {
        this.shape = shape;
    }

    public Vector3f shape() {
        return shape;
    }
}
