package io.bluestaggo.voxelthing.window;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL33C.glViewport;

public class Window {
	public static final int DEFAULT_WIDTH = 640;
	public static final int DEFAULT_HEIGHT = 360;
	public static final boolean LIMIT_FPS = false;

	private final long handle;
	private final GLFWErrorCallback errorCallback;
	private final GLFWCharCallback charCallback;
	private final GLFWCursorPosCallback cursorPosCallback;
	private final GLFWFramebufferSizeCallback framebufferSizeCallback;
	private final GLFWKeyCallback keyCallback;
	private final GLFWMouseButtonCallback mouseButtonCallback;
	private final GLFWScrollCallback scrollCallback;

	private int width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT;
	private double deltaTime, lastTick, fpsTimer;
	private int fpsCounter, finalFps;

	private double mouseX, mouseY;
	private double mouseDeltaX, mouseDeltaY;
	private double scrollX, scrollY;
	private boolean cursorGrabbed;

	private final KeyState[] keyStates;
	private final KeyState[] mouseStates;
	private final Set<Character> pressedCharacters = new HashSet<>();
	private final Set<Character> pressedCharactersImmutable = Collections.unmodifiableSet(pressedCharacters);

	public Window() {
		keyStates = Stream.generate(KeyState::new).limit(GLFW_KEY_LAST + 1).toArray(KeyState[]::new);
		mouseStates = Stream.generate(KeyState::new).limit(GLFW_MOUSE_BUTTON_LAST + 1).toArray(KeyState[]::new);

		errorCallback = GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit()) {
			throw new IllegalStateException("Could not initialize GLFW!");
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

		handle = glfwCreateWindow(DEFAULT_WIDTH, DEFAULT_HEIGHT, "Voxel Thing", 0, 0);
		if (handle == 0) {
			throw new IllegalStateException("Failed to open window!");
		}
		glfwMakeContextCurrent(handle);

		GL.createCapabilities();
		glViewport(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		glfwSwapInterval(LIMIT_FPS ? 1 : 0);

		charCallback = GLFWCharCallback.create(this::onChar).set(handle);
		cursorPosCallback = GLFWCursorPosCallback.create(this::onCursorPos).set(handle);
		framebufferSizeCallback = GLFWFramebufferSizeCallback.create(this::onFramebufferSize).set(handle);
		keyCallback = GLFWKeyCallback.create(this::onKey).set(handle);
		mouseButtonCallback = GLFWMouseButtonCallback.create(this::onMouseButton).set(handle);
		scrollCallback = GLFWScrollCallback.create(this::onScroll).set(handle);

		try {
			setIcon("/assets/icon.png");
		} catch (IOException ignored) {
		}

		lastTick = glfwGetTime();

		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	public void close() {
		glfwSetWindowShouldClose(handle, true);
	}

	public void destroy() {
		glfwTerminate();
		errorCallback.close();
		charCallback.close();
		cursorPosCallback.close();
		framebufferSizeCallback.close();
		keyCallback.close();
		mouseButtonCallback.close();
		scrollCallback.close();
	}

	private void onChar(long handle, int i) {
		char c = (char) i;
		pressedCharacters.add(c);
	}

	private void onCursorPos(long handle, double x, double y) {
		mouseDeltaX = x - mouseX;
		mouseDeltaY = mouseY - y;
		mouseX = x;
		mouseY = y;
	}

	private void onFramebufferSize(long handle, int width, int height) {
		glViewport(0, 0, width, height);
		this.width = width;
		this.height = height;
	}

	private void onKey(long handle, int key, int scancode, int action, int mods) {
		if (key < 0 || key >= keyStates.length) return;

		keyStates[key].setPressed(action > 0);
	}

	private void onMouseButton(long handle, int button, int action, int mods) {
		mouseStates[button].setPressed(action > 0);
	}

	private void onScroll(long handle, double x, double y) {
		this.scrollX += x;
		this.scrollY += y;
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(handle);
	}

	public void update() {
		mouseDeltaX = 0.0D;
		mouseDeltaY = 0.0D;
		scrollX = 0.0D;
		scrollY = 0.0D;

		for (KeyState keyState : keyStates) {
			keyState.update();
		}
		for (KeyState mouseState : mouseStates) {
			mouseState.update();
		}
		pressedCharacters.clear();

		glfwSwapBuffers(handle);
		glfwPollEvents();

		double currentTime = glfwGetTime();
		deltaTime = currentTime - lastTick;
		lastTick = currentTime;

		fpsTimer += deltaTime;
		fpsCounter++;
		if (fpsTimer > 1.0) {
			fpsTimer %= 1.0;
			finalFps = fpsCounter;
			fpsCounter = 0;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double getDeltaTime() {
		return deltaTime;
	}

	public int getFps() {
		return finalFps;
	}

	public double getMouseX() {
		return mouseX;
	}

	public double getMouseY() {
		return mouseY;
	}

	public double getMouseDeltaX() {
		return mouseDeltaX;
	}

	public double getMouseDeltaY() {
		return mouseDeltaY;
	}

	public double getScrollX() {
		return scrollX;
	}

	public double getScrollY() {
		return scrollY;
	}

	public void grabCursor() {
		glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		cursorGrabbed = true;
	}

	public void ungrabCursor() {
		glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		cursorGrabbed = false;
	}

	public void toggleGrabCursor() {
		if (cursorGrabbed) ungrabCursor();
		else grabCursor();
	}

	public boolean isCursorGrabbed() {
		return cursorGrabbed;
	}

	public void setIcon(String path) throws IOException {
		try (InputStream imageStream = getClass().getResourceAsStream(path);
		     GLFWImage image = GLFWImage.malloc();
		     GLFWImage.Buffer imageBuffer = GLFWImage.malloc(1)) {
			if (imageStream == null) {
				throw new IOException("Image \"" + path + "\" does not exist!");
			}

			// Load image
			BufferedImage bufferedImage = ImageIO.read(imageStream);
			int width = bufferedImage.getWidth();
			int height = bufferedImage.getHeight();

			// Load ARGB data
			int[] data = new int[width * height];
			bufferedImage.getRGB(0, 0, width, height, data, 0, width);

			// Convert ARGB to RGBA for OpenGL usage
			try (MemoryStack stack = MemoryStack.stackPush()) {
				ByteBuffer buffer = stack.malloc(width * height * 4);
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pixel = data[x + y * width];
						buffer.put((byte) ((pixel >> 16) & 0xFF));
						buffer.put((byte) ((pixel >> 8) & 0xFF));
						buffer.put((byte) (pixel & 0xFF));
						buffer.put((byte) ((pixel >> 24) & 0xFF));
					}
				}
				buffer.flip();

				// Set GLFW image
				image.set(bufferedImage.getWidth(), bufferedImage.getHeight(), buffer);
				imageBuffer.put(image);
				glfwSetWindowIcon(handle, imageBuffer);
			}
		}
	}

	public void setTitle(String title) {
		glfwSetWindowTitle(handle, title);
	}

	public boolean isKeyDown(int key) {
		return keyStates[key].isPressed();
	}

	public boolean isKeyJustPressed(int key) {
		return keyStates[key].justPressed();
	}

	public int[] getKeysJustPressed() {
		return IntStream.range(0, keyStates.length)
				.filter(this::isKeyJustPressed)
				.toArray();
	}

	public Set<Character> getCharactersPressed() {
		return pressedCharactersImmutable;
	}

	public boolean isMouseDown(int mouse) {
		return mouseStates[mouse].isPressed();
	}

	public boolean isMouseJustPressed(int mouse) {
		return mouseStates[mouse].justPressed();
	}

	public int[] getMouseButtonsJustPressed() {
		return IntStream.range(0, mouseStates.length)
				.filter(this::isMouseJustPressed)
				.toArray();
	}

	public static double getTimeElapsed() {
		return glfwGetTime();
	}
}
