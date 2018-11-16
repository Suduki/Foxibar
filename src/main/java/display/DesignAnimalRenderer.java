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

import javax.swing.text.html.HTMLDocument.HTMLReader.SpecialAction;

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
	
	public DesignAnimalRenderer(DisplayHandler mDisplayHandler, Simulation mSimulation) {
		skillsRenderCircle = new SkillsRenderCircle(200, 100);
	}

	@Override
	public void render(int pViewportWidth, int pViewportHeight) {
		skillsRenderCircle.update(pViewportWidth/2, pViewportHeight/2);
		skillsRenderCircle.render();
	}
	
	@Override
	public void setRegion(RegionI region) {
		mRegion = region;
	}

	@Override
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pMouse) {
		if (pEvent == MouseEvent.BUTTON) {
			int button = pMouse.getButtonIndex();
			int action = pMouse.getButtonState(button) ? GLFW_PRESS : GLFW_RELEASE;
			handleMouseClick(0, button, action, 0);
			
		}
		else if (pEvent == MouseEvent.MOTION) {
			handleMouseMotion(0, pMouse.getPos().x - mRegion.getPos().x, pMouse.getPos().y - mRegion.getPos().y);
		}
		
		return true;
	}

	private void handleMouseMotion(int i, int j, int k) {
		
	}

	private void handleMouseClick(int i, int button, int action, int j) {
	}

	@Override
	public boolean handleKeyboardEvent(KeyboardState pEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	public Object actionIncrease(int i) {
		return null;
	}
}
