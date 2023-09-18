package io.bluestaggo.voxelthing.world.storage;

import io.bluestaggo.pds.CompoundItem;

public class EmptySaveHandler implements ISaveHandler {
	@Override
	public CompoundItem loadData(String type) {
		return null;
	}

	@Override
	public void saveData(String type, CompoundItem data) {
	}

	@Override
	public CompoundItem loadChunkData(int x, int y, int z) {
		return null;
	}

	@Override
	public void saveChunkData(int x, int y, int z, CompoundItem data) {
	}
}
