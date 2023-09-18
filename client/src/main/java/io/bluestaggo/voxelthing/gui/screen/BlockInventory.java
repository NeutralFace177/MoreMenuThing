package io.bluestaggo.voxelthing.gui.screen;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.assets.Texture;
import io.bluestaggo.voxelthing.renderer.GLState;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;
import io.bluestaggo.voxelthing.world.Direction;
import io.bluestaggo.voxelthing.world.block.Block;
import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;

public class BlockInventory extends GuiScreen {
	private static final int ROWS = 10;
	private static final int COLUMNS = 5;

	public BlockInventory(Game game) {
		super(game);
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

		for (int y = 0; y < COLUMNS; y++) {
			for (int x = 0; x < ROWS; x++) {
				float slotX = (r.screen.getWidth() - slotWidth * ROWS) / 2.0f + x * slotWidth;
				float slotY = (r.screen.getHeight() - slotHeight * COLUMNS) / 2.0f + y * slotHeight;

				r.draw2D.drawQuad(hotbarQuad.at(slotX, slotY));

				int i = x + y * ROWS;
				if (i < Block.REGISTERED_BLOCKS_ORDERED.size()) {
					Block block = Block.REGISTERED_BLOCKS_ORDERED.get(i);
					if (block != null) {
						Vector2i texture = block.getTexture().get(Direction.NORTH);

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
		}
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

		if (newIndex >= 0 && newIndex < game.palette.length) {
			game.heldItem = newIndex;
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

		for (int y = 0; y < COLUMNS; y++) {
			for (int x = 0; x < ROWS; x++) {
				int slotX = (r.screen.getWidth() - slotWidth * ROWS) / 2 + x * slotWidth;
				int slotY = (r.screen.getHeight() - slotHeight * COLUMNS) / 2 + y * slotHeight;

				if (mx > slotX + blockOffX && mx < slotX + slotWidth - blockOffX
						&& my > slotY + blockOffY && my < slotY + slotHeight - blockOffY) {
					int i = x + y * ROWS;
					Block block = i < Block.REGISTERED_BLOCKS_ORDERED.size() ? Block.REGISTERED_BLOCKS_ORDERED.get(i) : null;
					game.palette[game.heldItem] = block;
				}
			}
		}

		game.closeGui();
	}
}
