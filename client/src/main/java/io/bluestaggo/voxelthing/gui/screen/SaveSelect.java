package io.bluestaggo.voxelthing.gui.screen;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.gui.control.GuiControl;
import io.bluestaggo.voxelthing.gui.control.LabeledButton;
import io.bluestaggo.voxelthing.gui.control.ScrollContainer;
import io.bluestaggo.voxelthing.gui.control.WorldButton;
import io.bluestaggo.voxelthing.renderer.GLState;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;
import io.bluestaggo.voxelthing.world.storage.FolderSaveHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.GL_BLEND;

public class SaveSelect extends GuiScreen {
	private final GuiControl newWorldButton;
	private final ScrollContainer worldContainer;

	public SaveSelect(Game game) {
		super(game);

		newWorldButton = addControl(new LabeledButton(this)
				.withText("New World")
				.at(-50.0f, -25.0f)
				.size(100.0f, 20.0f)
				.alignedAt(0.5f, 1.0f)
		);
		worldContainer = (ScrollContainer) addControl(new ScrollContainer(this)
				.at(0.0f, 30.0f)
				.size(0.0f, -60.0f)
				.alignedSize(1.0f, 1.0f)
		);

		Path worldsPath = game.saveDir.resolve("worlds");
		try (Stream<Path> worlds = Files.list(worldsPath)) {
			worlds.filter(Files::isDirectory)
					.map(p -> {
						try {
							return new FolderSaveHandler(p);
						} catch (IOException e) {
							return null;
						}
					})
					.filter(Objects::nonNull)
					.map(s -> new WorldButton(this, s)
							.at(-75.0f, 0.0f)
							.size(150.0f, 20.0f)
							.alignedAt(0.5f, 0.0f)
					)
					.forEach(worldContainer::addControl);
		} catch (IOException e) {
			System.out.println("Failed to load worlds!");
			e.printStackTrace();
			worldContainer.addControl(new LabeledButton(this)
					.withText("Failed to load worlds!")
					.at(-100.0f, 0.0f)
					.size(200.0f, 20.0f)
					.alignedAt(0.5f, 0.0f)
			);
		}
	}

	@Override
	public void tick() {
	}

	@Override
	public void draw() {
		MainRenderer r = game.renderer;

		try (var state = new GLState()) {
			state.enable(GL_BLEND);
			r.draw2D.drawQuad(Quad.shared()
					.at(0.0f, 30.0f)
					.size(r.screen.getWidth(), r.screen.getHeight() - 60.0f)
					.withColor(0.0f, 0.0f, 0.0f, 0.5f));

			r.fonts.outlined.printCentered("SELECT WORLD", r.screen.getWidth() / 2.0f, 10.0f);
		}

		super.draw();
	}

	@Override
	public void onControlClicked(GuiControl control, int button) {
		if (control instanceof WorldButton world) {
			game.startWorld(world.saveHandler);
		} else if (control == newWorldButton) {
			game.openGui(new CreateWorld(game, this));
		}
	}

	@Override
	protected void onMouseScrolled(double scroll) {
		worldContainer.scroll(scroll * -10.0);
	}
}
