package display;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.system.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

//import java.awt.Font;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
//import org.newdawn.slick.Color;
//import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;

import world.World;
import constants.Constants;
import math.Vector2f;
import agents.Animal;
import buttons.Button;
import buttons.ButtonKillAll;
import buttons.ButtonRenderAnimals;
import buttons.RegenerateWorld;
import buttons.RenderVision;

public class DisplayHandler {

	//private static TrueTypeFont font;
	//private static Font awtFont;

	static int startY = 0;
	static int startX = 0;
	static float zoomFactor = Constants.INIT_ZOOM;
	static int width = Math.round(Constants.WORLD_SIZE_X/zoomFactor);
	static int height = Math.round(Constants.WORLD_SIZE_Y/zoomFactor);

	private static CountDownLatch pauseLatch = null;

	private static RenderThread renderThread;
	public Thread renderThreadThread;

	public static float[][] terrainColor;

	private static final int PIXELS_X = Constants.PIXELS_X;
	private static final int PIXELS_Y = Constants.PIXELS_Y;

	public Vector2f worldCoordFromWindowCoord(float windowX, float windowY)
	{
		return new Vector2f(0f,0f);
	}

	public Vector2f windowCoordFromWorldCoord(float windowX, float windowY)
	{
		return new Vector2f(0f,0f);
	}

	public DisplayHandler() {
		renderThread = new RenderThread(this);
		renderThreadThread = new Thread(renderThread);
		renderThreadThread.start();
		Button.initAll();
		terrainColor = new float[Constants.WORLD_SIZE][3];
	}

	public void exit() {
		renderThread.stop();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	private static class RenderThread implements Runnable {
		private boolean running;
		private DisplayHandler displayHandler;
		private long window;

		public RenderThread(DisplayHandler displayHandler) {
			this.displayHandler = displayHandler;
		}

		@Override
		public void run() {
			initWindow();
			initOpenGL();
			loadResources();

			while(handleEvents()) {
				render();
				glfwSwapBuffers(window);			
			}

			displayHandler.exit();

			if (pauseLatch != null)
			{
				pauseLatch.countDown();
			}

			System.out.println("Render thread finished.");
		}

		private boolean handleEvents() {
			Button.updateAllButtons();

			if (glfwWindowShouldClose(window)) {
				return false;
			}

			glfwPollEvents();

			return true;
		}

		private void togglePause() {
			if (pauseLatch != null)
			{
				pauseLatch.countDown();
				pauseLatch = null;
			}
			else
			{
				pauseLatch = new CountDownLatch(1);
				main.Main.putEvent(new main.Main.PauseSimulationEvent(pauseLatch));
			}
		}

		private void handleKeyboardEvents(int action, int key) {
			if (action == GLFW_RELEASE) {
				switch (key) {
				case GLFW_KEY_ESCAPE:
					glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
					break;

				case GLFW_KEY_SPACE:
					togglePause();
					break;

				case GLFW_KEY_D:
					startX++;
					if (startX >= Constants.WORLD_SIZE_Y) {
						startX = 0;
					}
					break;

				case GLFW_KEY_W:
					startY--;
					if (startY < 0) {
						startY = Constants.WORLD_SIZE_X-1;
					}
					break;

				case GLFW_KEY_A:
					startX--;
					if (startX < 0) {
						startX = Constants.WORLD_SIZE_Y-1;
					}
					break;

				case GLFW_KEY_S:
					startY++;
					if (startY >= Constants.WORLD_SIZE_X) {
						startY = 0;
					}
					break;
				case GLFW_KEY_Q:
					zoomFactor*=Constants.ZOOM_SPEED;
					if (zoomFactor >= 20) {
						zoomFactor = 20;
					}
					break;
				case GLFW_KEY_E:
					zoomFactor/=Constants.ZOOM_SPEED;
					if (zoomFactor < 1f) {
						zoomFactor = 1f;
					}
					break;
				}
			}
		}

		private void handleMouseEvents(long window, int button, int action, int mods) {
			switch(button){
			case GLFW_MOUSE_BUTTON_1:
			case GLFW_MOUSE_BUTTON_2:
				DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
				DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);
				glfwGetCursorPos(window, posX,posY);

				float xPressed = ((float)posX.get(0))/Constants.PIXELS_X;
				float yPressed = ((float)posY.get(0))/Constants.PIXELS_Y;
				if (xPressed < 1 && xPressed >= 0 && yPressed < 1 && yPressed >= 0) {
					xPressed*=width;
					yPressed*=height;

					int i = startY + Constants.WORLD_SIZE_X * startX;
					for (int x = 0; x < xPressed; ++x, i = World.south[i]);
					for (int y = 0; y < yPressed; ++y, i = World.east[i]);

					if (Animal.containsAnimals[i] == -1) {
						Animal.resurrectAnimal(i, 0f, 1f, 3);
					}
				}

				//System.out.println("posX" + posX.get(0) + " posY" + posY.get(0));
				if ( button == GLFW_MOUSE_BUTTON_1 ){
					//	System.out.println("left mouse :>");
				} else {
					//	System.out.println("right mouse :>");
				}
				break;
			}
		}

		private void render() {
			glClear(GL_COLOR_BUFFER_BIT);

			renderStrings();
			
			renderGui();

			if (Constants.RENDER_TERRAIN) {
				glBegin(GL_QUADS);
				renderTerrain();
				glEnd();
			}

			if (Constants.RENDER_ANIMALS) {
				renderAllAnimals();
			}
		}
		
		private void renderGui() {
			for (Button button : mButtons) {
				Vector2f pos = button.getPosition();
				Vector2f size = button.getSize();
				
				// TODO: Bind texture.
				glBegin(GL_QUADS);
				glVertex2f(pos.x,          pos.y);
				glVertex2f(pos.x + size.x, pos.y);
				glVertex2f(pos.x + size.x, pos.y + size.y);
				glVertex2f(pos.x,          pos.y + size.y);
				glEnd();
			}
		}

		private void renderAllAnimals() {
			width = Math.round(Constants.WORLD_SIZE_X/zoomFactor);
			height = Math.round(Constants.WORLD_SIZE_Y/zoomFactor);
			float pixelsPerNodeX = ((float)Constants.PIXELS_X)/width;
			float pixelsPerNodeY = ((float)Constants.PIXELS_Y)/height;

			glBegin(GL_TRIANGLES);
			int i = startY + Constants.WORLD_SIZE_X * startX; 
			int j = i;
			for (int x = 0; x < width; ++x, j = World.south[j]) {
				i = j;
				for (int y = 0; y < height; ++y, i = World.east[i]) {
					float screenPositionX = x * pixelsPerNodeX;
					float screenPositionY = y * pixelsPerNodeY;

					// RENDER ANIMAL
					int id;
					if ((id = Animal.containsAnimals[i]) != -1) {
						float[] c = Animal.pool[id].color;
						glColor3f(c[0], c[1], c[2]);
						screenPositionX += pixelsPerNodeX/2;
						screenPositionY += pixelsPerNodeY/2;

						glVertex2f(screenPositionX, screenPositionY);
						glVertex2f(screenPositionX + Animal.pool[id].size*pixelsPerNodeX/2, screenPositionY - Animal.pool[id].size*pixelsPerNodeY);
						glVertex2f(screenPositionX - Animal.pool[id].size*pixelsPerNodeX/2, screenPositionY - Animal.pool[id].size*pixelsPerNodeY); 
					}
				}
			}
			glEnd();
		}

		private void renderTerrain() {

			width = Math.round(Constants.WORLD_SIZE_X/zoomFactor);
			height = Math.round(Constants.WORLD_SIZE_Y/zoomFactor);
			float pixelsPerNodeX = ((float)Constants.PIXELS_X)/width;
			float pixelsPerNodeY = ((float)Constants.PIXELS_Y)/height;

			int i = startY + Constants.WORLD_SIZE_X * startX; 
			int j = i;
			for (int x = 0; x < width; ++x, j = World.south[j]) {
				i = j;
				for (int y = 0; y < height; ++y, i = World.east[i]) {
					World.updateColor(terrainColor, i);
					float screenPositionX = x * pixelsPerNodeX;
					float screenPositionY = y * pixelsPerNodeY;
					renderQuad(terrainColor[i],
							screenPositionX, screenPositionY, 
							screenPositionX + pixelsPerNodeX, screenPositionY, 
							screenPositionX + pixelsPerNodeX, screenPositionY + pixelsPerNodeY, 
							screenPositionX, screenPositionY + pixelsPerNodeY);
				}
			}

		}

		private void renderStrings() {
			drawString(PIXELS_X + 20,20, "zoom: " + zoomFactor);
			drawString(PIXELS_X + 20,40, "fps:  " + (int)main.Main.simulationFps);
			drawString(PIXELS_X + 150,40, "seed: " + ((int)noise.Noise.seed-1));
			drawString(PIXELS_X + 20,60, "nAni: " + Animal.numAnimals);
		}

		private void initWindow() {
			GLFWErrorCallback.createPrint(System.err).set();

			// Initialize GLFW. Most GLFW functions will not work before doing this.
			if ( !glfwInit() )
				throw new IllegalStateException("Unable to initialize GLFW");

			// Configure GLFW
			glfwDefaultWindowHints(); // optional, the current window hints are already the default
			glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE); // the window will stay hidden after creation
			glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable

			// Create the window
			window = glfwCreateWindow(PIXELS_X + Constants.PIXELS_SIDEBOARD, PIXELS_Y, "FOXIBAR - DEAD OR ALIVE", NULL, NULL);
			if ( window == NULL ) {
				throw new RuntimeException("Failed to create the GLFW window");
			}

			// Setup a key callback. It will be called every time a key is pressed, repeated or released.
			glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
				handleKeyboardEvents(action, key);
			});


			glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallback() {
				@Override
				public void invoke(long window, int button, int action, int mods) {
					handleMouseEvents(window,button,action, mods);
				}
			});

			//			glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
			//				
			//				@Override
			//				public void invoke(long window, double xpos, double ypos) {
			//					// TODO Auto-generated method stub
			//					
			//				}
			//			};


			try ( MemoryStack stack = stackPush() ) {
				IntBuffer pWidth = stack.mallocInt(1); // int*
				IntBuffer pHeight = stack.mallocInt(1); // int*

				// Get the window size passed to glfwCreateWindow
				glfwGetWindowSize(window, pWidth, pHeight);

				// Get the resolution of the primary monitor
				GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

				// Center the window
				glfwSetWindowPos(
						window,
						(vidmode.width() - pWidth.get(0)) / 2,
						(vidmode.height() - pHeight.get(0)) / 2
						);
			} // the stack frame is popped automatically

			// Make the OpenGL context current
			glfwMakeContextCurrent(window);
			// Enable v-sync
			glfwSwapInterval(1);

			// Make the window visible
			glfwShowWindow(window);
		}

		private void initOpenGL()
		{
			GL.createCapabilities();

			// Initialization code OpenGL
			glEnable(GL_TEXTURE_2D);               

			glClearColor(0.0f, 0.0f, 0.0f, 0.0f);          

			// enable alpha blending
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			glViewport(0,0,PIXELS_X + Constants.PIXELS_SIDEBOARD,PIXELS_Y);

			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(0, PIXELS_X + Constants.PIXELS_SIDEBOARD, PIXELS_Y, 0, 1, -1);
			glMatrixMode(GL_MODELVIEW);

			//Font awtFont = new Font("Courier", Font.PLAIN, 18);
			//font = new TrueTypeFont(awtFont, true);
		}

		public void stop() {
			running = false;
		}
		
		List<Button> mButtons;
		
		private void loadResources() {
			float[] x = new float[5];
			float[] y = new float[5];
			
			for (int i = 0; i < 5; ++i) {
				x[i] = PIXELS_X + 120f*(i+1);
				y[i] = PIXELS_Y - 80f*(i+1);
			}
			
			mButtons = new ArrayList<Button>();
			mButtons.add(new ButtonKillAll       (new Vector2f(x[0], y[0])));
			mButtons.add(new ButtonRenderAnimals (new Vector2f(x[1], y[1])));
			mButtons.add(new RegenerateWorld     (new Vector2f(x[1], y[2])));
			mButtons.add(new RenderVision        (new Vector2f(x[0], y[1])));

			display.Texture defaultTexture = display.Texture.fromFile("/pics/defaultButton.png");
			for (Button button : mButtons) {
				button.setTexture(defaultTexture);
			}
		}
	}

	private static void drawString(int x, int y, String text)
	{
		glEnable(GL_TEXTURE_2D);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glColor3f(1f, 1f, 1f);
		//	font.drawString(new Float(x), new Float(y), text, new Color(1f, 1f, 1f));

		glDisable(GL_BLEND);

	}

	public static void renderTexture(Texture texture,
			float[] cornersX,  float[] cornersY, int numEdges)
	{
		texture.bind();
		glColor3f(1,1,1);
		glBegin(GL_QUADS); {
			for (int i = 0; i < numEdges; i++) {
				glVertex2f(cornersX[i], cornersY[i]);
			}
		} glEnd();
	}
	
	public static void renderQuad
	(float[] color, 
			float corner1X, float corner1Y,
			float corner2X, float corner2Y,
			float corner3X, float corner3Y,
			float corner4X, float corner4Y) {
		glColor3f(color[0], color[1], color[2]);
		glVertex2f(corner1X, corner1Y);
		glVertex2f(corner2X, corner2Y);
		glVertex2f(corner3X, corner3Y);
		glVertex2f(corner4X, corner4Y);
	}

}
