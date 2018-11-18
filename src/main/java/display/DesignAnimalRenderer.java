package display;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex2f;
import input.Mouse;

import javax.swing.text.html.HTMLDocument.HTMLReader.SpecialAction;

import main.Main;
import constants.Constants;
import gui.KeyboardState;
import gui.MouseEvent;
import gui.MouseState;
import gui.RegionI;
import simulation.Simulation;
import talents.SkillsRenderCircle;
import talents.Talents;

public class DesignAnimalRenderer implements gui.SceneRegionRenderer {
	
	private RegionI mRegion;
	SkillsRenderCircle skillsRenderCircle;
	
	private int mViewportWidth, mViewportHeight;
	
	Talents talents;
	
	public DesignAnimalRenderer(DisplayHandler mDisplayHandler, Simulation mSimulation) {
		talents = new Talents();
		talents.inheritRandom();
		skillsRenderCircle = new SkillsRenderCircle(200, 100, talents);
	}

	@Override
	public void render(int pViewportWidth, int pViewportHeight) {
		mViewportWidth = pViewportWidth;
		mViewportHeight = pViewportHeight;
		skillsRenderCircle.update(pViewportWidth / 2, pViewportHeight / 2);
		skillsRenderCircle.render();
	}
	
	@Override
	public void setRegion(RegionI region) {
		mRegion = region;
	}

	@Override
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pMouse) {
		return true;
	}

	@Override
	public boolean handleKeyboardEvent(KeyboardState pEvent) {
		return false;
	}

	public void actionIncrease(int i) {
		talents.talentsRelative[i] += 0.2f;
		talents.normalizeAndCalculateActuals();
	}
	
	public void actionSave() {
		Main.animalTypeToSpawn = Main.BRAINLER;
		Talents.typeToSpawn = talents;
		Talents.mutation = 0;
	}
	
	public void actionReset() {
		Talents.typeToSpawn = null;
		Talents.mutation = 0;
	}
}
