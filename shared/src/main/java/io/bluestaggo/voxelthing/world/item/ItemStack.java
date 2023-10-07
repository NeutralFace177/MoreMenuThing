package io.bluestaggo.voxelthing.world.item;

import io.bluestaggo.voxelthing.world.block.Block;

public class ItemStack {
    private int count;
    private Item item;

    public ItemStack(Item item, int count) {
        this.count = count;
        this.item = item;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void changeCount(int count) {
        this.count += count;
        if (this.count <= 0) {
            this.item = null;
            this.count = 0;
        }
    }

    public int getCount() {
        return count;
    }

    public Item getItem() {
        return item;
    }

}
