package io.bluestaggo.voxelthing.gui.screen;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.gui.control.GuiControl;
import io.bluestaggo.voxelthing.gui.control.LabeledButton;
import io.bluestaggo.voxelthing.gui.control.TextBox;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.world.WorldInfo;

import java.util.UUID;

public class CreateWorld extends GuiScreen {
	private final GuiControl createButton;
	private final GuiControl cancelButton;
	private final TextBox nameBox;
	private final TextBox seedBox;

	public CreateWorld(Game game, GuiScreen parent) {
		super(game, parent);

		createButton = addControl(new LabeledButton(this)
				.withText("Create")
				.at(5.0f, -25.0f)
				.size(100.0f, 20.0f)
				.alignedAt(0.5f, 1.0f)
		);
		cancelButton = addControl(new LabeledButton(this)
				.withText("Cancel")
				.at(-105.0f, -25.0f)
				.size(100.0f, 20.0f)
				.alignedAt(0.5f, 1.0f)
		);
		nameBox = (TextBox) addControl(new TextBox(this)
				.at(-50.0f, 50.0f)
				.size(100.0f, 20.0f)
				.alignedAt(0.5f, 0.0f)
		);
		seedBox = (TextBox) addControl(new TextBox(this)
				.at(-50.0f, 80.0f)
				.size(100.0f, 20.0f)
				.alignedAt(0.5f, 0.0f)
		);

		nameBox.text = "World";
	}

	@Override
	public void draw() {
		super.draw();

		MainRenderer r = game.renderer;

		r.fonts.outlined.printRight("Name", nameBox.getScaledX() - 5.0f,
				nameBox.getScaledY() + (nameBox.getScaledHeight() - r.fonts.outlined.lineHeight) / 2.0f);
		r.fonts.outlined.printRight("Seed", seedBox.getScaledX() - 5.0f,
				seedBox.getScaledY() + (seedBox.getScaledHeight() - r.fonts.outlined.lineHeight) / 2.0f);
	}

	@Override
	public void onControlClicked(GuiControl control, int button) {
		if (control == createButton) {
			var worldInfo = new WorldInfo();
			worldInfo.name = nameBox.text;

			if (seedBox.text.length() > 0) {
				try {
					worldInfo.seed = Long.parseLong(seedBox.text);
				} catch (NumberFormatException e) {
					worldInfo.seed = seedBox.text.hashCode();
				}
			}

			game.startWorld(UUID.randomUUID().toString(), worldInfo);
		} else if (control == cancelButton) {
			game.closeGui();
		}
	}
}
