package io.bluestaggo.voxelthing.gui.screen;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.assets.Texture;
import io.bluestaggo.voxelthing.math.MathUtil;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;
import io.bluestaggo.voxelthing.world.BlockRaycast;
import io.bluestaggo.voxelthing.world.Direction;
import io.bluestaggo.voxelthing.world.block.Block;
import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.*;

public class IngameGui extends GuiScreen {
	private int[] prevHoverProgress;
	private int[] hoverProgress;
	private int swingTick = 0;

	public IngameGui(Game game) {
		super(game);
		prevHoverProgress = new int[game.palette.length];
		hoverProgress = new int[game.palette.length];
	}

	@Override
	protected void onKeyPressed(int key) {
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
	public void tick() {
		for (int i = 0; i < game.palette.length; i++) {
			int hover = hoverProgress[i];
			prevHoverProgress[i] = hover;

			if (i == game.heldItem) {
				hover++;
			} else {
				hover--;
			}

			hover = MathUtil.clamp(hover, 0, Game.TICKS_PER_SECOND / 4);
			hoverProgress[i] = hover;
		}

		if (swingTick > 0) {
			swingTick--;
		}
	}

	@Override
	public void draw() {
		super.draw();

		if (!game.showThirdPerson()) {
			drawCrosshair();
			drawHand();
		}

		drawHotbar();
	}

	private void drawCrosshair() {
		MainRenderer r = game.renderer;
		Texture crosshairTexture = r.textures.getTexture("/assets/gui/crosshair.png");
		r.draw2D.drawQuad(Quad.shared()
				.at((r.screen.getWidth() - crosshairTexture.width) / 2, (r.screen.getHeight() - crosshairTexture.height) / 2)
				.withTexture(crosshairTexture));
	}

	private void drawHand() {
		MainRenderer r = game.renderer;

		Block block = getPlacedBlock();
		Texture playerTexture = r.textures.getTexture(game.player.getTexture());
		Texture blocksTexture = r.textures.getTexture("/assets/blocks.png");

		float handSize = r.screen.getHeight();
		float hover = MathUtil.lerp(prevHoverProgress[game.heldItem], hoverProgress[game.heldItem], (float) game.getPartialTick()) / (Game.TICKS_PER_SECOND / 4.0f);
		hover = MathUtil.squareOut(hover);
		double swing = Math.max(swingTick - game.getPartialTick(), 0.0) / 10.0;
		swing *= swing;

		float bobX = (float) (MathUtil.sinPi(swing) / -3.0) * handSize;
		float bobY = (float) ((1.0 - hover * (block == null ? 1.0 : 0.9))
				+ (MathUtil.sinPi(swing * 2.0) / 6.0)) * handSize;

		if (game.viewBobbingEnabled()) {
			bobX += (float) (game.player.getRenderWalk() * -0.05) * handSize;
			bobY += (float) ((Math.abs(game.player.getRenderWalk()) * 0.05)
					+ Math.max(game.player.getFallAmount() * 0.1, -0.25)) * handSize;
		}

		r.draw2D.drawQuad(Quad.shared()
				.at(r.screen.getWidth() - handSize + bobX,
						r.screen.getHeight() - handSize + bobY + handSize / 4.0f)
				.size(handSize, handSize)
				.withTexture(playerTexture)
				.withUV(1.0f - playerTexture.uCoord(32), 1.0f - playerTexture.vCoord(32), 1.0f, 1.0f));

		if (block != null) {
			Vector2i texture = block.getTexture().get(Direction.NORTH);

			float minU = blocksTexture.uCoord(texture.x * 16);
			float minV = blocksTexture.vCoord(texture.y * 16);
			float maxU = minU + blocksTexture.uCoord(16);
			float maxV = minV + blocksTexture.vCoord(16);

			Quad blockQuad = Quad.shared()
					.at(r.screen.getWidth() + (bobX - handSize / 1.8f), r.screen.getHeight() + (bobY - handSize / 1.8f))
					.size(handSize * 0.35f, handSize * 0.4f)
					.withTexture(blocksTexture)
					.withUV(minU, minV, maxU, maxV);

			r.draw2D.drawQuad(blockQuad);
			r.draw2D.drawQuad(blockQuad
					.size(handSize * 0.15f, handSize * 0.4f)
					.offset(handSize * 0.35f, 0.0f)
					.withColor(0.75f, 0.75f, 0.75f));
		}
	}

	private void drawHotbar() {
		MainRenderer r = game.renderer;

		Texture hotbarTexture = r.textures.getTexture("/assets/gui/hotbar.png");
		Texture blocksTexture = r.textures.getTexture("/assets/blocks.png");
		int slotWidth = hotbarTexture.width / 2;
		int slotHeight = hotbarTexture.height;

		float startX = (r.screen.getWidth() - slotWidth * game.palette.length) / 2.0f;
		float startY = r.screen.getHeight() - slotHeight - 5;

		var hotbarQuad = new Quad()
				.at(startX, startY)
				.size(slotWidth, slotHeight)
				.withTexture(hotbarTexture)
				.withUV(0.0f, 0.0f, 0.5f, 1.0f);
		var blockQuad = new Quad()
				.at(startX + (slotWidth - 16) / 2.0f, startY + (slotHeight - 16) / 2.0f)
				.size(16, 16)
				.withTexture(blocksTexture);

		for (int i = 0; i < game.palette.length; i++) {
			Block block = game.palette[i];

			float hover = MathUtil.lerp(prevHoverProgress[i], hoverProgress[i], (float) game.getPartialTick()) / (Game.TICKS_PER_SECOND / 4.0f);
			hover = MathUtil.squareOut(hover);
			hover *= slotHeight / 4.0f;

			hotbarQuad.offset(0, -hover);
			blockQuad.offset(0, -hover);

			float slotOffset = i == game.heldItem ? 0.5f : 0.0f;
			r.draw2D.drawQuad(hotbarQuad.withUV(slotOffset, 0.0f, 0.5f + slotOffset, 1.0f));

			if (block != null) {
				Vector2i texture = block.getTexture().get(Direction.NORTH);

				float minU = blocksTexture.uCoord(texture.x * 16);
				float minV = blocksTexture.vCoord(texture.y * 16);
				float maxU = minU + blocksTexture.uCoord(16);
				float maxV = minV + blocksTexture.vCoord(16);

				r.draw2D.drawQuad(blockQuad.withUV(minU, minV, maxU, maxV));
			}

			hotbarQuad.offset(slotWidth, hover);
			blockQuad.offset(slotWidth, hover);
		}
	}

	private Block getPlacedBlock() {
		if (game.heldItem < 0 || game.heldItem >= game.palette.length) {
			return null;
		}
		return game.palette[game.heldItem];
	}

	@Override
	protected void onMouseClicked(int button, int mx, int my) {
		super.onMouseClicked(button, mx, my);

		BlockRaycast raycast = game.getBlockRaycast();
		if (raycast.blockHit()) {
			int x = raycast.getHitX();
			int y = raycast.getHitY();
			int z = raycast.getHitZ();
			Direction face = raycast.getHitFace();

			if (button == 0) {
				game.world.setBlock(x, y, z, null);
			} else if (button == 1) {
				Block placedBlock = getPlacedBlock();
				if (placedBlock != null) {
					x += face.X;
					y += face.Y;
					z += face.Z;

					if (game.world.isAir(x, y, z)) {
						game.world.setBlock(x, y, z, placedBlock);
						swingTick = 10;
					}
				}
			}
		}

		if (button == 0) {
			swingTick = 10;
		}
	}
}
