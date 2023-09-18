package io.bluestaggo.voxelthing.world;

import io.bluestaggo.pds.CompoundItem;
import io.bluestaggo.voxelthing.world.generation.WorldType;

import java.util.Random;

public class WorldInfo {
	public String name = "world";
	public long seed = new Random().nextLong();
	public WorldType type;

	public void deserialize(CompoundItem data) {
		
		try {
			name = data.getString("name");
			seed = data.getLong("seed");
			type = WorldType.intToWorldType(data.getInt("worldType"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	public CompoundItem serialize() {
		var data = new CompoundItem();
		data.setString("name", name);
		data.setLong("seed", seed);
		data.setInt("worldType", WorldType.typeToInt(type));
		return data;
	}
}

