package io.bluestaggo.voxelthing.renderer.screen;

import io.bluestaggo.voxelthing.window.Window;
import org.joml.Matrix4f;

public class Screen {
	public static final float MAX_DEPTH = 100.0f;
	public static final int VIRTUAL_WIDTH = 320;
	public static final int VIRTUAL_HEIGHT = 180;

	private final Window window;
	private final Matrix4f viewProj = new Matrix4f();

	private boolean resized;
	private float width = -1;
	private float height = -1;
	private float autoScale = 0.0f;
	public float scale = 0.0f;

	public Screen(Window window) {
		this.window = window;
		updateDimensions();
	}

	public void updateDimensions() {
		float newWidth = window.getWidth();
		float newHeight = window.getHeight();
		autoScale = scale;

		if (autoScale <= 0.0f) {
			autoScale = 1.0f;
			while (newWidth / (autoScale + 1) >= VIRTUAL_WIDTH && newHeight / (autoScale + 1) >= VIRTUAL_HEIGHT) {
				autoScale++;
			}
		}

		newWidth /= autoScale;
		newHeight /= autoScale;

		if (width != newWidth || height != newHeight) {
			resized = true;
			width = newWidth;
			height = newHeight;
			viewProj.identity().ortho(0.0f, width, height, 0.0f, 0.0f, MAX_DEPTH);
		}
	}

	public Matrix4f getViewProj() {
		return viewProj;
	}

	public int getWidth() {
		return (int) Math.ceil(width);
	}

	public int getHeight() {
		return (int) Math.ceil(height);
	}

	public float getScale() {
		return autoScale;
	}

	public int getMouseX() {
		return (int) (window.getMouseX() / autoScale);
	}

	public int getMouseY() {
		return (int) (window.getMouseY() / autoScale);
	}

	public float fixScaling(float x) {
		return (int) (x * autoScale) / autoScale;
	}

	public boolean hasResized() {
		boolean wasResized = resized;
		resized = false;
		return wasResized;
	}
}
