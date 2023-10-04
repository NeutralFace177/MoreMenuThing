package io.bluestaggo.voxelthing.gui.screen;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.Identifier;
import io.bluestaggo.voxelthing.assets.Texture;
import io.bluestaggo.voxelthing.math.MathUtil;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Quad;
import io.bluestaggo.voxelthing.world.BlockRaycast;
import io.bluestaggo.voxelthing.world.Direction;
import io.bluestaggo.voxelthing.world.block.Block;
import io.bluestaggo.voxelthing.world.generation.blockInStructure;
import io.bluestaggo.voxelthing.world.generation.Structures;

import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.system.Struct;

import static org.lwjgl.glfw.GLFW.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IngameGui extends GuiScreen {
	private int[] prevHoverProgress;
	private int[] hoverProgress;
	private int swingTick = 0;
	private int maxMiningTick = 54;
	private int miningTick = maxMiningTick;
	int px,py,pz;
	private boolean firstBlock = true;
	//is left click held
	private boolean mouseHeld = false;
	public ArrayList<blockInStructure> structure = new ArrayList<>();

	int sx = 0;
	int sy = 0;
	int sz = 0;

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
		BlockRaycast raycast = game.getBlockRaycast();
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
		game.renderer.worldRenderer.mining = miningTick < maxMiningTick;
		game.renderer.worldRenderer.mineProgress = (int)Math.round(((double)miningTick / (double)maxMiningTick) * 6);

		if (swingTick > 0) {
			swingTick--;
		}
		if (miningTick > 0 && mouseHeld) {
			miningTick--;
		}
		int x = 0;
		int y = 0;
		int z = 0;
		if (raycast.blockHit()) {
			x = raycast.getHitX();
			y = raycast.getHitY();
			z = raycast.getHitZ();
		}

		if (px != x || py != y || pz != z) {
			miningTick = maxMiningTick;
		}
		if (miningTick == 0) {
			if (raycast.blockHit()) {
				game.palette[game.heldItem] = game.world.getBlock(x, y, z);
				game.world.setBlock(x, y, z, null);
				miningTick = maxMiningTick;
			}
			
		}
		px = raycast.getHitX();
		py = raycast.getHitY();
		pz = raycast.getHitZ();
	}

	@Override
	public void draw() {
		super.draw();

		if (!game.showThirdPerson()) {
			drawCrosshair();
			drawHand();
		}

		drawHotbar();
		drawHealthBar();
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
		var halfQuad = new Quad()
				.at(startX + (slotWidth - 16) / 2.0f, startY + slotHeight / 2.0f)
				.size(16, 8)
				.withTexture(blocksTexture);

		for (int i = 0; i < game.palette.length; i++) {
			Block block = game.palette[i];

			float hover = MathUtil.lerp(prevHoverProgress[i], hoverProgress[i], (float) game.getPartialTick()) / (Game.TICKS_PER_SECOND / 4.0f);
			hover = MathUtil.squareOut(hover);
			hover *= slotHeight / 4.0f;

			hotbarQuad.offset(0, -hover);
			blockQuad.offset(0, -hover);
			halfQuad.offset(0, -hover);

			float slotOffset = i == game.heldItem ? 0.5f : 0.0f;
			r.draw2D.drawQuad(hotbarQuad.withUV(slotOffset, 0.0f, 0.5f + slotOffset, 1.0f));

			if (block != null) {
				Vector2i texture = block.getTexture().get(Direction.NORTH);
				boolean slab = Block.REGISTERED_SLABS_ORDERED.contains(block);

				float minU = blocksTexture.uCoord(texture.x * 16);
				float minV = blocksTexture.vCoord(texture.y * 16);
				float maxU = minU + blocksTexture.uCoord(16);
				float maxV = slab ? minV + blocksTexture.vCoord(8) : minV + blocksTexture.vCoord(16);

				if (slab) {
					r.draw2D.drawQuad(halfQuad.withUV(minU, minV, maxU, maxV));
				} else {
					r.draw2D.drawQuad(blockQuad.withUV(minU, minV, maxU, maxV));
				}

			}

			hotbarQuad.offset(slotWidth, hover);
			blockQuad.offset(slotWidth, hover);
		}
	}

	private void drawHealthBar() {
		MainRenderer r = game.renderer;

		
		int[] s = new int[]{7,9,11,13,15,17};
		int[] g = new int[]{17+15+13+11+9+7, 17+15+13+11+9, 17+15+13+11,17+15+13,17+15,17};

		float startX = (r.screen.getWidth() - 32 * game.palette.length) / 2.0f;
		float startY = r.screen.getHeight() - 32 - 5;
		int h = game.player.health;
		//health divided by 6 floored
		int value = (int)Math.floor((double)h/(double)6);
		//undivide by 6
		int v = value * 6;
		int a = value == 0 ? 1 : value;
		float j = g[(h-v)];
		float l = j + s[(h-v)];
		int i = 0;
		System.out.println("value:" + value + " v:" + v + " a:" + a + " h:" + h);
		for (i = 0; i < a; i++) {
			if (value != 0) {
				r.draw2D.drawQuad(healthIcon(startX + i * s[5] + 57, startY - 10, s[5], new Vector4f(1/512f, 1f/512f, 18f/512f, 18f/512f)));
			}
			int m = value == 0 ? 0 : i+1;
			if (i == a-1 && v != h) {
				r.draw2D.drawQuad(healthIcon(startX + (m) * s[5] + 57, startY - 13 + s[5-(h-v-1)]/2, s[h-v-1],
		 		 new Vector4f( (1f + (2 * (6-(h-v))) + j) / 512f, 1f/512f, (1f + (2 * (6-(h-v+1))) + l) / 512f, (1f + s[h-v-1]) / 512f)));;
			}
		}
	}

	private Quad healthIcon(float sx, float sy, float size, Vector4f uv) {
		Texture healthTexture = game.renderer.textures.getTexture("/assets/gui/icons.png");
	//	System.out.println("x:" + uv.x*512 + " y:" + uv.y*512 + " z:" + uv.z*512 + " w:" + uv.w*512);
		return new Quad()
			.at(sx, sy)
			.size(size, size)
			.withTexture(healthTexture)
			.withUV(uv.x, uv.y, uv.z, uv.w);
	}

	private Block getPlacedBlock() {
		if (game.heldItem < 0 || game.heldItem >= game.palette.length) {
			return null;
		}
		return game.palette[game.heldItem];
	}

	
	@Override
	protected void onMouseReleased(int button, int mx, int my) {
		super.onMouseReleased(button, mx, my);
		if (button == 0) {
			mouseHeld = false;
			miningTick = maxMiningTick;
		}
	}

	@Override
	protected void onMouseHeld(int button, int mx, int my) {
		super.onMouseHeld(button, mx, my);
		if (button == 0) {
			mouseHeld = true;
			BlockRaycast raycast = game.getBlockRaycast();
			if (raycast.blockHit()) {
				maxMiningTick = game.player.survival ? game.world.getBlock(raycast.getHitX(), raycast.getHitY(), raycast.getHitZ()).hardnessTick() : 5; 
			}
		}
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
			if (raycast.blockHit()) {
				maxMiningTick = game.world.getBlock(raycast.getHitX(), raycast.getHitY(), raycast.getHitZ()).hardnessTick();
				miningTick = maxMiningTick;
			}
				if (!game.player.survival) {
					game.world.setBlock(x, y, z, null);
					if (game.structureMode) {

					for (int i = 0; i < structure.size(); i++) {
						if (sx-x == structure.get(i).x && sy-y == structure.get(i).y && sz-z == structure.get(i).z) {
							structure.remove(i);
							String a = "{";
							for (int j = 0; j < structure.size(); j++) {
								a += "s(";
								a += Block.getCode(structure.get(j).block) + ", ";
								a += structure.get(j).x + ", ";
								a += structure.get(j).y + ", ";
								a += structure.get(j).z;
								a += ")";
								a += j == structure.size()-1 ? "" : ", ";
							}
							a += "}";
							System.out.println("\n" + a);
							break;
						}
					}
				} 
				}

				
			} else if (button == 1) {
				Block placedBlock = getPlacedBlock();

				if (game.structurePaintMode) {
					for (int i = 0; i < game.structureToPaint.getStructure().length; i++) {
						game.world.setBlock(x - game.structureToPaint.getStructure()[i].x, y - game.structureToPaint.getStructure()[i].y + 1, z - game.structureToPaint.getStructure()[i].z, game.structureToPaint.getStructure()[i].block);
					}
				}


				if (placedBlock != null) {
					x += face.X;
					y += face.Y;
					z += face.Z;

					if (game.world.isAir(x, y, z)) {
						game.world.setBlock(x, y, z, placedBlock);
						
						if (game.structureMode) {
							if (firstBlock) {
								sx = x;
								sy = y;
								sz = z;
								firstBlock = false;
							}

							structure.add(s(placedBlock, sx - x, sy - y, sz - z));
							String a = "{";
							for (int i = 0; i < structure.size(); i++) {
								a += "s(";
								a += Block.getCode(structure.get(i).block) + ", ";
								a += structure.get(i).x + ", ";
								a += structure.get(i).y + ", ";
								a += structure.get(i).z;
								a += ")";
								a += i == structure.size()-1 ? "" : ", ";
							}
							a += "}";
							System.out.println("\n" + a);
						}
						
						swingTick = 10;
					}
				}
			}
		}

		if (button == 0) {
			swingTick = 10;
		}
	}


	//shorter way of new Structure
	public static blockInStructure s(Block block, int x, int y, int z) {
		return new blockInStructure(block, x, y, z);
	}
}
