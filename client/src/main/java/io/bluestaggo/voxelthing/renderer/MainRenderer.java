package io.bluestaggo.voxelthing.renderer;

import io.bluestaggo.voxelthing.Game;
import io.bluestaggo.voxelthing.assets.FontManager;
import io.bluestaggo.voxelthing.assets.TextureManager;
import io.bluestaggo.voxelthing.renderer.draw.Draw2D;
import io.bluestaggo.voxelthing.renderer.draw.Draw3D;
import io.bluestaggo.voxelthing.renderer.screen.Screen;
import io.bluestaggo.voxelthing.renderer.shader.*;
import io.bluestaggo.voxelthing.renderer.world.BlockRenderer;
import io.bluestaggo.voxelthing.renderer.world.Camera;
import io.bluestaggo.voxelthing.renderer.world.EntityRenderer;
import io.bluestaggo.voxelthing.renderer.world.WorldRenderer;
import io.bluestaggo.voxelthing.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;

import static org.lwjgl.opengl.GL33C.*;

public class MainRenderer {
	public final Game game;
	public final Camera camera;
	public final Screen screen;

	public final TextureManager textures;
	public final FontManager fonts;

	public final WorldShader worldShader;
	public final SkyShader skyShader;
	public final CloudShader cloudShader;
	public final ScreenShader screenShader;

	public final Draw3D draw3D;
	public final WorldRenderer worldRenderer;
	public final BlockRenderer blockRenderer;
	public final EntityRenderer entityRenderer;

	public final Draw2D draw2D;

	private final Vector4f fogColor = new Vector4f(0.6f, 0.8f, 1.0f, 1.0f);
	private final Vector4f skyColor = new Vector4f(0.2f, 0.6f, 1.0f, 1.0f);
	private final Framebuffer skyFramebuffer;

	private final Vector3f prevUpdatePos = new Vector3f();

	public MainRenderer(Game game) {
		this.game = game;

		try {
			camera = new Camera(game.window);
			camera.getPosition(prevUpdatePos);
			screen = new Screen(game.window);

			textures = new TextureManager();
			fonts = new FontManager(this);

			worldShader = new WorldShader();
			skyShader = new SkyShader();
			cloudShader = new CloudShader();
			screenShader = new ScreenShader();

			draw3D = new Draw3D(this);
			worldRenderer = new WorldRenderer(this);
			blockRenderer = new BlockRenderer();
			entityRenderer = new EntityRenderer(this);

			draw2D = new Draw2D(this);

			skyFramebuffer = new Framebuffer(game.window.getWidth(), game.window.getHeight());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void draw() {
		screen.updateDimensions();
		skyFramebuffer.resize(game.window.getWidth(), game.window.getHeight());

		if (prevUpdatePos.distance(camera.getPosition()) > 8.0f) {
			worldRenderer.moveRenderers();
			camera.getPosition(prevUpdatePos);
		}

		camera.setFar(worldRenderer.renderDistance * 32);

		glClearColor(skyColor.x, skyColor.y, skyColor.z, skyColor.w);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		if (game.world != null) {
			render3D();
		}
		render2D();
	}

	private void render3D() {
		draw3D.setup();

		Matrix4f view = camera.getView();
		Matrix4f proj = camera.getProj();
		Matrix4f viewProj = proj.mul(view, new Matrix4f());

		try (var state = new GLState()) {
			state.enable(GL_CULL_FACE);
			state.enable(GL_DEPTH_TEST);
			glCullFace(GL_FRONT);

			setupSkyShader(view, proj);
			setupCloudShader(viewProj);
			skyFramebuffer.use();
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			worldRenderer.drawSky();
			Framebuffer.stop();
			worldRenderer.drawSky();

			setupWorldShader(viewProj);
			textures.getMipmappedTexture("/assets/blocks.png").use();
			useSkyTexture(1);
			worldRenderer.draw();

			state.disable(GL_CULL_FACE);

			String skin = game.getSkin();
			game.player.setTexture(skin);
			if (game.showThirdPerson()) {
				entityRenderer.renderEntity(game.player);
			}

			textures.getMipmappedTexture("/assets/environment/clouds.png").use();
			worldRenderer.drawClouds();
			Shader.stop();
		}
	}

	private void render2D() {
		draw2D.setup();

		screenShader.use();
		screenShader.mvp.set(screen.getViewProj());
	}

	private void setupWorldShader(Matrix4f viewProj) {
		worldShader.use();
		worldShader.mvp.set(viewProj);
		setupFogShader(worldShader);
	}

	private void setupSkyShader(Matrix4f view, Matrix4f proj) {
		skyShader.use();
		skyShader.view.set(view);
		skyShader.proj.set(proj);
		skyShader.fogCol.set(fogColor);
		skyShader.skyCol.set(skyColor);
	}

	private void setupCloudShader(Matrix4f viewPoj) {
		cloudShader.use();
		cloudShader.viewProj.set(viewPoj);
		cloudShader.camPos.set(camera.getPosition());
		cloudShader.camFar.set(camera.getFar());
		cloudShader.ticks.set((float) Window.getTimeElapsed());
	}

	public void setupFogShader(BaseFogShader shader) {
		shader.setupFog((float) game.window.getWidth(),
				(float) game.window.getHeight(),
				camera.getPosition(),
				camera.getFar());
	}

	public void useSkyTexture(int i) {
		glActiveTexture(GL_TEXTURE0 + i);
		glBindTexture(GL_TEXTURE_2D, skyFramebuffer.getTexture());
		glActiveTexture(GL_TEXTURE0);
	}

	public void unload() {
		textures.clear();

		draw2D.unload();

		draw3D.unload();
		worldRenderer.unload();

		skyShader.unload();
		worldShader.unload();
		skyFramebuffer.unload();
	}
}
