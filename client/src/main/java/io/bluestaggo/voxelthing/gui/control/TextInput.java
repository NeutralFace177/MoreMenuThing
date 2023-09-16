package io.bluestaggo.voxelthing.gui.control;

import io.bluestaggo.voxelthing.gui.GuiScreen;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;
import io.bluestaggo.voxelthing.Game;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Arrays;

import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCharCallbackI;

public class TextInput extends LabeledButton {

    public boolean imputting = false;
    public String emptyText = "________";

    public TextInput(GuiScreen screen) {
        super(screen);
        System.out.println();
    }


    public void inputText() {
            GLFWCharCallbackI keyCallback;
            glfwSetCharCallback(Game.getInstance().window.getHandle(), keyCallback = new GLFWCharCallback() {
                @Override
                public void invoke(long window, int key) {
                    if (key != 0) {
                        text += ((char)key);
                    }
                    if (key == GLFW_KEY_BACKSPACE) {
                        text = "";
                    }
                    
                }
        }); 
    }

    public TextInput setEmptyText(String txt) {
        emptyText = txt;
        return this;
    }

    @Override
	public void draw() {
        if (text == "") {
         System.out.println("null mc mull");
        }
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
					text == "" ? emptyText : text,
					sx + width / 2.0f,
					sy + (height - r.fonts.normal.lineHeight) / 2.0f
			);
		}
	
	}
    
}
