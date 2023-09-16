package io.bluestaggo.voxelthing.gui;


import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.gui.control.GuiControl;
import io.bluestaggo.voxelthing.gui.control.LabeledButton;
import io.bluestaggo.voxelthing.gui.control.TextInput;
import io.bluestaggo.voxelthing.renderer.GLState;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.glfw.GLFW.*;

public class WorldCreateMenu extends GuiScreen{

    private final GuiControl newWorldButton;
    private final GuiControl worldNameControl;
    private final TextInput worldNameInput;
    private final GuiControl seedControl;
    private final TextInput seedInput;

    public WorldCreateMenu(Game game) {
        super(game);
        newWorldButton = addControl(new LabeledButton(this)
				.withText("Create World")
				.at(-50.0f, -25.0f)
				.size(100.0f, 20.0f)
				.alignedAt(0.5f, 1.0f)
		);
        
        worldNameInput = new TextInput(this);
        worldNameInput.withText("").alignedAt(0.30f, 0.25f).at(-50.0f,0f).size(100.0f, 20.0f).at(-50, 5);
        
        worldNameControl = addControl(worldNameInput);
        
        seedInput = new TextInput(this);
        seedInput.alignedAt(0.70f, 0.25f).at(-50.0f,0f).size(100.0f, 20.0f).at(-50, 5);
        seedControl = addControl(seedInput);
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

			r.fonts.outlined.printCentered("CREATE NEW WORLD", r.screen.getWidth() / 2.0f, 10.0f);
            r.fonts.normal.printCentered("NAME:", (r.screen.getWidth() / 3.3333333333333333f), 40.0f);
            r.fonts.normal.printCentered("SEED:", (r.screen.getWidth() / 1.444444444444444444f), 40.0f);
		}
		super.draw();
	}

    @Override
	protected void onKeyPressed(int key) {
		if (key == GLFW_KEY_ESCAPE) {
            game.openGui(new SaveSelect(game));
        }
	}

    @Override
	public void onControlClicked(GuiControl control, int button) {
		if (control == worldNameControl) {
			worldNameInput.imputting = true;
            seedInput.imputting = false;
		}
        if (control == seedControl) {
            seedInput.imputting = true;
            worldNameInput.imputting = false;
        }
        if (control == newWorldButton) {
            game.startWorld(worldNameInput.text.toString());
			game.openGui(null);
        }
		
	}
}
