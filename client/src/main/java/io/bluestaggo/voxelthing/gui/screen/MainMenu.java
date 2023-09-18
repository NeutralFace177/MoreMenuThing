package io.bluestaggo.voxelthing.gui.screen;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.gui.control.GuiControl;
import io.bluestaggo.voxelthing.gui.control.LabeledButton;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;
import io.bluestaggo.voxelthing.window.Window;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;

public class MainMenu extends GuiScreen {
	private static final List<String> SPLASHES;
	private static final Random random = new Random();
	private final String splash;

	private final GuiControl playButton;

	static {
		List<String> splashes = List.of("No splashes?");

		try (InputStream istream = MainMenu.class.getResourceAsStream("/splashes.txt")) {
			if (istream != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
				splashes = reader.lines()
						.filter(s -> !s.isEmpty())
						.toList();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		SPLASHES = splashes;
	}

	public MainMenu(Game game) {
		super(game);
		splash = SPLASHES.get(random.nextInt(SPLASHES.size()));

		playButton = addControl(new LabeledButton(this)
				.withText("Singleplayer")
				.at(-50.0f, 0.0f)
				.size(100.0f, 20.0f)
				.alignedAt(0.5f, 0.5f)
		);
	}

	@Override
	public void draw() {
		MainRenderer r = game.renderer;

		r.draw2D.drawQuad(Quad.shared()
				.at(0, 0)
				.size(r.screen.getWidth(), r.screen.getHeight())
				.withTexture(r.textures.getTexture("/assets/gui/titlebg.png")));

		float hover = (float) Math.sin(Window.getTimeElapsed() * Math.PI);
		r.fonts.outlined.printCentered("VOXEL THING",
				r.screen.getWidth() / 2.0f,
				20.0f + hover * 2.0f,
				0.0f, 1.0f, 1.0f, 4.0f);
		r.fonts.shadowed.printCentered("§cffff55" + splash,
				r.screen.getWidth() / 2.0f,
				60.0f + hover * 4.0f);

		super.draw();
	}

	@Override
	protected void onKeyPressed(int key) {
		if (key == GLFW_KEY_TAB) {
			game.openGui(new MainMenu(game));
		}
	}

	@Override
	public void onControlClicked(GuiControl control, int button) {
		if (control == playButton) {
			game.openGui(new SaveSelect(game));
		}
	}
}
