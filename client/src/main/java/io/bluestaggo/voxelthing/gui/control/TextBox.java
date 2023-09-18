package io.bluestaggo.voxelthing.gui.control;

import io.bluestaggo.voxelthing.gui.screen.GuiScreen;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;
import io.bluestaggo.voxelthing.window.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;

public class TextBox extends GuiFocusable {
	public String text = "";

	public TextBox(GuiScreen screen) {
		super(screen);
	}

	@Override
	public void onKeyPressed(int key) {
		if (key == GLFW_KEY_BACKSPACE && text.length() > 0) {
			text = text.substring(0, text.length() - 1);
		}
	}

	@Override
	public void onCharacterTyped(char c) {
		text += c;
	}

	@Override
	public void draw() {
		MainRenderer r = screen.game.renderer;
		float sx = getScaledX();
		float sy = getScaledY();
		float sw = getScaledWidth();
		float sh = getScaledHeight();

		float hr = 0.25f;
		float hg = 0.25f;
		float hb = 0.25f;
		String printText = text;

		if (screen.isFocused(this)) {
			hr = 1.0f;
			hg = 1.0f;
			hb = 1.0f;

			if (Window.getTimeElapsed() % 0.4 < 0.2) {
				printText += "|";
			}
		}

		r.draw2D.drawQuad(Quad.shared()
				.at(sx, sy)
				.size(sw, sh)
				.withColor(hr, hg, hb)
		);
		r.draw2D.drawQuad(Quad.shared()
				.at(sx + 1.0f, sy + 1.0f)
				.size(sw - 2.0f, sh - 2.0f)
				.withColor(0.0f, 0.0f, 0.0f)
		);
		r.fonts.shadowed.print(
				printText,
				sx + 5.0f,
				sy + (sh - r.fonts.normal.lineHeight) / 2.0f
		);
	}
}
