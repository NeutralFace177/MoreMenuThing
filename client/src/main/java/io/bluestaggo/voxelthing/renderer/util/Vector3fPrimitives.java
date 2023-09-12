package io.bluestaggo.voxelthing.renderer.util;

import io.bluestaggo.voxelthing.renderer.vertices.Bindings;
import io.bluestaggo.voxelthing.renderer.vertices.VertexType;

public class Vector3fPrimitives extends Primitives {
	@Override
	protected Bindings newBindings() {
		return new Bindings(VertexType.VECTOR3F);
	}

	@Override
	protected void addPosition(Bindings bindings, float x, float y, float z) {
		bindings.addVertices(x, y, z);
	}

	@Override
	protected void addColor(Bindings bindings, float r, float g, float b) {
	}

	@Override
	protected void addUv(Bindings bindings, float u, float v) {
	}
}
