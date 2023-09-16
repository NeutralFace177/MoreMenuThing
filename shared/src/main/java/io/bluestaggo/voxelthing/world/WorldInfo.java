package io.bluestaggo.voxelthing.world;

import io.bluestaggo.pds.CompoundItem;
import io.bluestaggo.pds.IntItem;
import io.bluestaggo.pds.LongItem;
import io.bluestaggo.voxelthing.world.generation.WorldType;

public class WorldInfo {
	public long seed;
	public WorldType type;

	public void deserialize(CompoundItem data) {
		seed = data.map.get("seed").getLong();
		type = type.intTWorldType(data.map.get("worldType").getInt());
	}

	public CompoundItem serialize() {
		var data = new CompoundItem();
		data.map.put("seed", new LongItem(seed));
		data.map.put("worldType", new IntItem(type.typeToInt(type)));
		return data;
	}
}
