package display.hex;

import main.Main;


import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3f;

import org.joml.Vector3f;

import display.Circle;

public abstract class TubeRenderer {
	
	private final float[] minColor;
	private final float[] maxColor;
	
	private final Circle circle;
	private final Circle nextCircle;
	
	private final Vector3f direction = new Vector3f();
	private final Vector3f nextDirection = new Vector3f();
	
	protected final Vector3f pos = new Vector3f();
	private final Vector3f nextPos = new Vector3f();
	
	private final Vector3f windForce;
	private final Vector3f wind;
	private final Vector3f nextWind;
	
	private final boolean affectedByWind;
	private final float windStiffness;
	
	public TubeRenderer(final float[] minColor, final float[] maxColor, int numVertices, 
			boolean affectedByWind, float windStiffness, boolean renderRoof, boolean renderFloor) {
		super();
		this.minColor = minColor;
		this.maxColor = maxColor;
		
		circle = new Circle(numVertices, 1, null);
		nextCircle = new Circle(numVertices, 1, null);
		
		this.affectedByWind = affectedByWind;
		
		if (affectedByWind) {
			windForce = new Vector3f();
			wind = new Vector3f();
			nextWind = new Vector3f();
			this.windStiffness = windStiffness;
		} else {
			windForce = null;
			wind = null;
			nextWind = null;
			this.windStiffness = 0;
		}
	}
	
	public void renderTube(Vector3f groundPos, float tubeMaxHeight, float tubeMaxRadius, float startHeight) {
		pos.set(groundPos);
		int numSplits = (int) tubeMaxHeight + 2;
		
		float splitDistance = tubeMaxHeight / numSplits;
		
		float currentHeight = startHeight;
		
		if (affectedByWind) {
			initWind(groundPos, windStiffness, currentHeight);
		}
		
		for (int split = 0; split < numSplits; ++split) {
			if (affectedByWind) {
				updatePositionFromWind(splitDistance, currentHeight);
				nextPos.set(nextWind).mul(splitDistance);
			}
			else {
				nextPos.set(0, splitDistance, 0);
			}
			float radius = heightToRadius(((float)split), tubeMaxRadius);
			float nextRadius = heightToRadius(((float)split + 1) / numSplits, tubeMaxRadius);
			
			drawLine();
			
			circle.set(nextCircle);
			wind.set(nextWind);
			pos.set(nextPos);
		}
		
		circle.resetCircle();
		nextCircle.resetCircle();
	}

	private void updatePositionFromWind(float splitDistance, float currentHeight) {
		float nextWindHeight = currentHeight + splitDistance;
		nextWind.set(windForce).mul(nextWindHeight, 1f, nextWindHeight);
		nextWind.normalize();
		nextCircle.rotateTowards(nextWind);
	}

	private void initWind(Vector3f groundPos, float stiffness, float currentHeight) {
		Main.mSimulation.mWorld.wind.getWindForce(groundPos, windForce);
		windForce.y = stiffness;
		wind.set(windForce).mul(currentHeight, 1f, currentHeight);
	}
	
	private void drawLine() {
		glColor3f(0, 0, 0);
		glBegin(GL_LINES);
		glVertex3f(pos.x, pos.y, pos.z);
		glVertex3f(nextPos.x, nextPos.y, nextPos.z);
		glEnd();
	}

	private void renderTubeSegment() {
		System.err.println("renderTubeSegment not implemented");
	}
	
	private float heightToRadius(float h, float tubeMaxRadius) {
		if (h < 0 || h > 1) {
			System.err.println(this.getClass().getSimpleName() + " received invalid height: " + h);
		}
		return (1f - h) * tubeMaxRadius;
	}
}
