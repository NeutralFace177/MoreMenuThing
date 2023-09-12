package io.bluestaggo.voxelthing.gui;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.gui.control.GuiControl;
import io.bluestaggo.voxelthing.gui.control.LabeledButton;
import io.bluestaggo.voxelthing.renderer.GLState;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.glfw.GLFW.*;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class SaveSelect extends GuiScreen {
	private final GuiControl newWorldButton;
	private ArrayList<GuiControl> saveButtons = new ArrayList<GuiControl>();
	private String[] directories;

	public SaveSelect(Game game) {
		super(game);

		newWorldButton = addControl(new LabeledButton(this)
				.withText("New World")
				.at(-50.0f, -25.0f)
				.size(100.0f, 20.0f)
				.alignedAt(0.5f, 1.0f)
		);
		


		String path = game.saveDir.toString() + "\\worlds";
 
        File file = new File(path);
    	directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
				File file = new File(dir, name);
				if (file.isDirectory() && new File(file.getPath(),"player.dat").exists()) {
               	 return new File(dir, name).isDirectory();
				}
				return false;
            }
        });
 
        for (int i = 0; i < directories.length; i++) {
			GuiControl a = addControl(new LabeledButton(this).withText(directories[i]).at(-50,0 + 22 * i).alignedAt(0.5f, 0.18f).size(100,20));
			
			saveButtons.add(a);	
		}


		
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
		if (control == newWorldButton) {
			game.startWorld();
			game.openGui(null);
		}
		for (int i = 0; i < saveButtons.size(); i++) {
			if (control == saveButtons.get(i)) {
				game.startWorld(directories[i]);
				game.openGui(null);
			}
		}
	}
}
