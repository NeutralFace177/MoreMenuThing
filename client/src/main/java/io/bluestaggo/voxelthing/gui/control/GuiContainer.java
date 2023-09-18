package io.bluestaggo.voxelthing.gui.control;

import io.bluestaggo.voxelthing.gui.screen.GuiScreen;

import java.util.ArrayList;
import java.util.List;

public class GuiContainer extends GuiControl {
	protected final List<GuiControl> controls = new ArrayList<>();

	public GuiContainer(GuiScreen screen) {
		super(screen);
	}

	public GuiControl addControl(GuiControl control) {
		controls.add(control);
		control.container = this;
		return control;
	}

	@Override
	public void draw() {
		for (GuiControl control : controls) {
			control.draw();
		}
	}

	@Override
	public void checkMouseClicked(int button, int mx, int my) {
		for (GuiControl control : controls) {
			control.checkMouseClicked(button, mx, my);
		}
	}
}
