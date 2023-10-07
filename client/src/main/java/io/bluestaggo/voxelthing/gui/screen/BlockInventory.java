package io.bluestaggo.voxelthing.gui.screen;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.assets.Texture;
import io.bluestaggo.voxelthing.gui.control.GuiControl;
import io.bluestaggo.voxelthing.gui.control.LabeledButton;
import io.bluestaggo.voxelthing.renderer.GLState;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;
import io.bluestaggo.voxelthing.world.Direction;
import io.bluestaggo.voxelthing.world.block.Block;
import io.bluestaggo.voxelthing.world.inventory.Hotbar;
import io.bluestaggo.voxelthing.world.item.Item;
import io.bluestaggo.voxelthing.world.item.ItemStack;

import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;

public class BlockInventory extends GuiScreen {
	private static final int ROWS = 5;
	private static final int COLUMNS = 14;

	private final GuiControl itemTab;
	private final GuiControl blockTab;
	private final GuiControl slabTab;

	private int tab = 1;

	public BlockInventory(Game game) {
		super(game);
		itemTab = addControl(new LabeledButton(this)
				.withText("Items")
				.at(-70, 0)
				.size(40.0f, 20.0f)
				.alignedAt(0.5f, 0.125f)
		);
		blockTab = addControl(new LabeledButton(this)
				.withText("Blocks")
				.at(-20, 0)
				.size(40.0f, 20.0f)
				.alignedAt(0.5f, 0.125f)
		);
		slabTab = addControl(new LabeledButton(this)
				.withText("Slabs")
				.at(30, 0)
				.size(40.0f, 20.0f)
				.alignedAt(0.5f, 0.125f)
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

			String title = "SELECT BLOCK";
			r.fonts.outlined.print(title, (r.screen.getWidth() - r.fonts.outlined.getStringLength(title)) / 2.0f, 20);
		}

		Texture hotbarTexture = r.textures.getTexture("/assets/gui/hotbar.png");
		Texture blocksTexture = r.textures.getTexture("/assets/blocks.png");
		Texture itemsTexture = r.textures.getTexture("/assets/items.png");
		int slotWidth = hotbarTexture.width / 2;
		int slotHeight = hotbarTexture.height;
		int blockOffX = (slotWidth - 16) / 2;
		int blockOffY = (slotHeight - 16) / 2;

		var hotbarQuad = new Quad()
				.size(slotWidth, slotHeight)
				.withTexture(hotbarTexture)
				.withUV(0.0f, 0.0f, 0.5f, 1.0f);
		var blockQuad = new Quad()
				.size(16, 16)
				.withTexture(blocksTexture);
		var itemQuad = new Quad()
				.size(16, 16)
				.withTexture(itemsTexture);

		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				float slotX = (r.screen.getWidth() - slotWidth * COLUMNS) / 2.0f + x * slotWidth;
				float slotY = (r.screen.getHeight() - slotHeight * ROWS) / 2.0f + y * slotHeight;

				r.draw2D.drawQuad(hotbarQuad.at(slotX, slotY));

				int i = x + y * COLUMNS;
				if (tab == 1) {
					if (i < Item.REGISTERED_BLOCK_ITEMS_ORDERED.size() - Item.REGISTERED_SLAB_ITEMS_ORDERED.size()) {
						Item item = Item.REGISTERED_BLOCK_ITEMS_ORDERED.get(i);
						if (item != null) {
							Vector2i texture = item.getTex().get();

							float minU = blocksTexture.uCoord(texture.x * 16);
							float minV = blocksTexture.vCoord(texture.y * 16);
							float maxU = minU + blocksTexture.uCoord(16);
							float maxV = minV + blocksTexture.vCoord(16);

							r.draw2D.drawQuad(blockQuad
									.at(slotX + blockOffX, slotY + blockOffY)
									.withUV(minU, minV, maxU, maxV)
							);
						}
					}
				} 
				if (tab == 2) {
					if (i < Item.REGISTERED_SLAB_ITEMS_ORDERED.size()) {
						Item item = Item.REGISTERED_SLAB_ITEMS_ORDERED.get(i);
						if (item != null) {
							Vector2i texture = item.getTex().get();

							float minU = blocksTexture.uCoord(texture.x * 16);
							float minV = blocksTexture.vCoord(texture.y * 16);
							float maxU = minU + blocksTexture.uCoord(16);
							float maxV = minV + blocksTexture.vCoord(8);

							r.draw2D.drawQuad(blockQuad
									.at(slotX + blockOffX, slotY + blockOffY + 8)
									.withUV(minU, minV, maxU, maxV)
									.size(16,8)
							);

						}
					}
				}
				if (tab == 3) {
					if (i < Item.REGISTERED_ITEMS_ONLY_ORDERED.size()) {
						Item item = Item.REGISTERED_ITEMS_ONLY_ORDERED.get(i);
						if (item != null) {
							Vector2i texture = item.getTex().get();

							float minU = blocksTexture.uCoord(texture.x * 16);
							float minV = blocksTexture.vCoord(texture.y * 16);
							float maxU = minU + blocksTexture.uCoord(16);
							float maxV = minV + blocksTexture.vCoord(16);

							r.draw2D.drawQuad(itemQuad
									.at(slotX + blockOffX, slotY + blockOffY)
									.withUV(minU, minV, maxU, maxV)
							);
						}
					}
				}

			}
		}
		slabTab.draw();
		blockTab.draw();
		itemTab.draw();
	}

	@Override
	protected void onKeyPressed(int key) {
		if (key == GLFW_KEY_E) {
			game.closeGui();
		}

		int newIndex = game.heldItem;

		if (key >= GLFW_KEY_1 && key <= GLFW_KEY_9) {
			newIndex = key - GLFW_KEY_1;
		} else if (key == GLFW_KEY_0) {
			newIndex = 9;
		}

		if (newIndex >= 0 && newIndex < Hotbar.COLUMNS) {
			game.heldItem = newIndex;
		}
	}

	@Override
	public void onControlClicked(GuiControl control, int button) {
		if (control == blockTab) {
			tab = 1;
		}
		if (control == slabTab) {
			tab = 2;
		}
		if (control == itemTab) {
			tab = 3;
		}
	}

	@Override
	protected void onMouseClicked(int button, int mx, int my) {
		super.onMouseClicked(button, mx, my);

		MainRenderer r = game.renderer;

		Texture hotbarTexture = r.textures.getTexture("/assets/gui/hotbar.png");
		int slotWidth = hotbarTexture.width / 2;
		int slotHeight = hotbarTexture.height;
		int blockOffX = (slotWidth - 16) / 2;
		int blockOffY = (slotHeight - 16) / 2;

		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				int slotX = (r.screen.getWidth() - slotWidth * COLUMNS) / 2 + x * slotWidth;
				int slotY = (r.screen.getHeight() - slotHeight * ROWS) / 2 + y * slotHeight;

				if (mx > slotX + blockOffX && mx < slotX + slotWidth - blockOffX
						&& my > slotY + blockOffY && my < slotY + slotHeight - blockOffY) {
					int i = x + y * COLUMNS;
					Item item = Item.STICK;
					if (tab == 1) {
						item = (i < Item.REGISTERED_BLOCK_ITEMS_ORDERED.size() && tab == 1) ? Item.REGISTERED_BLOCK_ITEMS_ORDERED.get(i) : null;
					}
					if (tab == 2) {
						item = Item.REGISTERED_SLAB_ITEMS_ORDERED.get(i);
					}
					if (tab == 3) {
						item = Item.REGISTERED_ITEMS_ONLY_ORDERED.get(i);	
					}

					game.player.hotbar.setItem(new ItemStack(item, 64), 0, game.heldItem);
				}
			}
		}

		//game.closeGui();
	}
}
