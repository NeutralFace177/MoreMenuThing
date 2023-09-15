package io.bluestaggo.voxelthing.gui.control;

import io.bluestaggo.voxelthing.gui.GuiScreen;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;
import io.bluestaggo.voxelthing.Game;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Arrays;

import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;

public class TextInput extends LabeledButton {

    public boolean imputting = true;


    public TextInput(GuiScreen screen) {
        super(screen);
        System.out.println();
    }


    public void inputText() {
            GLFWKeyCallbackI keyCallback;
            glfwSetKeyCallback(Game.getInstance().window.getHandle(), keyCallback = new GLFWKeyCallback() {
                @Override
                public void invoke(long window, int key, int scancode, int action, int mods) {
                    if (action != 0) {
                        if (key == GLFW_KEY_BACKSPACE) {
                            text = "";
                        }
                        if (mods != 1 && key >= 65 && key <= 90) {
                            key += 32;
                        }
                        text += ((char)key);
                    }
                }
            });
        
        
    }

    @Override
	public void draw() {
        if (imputting) {
            inputText();
        }
		if (enabled) {
			MainRenderer r = screen.game.renderer;
			float sx = getScaledX();
			float sy = getScaledY();

			r.draw2D.drawQuad(Quad.shared()
					.at(sx, sy)
					.size(width, height)
					.withColor(0.25f, 0.25f, 0.25f)
			);
			r.draw2D.drawQuad(Quad.shared()
					.at(sx + 1.0f, sy + 1.0f)
					.size(width - 2.0f, height - 2.0f)
					.withColor(0.15f, 0.15f, 0.15f)
			);
 
			r.fonts.shadowed.printCentered(
					text == "" ? "______" : text,
					sx + width / 2.0f,
					sy + (height - r.fonts.normal.lineHeight) / 2.0f
			);
		}
	
	}
    
}
