package display;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.util.ArrayList;
import java.util.List;

import agents.Animal;
import buttons.Button;
import constants.Constants;
import gui.KeyboardState;
import gui.MouseEvent;
import gui.MouseState;
import input.Mouse;
import math.Vector2f;
import simulation.Simulation;
import world.World;

public class LegacyRenderer implements InputHandlerI, gui.SceneRegionRenderer {

	static final int PIXELS_X = Constants.PIXELS_X;
	static final int PIXELS_Y = Constants.PIXELS_Y;
	private static Texture defaultTexture;
	static int startY = 0;
	static int startX = 0;
	static float zoomFactor = Constants.INIT_ZOOM;
	static int width = Math.round(Constants.WORLD_SIZE_X/zoomFactor);
	static int height = Math.round(Constants.WORLD_SIZE_Y/zoomFactor);
	private static Mouse mouse = new input.Mouse();
	public static float[][] terrainColor;
	private Simulation mSimulation;
	private DisplayHandler mDisplayHandler;
	private boolean mSimulationPaused = false;
	
	private float[] circleVerticesX;
	private float[] circleVerticesY;
	List<Button> mButtons;
	static float x0 = 0;
	static float y0 = 0;
	Button mClickButton = null;

	public LegacyRenderer(Window pWindow, DisplayHandler pDisplayHandler, Simulation pSimulation) {
		mDisplayHandler = pDisplayHandler;
		mSimulation = pSimulation;
		terrainColor = new float[Constants.WORLD_SIZE][3];
		pWindow.setInputHandler(this);
	}
	
	public void loadResources() {
		
		// TODO: This should not be in "loadResources".
		glEnable(GL_TEXTURE_2D);               

		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		glViewport(0,0,PIXELS_X + Constants.PIXELS_SIDEBOARD,PIXELS_Y);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, PIXELS_X + Constants.PIXELS_SIDEBOARD, PIXELS_Y, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		
		float[] x = new float[5];
		float[] y = new float[5];
		
		for (int i = 0; i < 5; ++i) {
			x[i] = PIXELS_X + 120f*(i+1);
			y[i] = PIXELS_Y - 80f*(i+1);
		}
		
		defaultTexture = display.Texture.fromFile("pics/defaultButton.png");
		
		Button button;
		mButtons = new ArrayList<Button>();
		
		button = new Button(x[0], y[0]);
		button.setTexture(display.Texture.fromFile("pics/killAllButtonTexture.png"));
		button.setClickMessage(mSimulation, new messages.KillAllAnimals());
		mButtons.add(button);
		
		button = new Button(x[1], y[1]);
		button.setTexture(display.Texture.fromFile("pics/renderAnimals.png"));
		button.setClickMessage(mDisplayHandler, new messages.ToggleRenderAnimals());
		mButtons.add(button);
		
		button = new Button(x[1], y[2]);
		button.setTexture(display.Texture.fromFile("pics/regenerateWorld.png"));
		button.setClickMessage(mSimulation, new messages.RegenerateWorld());
		mButtons.add(button);
		
		button = new Button(x[1], y[4]);
		button.setTexture(display.Texture.fromFile("pics/savebrain.png"));
		button.setClickMessage(mSimulation, new messages.SaveBrains());
		mButtons.add(button);
		
		button = new Button(x[0], y[4]);
		button.setTexture(display.Texture.fromFile("pics/loadbrain.png"));
		button.setClickMessage(mSimulation, new messages.LoadBrains());
		mButtons.add(button);
		
		button = new Button(x[0], y[1]);
		button.setTexture(defaultTexture);
		mButtons.add(button);
		
	}
	
	@Override // SceneRegionRenderer
	public void render(int pViewportWidth, int pViewportHeight) {
		glDisable(GL_CULL_FACE);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable(GL_TEXTURE_2D);
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, PIXELS_X + Constants.PIXELS_SIDEBOARD, PIXELS_Y, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();

		renderStrings();

		renderGui();

		if (RenderState.RENDER_TERRAIN) {
			renderTerrain();
		}

		if (RenderState.RENDER_ANIMALS) {
			renderAllAnimals();
		}

		if (RenderState.RENDER_VISION) {
			renderVision();
		}
		glDisable(GL_TEXTURE_2D);
		glColor3f(1,1,1);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
	}
	
	private void renderVision() {
		width = Math.round(Constants.WORLD_SIZE_X/zoomFactor);
		height = Math.round(Constants.WORLD_SIZE_Y/zoomFactor);
		float pixelsPerNodeX = ((float)Constants.PIXELS_X)/width;
		float pixelsPerNodeY = ((float)Constants.PIXELS_Y)/height;

		glBegin(GL_LINES);
		for (int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
			if (Animal.pool[id].isAlive) {
				int pos = Animal.pool[id].pos;
				float x = (pos % Constants.WORLD_SIZE_X)*pixelsPerNodeY + pixelsPerNodeY/2;
				float y = (pos / Constants.WORLD_SIZE_X)*pixelsPerNodeX + pixelsPerNodeX/2;
				for (int id2 : Animal.pool[id].nearbyAnimals) {
					if (id2 != -1) {
						int pos2 = Animal.pool[id2].pos;
						float x2 = (pos2 % Constants.WORLD_SIZE_X)*pixelsPerNodeY + pixelsPerNodeY/2;
						float y2 = (pos2 / Constants.WORLD_SIZE_X)*pixelsPerNodeX + pixelsPerNodeX/2;
						float distance = Math.abs(x-x2) + Math.abs(y-y2);
						if (distance < Constants.MAX_DISTANCE_AN_ANIMAL_CAN_SEE) {
							if (Animal.pool[id].species.speciesId == Constants.SpeciesId.BLOODLING) {

								glColor3f(Animal.pool[id].secondaryColor[0], Animal.pool[id].secondaryColor[1], Animal.pool[id].secondaryColor[2]);
								glVertex2f(y, x);
								glVertex2f(y2, x2);
							}
						}
					}
				}
			}
		}
		glEnd();
	}

	private void renderGui()
	{
		//Vector2f m = mouse.getPos();

		glEnable(GL_TEXTURE_2D);
		glColor3f(1,1,1);
		for (Button button : mButtons) {
			Vector2f pos = button.getPosition();
			Vector2f size = button.getSize();
			display.Texture tex = button.getTexture(); 
			if (tex != null)
			{
				tex.bind(0);
			}
			glBegin(GL_QUADS);
			glTexCoord2f(0,0); glVertex2f(pos.x,          pos.y);
			glTexCoord2f(1,0); glVertex2f(pos.x + size.x, pos.y);
			glTexCoord2f(1,1); glVertex2f(pos.x + size.x, pos.y + size.y);
			glTexCoord2f(0,1); glVertex2f(pos.x,          pos.y + size.y);
			glEnd();
		}
		display.Texture.unbind(0);
		glDisable(GL_TEXTURE_2D);
	}

	private void renderAllAnimals() {
		
		width = Math.round(zoomFactor*Constants.WORLD_SIZE_X);
		height = Math.round(zoomFactor*Constants.WORLD_SIZE_Y);
		float pixelsPerNodeX = ((float)Constants.PIXELS_X)/width;
		float pixelsPerNodeY = ((float)Constants.PIXELS_Y)/height;

		glBegin(GL_TRIANGLES);

		int xOffset = getXOffset();
		int yOffset = getYOffset();
		
		int j = yOffset + Constants.WORLD_SIZE_X * xOffset;
		for (int x = 0; x < width; ++x, j = World.south[j]) {
			int i = j;
			for (int y = 0; y < height; ++y, i = World.east[i]) {
				float screenPositionX = x * pixelsPerNodeX + pixelsPerNodeX/2;
				float screenPositionY = y * pixelsPerNodeY + pixelsPerNodeY/2;

				// RENDER ANIMAL
				int id;
				if ((id = Animal.containsAnimals[i]) != -1 && shouldThisAnimalBePrinted(id)) {
//					if (Animal.pool[id].species.speciesId == Constants.SpeciesId.GRASSLER) {
//						renderTriangle(Animal.pool[id].color, Animal.pool[id].size*pixelsPerNodeX, 
//								Animal.pool[id].size*pixelsPerNodeY, screenPositionX, screenPositionY);
//						continue;
//					}
					float ageFactor = 1f - ((float)Animal.pool[id].age)/(Animal.AGE_DEATH);
					float hungerFactor = Animal.pool[id].hunger/(Animal.HUNGRY_HUNGER*2);
					float healthFactor = Animal.pool[id].health;
					if (RenderState.DRAW_VISION_CIRCLE) {
						if (RenderState.FOLLOW_BLOODLING && id == Constants.SpeciesId.BEST_BLOODLING_ID) {
							renderCircle(Animal.pool[id].primaryColor, Constants.MAX_DISTANCE_AN_ANIMAL_CAN_SEE*pixelsPerNodeX, screenPositionX, screenPositionY);
						}
						else if (RenderState.FOLLOW_GRASSLER && id == Constants.SpeciesId.BEST_GRASSLER_ID) {
							renderCircle(Animal.pool[id].primaryColor, Constants.MAX_DISTANCE_AN_ANIMAL_CAN_SEE*pixelsPerNodeX, screenPositionX, screenPositionY);
						}
					}
					if (RenderState.RENDER_AGE && RenderState.RENDER_HUNGER && RenderState.RENDER_HEALTH) {
//						if (Constants.BEST_ID == id) {
						renderThreePartsOfAnimal(Animal.pool[id].secondaryColor, Animal.pool[id].primaryColor, 
								ageFactor, healthFactor, hungerFactor, 
								Animal.pool[id].size*pixelsPerNodeX, 
								Animal.pool[id].size*pixelsPerNodeY, screenPositionX, screenPositionY);
//							renderThreePartsOfAnimal(Animal.pool[id].secondaryColor, Animal.pool[id].mainColor, 
//									ageFactor, healthFactor, hungerFactor, 
//									Animal.pool[id].size*pixelsPerNodeX, 
//									Animal.pool[id].size*pixelsPerNodeY, screenPositionX, screenPositionY);
////						}
////						else {
////							renderTriangle(Animal.pool[id].secondaryColor, Animal.pool[id].size*pixelsPerNodeX, 
////									Animal.pool[id].size*pixelsPerNodeY, screenPositionX, screenPositionY);
////						}
					}
					else if (RenderState.RENDER_AGE && RenderState.RENDER_HUNGER) {
						renderTwoPartsOfAnimal(Animal.pool[id].secondaryColor, Animal.pool[id].primaryColor, 
								ageFactor, hungerFactor, 
								Animal.pool[id].size*pixelsPerNodeX, 
								Animal.pool[id].size*pixelsPerNodeY, screenPositionX, screenPositionY);
					}
					else if (RenderState.RENDER_AGE) {
						renderPartOfAnimal(Animal.pool[id].secondaryColor, Animal.pool[id].primaryColor, ageFactor, 
								Animal.pool[id].size*pixelsPerNodeX, 
								Animal.pool[id].size*pixelsPerNodeY, screenPositionX, screenPositionY);
						
					}
					else if (RenderState.RENDER_HUNGER) {
						renderPartOfAnimal(Animal.pool[id].secondaryColor, Animal.pool[id].primaryColor, hungerFactor, 
								Animal.pool[id].size*pixelsPerNodeX, 
								Animal.pool[id].size*pixelsPerNodeY, screenPositionX, screenPositionY);
					}
					else {
						renderTriangle(Animal.pool[id].primaryColor, Animal.pool[id].size*pixelsPerNodeX, 
								Animal.pool[id].size*pixelsPerNodeY, screenPositionX, screenPositionY);
					}

				}

			}
		}
		glEnd();
	}
	
	private boolean shouldThisAnimalBePrinted(int id) {
		if (!RenderState.LIMIT_VISION) {
			return true;
		}
		else {
			if (RenderState.FOLLOW_BLOODLING) {
				if (Constants.SpeciesId.BEST_BLOODLING_ID == -1) {
					return true;
				}
				for (int nearby : Animal.pool[Constants.SpeciesId.BEST_BLOODLING_ID].nearbyAnimals) {
					if (nearby == id || id == Constants.SpeciesId.BEST_BLOODLING_ID) {
						return true;
					}
				}
			}
			else if (RenderState.FOLLOW_GRASSLER) {
				if (Constants.SpeciesId.BEST_GRASSLER_ID == -1) {
					return true;
				}
				for (int nearby : Animal.pool[Constants.SpeciesId.BEST_GRASSLER_ID].nearbyAnimals) {
					if (nearby == id || id == Constants.SpeciesId.BEST_GRASSLER_ID) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void renderPartOfAnimal(float[] colorBackground, float[] colorAnimal, float factor, float sizeX, float sizeY, float screenPositionX, float screenPositionY) {
		renderTriangle(colorBackground, sizeX, 
				sizeY, screenPositionX, screenPositionY);
		
		if (factor < 1f) {
			renderTriangle(colorAnimal, sizeX*factor, 
					sizeY*factor, screenPositionX, screenPositionY);
		}
	}
	
	private void renderTriangle(float[] color, float sizeX, float sizeY, float screenPositionX, float screenPositionY) {
		glColor3f(color[0], color[1], color[2]);

		glVertex2f(screenPositionX, screenPositionY);
		glVertex2f(screenPositionX + sizeX, screenPositionY - sizeY);
		glVertex2f(screenPositionX - sizeX, screenPositionY - sizeY);
		
	}
	
	private void renderOuterTriangle(float[] color, float sizeX, float sizeY, float screenPositionX, float screenPositionY) {
		glColor3f(color[0], color[1], color[2]);

		glVertex2f(screenPositionX, screenPositionY+1);
		glVertex2f(screenPositionX+2 + sizeX, screenPositionY-1 - sizeY);
		glVertex2f(screenPositionX-2 - sizeX, screenPositionY-1 - sizeY);
		
	}
	
	private void renderCircle(float[] color, float radius, float screenPositionX, float screenPositionY) {
		glEnd();
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//		glBlendFunc(GL_ONE, GL_ONE);
		glBegin(GL_TRIANGLES);
		glColor4f(color[0], color[1], color[2], 0.3f);

		if (circleVerticesX == null) {
			initCircle();
		}
		for (int i = 0; i < circleVerticesX.length; i++) {
			glVertex2f(screenPositionX, screenPositionY);
			glVertex2f(circleVerticesX[i]*radius + screenPositionX, circleVerticesY[i]*radius + screenPositionY);
			if (i+1 < circleVerticesX.length) {
				glVertex2f(circleVerticesX[i+1]*radius + screenPositionX, circleVerticesY[i+1]*radius + screenPositionY);
			}
			else {
				glVertex2f(circleVerticesX[0]*radius + screenPositionX, circleVerticesY[0]*radius + screenPositionY);
			}
		}
		glEnd();
		glDisable(GL_BLEND);
		glBegin(GL_TRIANGLES);
	}
	
	private void initCircle() {
		int numVertices = 20;
		circleVerticesX = new float[numVertices];
		circleVerticesY = new float[numVertices];
		float angle = 0;
		for (int i = 0; i < numVertices; ++i) {
			angle += Math.PI*2 /numVertices;
			circleVerticesX[i] = (float)Math.cos(angle);
			circleVerticesY[i] = (float)Math.sin(angle);
		}
	}
	
	private void renderThreePartsOfAnimal(float[] colorBackground, float[] colorAnimal,
			float factorLeft, float factorTop, float factorRight, float sizeX, float sizeY,
			float screenPositionX, float screenPositionY) {

		renderOuterTriangle(colorBackground, sizeX, 
				sizeY, screenPositionX, screenPositionY);
		
		if (factorLeft > 1f) {
			factorLeft = 1f;
		}
		if (factorTop > 1f) {
			factorTop = 1f;
		}
		if (factorRight > 1f) {
			factorRight = 1f;
		}
		
		float scale = 0.7f;
		
		renderLeftTriangle(colorAnimal, sizeX*factorLeft*scale, 
				sizeY*factorLeft*scale, screenPositionX, screenPositionY);
		renderRightTriangle(colorAnimal, sizeX*factorRight*scale, 
				sizeY*factorRight*scale, screenPositionX, screenPositionY);
		renderTopBar(colorAnimal, sizeX, sizeY, screenPositionX, screenPositionY, scale*sizeY, factorTop);
	}
	
	private void renderTopBar(float[] color, float sizeX, float sizeY, float screenPositionX, float screenPositionY, float height, float factor) {
		glColor3f(color[0], color[1], color[2]);

		glVertex2f(screenPositionX, screenPositionY - height);
		glVertex2f(screenPositionX + sizeX * (2*factor - 1f), screenPositionY - sizeY);
		glVertex2f(screenPositionX - sizeX, screenPositionY - sizeY);
	}

	private void renderTwoPartsOfAnimal(float[] colorBackground, float[] colorAnimal,
			float factorLeft, float factorRight, float sizeX, float sizeY,
			float screenPositionX, float screenPositionY) {
		renderOuterTriangle(colorAnimal, sizeX, 
				sizeY, screenPositionX, screenPositionY);
		renderTriangle(colorBackground, sizeX, 
				sizeY, screenPositionX, screenPositionY);
		
		if (factorLeft > 1f) {
			factorLeft = 1f;
		}
		if (factorRight > 1f) {
			factorRight = 1f;
		}
		
		renderLeftTriangle(colorAnimal, sizeX*factorLeft, 
				sizeY*factorLeft, screenPositionX, screenPositionY);
		renderRightTriangle(colorAnimal, sizeX*factorRight, 
				sizeY*factorRight, screenPositionX, screenPositionY);
	}
	
	private void renderRightTriangle(float[] color, float sizeX, float sizeY, float screenPositionX, float screenPositionY) {
		glColor3f(color[0], color[1], color[2]);

		glVertex2f(screenPositionX, screenPositionY);
		glVertex2f(screenPositionX + sizeX, screenPositionY - sizeY);
		glVertex2f(screenPositionX, screenPositionY - sizeY);
	}
	
	private void renderLeftTriangle(float[] color, float sizeX, float sizeY, float screenPositionX, float screenPositionY) {
		glColor3f(color[0], color[1], color[2]);

		glVertex2f(screenPositionX, screenPositionY);
		glVertex2f(screenPositionX, screenPositionY - sizeY);
		glVertex2f(screenPositionX - sizeX, screenPositionY - sizeY);
	}
	
	private void renderTerrain() {
		width = Math.round(zoomFactor*Constants.WORLD_SIZE_X);
		height = Math.round(zoomFactor*Constants.WORLD_SIZE_Y);
		
		int xOffset = getXOffset();
		int yOffset = getYOffset();

		float pixelsPerNodeX = ((float)Constants.PIXELS_X)/width;
		float pixelsPerNodeY = ((float)Constants.PIXELS_Y)/height;

		glBegin(GL_QUADS);
		int i = (yOffset + Constants.WORLD_SIZE_X * xOffset) % Constants.WORLD_SIZE; 
		int j = i;
		for (int x = 0; x < width; ++x, j = World.south[j])
		{
			i = j;
			for (int y = 0; y < height; ++y, i = World.east[i])
			{
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

		glEnd();
		glColor3f(1,1,1);
	}
	
	private int getXOffset() {
		int xOffset = 0;
		int bestId = -1;
		if (RenderState.FOLLOW_BLOODLING && Constants.SpeciesId.BEST_BLOODLING_ID != -1) {
			bestId = Constants.SpeciesId.BEST_BLOODLING_ID;
			xOffset = (int) (Animal.pool[bestId].oldX + (2f - zoomFactor)*Constants.WORLD_SIZE_X/2);
			xOffset =  xOffset % Constants.WORLD_SIZE_X;
		}
		else if (RenderState.FOLLOW_GRASSLER && Constants.SpeciesId.BEST_GRASSLER_ID != -1) {
			bestId = Constants.SpeciesId.BEST_GRASSLER_ID;
			xOffset = (int) (Animal.pool[bestId].oldX + (2f - zoomFactor)*Constants.WORLD_SIZE_X/2);
			xOffset =  xOffset % Constants.WORLD_SIZE_X;
		}
		else {
			xOffset = (int) (x0 * Constants.WORLD_SIZE_X);
			for (;xOffset < 0; xOffset+=Constants.WORLD_SIZE_X);
			for (;xOffset >= Constants.WORLD_SIZE_X; xOffset-=Constants.WORLD_SIZE_X);
		}
		return xOffset;
	}
	
	private int getYOffset() {
		int yOffset = 0;
		int bestId = -1;
		if (RenderState.FOLLOW_BLOODLING && Constants.SpeciesId.BEST_BLOODLING_ID != -1) {
			bestId = Constants.SpeciesId.BEST_BLOODLING_ID;
			yOffset = (int) (Animal.pool[bestId].oldY + (2f - zoomFactor)*Constants.WORLD_SIZE_Y/2);
			yOffset =  yOffset % Constants.WORLD_SIZE_Y;

		}
		else if (RenderState.FOLLOW_GRASSLER && Constants.SpeciesId.BEST_GRASSLER_ID != -1) {
			bestId = Constants.SpeciesId.BEST_GRASSLER_ID;
			yOffset = (int) (Animal.pool[bestId].oldY + (2f - zoomFactor)*Constants.WORLD_SIZE_Y/2);
			yOffset =  yOffset % Constants.WORLD_SIZE_Y;
		}
		else {
			yOffset = (int) (y0 * Constants.WORLD_SIZE_Y);
			for (;yOffset < 0; yOffset+=Constants.WORLD_SIZE_Y);
			for (;yOffset >= Constants.WORLD_SIZE_Y; yOffset-=Constants.WORLD_SIZE_Y);
		}
		return yOffset;
	}

	private void renderStrings() {
		drawString(PIXELS_X + 20,20, "zoom: " + zoomFactor);
		//drawString(PIXELS_X + 20,40, "fps:  " + (int)main.Main.simulationFps);
		drawString(PIXELS_X + 150,40, "seed: " + ((int)noise.Noise.seed-1));
		drawString(PIXELS_X + 20,60, "nAni: " + Animal.numAnimals);
	}

	private void togglePause() {
		if ((mSimulationPaused ^= true))
		{
			mSimulation.message(new messages.PauseSimulation());
		}
		else
		{
			mSimulation.message(new messages.UnpauseSimulation());
		}
	}

	public void handleKeyboardEvents(int action, int key) {
		if (action == GLFW_RELEASE) {
			switch (key) {
			case GLFW_KEY_ESCAPE:
				//mWindow2.requestClose();
				//mWindow.requestClose();
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

			case GLFW_KEY_K:
				mSimulation.message(new messages.KillAllAnimals());
				break;

			case GLFW_KEY_R:
				zoomFactor = 1.0f;
				x0 = 0;
				y0 = 0;
				break;

			case GLFW_KEY_2:
				utils.FPSLimiter.mWantedFps /= 2;
				break;

			case GLFW_KEY_1:
				utils.FPSLimiter.mWantedFps *= 2;
				break;

			case GLFW_KEY_3:
				utils.FPSLimiter.mWantedFps = Constants.WANTED_FPS;
				break;
			}
		}
	}

	public void handleMouseMotion(long window, double xpos, double ypos)
	{
		mouse.setPosition((float)xpos,  (float)ypos);

		if (insideViewport(mouse.getPos())) {
			if (mouse.buttonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
				addGrassling();
			}
			if (mouse.buttonPressed(GLFW_MOUSE_BUTTON_RIGHT)) {
				addBloodling();
			}
		}
	}

	public void handleScrollWheel(long window, double xoffset, double yoffset)
	{
		System.out.println("Scroll: (" + xoffset + "," + yoffset + ")");


		Vector2f mpos = mouse.getPos();
		if (insideViewport(mpos))
		{
			float dz = 1.0f;
			if (yoffset < 0)
			{
				dz *= Constants.ZOOM_SPEED;
			}
			if (yoffset > 0)
			{
				dz /= Constants.ZOOM_SPEED;
			}

			float tx = mpos.x/Constants.PIXELS_X;
			float ty = mpos.y/Constants.PIXELS_Y;

			float z = zoomFactor*(1.0f - dz);
			x0 = x0 + tx*z;
			y0 = y0 + ty*z;

			zoomFactor *= dz;
		}
	}

	private Vector2f worldPosFromViewPos(float x, float y)
	{
		float u = x0 + zoomFactor*x;
		float v = y0 + zoomFactor*y;
		for (;u < 0; u+=1.0f); for (;u >= 1.0f; u-=1.0f);
		for (;v < 0; v+=1.0f); for (;v >= 1.0f; v-=1.0f);

		return new Vector2f(u*Constants.WORLD_SIZE_X, v*Constants.WORLD_SIZE_Y);
	}

	boolean insideViewport(Vector2f pos)
	{
		return pos.x >= 0 && pos.x < Constants.PIXELS_X && pos.y >= 0 && pos.y < Constants.PIXELS_Y;
	}

	private boolean insideGui(Vector2f pos)
	{
		return pos.x >= Constants.PIXELS_X && pos.x < Constants.WINDOW_WIDTH && pos.y >= 0 && pos.y < Constants.PIXELS_Y;
	}

	private void guiStartClick(Vector2f pos)
	{
		for (Button button : mButtons)
		{
			if (button.insideBounds(pos.x, pos.y))
			{
				mClickButton = button;
				break;
			}
		}
	}

	private void guiEndClick(Vector2f pos)
	{
		if (mClickButton == null)
		{
			return;
		}

		Button endButton = null;
		for (Button button : mButtons)
		{
			if (button.insideBounds(pos.x, pos.y))
			{
				endButton = button;
				break;
			}
		}

		if (mClickButton == endButton)
		{
			mClickButton.click();
			System.err.println("Woho! A button was clicked!");
		}

		mClickButton = null;
	}

	public void handleMouseEvents(long window, int button, int action, int mods) {
		mouse.setButtonPressed(button, action == GLFW_PRESS);

		if (insideViewport(mouse.getPos())) {
			if (mouse.buttonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
				addGrassling();
			}
			if (mouse.buttonPressed(GLFW_MOUSE_BUTTON_RIGHT)) {
				addBloodling();
			}
		}
		else if (insideGui(mouse.getPos()))
		{
			if (action == GLFW_PRESS)
			{
				guiStartClick(mouse.getPos());
			}
			else
			{
				guiEndClick(mouse.getPos());
			}
		}
	}

	private void addGrassling() {
		mSimulation.message( new messages.Message() {
			Mouse eventmouse = new Mouse(mouse);
			@Override
			public void evaluate(simulation.Simulation simulation) {
				float viewX = eventmouse.getX()/Constants.PIXELS_X;
				float viewY = eventmouse.getY()/Constants.PIXELS_Y;

				Vector2f worldPos = worldPosFromViewPos(viewX, viewY);

				int pos = (int)worldPos.x * Constants.WORLD_SIZE_Y + (int)worldPos.y;
				if (Animal.containsAnimals[pos] == -1) {
					Animal.resurrectAnimal(pos, Animal.BIRTH_HUNGER, Constants.Species.GRASSLER, 
							null, Constants.Species.GRASSLER, null);
				}
			}

			public String messageName() { return "AddAnimal"; }
		});								
	}
	
	private void addBloodling() {
		mSimulation.message( new messages.Message() {
			Mouse eventmouse = new Mouse(mouse);
			@Override
			public void evaluate(simulation.Simulation simulation) {
				float viewX = eventmouse.getX()/Constants.PIXELS_X;
				float viewY = eventmouse.getY()/Constants.PIXELS_Y;

				Vector2f worldPos = worldPosFromViewPos(viewX, viewY);

				int pos = (int)worldPos.x * Constants.WORLD_SIZE_Y + (int)worldPos.y;
				if (Animal.containsAnimals[pos] == -1) {
					Animal.resurrectAnimal(pos, Animal.BIRTH_HUNGER, Constants.Species.BLOODLING,  
							null, Constants.Species.BLOODLING, null);
				}
			}

			public String messageName() { return "AddAnimal"; }
		});								
	}

	private static void drawString(int x, int y, String text)
	{
		glEnable(GL_TEXTURE_2D);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_COLOR);
		glColor3f(1f, 1f, 1f);
		//	font.drawString(new Float(x), new Float(y), text, new Color(1f, 1f, 1f));

		glDisable(GL_BLEND);

	}

	public static void renderTexture(Texture texture,
			float[] cornersX,  float[] cornersY, int numEdges)
	{
		texture.bind(0);
		glColor3f(1,1,1);
		glBegin(GL_QUADS); {
			for (int i = 0; i < numEdges; i++) {
				glVertex2f(cornersX[i], cornersY[i]);
			}
		} glEnd();
	}

	public static void renderQuad(float[] color, 
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

	@Override
	public void handleFramebufferSize(long window, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pState) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleKeyboardEvent(KeyboardState pEvent) {
		// TODO Auto-generated method stub
		return false;
	}

}