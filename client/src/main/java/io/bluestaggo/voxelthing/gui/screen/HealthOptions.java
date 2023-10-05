package io.bluestaggo.voxelthing.gui.screen;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.gui.control.TextBox;

import static org.lwjgl.glfw.GLFW.*;

public class HealthOptions extends GuiScreen {

    private final TextBox chatBox;
    private final TextBox chatTextBox;

    public HealthOptions(Game game) {
        super(game);
        chatBox = (TextBox) addControl(new TextBox(this)
         .at(-25.0f, 50.0f)
         .size(50.0f, 20.0f)
         .alignedAt(0.5f, 0.0f)
        );
         chatTextBox = (TextBox) addControl(new TextBox(this)
         .at(-75.0f, 50.0f)
         .size(50.0f, 20.0f)
         .alignedAt(0.5f, 0.0f)
         );
    }

    @Override
	public void draw() {
		super.draw();
        game.renderer.fonts.outlined.printRight("UNFINISHED, WONT WORK PROPERLY", 10,
		10);
	}

    @Override
    public void onKeyPressed(int key) {
        if (key == GLFW_KEY_ESCAPE) {
            game.openGui(null);
        }
    }

    @Override
	public void tick() {
        int a = chatBox.text == "" || chatBox.text == null ? 0 : Integer.parseInt(chatBox.text);
        int b = chatTextBox.text == "" || chatTextBox.text == null ? 0 : Integer.parseInt(chatTextBox.text);
        a = a > 6 ? 6 : a;
        a = a < 1 ? 1 : a;
        b =  b > 6 ? 6 : b;
        b = b < 1 ? 1 : b;
        if (a != 0) {
            IngameGui.getInstance().healthVisualMin = a;
        }
        if (b != 0) {
            IngameGui.getInstance().healthVisualMax = b;
        }
	}
    
}
