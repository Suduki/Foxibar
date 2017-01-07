package display;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;

import java.awt.Font;

import main.Main;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;

import world.World;
import constants.Constants;
import agents.Animal;
import buttons.Button;
import static constants.Constants.Neighbours.*;

public class DisplayHandler {

	private static TrueTypeFont font;
	private static Font awtFont;
	static int startY = 0;
	static int startX = 0;
	static float zoomFactor = Constants.INIT_ZOOM;
	static int width = Math.round(Constants.WORLD_SIZE_X/zoomFactor);
	static int height = Math.round(Constants.WORLD_SIZE_Y/zoomFactor);
	
	private static RenderThread renderThread;
	public Thread renderThreadThread;
	
	public static float[][] terrainColor;
	
	private static final int PIXELS_X = Constants.PIXELS_X;
	private static final int PIXELS_Y = Constants.PIXELS_Y;
	
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
		
		public RenderThread(DisplayHandler displayHandler) {
			this.displayHandler = displayHandler;
		}
		@Override
		public void run() {
			initializeDisplay();

			running = true;
			while(running) {
				glClear(GL_COLOR_BUFFER_BIT);
				renderStrings();
				renderTerrain();
				renderAllAnimals();
				Button.updateAllButtons();
				Display.update();
				Display.sync(60);
				glClear(GL_COLOR_BUFFER_BIT);
				checkKeyboardForDisplayChanges();
			}

			Display.destroy();
			System.out.println("cake");
		}

		private void renderAllAnimals() {
			width = Math.round(Constants.WORLD_SIZE_X/zoomFactor);
			height = Math.round(Constants.WORLD_SIZE_Y/zoomFactor);
			float pixelsPerNodeX = ((float)Constants.PIXELS_X)/width;
			float pixelsPerNodeY = ((float)Constants.PIXELS_Y)/height;
			
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
						screenPositionX += pixelsPerNodeX/2;
						screenPositionY += pixelsPerNodeY/2;
						renderQuad(Animal.pool[id].color,
								screenPositionX, 
								screenPositionY, 
								screenPositionX, 
								screenPositionY, 
								screenPositionX + Animal.pool[id].size*pixelsPerNodeX/2,
								screenPositionY - Animal.pool[id].size*pixelsPerNodeY,
								screenPositionX - Animal.pool[id].size*pixelsPerNodeX/2,
								screenPositionY - Animal.pool[id].size*pixelsPerNodeY); 
					}
				}
			}
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
		
		private void checkKeyboardForDisplayChanges() {
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				displayHandler.exit();
			}
			if (Display.isCloseRequested()) {
				displayHandler.exit();
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				main.Main.doPause = true;
			}
			else {
				main.Main.doPause = false;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				startX++;
				if (startX >= Constants.WORLD_SIZE_Y) {
					startX = 0;
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				startY--;
				if (startY < 0) {
					startY = Constants.WORLD_SIZE_X-1;
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				startX--;
				if (startX < 0) {
					startX = Constants.WORLD_SIZE_Y-1;
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				startY++;
				if (startY >= Constants.WORLD_SIZE_X) {
					startY = 0;
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				zoomFactor*=1.01f;
				if (zoomFactor >= 20) {
					zoomFactor = 20;
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
				zoomFactor/=1.01f;
				if (zoomFactor < 1f) {
					zoomFactor = 1f;
				}
			}
			if (Mouse.isInsideWindow()) {

				if (Mouse.isButtonDown(0))
				{
					float xPressed = ((float)Mouse.getX())/Constants.PIXELS_X;
					float yPressed = 1 - ((float)Mouse.getY())/Constants.PIXELS_Y;
					if (xPressed < 1 && xPressed >= 0 && yPressed < 1 && yPressed >= 0) {
						xPressed*=width;
						yPressed*=height;
						
						int i = startY + Constants.WORLD_SIZE_X * startX; 
						for (int x = 0; x < xPressed; ++x, i = World.south[i]);
						for (int y = 0; y < yPressed; ++y, i = World.east[i]);
						
						if (Animal.containsAnimals[i] == -1) {
							Animal.resurrectAnimal(i, 0f, 1f);
						}
					}
				}
			}
		}
		
		private void initializeDisplay() {
			try {
				Display.setDisplayMode(new DisplayMode(PIXELS_X + Constants.PIXELS_SIDEBOARD, PIXELS_Y));
				Display.setTitle("FOXIBAR - DEAD OR ALIVE");
				Display.create();
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
			
			// Initialization code OpenGL
	        GL11.glEnable(GL11.GL_TEXTURE_2D);               
	         
	        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);          
	         
	            // enable alpha blending
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

	        GL11.glViewport(0,0,PIXELS_X + Constants.PIXELS_SIDEBOARD,PIXELS_Y);

			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(0, PIXELS_X + Constants.PIXELS_SIDEBOARD, PIXELS_Y, 0, 1, -1);
			glMatrixMode(GL_MODELVIEW);
			
			Font awtFont = new Font("Courier", Font.PLAIN, 18);
			font = new TrueTypeFont(awtFont, true);
		}
		public void stop() {
			running = false;
		}
	}

	private static void drawString(int x, int y, String text)
	{
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor3f(1f, 1f, 1f);
		font.drawString(new Float(x), new Float(y), text, new Color(1f, 1f, 1f));
		
		GL11.glDisable(GL11.GL_BLEND);
		
	}
	
	
	public static void render(Animal animal) {
	}
	
	public static void drawCircle(float x, float y, float r, double startingAngleDeg, 
			double endAngleDeg, int slices, float red, float green, float blue) {
        int radius = (int) r;

        double arcAngleLength = (endAngleDeg - startingAngleDeg) / 360f;

        float[] vertexesX = new float[4];
        float[] vertexesY = new float[4];

        double initAngle = Math.PI / 180f * startingAngleDeg;
        float prevXA = (float) Math.sin(initAngle) * radius;
        float prevYA = (float) Math.cos(initAngle) * radius;

        for(int arcIndex = 0; arcIndex < slices+1; arcIndex++) {
            double angle = Math.PI * 2 * ((float)arcIndex) / ((float)slices);
            angle += Math.PI / 180f;
            angle *= arcAngleLength;
            int index = 0;
            float xa = (float) Math.sin(angle) * radius;
            float ya = (float) Math.cos(angle) * radius;
            vertexesX[index] = x;
            vertexesY[index] = y;
            vertexesX[index+1] = x+prevXA;
            vertexesY[index+1] = y+prevYA;
            vertexesX[index+2] = x+xa;
            vertexesY[index+2] = y+ya;
            vertexesX[index+3] = x;
            vertexesY[index+3] = y;
            
            renderQuad(red, green, blue, vertexesX, vertexesY, 4);
            
            prevXA = xa;
            prevYA = ya;
        }
    }
	
	public static void renderQuad(float red, float green, float blue, 
			float[] cornersX,  float[] cornersY, int numEdges)
	{
		glBegin(GL_QUADS); {
			GL11.glColor3f(red, green, blue);
			for (int i = 0; i < numEdges; ++i) {
				GL11.glVertex2f(cornersX[i], cornersY[i]);
			}
		} glEnd();
	}
	
	public static void renderTexture(Texture texture,
			float[] cornersX,  float[] cornersY, int numEdges)
	{
		org.newdawn.slick.Color.white.bind();
		texture.bind();
		glBegin(GL_QUADS); {
			for (int i = 0; i < numEdges; i++) {
				GL11.glVertex2f(cornersX[i], cornersY[i]);
			}
		} glEnd();
	}

	public static void renderQuad
		(float[] color, 
			float corner1X, float corner1Y,
			float corner2X, float corner2Y,
			float corner3X, float corner3Y,
			float corner4X, float corner4Y) {
		glBegin(GL_QUADS); {
			GL11.glColor3f(color[0], color[1], color[2]);
			GL11.glVertex2f(corner1X, corner1Y);
			GL11.glVertex2f(corner2X, corner2Y);
			GL11.glVertex2f(corner3X, corner3Y);
			GL11.glVertex2f(corner4X, corner4Y);
		} glEnd();
	}

}
