package io.bluestaggo.voxelthing.gui.control;

import io.bluestaggo.voxelthing.gui.screen.GuiScreen;
import io.bluestaggo.voxelthing.renderer.GLState;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;

public class ScrollContainer extends GuiContainer {
	private float contentHeight;
	private float scrollAmount;

	public ScrollContainer(GuiScreen screen) {
		super(screen);
	}

	@Override
	public GuiControl addControl(GuiControl control) {
		super.addControl(control);
		control.y = contentHeight;
		control.alignY = 0.0f;
		control.alignHeight = 0.0f;
		contentHeight += control.height;
		return control;
	}

	public void scroll(double amount) {
		if (canScroll()) {
			scrollAmount += amount;
		}

		float max = Math.max(contentHeight - getScaledHeight(), 0);

		if (scrollAmount > max) {
			scrollAmount = max;
		} else if (scrollAmount < 0.0f) {
			scrollAmount = 0.0f;
		}
	}

	public boolean canScroll() {
		return contentHeight > getScaledHeight();
	}

	@Override
	public void draw() {
		scroll(0.0);

		MainRenderer r = screen.game.renderer;

		try (var state = new GLState()) {
			state.scissor(
					(int) (r.screen.fixScaling(getScaledX()) * r.screen.getScale()),
					(int) (r.screen.fixScaling(getScaledY()) * r.screen.getScale()),
					(int) ((r.screen.fixScaling(getScaledX() + getScaledWidth()) - getScaledX()) * r.screen.getScale()),
					(int) ((r.screen.fixScaling(getScaledY() + getScaledHeight()) - getScaledY()) * r.screen.getScale())
			);

			for (GuiControl control : controls) {
				control.y -= scrollAmount;
				if (control.y + control.height >= 0 && control.y < getScaledHeight()) {
					control.draw();
				}
				control.y += scrollAmount;
			}
		}

		float sx = getScaledX();
		float sy = getScaledY();
		float sw = getScaledWidth();
		float sh = getScaledHeight();

		r.draw2D.drawQuad(Quad.shared()
				.at(sx + sw - 4.0f, sy)
				.size(4.0f, sh)
				.withColor(0.25f, 0.25f, 0.25f));

		if (canScroll()) {
			float range = 1.0f / contentHeight * getScaledHeight();
			r.draw2D.drawQuad(Quad.shared()
					.at(sx + sw - 4.0f, sy + scrollAmount * range)
					.size(4.0f, getScaledHeight() * range)
					.withColor(1.0f, 1.0f, 1.0f));
		}
	}

	@Override
	public void checkMouseClicked(int button, int mx, int my) {
		if (!intersects(mx, my)) {
			return;
		}

		super.checkMouseClicked(button, mx, my - (int) scrollAmount);
	}
}
