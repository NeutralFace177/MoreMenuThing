package io.bluestaggo.voxelthing.gui.control;

import io.bluestaggo.voxelthing.gui.GuiScreen;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;

public class GuiControl {
	protected final GuiScreen screen;
	public float x;
	public float y;
	public float width;
	public float height;
	public float alignX;
	public float alignY;
	public boolean enabled = true;

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

	public float getScaledX() {
		return x + screen.game.renderer.screen.getWidth() * alignX;
	}

	public float getScaledY() {
		return y + screen.game.renderer.screen.getHeight() * alignY;
	}

	public void onClick(int button) {
		screen.onControlClicked(this, button);
	}

	public void draw() {
		if (enabled) {
			MainRenderer r = screen.game.renderer;
			r.draw2D.drawQuad(Quad.shared()
				.at(getScaledX(), getScaledY())
				.size(width, height)
		);
		}
		
	}

	public boolean intersects(int x, int y) {
		float sx = getScaledX();
		float sy = getScaledY();

		return x >= sx && x < sx + width
				&& y >= sy && y < sy + height;
	}
}
