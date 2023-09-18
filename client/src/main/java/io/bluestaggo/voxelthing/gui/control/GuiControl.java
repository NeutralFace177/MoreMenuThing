package io.bluestaggo.voxelthing.gui.control;

import io.bluestaggo.voxelthing.gui.screen.GuiScreen;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;

public class GuiControl {
	protected final GuiScreen screen;
	protected GuiContainer container;
	public float x;
	public float y;
	public float width;
	public float height;
	public float alignX;
	public float alignY;
	public float alignWidth;
	public float alignHeight;

	public GuiControl(GuiScreen screen) {
		this.screen = screen;
	}

	public GuiControl at(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public GuiControl size(float width, float height) {
		this.width = width;
		this.height = height;
		return this;
	}

	public GuiControl alignedAt(float alignX, float alignY) {
		this.alignX = alignX;
		this.alignY = alignY;
		return this;
	}

	public GuiControl alignedSize(float alignWidth, float alignHeight) {
		this.alignWidth = alignWidth;
		this.alignHeight = alignHeight;
		return this;
	}

	public float getScaledX() {
		float offsetX = container != null ? container.getScaledX() : 0.0f;
		float parentWidth = container != null ? container.getScaledWidth() : screen.game.renderer.screen.getWidth();
		return x + offsetX + parentWidth * alignX;
	}

	public float getScaledY() {
		float offsetY = container != null ? container.getScaledY() : 0.0f;
		float parentHeight = container != null ? container.getScaledHeight() : screen.game.renderer.screen.getHeight();
		return y + offsetY + parentHeight * alignY;
	}

	public float getScaledWidth() {
		float parentWidth = container != null ? container.width : screen.game.renderer.screen.getWidth();
		return width + parentWidth * alignWidth;
	}

	public float getScaledHeight() {
		float parentHeight = container != null ? container.height : screen.game.renderer.screen.getHeight();
		return height + parentHeight * alignHeight;
	}

	public void onClick(int button) {
		screen.onControlClicked(this, button);
	}

	public void draw() {
		MainRenderer r = screen.game.renderer;
		r.draw2D.drawQuad(Quad.shared()
				.at(getScaledX(), getScaledY())
				.size(getScaledWidth(), getScaledHeight())
		);
	}

	public boolean intersects(int x, int y) {
		float sx = getScaledX();
		float sy = getScaledY();
		float sw = getScaledWidth();
		float sh = getScaledHeight();

		return x >= sx && x < sx + sw
				&& y >= sy && y < sy + sh;
	}

	public void checkMouseClicked(int button, int mx, int my) {
		if (intersects(mx, my)) {
			onClick(button);
		}
	}

	public boolean isInContainer(GuiContainer container) {
		return this.container == container;
	}
}
