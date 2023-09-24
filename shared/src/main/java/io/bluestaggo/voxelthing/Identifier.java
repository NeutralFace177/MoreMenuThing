package io.bluestaggo.voxelthing;

import io.bluestaggo.pds.CompoundItem;
import io.bluestaggo.pds.StringItem;
import io.bluestaggo.pds.StructureItem;

import java.util.Objects;

public class Identifier {
	public final String namespace;
	public final String name;
	public final String fullName;

	public Identifier(String name) {
		this("vt", name);
	}

	public Identifier(String namespace, String name) {
		this.namespace = namespace;
		this.name = name;
		this.fullName = namespace + ":" + name;
	}

	public Identifier(String name, Object o) {
		this.name = name;
		this.namespace = "vt";
		this.fullName = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Identifier that = (Identifier) o;
		return Objects.equals(namespace, that.namespace) && Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(namespace, name);
	}

	@Override
	public String toString() {
		return fullName;
	}

	public StructureItem serialize() {
		var item = new CompoundItem();
		item.map.put("namespace", new StringItem(namespace));
		item.map.put("name", new StringItem(name));
		return item;
	}

	public static Identifier deserialize(StructureItem item) {
		String namespace = item.getMap().get("namespace").getString();
		String name = item.getMap().get("name").getString();
		return new Identifier(namespace, name);
	}
}
