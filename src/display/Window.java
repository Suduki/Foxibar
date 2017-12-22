package display;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import constants.Constants;

public class Window {

	public static final int OPENGL_MAJOR_VERSION = 2;
	public static final int OPENGL_MINOR_VERSION = 0;
		
	private long mWindowId;
	private int mWidth;
	private int mHeight;
	private InputHandlerI mInputHandler;
	
	public void swapBuffers() {

		glfwSwapBuffers(mWindowId);
	}
	
	public boolean handleEvents() {

		if (glfwWindowShouldClose(mWindowId)) {
			return false;
		}

		glfwPollEvents();

		return true;
	}
	
	/*
	 * Makes this windows OpenGL context current.
	 */
	public void makeCurrent() {
		glfwMakeContextCurrent(mWindowId);
	}
	
	/*
	 * Will cause all subsequent calls to handleEvents to return false.
	 */
	public void requestClose() {
		
		glfwSetWindowShouldClose(mWindowId, true);
	}
	
	public int getWidth() {
		return mWidth;
	}
	
	public int getHeight() {
		return mHeight;
	}
	
	public float getAspectRatio() {
		return (float)mWidth/(float)mHeight;
	}
	
	public Window(int pWidth, int pHeight, String pTitle, InputHandlerI pInputHandler) {
		mWidth = pWidth;
		mHeight = pHeight;
		mInputHandler = pInputHandler;
		
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, OPENGL_MAJOR_VERSION);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, OPENGL_MINOR_VERSION);

		// Create the window
		mWindowId = glfwCreateWindow(pWidth, pHeight, pTitle, NULL, NULL);
		if (mWindowId == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(mWindowId, (window, key, scancode, action, mods) -> {
			if (mInputHandler != null) mInputHandler.handleKeyboardEvents(action, key);
		});


		glfwSetMouseButtonCallback(mWindowId, (window, button, action, mods) -> {
			if (mInputHandler != null) mInputHandler.handleMouseEvents(window,button,action, mods);
		});

		glfwSetCursorPosCallback(mWindowId, (window, xpos, ypos) -> {
			if (mInputHandler != null) mInputHandler.handleMouseMotion(window, xpos, ypos);
		});

		glfwSetScrollCallback(mWindowId, (window, xoffset, yoffset) -> {
			if (mInputHandler != null) mInputHandler.handleScrollWheel(window, xoffset, yoffset);
		});

		try ( MemoryStack stack = stackPush() ) {
			IntBuffer width = stack.mallocInt(1); // int*
			IntBuffer height = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(mWindowId, width, height);

			// Get the resolution of the primary monitor
			GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					mWindowId,
					(videoMode.width() - width.get(0)) / 2,
					(videoMode.height() - height.get(0)) / 2
					);
		} // the stack frame is popped automatically

		makeCurrent();
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(mWindowId);
	}
}
