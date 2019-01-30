package display;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.List;

import org.joml.Vector2f;

import agents.Animal;
import agents.AgentManager;
import buttons.Button;
import constants.Constants;
import gui.KeyboardState;
import gui.MouseEvent;
import gui.MouseState;
import gui.RegionI;
import input.Mouse;
import main.Main;
import simulation.Simulation;
import world.World;

public class LegacyRenderer implements gui.SceneRegionRenderer {

	static final int PIXELS_X = Constants.PIXELS_X;
	static final int PIXELS_Y = Constants.PIXELS_Y;
	static int startY = 0;
	static int startX = 0;
	public static float zoomFactor = Constants.INIT_ZOOM;
	static int width = Math.round(Simulation.WORLD_SIZE_X/zoomFactor);
	static int height = Math.round(Simulation.WORLD_SIZE_Y/zoomFactor);
	private static Mouse mouse = new input.Mouse();
	public static float[][][] terrainColor = new float[Simulation.WORLD_SIZE_X][Simulation.WORLD_SIZE_Y][3];
	private Simulation mSimulation;
	private DisplayHandler mDisplayHandler;
	RegionI mRegion;
	
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

public void actionSpawnAnimals() {
	mSimulation.message(new messages.SpawnAnimals());
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
		
		width = Math.round(zoomFactor*Main.mSimulation.WORLD_SIZE_X);
		height = Math.round(zoomFactor*Main.mSimulation.WORLD_SIZE_Y);

		glBegin(GL_TRIANGLES);

		
		for (AgentManager<?> manager : Main.mSimulation.agentManagers) {
			for (int i = 0; i < manager.alive.size(); ++i) {
				Animal a = manager.alive.get(i);
				if (a != null) {
					renderAgent(a);
				}
				else {
					System.err.println("RendererThread sad because the manager.alive list was modified "
							+ "when trying to render. Should not cause crashes though?");
				}
			}
		}
		
		glEnd();
	}
	
	private boolean isWithinView(Animal a) {
		return true;
	}
	
	private void renderAgent(Animal a) {
		if (!isWithinView(a)) return;
		
		if(a == null || a.pos == null) {
			System.err.println("Trying to render bad animal.");
		}
		
		float x = World.wrapX(a.pos.x + getXOffset());
		float y = World.wrapY(a.pos.y + getYOffset());
		
		float pixelsPerNodeX = ((float)Constants.PIXELS_X)/width;
		float pixelsPerNodeY = ((float)Constants.PIXELS_Y)/height;
		float screenPositionX = x * pixelsPerNodeX + pixelsPerNodeX/2;
		float screenPositionY = y * pixelsPerNodeY + pixelsPerNodeY/2;
		if (a != null && shouldThisAnimalBePrinted(a)) {
			float ageFactor = 1f - ((float)a.age)/(a.maxAge);
			float hungerFactor = a.stomach.getRelativeFullness();
			float healthFactor = a.health;
			float size = 0.5f * a.size + 0.5f;
			if (RenderState.RENDER_AGE && RenderState.RENDER_HUNGER && RenderState.RENDER_HEALTH) {
				renderThreePartsOfAnimal(a.secondaryColor, a.color, 
						ageFactor, healthFactor, hungerFactor, 
						size*pixelsPerNodeX, 
						size*pixelsPerNodeY, screenPositionX, screenPositionY);
			}
			else {
				renderTriangle(a.color, a.secondaryColor, size*pixelsPerNodeX, size*pixelsPerNodeY, screenPositionX, screenPositionY);
			}
		}
	}
	
	private boolean shouldThisAnimalBePrinted(Animal tmp) {
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
	
	
	private void renderTriangle(float[] color, float[] secColor, float sizeX, float sizeY, float screenPositionX, float screenPositionY) {
		
		glColor3f(secColor[0], secColor[1], secColor[2]);
		glVertex2f(screenPositionX, screenPositionY);
		
		glColor3f(color[0], color[1], color[2]);
		glVertex2f(screenPositionX + sizeX, screenPositionY - sizeY);
		glVertex2f(screenPositionX - sizeX, screenPositionY - sizeY);
		
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
		
		float topBarMajority = 0.7f;
		
		renderLeftTriangle(colorAnimal, sizeX*factorLeft*topBarMajority, 
				sizeY*factorLeft*topBarMajority, screenPositionX, screenPositionY);
		renderRightTriangle(colorAnimal, sizeX*factorRight*topBarMajority, 
				sizeY*factorRight*topBarMajority, screenPositionX, screenPositionY);
		renderTopBar(colorAnimal, sizeX, sizeY, screenPositionX, screenPositionY, topBarMajority*sizeY, factorTop);
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
		width = Math.round(zoomFactor*Main.mSimulation.WORLD_SIZE_X);
		height = Math.round(zoomFactor*Main.mSimulation.WORLD_SIZE_Y);
		
		int xOffset = getXOffset();
		int yOffset = getYOffset();

		float pixelsPerNodeX = ((float)Constants.PIXELS_X)/width;
		float pixelsPerNodeY = ((float)Constants.PIXELS_Y)/height;

		glBegin(GL_QUADS);
		int i = (yOffset + Main.mSimulation.WORLD_SIZE_X * xOffset) % Main.mSimulation.WORLD_SIZE; 
		int j = i;
		Main.mSimulation.mWorld.updateColor(terrainColor);
		for (int x = 0; x < width; ++x, j = (int) World.wrapX(x + xOffset))
		{
			i = j;
			for (int y = 0; y < height; ++y, i = (int) World.wrapY(y + yOffset))
			{
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
//			xOffset = (int) (Animal.pool[bestId].oldX + (2f - zoomFactor)*Simulation.WORLD_SIZE_X/2);
//			xOffset =  xOffset % Simulation.WORLD_SIZE_X;
//		}
//		else if (RenderState.FOLLOW_GRASSLER && Constants.SpeciesId.BEST_GRASSLER_ID != -1) {
//			bestId = Constants.SpeciesId.BEST_GRASSLER_ID;
//			xOffset = (int) (Animal.pool[bestId].oldX + (2f - zoomFactor)*Simulation.WORLD_SIZE_X/2);
//			xOffset =  xOffset % Simulation.WORLD_SIZE_X;
//		}
//		else {
			xOffset = (int) (x0 * Main.mSimulation.WORLD_SIZE_X);
			for (;xOffset < 0; xOffset+=Main.mSimulation.WORLD_SIZE_X);
			for (;xOffset >= Main.mSimulation.WORLD_SIZE_X; xOffset-=Main.mSimulation.WORLD_SIZE_X);
//		}
		return xOffset;
	}
	
	private int getYOffset() {
		int yOffset = 0;
		int bestId = -1;
//		if (RenderState.FOLLOW_BLOODLING && Constants.SpeciesId.BEST_BLOODLING_ID != -1) {
//			bestId = Constants.SpeciesId.BEST_BLOODLING_ID;
//			yOffset = (int) (Animal.pool[bestId].oldY + (2f - zoomFactor)*Simulation.WORLD_SIZE_Y/2);
//			yOffset =  yOffset % Simulation.WORLD_SIZE_Y;
//
//		}
//		else if (RenderState.FOLLOW_GRASSLER && Constants.SpeciesId.BEST_GRASSLER_ID != -1) {
//			bestId = Constants.SpeciesId.BEST_GRASSLER_ID;
//			yOffset = (int) (Animal.pool[bestId].oldY + (2f - zoomFactor)*Simulation.WORLD_SIZE_Y/2);
//			yOffset =  yOffset % Simulation.WORLD_SIZE_Y;
//		}
//		else {
			yOffset = (int) (y0 * Main.mSimulation.WORLD_SIZE_Y);
			for (;yOffset < 0; yOffset+=Main.mSimulation.WORLD_SIZE_Y);
			for (;yOffset >= Main.mSimulation.WORLD_SIZE_Y; yOffset-=Main.mSimulation.WORLD_SIZE_Y);
//		}
		return yOffset;
	}

	public void handleKeyboardEvents(int action, int key) {
		if (action == GLFW_RELEASE) {
			switch (key) {

			case GLFW_KEY_D:
				startX++;
				if (startX >= Main.mSimulation.WORLD_SIZE_Y) {
					startX = 0;
				}
				break;

			case GLFW_KEY_W:
				startY--;
				if (startY < 0) {
					startY = Main.mSimulation.WORLD_SIZE_X-1;
				}
				break;

			case GLFW_KEY_A:
				startX--;
				if (startX < 0) {
					startX = Main.mSimulation.WORLD_SIZE_Y-1;
				}
				break;

			case GLFW_KEY_S:
				startY++;
				if (startY >= Main.mSimulation.WORLD_SIZE_X) {
					startY = 0;
				}
				break;

			case GLFW_KEY_R:
				zoomFactor = 1.0f;
				x0 = 0;
				y0 = 0;
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
				addAgent(Main.animalTypeToSpawn, 0);
			}
			if (mouse.buttonPressed(GLFW_MOUSE_BUTTON_RIGHT)) {//TODO: correct pos
				addAgent(Main.BLOODLING, 1);
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

		worldPos.x = u*Main.mSimulation.WORLD_SIZE_X;
		worldPos.y = v*Main.mSimulation.WORLD_SIZE_Y;
	}

	boolean insideViewport(Vector2f pos)
	{
		return pos.x >= 0 && pos.x < Constants.PIXELS_X && pos.y >= 0 && pos.y < Constants.PIXELS_Y;
	}

	public void handleMouseEvents(long window, int button, int action, int mods) {
		mouse.setButtonPressed(button, action == GLFW_PRESS);

		if (insideViewport(mouse.getPos())) {
			if (mouse.buttonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
				addAgent(Main.animalTypeToSpawn, 0);
			}
			if (mouse.buttonPressed(GLFW_MOUSE_BUTTON_RIGHT)) {
				addAgent(Main.BLOODLING, 1);
			}
		}
	}
	private Vector2f worldPos = new Vector2f(0, 0);
	private void addAgent(int managerId, int speciesId) {
		mSimulation.message( new messages.Message() {
			Mouse eventmouse = new Mouse(mouse);
			@Override
			public void evaluate(simulation.Simulation simulation) {
				float viewX = eventmouse.getX()/Constants.PIXELS_X;
				float viewY = eventmouse.getY()/Constants.PIXELS_Y;
				worldPosFromViewPos(viewX, viewY);

				Main.mSimulation.spawnAgent((int) worldPos.x, (int) worldPos.y, managerId);
			}

			public String messageName() { return "AddAnimal"; }
		});								
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
		return false;
	}

	@Override
	public void setRegion(RegionI region) {
		mRegion = region;
	}

	public void actionSpawnWithMouse(int i) {
		mDisplayHandler.message(new messages.ActionSpawnWithMouse(i));
	}
}
