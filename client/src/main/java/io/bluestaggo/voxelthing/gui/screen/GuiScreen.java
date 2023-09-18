package io.bluestaggo.voxelthing.gui.screen;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.gui.control.GuiControl;
import io.bluestaggo.voxelthing.gui.control.GuiFocusable;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiScreen {
	public final Game game;
	public final GuiScreen parent;

	private final List<GuiControl> controls = new ArrayList<>();
	private GuiFocusable focusedControl;

	public GuiScreen(Game game) {
		this(game, null);
	}

	public GuiScreen(Game game, GuiScreen parent) {
		this.game = game;
		this.parent = parent;
	}

	public GuiControl addControl(GuiControl control) {
		controls.add(control);
		return control;
	}

	public void draw() {
		for (GuiControl control : controls) {
			control.draw();
		}
	}

	public void tick() {
	}

	public void handleInput() {
		for (int key : game.window.getKeysJustPressed()) {
			onKeyPressed(key);
			if (focusedControl != null) {
				focusedControl.onKeyPressed(key);
			}
		}

		for (char c : game.window.getCharactersPressed()) {
			onCharacterTyped(c);
			if (focusedControl != null) {
				focusedControl.onCharacterTyped(c);
			}
		}

		for (int mouse : game.window.getMouseButtonsJustPressed()) {
			onMouseClicked(mouse, game.renderer.screen.getMouseX(), game.renderer.screen.getMouseY());
		}

		if (game.window.getScrollY() != 0.0) {
			onMouseScrolled(game.window.getScrollY());
		}
	}

	protected void onKeyPressed(int key) {
	}

	protected void onCharacterTyped(char c) {
	}

	protected void onMouseClicked(int button, int mx, int my) {
		focusedControl = null;
		for (GuiControl control : controls) {
			control.checkMouseClicked(button, mx, my);
			if (control instanceof GuiFocusable focusable && control.intersects(mx, my)) {
				focusedControl = focusable;
			}
		}
	}

	protected void onMouseScrolled(double scroll) {
	}

	public void onControlClicked(GuiControl control, int button) {
	}

	public void focusControl(GuiFocusable focusable) {
		this.focusedControl = focusable;
	}

	public boolean isFocused(GuiFocusable focusable) {
		return focusable == focusedControl;
	}
}
