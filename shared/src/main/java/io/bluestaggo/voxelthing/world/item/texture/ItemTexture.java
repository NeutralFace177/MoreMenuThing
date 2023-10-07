package io.bluestaggo.voxelthing.world.item.texture;

import org.joml.Vector2i;

import io.bluestaggo.voxelthing.world.item.Item;

public class ItemTexture {
    protected final Vector2i vec;

    public ItemTexture(int x, int y) {
		vec = new Vector2i(x, y);
	}

    public Vector2i get() {
        return vec;
    }

}
