package io.bluestaggo.voxelthing.gui.control;

import io.bluestaggo.pds.CompoundItem;
import io.bluestaggo.pds.StringItem;
import io.bluestaggo.voxelthing.gui.screen.GuiScreen;
import io.bluestaggo.voxelthing.world.storage.ISaveHandler;

public class WorldButton extends LabeledButton {
	public final ISaveHandler saveHandler;

	public WorldButton(GuiScreen screen, ISaveHandler saveHandler) {
		super(screen);

		this.saveHandler = saveHandler;

		CompoundItem data = saveHandler.loadData("world");
		if (data == null) {
			data = new CompoundItem();
		}

		text = data.map.get("name") instanceof StringItem nameItem ? nameItem.value : "???";
	}
}
