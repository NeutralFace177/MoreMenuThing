package io.bluestaggo.pds;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CompoundItem extends StructureItem {
	public final Map<String, StructureItem> map;

	public CompoundItem() {
		this(new HashMap<>());
	}

	public CompoundItem(Map<String, StructureItem> list) {
		this.map = list;
	}

	@Override
	public Map<String, StructureItem> getMap() {
		return map;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(map.size() + " items:");
		for (var item : map.entrySet()) {
			builder.append("\n").append(item.getKey()).append(": ").append(item.getValue());
		}
		builder.append("\nend");
		return builder.toString();
	}

	@Override
	protected void read(DataInputStream stream) throws IOException {
		int length = stream.readInt();
		for (int i = 0; i < length; i++) {
			map.put(readString(stream), readItem(stream));
		}
	}

	@Override
	protected void write(DataOutputStream stream) throws IOException {
		stream.writeInt(map.size());
		for (var item : map.entrySet()) {
			writeString(item.getKey(), stream);
			item.getValue().writeItem(stream);
		}
	}

	public String getString(String key) {
		return map.get(key).getString();
	}

	public byte getByte(String key) {
		return map.get(key).getByte();
	}

	public short getShort(String key) {
		return map.get(key).getShort();
	}

	public int getInt(String key) {
		return map.get(key).getInt();
	}

	public int getUnsignedByte(String key) {
		return map.get(key).getUnsignedByte();
	}

	public int getUnsignedShort(String key) {
		return map.get(key).getUnsignedShort();
	}

	public long getLong(String key) {
		return map.get(key).getLong();
	}

	public long getUnsignedInt(String key) {
		return map.get(key).getUnsignedInt();
	}

	public boolean getBoolean(String key) {
		return map.get(key).getBoolean();
	}

	public float getFloat(String key) {
		return map.get(key).getFloat();
	}

	public double getDouble(String key) {
		return map.get(key).getDouble();
	}

	public byte[] getByteArray(String key) {
		return map.get(key).getByteArray();
	}

	public short[] getShortArray(String key) {
		return map.get(key).getShortArray();
	}

	public int[] getIntArray(String key) {
		return map.get(key).getIntArray();
	}

	public long[] getLongArray(String key) {
		return map.get(key).getLongArray();
	}

	public StructureItem getItem(String key) {
		return map.get(key);
	}

	public void setString(String key, String value) {
		map.put(key, new StringItem(value));
	}

	public void setByte(String key, byte value) {
		map.put(key, new ByteItem(value));
	}

	public void setShort(String key, short value) {
		map.put(key, new ShortItem(value));
	}

	public void setInt(String key, int value) {
		map.put(key, new IntItem(value));
	}

	public void setUnsignedByte(String key, int value) {
		map.put(key, new ByteItem(value));
	}

	public void setUnsignedShort(String key, int value) {
		map.put(key, new ShortItem(value));
	}

	public void setLong(String key, long value) {
		map.put(key, new LongItem(value));
	}

	public void setUnsignedInt(String key, long value) {
		map.put(key, new IntItem((int) value));
	}

	public void setBoolean(String key, boolean value) {
		map.put(key, new ByteItem(value));
	}

	public void setFloat(String key, float value) {
		map.put(key, new FloatItem(value));
	}

	public void setDouble(String key, double value) {
		map.put(key, new DoubleItem(value));
	}

	public void setByteArray(String key, byte[] value) {
		map.put(key, new ByteArrayItem(value));
	}

	public void setShortArray(String key, short[] value) {
		map.put(key, new ShortArrayItem(value));
	}

	public void setIntArray(String key, int[] value) {
		map.put(key, new IntArrayItem(value));
	}

	public void setLongArray(String key, long[] value) {
		map.put(key, new LongArrayItem(value));
	}

	public void setItem(String key, StructureItem value) {
		map.put(key, value);
	}

	public boolean containsKey(String key) {
		return map.containsKey(key);
	}
}
