package io.bluestaggo.voxelthing.gui.control;

import io.bluestaggo.voxelthing.gui.screen.GuiScreen;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;

public class LabeledButton extends GuiControl {
	public String text;
	public boolean disabled;

	public LabeledButton(GuiScreen screen) {
		super(screen);
	}

	public LabeledButton withText(String text) {
		this.text = text;
		return this;
	}

	public LabeledButton disable() {
		this.disabled = true;
		return this;
	}

	public LabeledButton enable() {
		this.disabled = false;
		return this;
	}

	public LabeledButton enableIf(boolean enabled) {
		this.disabled = !enabled;
		return this;
	}

	@Override
	public void draw() {
		MainRenderer r = screen.game.renderer;
		float sx = getScaledX();
		float sy = getScaledY();
		float sw = getScaledWidth();
		float sh = getScaledHeight();

		r.draw2D.drawQuad(Quad.shared()
				.at(sx, sy)
				.size(sw, sh)
				.withColor(0.25f, 0.25f, 0.25f)
		);
		r.draw2D.drawQuad(Quad.shared()
				.at(sx + 1.0f, sy + 1.0f)
				.size(sw - 2.0f, sh - 2.0f)
				.withColor(0.5f, 0.5f, 0.5f)
		);
		r.fonts.shadowed.printCentered(
				text,
				sx + sw / 2.0f,
				sy + (sh - r.fonts.normal.lineHeight) / 2.0f
		);
	}
}
