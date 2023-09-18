package io.bluestaggo.voxelthing.world.storage;

import io.bluestaggo.pds.CompoundItem;

public interface ISaveHandler {
	CompoundItem loadData(String type);
	void saveData(String type, CompoundItem data);
	CompoundItem loadChunkData(int x, int y, int z);
	void saveChunkData(int x, int y, int z, CompoundItem data);
}
