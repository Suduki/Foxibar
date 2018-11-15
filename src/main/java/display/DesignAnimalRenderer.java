package display;

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
import skills.SkillSet;

public class DesignAnimalRenderer implements gui.SceneRegionRenderer {
	

	private float[] circleVerticesX;
	private float[] circleVerticesY;
	private RegionI mRegion;
	
	public DesignAnimalRenderer(DisplayHandler mDisplayHandler, Simulation mSimulation) {
	}

	private void renderCircle(float[] color, float radius, float screenPositionX, float screenPositionY, int numVertices, float alpha) {
//		glEnd();
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//		glBlendFunc(GL_ONE, GL_ONE);
		glBegin(GL_TRIANGLES);
		glColor4f(color[0], color[1], color[2], alpha);

		if (circleVerticesX == null) {
			initCircle(numVertices);
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
//		glBegin(GL_TRIANGLES);
	}
	
	private void initCircle(int numVertices) {
		circleVerticesX = new float[numVertices];
		circleVerticesY = new float[numVertices];
		float angle = 0;
		for (int i = 0; i < numVertices; ++i) {
			angle += Math.PI*2 /numVertices;
			circleVerticesX[i] = (float)Math.cos(angle);
			circleVerticesY[i] = (float)Math.sin(angle);
		}
	}

	@Override
	public void render(int pViewportWidth, int pViewportHeight) {
		renderCircle(Constants.Colors.DesignYourAnimal.BACKGROUND, 200, 250, 250, SkillSet.NUM_SKILLS, 1f);
		renderCircle(Constants.Colors.DesignYourAnimal.MIDDLE, 50, 250, 250, SkillSet.NUM_SKILLS, 1f);
	}
	
	@Override
	public void setRegion(RegionI region) {
		mRegion = region;
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

	public Object actionIncrease(int i) {
		return null;
	}
}
