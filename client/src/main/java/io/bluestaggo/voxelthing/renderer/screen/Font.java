package io.bluestaggo.voxelthing.renderer.screen;

import io.bluestaggo.voxelthing.assets.Texture;
import io.bluestaggo.voxelthing.math.MathUtil;
import io.bluestaggo.voxelthing.renderer.MainRenderer;
import io.bluestaggo.voxelthing.renderer.draw.Draw2D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;

public class Font {
	private final MainRenderer renderer;
	private final String texturePath;

	private final int[] charWidths = new int[256];
	public final int lineHeight;
	public final int charSpacing;
	public final int spaceWidth;

	public Font(MainRenderer renderer, String texturePath) throws IOException {
		this.renderer = renderer;
		this.texturePath = texturePath;

		try (InputStream imageStream = getClass().getResourceAsStream(texturePath)) {
			if (imageStream == null) {
				throw new IOException("Image \"" + texturePath + "\" does not exist!");
			}

			BufferedImage image = ImageIO.read(imageStream);
			WritableRaster raster = image.getRaster();
			int[] color = new int[4];

			int code = image.getRGB(0, 0);
			lineHeight = (code & 0xF00000) >> 20;
			charSpacing = ((code & 0xF0000) >> 16) - 8;
			spaceWidth = (code & 0xF000) >> 12;

			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 16; y++) {
					int i = y * 16 + x;
					if (i == ' ') {
						charWidths[i] = spaceWidth;
						continue;
					}

					int charWidth = 0;
					loopWidth: for (int sx = 0; sx < 16; sx++) {
						for (int sy = 0; sy <= 16; sy++) {
							if (sy == 16) {
								break loopWidth;
							}

							raster.getPixel(sx + x * 16, sy + y * 16, color);
							if (color[3] > 0) {
								charWidth++;
								continue loopWidth;
							}
						}
					}

					charWidths[i] = charWidth;
				}
			}
		}
	}

	public void print(String text, float x, float y) {
		print(text, x, y, 1.0f, 1.0f, 1.0f);
	}

	public void print(String text, float x, float y, float r, float g, float b) {
		print(text, x, y, r, g, b, 1.0f);
	}

	public void print(String text, float x, float y, float r, float g, float b, float size) {
		Draw2D d = renderer.draw2D;
		renderer.textures.getTexture(texturePath).use();

		float modR = 1.0f;
		float modG = 1.0f;
		float modB = 1.0f;
		float ox = x;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '\u00a7' && i < text.length() + 1) {
				c = Character.toLowerCase(text.charAt(++i));
				if (c == 'c' && text.length() - i > 6) {
					int cr = MathUtil.hexValue(text.charAt(++i));
					cr = (cr << 4) + MathUtil.hexValue(text.charAt(++i));
					int cg = MathUtil.hexValue(text.charAt(++i));
					cg = (cg << 4) + MathUtil.hexValue(text.charAt(++i));
					int cb = MathUtil.hexValue(text.charAt(++i));
					cb = (cb << 4) + MathUtil.hexValue(text.charAt(++i));
					modR = cr / 255.0f;
					modG = cg / 255.0f;
					modB = cb / 255.0f;
					continue;
				}
			}

			if (c == '\n') {
				x = ox;
				y += lineHeight;
				continue;
			}

			if (c > 255 || c == 0) {
				continue;
			}

			int w = charWidths[c];
			if (c != ' ') {
				float uMin = (c % 16) / 16.0f;
				float vMin = (c / 16) / 16.0f;
				float uMax = uMin + w / 256.0f;
				float vMax = vMin + 1.0f / 16.0f;

				d.addVertex(
						renderer.screen.fixScaling(x),
						renderer.screen.fixScaling(y),
						r * modR,
						g * modG,
						b * modB,
						uMin,
						vMin
				);
				d.addVertex(
						renderer.screen.fixScaling(x),
						renderer.screen.fixScaling(y + size * 16),
						r * modR,
						g * modG,
						b * modB,
						uMin,
						vMax
				);
				d.addVertex(
						renderer.screen.fixScaling(x + size * w),
						renderer.screen.fixScaling(y + size * 16),
						r * modR,
						g * modG,
						b * modB,
						uMax,
						vMax
				);
				d.addVertex(
						renderer.screen.fixScaling(x + size * w),
						renderer.screen.fixScaling(y),
						r * modR,
						g * modG,
						b * modB,
						uMax,
						vMin
				);
				d.addIndices(2, 3, 0, 0, 1, 2);
			}

			x += (w + charSpacing) * size;
		}

		d.draw();
		Texture.stop();
	}

	public void printCentered(String text, float x, float y) {
		printCentered(text, x, y, 1.0f, 1.0f, 1.0f);
	}

	public void printCentered(String text, float x, float y, float r, float g, float b) {
		printCentered(text, x, y, r, g, b, 1.0f);
	}

	public void printCentered(String text, float x, float y, float r, float g, float b, float size) {
		print(text, x - getStringLength(text) * size / 2.0f, y, r, g, b, size);
	}

	public void printRight(String text, float x, float y) {
		printRight(text, x, y, 1.0f, 1.0f, 1.0f);
	}

	public void printRight(String text, float x, float y, float r, float g, float b) {
		printRight(text, x, y, r, g, b, 1.0f);
	}

	public void printRight(String text, float x, float y, float r, float g, float b, float size) {
		print(text, x - getStringLength(text) * size, y, r, g, b, size);
	}

	public int getStringLength(String text) {
		int x = 0;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '\u00a7' && i < text.length() + 1) {
				c = Character.toLowerCase(text.charAt(++i));
				if (c == 'c' && text.length() - i > 6) {
					i += 6;
					continue;
				}
			}

			if (c > 255 || c == 0 || c == '\n') {
				continue;
			}

			x += charWidths[c] + charSpacing;
		}

		if (x > 0) {
			x -= charSpacing;
		}

		return x;
	}
}
