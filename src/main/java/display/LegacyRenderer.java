package display;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.util.ArrayList;
import java.util.List;

import agents.Agent;
import agents.AgentManager;
import agents.Animal;
import buttons.Button;
import constants.Constants;
import gui.KeyboardState;
import gui.MouseEvent;
import gui.MouseState;
import gui.Region;
import input.Mouse;
import main.Main;
import math.Vector2f;
import simulation.Simulation;
import world.World;

public class LegacyRenderer implements gui.SceneRegionRenderer {

	static final int PIXELS_X = Constants.PIXELS_X;
	static final int PIXELS_Y = Constants.PIXELS_Y;
	static int startY = 0;
	static int startX = 0;
	static float zoomFactor = Constants.INIT_ZOOM;
	static int width = Math.round(Constants.WORLD_SIZE_X/zoomFactor);
	static int height = Math.round(Constants.WORLD_SIZE_Y/zoomFactor);
	private static Mouse mouse = new input.Mouse();
	public static float[][][] terrainColor = new float[Constants.WORLD_SIZE_V.x][Constants.WORLD_SIZE_V.y][3];
	private Simulation mSimulation;
	private DisplayHandler mDisplayHandler;
	private boolean mSimulationPaused = false;
	Region mRegion;
	
	private float[] circleVerticesX;
	private float[] circleVerticesY;
	List<Button> mButtons;
	static float x0 = 0;
	static float y0 = 0;

	public LegacyRenderer(DisplayHandler pDisplayHandler, Simulation pSimulation) {
		mDisplayHandler = pDisplayHandler;
		mSimulation = pSimulation;
		//terrainColor = new float[Constants.WORLD_SIZE][3];
		loadResources();
	}
	
	public void loadResources() {
		
		// TODO: This should not be in "loadResources".
		glEnable(GL_TEXTURE_2D);               

		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		glViewport(0,0,PIXELS_X,PIXELS_Y);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, PIXELS_X, PIXELS_Y, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
	}

public void actionKillAllAnimals() {
	mSimulation.message(new messages.KillAllAnimals());
}

public void actionToggleRenderAnimals() {
	mDisplayHandler.message(new messages.ToggleRenderAnimals());
}
public void actionRegenerateWorld() {
	mSimulation.message(new messages.RegenerateWorld());
}

public void actionSaveBrains() {
	mSimulation.message(new messages.SaveBrains());
}
	
public void actionLoadBrains() {
	mSimulation.message(new messages.LoadBrains());
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
		glOrtho(0, PIXELS_X, PIXELS_Y, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();

		Texture.unbind(0);
		//renderStrings();

		if (RenderState.RENDER_TERRAIN) {
			renderTerrain();
		}

		if (RenderState.RENDER_ANIMALS) {
			renderAllAnimals();
		}

		glDisable(GL_TEXTURE_2D);
		glColor3f(1,1,1);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
	}
	
	private void renderAllAnimals() {
		
		width = Math.round(zoomFactor*Constants.WORLD_SIZE_X);
		height = Math.round(zoomFactor*Constants.WORLD_SIZE_Y);

		glBegin(GL_TRIANGLES);

		
		for (AgentManager<?> manager : Main.simulation.agentManagers) {
			for (int i = 0; i < manager.alive.size(); ++i) {
				Agent a = manager.alive.get(i);
				renderAgent(a);
			}
		}
		
		glEnd();
	}
	
	private boolean isWithinView(Agent a) {
		return true;
	}
	
	private void renderAgent(Agent a) {
		if (!isWithinView(a)) return;
		
		float x = World.wrapX(a.pos.x + getXOffset());
		float y = World.wrapY(a.pos.y + getYOffset());
		
		float pixelsPerNodeX = ((float)Constants.PIXELS_X)/width;
		float pixelsPerNodeY = ((float)Constants.PIXELS_Y)/height;
		float screenPositionX = x * pixelsPerNodeX + pixelsPerNodeX/2;
		float screenPositionY = y * pixelsPerNodeY + pixelsPerNodeY/2;
		if (a != null && shouldThisAnimalBePrinted(a)) {
//					if (World.animalManager.pool[id].species.speciesId == Constants.SpeciesId.GRASSLER) {
//						renderTriangle(World.animalManager.pool[id].color, World.animalManager.pool[id].size*pixelsPerNodeX, 
//								World.animalManager.pool[id].size*pixelsPerNodeY, screenPositionX, screenPositionY);
//						continue;
//					}
			float ageFactor = 1f - ((float)a.age)/(a.maxAge);
			float hungerFactor = a.stomach.getRelativeFullness();
			float healthFactor = a.health;
			float size = 0.5f * a.size + 0.5f;
//					if (RenderState.DRAW_VISION_CIRCLE) {
//						if (RenderState.FOLLOW_BLOODLING && id == World.animalManager.species[0].) {
//							renderCircle(id.primaryColor, Constants.MAX_DISTANCE_AN_ANIMAL_CAN_SEE*pixelsPerNodeX, screenPositionX, screenPositionY);
//						}
//						else if (RenderState.FOLLOW_GRASSLER && id == Constants.SpeciesId.BEST_GRASSLER_ID) {
//							renderCircle(World.animalManager.pool[id].primaryColor, Constants.MAX_DISTANCE_AN_ANIMAL_CAN_SEE*pixelsPerNodeX, screenPositionX, screenPositionY);
//						}
//					}
			if (RenderState.RENDER_AGE && RenderState.RENDER_HUNGER && RenderState.RENDER_HEALTH) {
//						if (Constants.BEST_ID == id) {
				renderThreePartsOfAnimal(a.secondaryColor, a.color, 
						ageFactor, healthFactor, hungerFactor, 
						size*pixelsPerNodeX, 
						size*pixelsPerNodeY, screenPositionX, screenPositionY);
//							renderThreePartsOfAnimal(id.secondaryColor, id.mainColor, 
//									ageFactor, healthFactor, hungerFactor, 
//									id.size*pixelsPerNodeX, 
//									id.size*pixelsPerNodeY, screenPositionX, screenPositionY);
////						}
////						else {
////							renderTriangle(id.secondaryColor, id.size*pixelsPerNodeX, 
////									id.size*pixelsPerNodeY, screenPositionX, screenPositionY);
////						}
			}
			else if (RenderState.RENDER_AGE && RenderState.RENDER_HUNGER) {
				renderTwoPartsOfAnimal(a.secondaryColor, a.color, 
						ageFactor, hungerFactor, 
						size*pixelsPerNodeX, 
						size*pixelsPerNodeY, screenPositionX, screenPositionY);
			}
			else if (RenderState.RENDER_AGE) {
				renderPartOfAnimal(a.secondaryColor, a.color, ageFactor, 
						size*pixelsPerNodeX, 
						size*pixelsPerNodeY, screenPositionX, screenPositionY);
				
			}
			else if (RenderState.RENDER_HUNGER) {
				renderPartOfAnimal(a.secondaryColor, a.color, hungerFactor, 
						size*pixelsPerNodeX, 
						size*pixelsPerNodeY, screenPositionX, screenPositionY);
			}
			else {
				renderTriangle(a.color, size*pixelsPerNodeX, 
						size*pixelsPerNodeY, screenPositionX, screenPositionY);
			}
		}
	}
	
	private boolean shouldThisAnimalBePrinted(Agent tmp) {
		return true;
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
		for (int x = 0; x < width; ++x, j = (int) World.wrapX(x + xOffset))
		{
			i = j;
			for (int y = 0; y < height; ++y, i = (int) World.wrapY(y + yOffset))
			{
				Main.simulation.mWorld.updateColor(terrainColor, j, i);
				float screenPositionX = x * pixelsPerNodeX;
				float screenPositionY = y * pixelsPerNodeY;
				renderQuad(terrainColor[j][i],
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
//		int bestId = -1;
//		if (RenderState.FOLLOW_BLOODLING && Constants.SpeciesId.BEST_BLOODLING_ID != -1) {
//			bestId = Constants.SpeciesId.BEST_BLOODLING_ID;
//			xOffset = (int) (Animal.pool[bestId].oldX + (2f - zoomFactor)*Constants.WORLD_SIZE_X/2);
//			xOffset =  xOffset % Constants.WORLD_SIZE_X;
//		}
//		else if (RenderState.FOLLOW_GRASSLER && Constants.SpeciesId.BEST_GRASSLER_ID != -1) {
//			bestId = Constants.SpeciesId.BEST_GRASSLER_ID;
//			xOffset = (int) (Animal.pool[bestId].oldX + (2f - zoomFactor)*Constants.WORLD_SIZE_X/2);
//			xOffset =  xOffset % Constants.WORLD_SIZE_X;
//		}
//		else {
			xOffset = (int) (x0 * Constants.WORLD_SIZE_X);
			for (;xOffset < 0; xOffset+=Constants.WORLD_SIZE_X);
			for (;xOffset >= Constants.WORLD_SIZE_X; xOffset-=Constants.WORLD_SIZE_X);
//		}
		return xOffset;
	}
	
	private int getYOffset() {
		int yOffset = 0;
		int bestId = -1;
//		if (RenderState.FOLLOW_BLOODLING && Constants.SpeciesId.BEST_BLOODLING_ID != -1) {
//			bestId = Constants.SpeciesId.BEST_BLOODLING_ID;
//			yOffset = (int) (Animal.pool[bestId].oldY + (2f - zoomFactor)*Constants.WORLD_SIZE_Y/2);
//			yOffset =  yOffset % Constants.WORLD_SIZE_Y;
//
//		}
//		else if (RenderState.FOLLOW_GRASSLER && Constants.SpeciesId.BEST_GRASSLER_ID != -1) {
//			bestId = Constants.SpeciesId.BEST_GRASSLER_ID;
//			yOffset = (int) (Animal.pool[bestId].oldY + (2f - zoomFactor)*Constants.WORLD_SIZE_Y/2);
//			yOffset =  yOffset % Constants.WORLD_SIZE_Y;
//		}
//		else {
			yOffset = (int) (y0 * Constants.WORLD_SIZE_Y);
			for (;yOffset < 0; yOffset+=Constants.WORLD_SIZE_Y);
			for (;yOffset >= Constants.WORLD_SIZE_Y; yOffset-=Constants.WORLD_SIZE_Y);
//		}
		return yOffset;
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
		xpos = (PIXELS_X) * xpos/mRegion.getSize().x;
		ypos = PIXELS_Y * ypos/mRegion.getSize().y;
		
		mouse.setPosition((float)xpos,  (float)ypos);

		if (insideViewport(mouse.getPos())) {
			if (mouse.buttonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
				addAgent(0, 0);
			}
			if (mouse.buttonPressed(GLFW_MOUSE_BUTTON_RIGHT)) {//TODO: correct pos
				addAgent(1, 1);
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

	private void worldPosFromViewPos(float x, float y)
	{
		float u = x0 + zoomFactor*x;
		float v = y0 + zoomFactor*y;
		for (;u < 0; u+=1.0f); for (;u >= 1.0f; u-=1.0f);
		for (;v < 0; v+=1.0f); for (;v >= 1.0f; v-=1.0f);

		worldPos.x = u*Constants.WORLD_SIZE_X;
		worldPos.y = v*Constants.WORLD_SIZE_Y;
	}

	boolean insideViewport(Vector2f pos)
	{
		return pos.x >= 0 && pos.x < Constants.PIXELS_X && pos.y >= 0 && pos.y < Constants.PIXELS_Y;
	}

	public void handleMouseEvents(long window, int button, int action, int mods) {
		mouse.setButtonPressed(button, action == GLFW_PRESS);

		if (insideViewport(mouse.getPos())) {
			if (mouse.buttonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
				addAgent(0, 0);
			}
			if (mouse.buttonPressed(GLFW_MOUSE_BUTTON_RIGHT)) {
				addAgent(1, 1);
			}
		}
	}

//	private void addGrassling(Species species) {
//		mSimulation.message( new messages.Message() {
//			Mouse eventmouse = new Mouse(mouse);
//			@Override
//			public void evaluate(simulation.Simulation simulation) {
//				float viewX = eventmouse.getX()/Constants.PIXELS_X;
//				float viewY = eventmouse.getY()/Constants.PIXELS_Y;
//
//				Vector2f worldPos = worldPosFromViewPos(viewX, viewY);
//
//				int pos = (int)worldPos.x * Constants.WORLD_SIZE_Y + (int)worldPos.y;
//				if (World.animalManager.containsAnimals[pos] == null) {
//				}
//			}
//
//			public String messageName() { return "AddAnimal"; }
//		});								
//	}
	private Vector2f worldPos = new Vector2f(0, 0);
	private void addAgent(int managerId, int speciesId) {
		mSimulation.message( new messages.Message() {
			Mouse eventmouse = new Mouse(mouse);
			@Override
			public void evaluate(simulation.Simulation simulation) {
				float viewX = eventmouse.getX()/Constants.PIXELS_X;
				float viewY = eventmouse.getY()/Constants.PIXELS_Y;

				worldPosFromViewPos(viewX, viewY);

				Main.simulation.spawnAgent((int) worldPos.x, (int) worldPos.y, managerId);
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
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pMouse) {
		if (pEvent == MouseEvent.BUTTON) {
			int button = pMouse.getButtonIndex();
			int action = pMouse.getButtonState(button) ? GLFW_PRESS : GLFW_RELEASE;
			handleMouseEvents(0, button, action, 0);
			
		}
		else if (pEvent == MouseEvent.MOTION) {
			handleMouseMotion(0, pMouse.getPos().x - mRegion.getPos().x, pMouse.getPos().y - mRegion.getPos().y);
		}
		
		return true;
	}

	@Override
	public boolean handleKeyboardEvent(KeyboardState pKeyboard) {
		System.out.println("Key[" + pKeyboard.getKeyIndex() + "]: " + pKeyboard.getKeyState());
		handleKeyboardEvents(pKeyboard.getKeyState() ? GLFW_PRESS : GLFW_RELEASE, pKeyboard.getKeyIndex());
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRegion(Region region) {
		mRegion = region;
	}
}
