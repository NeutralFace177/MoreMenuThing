package io.bluestaggo.voxelthing.gui;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.assets.Texture;
import io.bluestaggo.voxelthing.gui.control.GuiControl;
import io.bluestaggo.voxelthing.gui.control.LabeledButton;
import io.bluestaggo.voxelthing.renderer.GLState;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;

public class OptionsMenu extends GuiScreen {

	private final GuiControl goTo69dot420MilAway;
    private final GuiControl crashYourGame;
    private final GuiControl returnButton;

    public OptionsMenu(Game game) {
        super(game);
        //I don't know what options to add so I left some goofy options
        goTo69dot420MilAway = addControl(new LabeledButton(this)
			.withText("Click For Siezure (epilespy warning)")
			.at(-90, 32)
			.size(180.0f, 20.0f)
			.alignedAt(0.5f, 0.5f)
		);
        crashYourGame = addControl(new LabeledButton(this)
            .withText("Crash Your Game")
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

			String title = "Options";
			r.fonts.outlined.print(title, (r.screen.getWidth() - r.fonts.outlined.getStringLength(title)) / 2.0f, 20);
		}
        goTo69dot420MilAway.draw();
        returnButton.draw();
        crashYourGame.draw();
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
        if (control == goTo69dot420MilAway) {
            game.player.posX = 6.9420 * Math.pow(10, 6);
            game.player.posZ = game.player.posX;
            game.player.posY = 200;
        }
        if (control == crashYourGame) {
            game.window.destroy();
        }
	}
    
}
