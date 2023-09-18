package io.bluestaggo.voxelthing.gui.screen;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.assets.Texture;
import io.bluestaggo.voxelthing.gui.OptionsMenu;
import io.bluestaggo.voxelthing.gui.control.GuiControl;
import io.bluestaggo.voxelthing.gui.control.LabeledButton;
import io.bluestaggo.voxelthing.renderer.GLState;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;

public class PauseMenu extends GuiScreen {

	private final GuiControl saveQuitButton;
    private final GuiControl optionsButton;
    private final GuiControl returnButton;

    public PauseMenu(Game game) {
        super(game);
        saveQuitButton = addControl(new LabeledButton(this)
				.withText("Save And Quit")
				.at(-50, 32)
				.size(100.0f, 20.0f)
				.alignedAt(0.5f, 0.5f)
		);
        optionsButton = addControl(new LabeledButton(this)
            .withText("Options")
            .at(-50, 0)
            .size(100.0f, 20.0f)
            .alignedAt(0.5f, 0.5f)
        );
        returnButton = addControl(new LabeledButton(this)
            .withText("Return to Game")
            .at(-50, -32)
            .size(100.0f, 20.0f)
            .alignedAt(0.5f, 0.5f)
        );
    }

    @Override
	public void draw() {
		super.draw();

		MainRenderer r = game.renderer;

		try (var state = new GLState()) {
			Texture.stop();
			state.enable(GL_BLEND);
			r.draw2D.drawQuad(Quad.shared()
					.at(0, 0)
					.size(r.screen.getWidth(), r.screen.getHeight())
					.withColor(0.0f, 0.0f, 0.0f, 0.8f)
			);

			String title = "Game Isn't Stopped Btw";
			r.fonts.outlined.print(title, (r.screen.getWidth() - r.fonts.outlined.getStringLength(title)) / 2.0f, 20);
		}
        saveQuitButton.draw();
        returnButton.draw();
        optionsButton.draw();
    }

    @Override
	protected void onKeyPressed(int key) {
		if (key == GLFW_KEY_ESCAPE) {
			game.closeGui();
		}
	}

    @Override
	public void onControlClicked(GuiControl control, int button) {
		if (control == returnButton) {
            game.closeGui();
        }
        if (control == saveQuitButton) {
            game.exitWorld();
            game.world = null;
            game.openGui(new MainMenu(game));
        }
        if (control == optionsButton) {
            game.openGui(new OptionsMenu(game));
        }
	}
    
}
