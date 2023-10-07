package io.bluestaggo.voxelthing.world.inventory;

import io.bluestaggo.voxelthing.world.item.ItemStack;
import io.bluestaggo.voxelthing.world.item.Item;

import org.joml.Vector3i;

import java.util.ArrayList;

import org.joml.Vector2i;

public class Hotbar {

    public static int MAX_ITEM_COUNT = 8;
    public static int ROWS = 1;
    public static int COLUMNS = 9;    
    protected ItemStack[][] inventory = new ItemStack[ROWS][COLUMNS];

    public Hotbar() {

    }

    public void tick() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                try {
                    if (inventory[i][j].getCount() == 0 || inventory[i][j].getItem() == null) {
                        inventory[i][j] = null;
                    }
                } catch (NullPointerException e) {
                    
                }
                
            }
        }
    }

    public ItemStack getItem(int row, int column) {
        return inventory[row][column];
    }

    public void setItem(ItemStack item, int row, int column) {
        inventory[row][column] = item;
    }

    public void changeCount(int row, int column, int count) {
        inventory[row][column].changeCount(count);
    }

    public boolean contains(Item item) {
        return contains(new ItemStack(item, -1));
    }

    //returns true if an item of that type is in inventory
    public boolean contains(ItemStack item) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                try {
                    if (inventory[i][j].getItem() == item.getItem()) {
                    return true;
                }
                } catch (NullPointerException e) {
                    // TODO: handle exception
                }
                
            }
        }
        return false;
    }

    public boolean containsFree(Item item) {
        return containsFree(new ItemStack(item, -1));
    }
    
    //returns true if there is an item of the same type that isnt at full stack
    public boolean containsFree(ItemStack item) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                try {
                    if (inventory[i][j].getItem() == item.getItem() && inventory[i][j].getCount() < MAX_ITEM_COUNT) {
                    return true;
                }
                } catch (NullPointerException e) {

                }

            }
        }
        return false;
    }

    //gets array position of all items of the same type
    public ArrayList<Vector2i> getMatchingContents(ItemStack item) {
        ArrayList<Vector2i> array = new ArrayList<Vector2i>();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                try {
                    if (inventory[i][j].getItem() == item.getItem()) {
                    array.add(new Vector2i(i, j));
                }
                } catch (NullPointerException e) {
                    
                }
                
            }
        }
        return array;
    }

    //gets array position of all items of the same type that aren't a full stack
    public ArrayList<Vector3i> getFreeMatchingContents(ItemStack item) {
        ArrayList<Vector3i> array = new ArrayList<Vector3i>(); 
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                try {
                    if (inventory[i][j].getItem() == item.getItem() && inventory[i][j].getCount() < MAX_ITEM_COUNT) {
                    array.add(new Vector3i(i, j, inventory[i][j].getCount()));
                }
                } catch (NullPointerException e) {
                    
                }
                
            }
        }
        return array;
    }

    //gets array position of all empty slots
    public ArrayList<Vector2i> getFreeSlots() {
         ArrayList<Vector2i> array = new ArrayList<Vector2i>(); 
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (inventory[i][j] == null) {
                    array.add(new Vector2i(i, j));
                }
            }
        }
        return array;
    }

    //boolean returns if item was picked up, or false if it couldnt be picked up into this inventory
    public boolean onPickUp(ItemStack itemStack) {
        ItemStack item = itemStack;
        ArrayList<Vector3i> freeSimilarStacks = getFreeMatchingContents(item);
        ArrayList<Vector2i> freeSpots = getFreeSlots();
        boolean containsItem = containsFree(item);
        int count;
        if (item.getCount() >= MAX_ITEM_COUNT) {
            count = MAX_ITEM_COUNT;
        } else {
            count = item.getCount();
        }
        if (containsItem) {
            for (int i = 0; i < freeSimilarStacks.size(); i++) {
                freeSimilarStacks = getFreeMatchingContents(item);
                if (item.getCount() >= MAX_ITEM_COUNT) {
                    count = MAX_ITEM_COUNT;
                } else {
                    count = item.getCount();
                }
                inventory[freeSimilarStacks.get(i).x][freeSimilarStacks.get(i).y].changeCount(count);
                item.changeCount(count);
                if (item.getCount() <= 0) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < freeSpots.size(); i++) {
                freeSpots = getFreeSlots();
                if (item.getCount() >= MAX_ITEM_COUNT) {
                    count = MAX_ITEM_COUNT;
                } else {
                    count = item.getCount();
                }
                inventory[freeSpots.get(i).x][freeSpots.get(i).y] = new ItemStack(item.getItem(), count);
                item.changeCount(-MAX_ITEM_COUNT);
                if (item.getCount() <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

}
