package io.bluestaggo.voxelthing.gui.screen;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.gui.control.TextBox;
import io.bluestaggo.voxelthing.world.generation.Structures;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.charset.Charset;

public class Chat extends GuiScreen {

    private final TextBox chatBox;

    public Chat(Game game) {
        super(game);

        chatBox = (TextBox) addControl(new TextBox(this)
				.at(-50.0f, 50.0f)
				.size(100.0f, 20.0f)
				.alignedAt(0.5f, 0.0f)
		);
    }

    @Override
	public void draw() {
		super.draw();
	}
    
    @Override
    public void onKeyPressed(int key) {
        if (key == GLFW_KEY_ENTER) {
            command();
        }
        if (key == GLFW_KEY_ESCAPE) {
            game.openGui(null);
        }
    }

    public void command() {
        
        try {

            if (chatBox.text.equals("struct")) {
                game.structureMode = true;
                game.structurePaintMode = false;
            }
            if (chatBox.text.substring(0, 5).equals("paint")) {
                game.structureMode = false;
                game.structurePaintMode = true;
            }
            if (chatBox.text.equals("normal")) {
                game.structureMode = false;
                game.structurePaintMode = false;
            }
            if (chatBox.text.equals("survival")) {
                game.player.survival = true;
            }
            if (chatBox.text.equals("creative")) {
                game.player.survival = false;
            }
        } catch (Exception e) {

        }
    }

    @Override
	public void tick() {
        
	}
    
}
