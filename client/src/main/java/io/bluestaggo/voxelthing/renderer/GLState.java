package io.bluestaggo.voxelthing.renderer;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL33C.*;

public class GLState implements AutoCloseable {
	private final GLState parent;
	private final List<Integer> enabled = new ArrayList<>();
	private final List<Integer> disabled = new ArrayList<>();
	private int depthFunc = 0;

	public GLState() {
		this(null);
	}

	public GLState(GLState parent) {
		this.parent = parent;
	}

	public void enable(int state) {
		if (parent != null && parent.enabled.contains(state)) {
			return;
		}

		if (disabled.contains(state)) {
			disabled.remove((Integer)state);
		} else {
			enabled.add(state);
		}
		glEnable(state);
	}

	public void disable(int state) {
		if (parent != null && parent.disabled.contains(state)) {
			return;
		}

		if (enabled.contains(state)) {
			enabled.remove((Integer)state);
		} else {
			disabled.add(state);
		}
		glDisable(state);
	}

	public void scissor(int x, int y, int width, int height) {
		glScissor(x, y, width, height);
		enable(GL_SCISSOR_TEST);
	}

	@Override
	public void close() {
		for (int state : enabled) {
			glDisable(state);
		}

		for (int state : disabled) {
			glEnable(state);
		}

		if (depthFunc > 0) {
			glDepthFunc(GL_LESS);
		}
	}
}
