package io.bluestaggo.voxelthing.gui.screen;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.renderer.GLState;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.glfw.GLFW.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class InfoScreen extends GuiScreen {
    private float scrollAmount;
    private static final List<String> text;
    static {
        List<String> lines = List.of("No Info Or Patch Notes :(");

        try (InputStream istream = MainMenu.class.getResourceAsStream("/info.txt")) {
			if (istream != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
				lines = reader.lines()
                        .toList();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        text = lines;
    }

    public InfoScreen(Game game) {
        super(game);
    }

    @Override
    protected void onMouseScrolled(double scroll) {
        scrollAmount += scroll;
    }

    @Override 
    protected void onKeyPressed(int key) {
        if (key == GLFW_KEY_ESCAPE) {
            game.openGui(parent);
        }
    }

    @Override
    public void draw() {
        MainRenderer r =  game.renderer;
        r.draw2D.drawQuad(Quad.shared()
				.at(0, 0)
				.size(r.screen.getWidth(), r.screen.getHeight())
				.withTexture(r.textures.getTexture("/assets/gui/titlebg.png")));
       try (var state = new GLState()) {
			state.enable(GL_BLEND);
			r.draw2D.drawQuad(Quad.shared()
					.at(90.0f, 0.0f)
					.size(r.screen.getWidth() - 180.0f, r.screen.getHeight())
					.withColor(0.0f, 0.0f, 0.0f, 0.65f));

			r.fonts.outlined.printCentered("INFO AND PATCH NOTES", r.screen.getWidth() / 2.0f, 10.0f);
            for (int i = 0; i < text.size(); i++) {
                r.fonts.shadowed.print(text.get(i), r.screen.getWidth() / 3 - 10, 40.0f + 7.0f * i + scrollAmount * 5, 1.0f, 1.0f, 1.0f, 0.45f);
            }
           
		}
        super.draw();
    }
    
}
