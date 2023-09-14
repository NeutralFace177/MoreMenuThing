package io.bluestaggo.voxelthing.gui.control;

import io.bluestaggo.voxelthing.gui.GuiScreen;
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
		if (enabled) {
			MainRenderer r = screen.game.renderer;
			float sx = getScaledX();
			float sy = getScaledY();

			r.draw2D.drawQuad(Quad.shared()
					.at(sx, sy)
					.size(width, height)
					.withColor(0.25f, 0.25f, 0.25f)
			);
			r.draw2D.drawQuad(Quad.shared()
					.at(sx + 1.0f, sy + 1.0f)
					.size(width - 2.0f, height - 2.0f)
					.withColor(0.5f, 0.5f, 0.5f)
			);
			r.fonts.shadowed.printCentered(
					text,
					sx + width / 2.0f,
					sy + (height - r.fonts.normal.lineHeight) / 2.0f
			);
		}
	
	}
}
