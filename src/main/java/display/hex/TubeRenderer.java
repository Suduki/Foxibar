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

	protected float[] minColor = new float[4];
	protected float[] maxColor = new float[4];

	private final Circle circle;
	private final Circle nextCircle;

	protected final Vector3f groundPos = new Vector3f();
	protected final Vector3f pos = new Vector3f();
	private final Vector3f nextPos = new Vector3f();

	private final Vector3f windForce;
	private final Vector3f wind;
	private final Vector3f nextWind;

	private final boolean affectedByWind;
	private final float windStiffness;
	
	private final boolean renderRoof;
	private final boolean prettyTubes;

	public TubeRenderer(final float[] minColor, final float[] maxColor, int numVertices, boolean affectedByWind,
			float windStiffness, boolean renderRoof, boolean renderFloor, boolean prettyTubes) {
		super();
		setColor(minColor, maxColor, 1, 1);

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
		
		this.renderRoof = renderRoof;
		this.prettyTubes = prettyTubes;
	}

	protected void setColor(final float[] minColor, final float[] maxColor, float alpha1, float alpha2) {
		for (int i = 0; i < 3; ++i) {
			this.minColor[i] = minColor[i];
			this.maxColor[i] = maxColor[i];
		}
		this.minColor[3] = alpha1;
		this.maxColor[3] = alpha2;
	}

	public void renderTube(Vector3f groundPos, float tubeMaxHeight, float tubeMaxRadius, float startHeight) {
		pos.set(groundPos);
		int numSplits = (int) tubeMaxHeight + 2;

		float splitDistance = tubeMaxHeight / numSplits;

		float nextHeight = startHeight;

		if (affectedByWind) {
			Main.mSimulation.mWorld.wind.getWindForce(groundPos, windForce);
			windForce.y = windStiffness;
			wind.set(windForce).mul(nextHeight, 1f, nextHeight);
		}
		
		float nextRadius = 0;

		for (int split = 0; split < numSplits; ++split) {
			if (affectedByWind) {
				nextHeight += splitDistance;
				nextWind.set(windForce).mul(nextHeight, 1f, nextHeight);
				nextWind.normalize();
				nextWind.mul(splitDistance);
				nextPos.set(pos).add(nextWind);
				float rotationFactor = 0.2f;
				nextCircle.rotateTowards(nextWind.mul(rotationFactor, 1, rotationFactor));
			} else {
				nextPos.set(pos).add(0, splitDistance, 0);
			}
			
			float radius = heightToRadius(((float) split) / numSplits, tubeMaxRadius);
			nextRadius = heightToRadius(((float) split + 1) / numSplits, tubeMaxRadius);

			renderTubeSegment(groundPos, tubeMaxHeight, radius, nextRadius, startHeight);

			if (affectedByWind) {
				circle.set(nextCircle);
				wind.set(nextWind);
			}
			pos.set(nextPos);
		}
		
		if (renderRoof) {
			renderRoof(groundPos, tubeMaxHeight, nextRadius);
		}

		circle.resetCircle();
		nextCircle.resetCircle();
	}

	private void drawLine() {
		glEnd();
		glColor3f(0, 0, 0);
		glBegin(GL_LINES);
		glVertex3f(pos.x, pos.y, pos.z);
		glVertex3f(nextPos.x, nextPos.y, nextPos.z);
		glEnd();
		glBegin(GL_QUADS);
	}
	

	private void renderTubeSegment(Vector3f groundPos, float tubeMaxHeight, float radius, float nextRadius, float startHeight) {
		if (!prettyTubes) {
			float currentRelativeY = ((pos.y + nextPos.y) / 2 - groundPos.y) / tubeMaxHeight;
			setColorForHeight(currentRelativeY);
		}
		
		
		for (int i = 0; i < circle.vertices.length; ++i) {
			float x1 = circle.getScaledXAt(i, radius) + pos.x;
			float y1 = circle.getScaledYAt(i, radius) + pos.y + startHeight;
			float z1 = circle.getScaledZAt(i, radius) + pos.z;

			float x2 = circle.getScaledXAt(i + 1, radius) + pos.x;
			float y2 = circle.getScaledYAt(i + 1, radius) + pos.y + startHeight;
			float z2 = circle.getScaledZAt(i + 1, radius) + pos.z;

			float x1Next = nextCircle.getScaledXAt(i, nextRadius) + nextPos.x;
			float y1Next = nextCircle.getScaledYAt(i, nextRadius) + nextPos.y + startHeight;
			float z1Next = nextCircle.getScaledZAt(i, nextRadius) + nextPos.z;

			float x2Next = nextCircle.getScaledXAt(i + 1, nextRadius) + nextPos.x;
			float y2Next = nextCircle.getScaledYAt(i + 1, nextRadius) + nextPos.y + startHeight;
			float z2Next = nextCircle.getScaledZAt(i + 1, nextRadius) + nextPos.z;
			
			if (prettyTubes) {
				setColorForHeight((y2 - groundPos.y - startHeight) / tubeMaxHeight);
				glVertex3f(x2, y2, z2);
				setColorForHeight((y1 - groundPos.y - startHeight) / tubeMaxHeight);
				glVertex3f(x1, y1, z1);
				setColorForHeight((y1Next - groundPos.y - startHeight) / tubeMaxHeight);
				glVertex3f(x1Next, y1Next, z1Next);
				setColorForHeight((y2Next - groundPos.y - startHeight) / tubeMaxHeight);
				glVertex3f(x2Next, y2Next, z2Next);
			}
			else {
				glVertex3f(x2, y2, z2);
				glVertex3f(x1, y1, z1);
				glVertex3f(x1Next, y1Next, z1Next);
				glVertex3f(x2Next, y2Next, z2Next);
			}
		}
	}
	
	private void renderRoof(Vector3f groundPos, float tubeMaxHeight, float nextRadius) {
		for (int i = 0; i < circle.vertices.length; ++i) {
			float x1 = pos.x;
			float y1 = pos.y;
			float z1 = pos.z;
			
			float x1Next = nextCircle.getScaledXAt(i, nextRadius) + pos.x;
			float y1Next = nextCircle.getScaledYAt(i, nextRadius) + pos.y;
			float z1Next = nextCircle.getScaledZAt(i, nextRadius) + pos.z;
			
			float x2Next = nextCircle.getScaledXAt(i + 1, nextRadius) + pos.x;
			float y2Next = nextCircle.getScaledYAt(i + 1, nextRadius) + pos.y;
			float z2Next = nextCircle.getScaledZAt(i + 1, nextRadius) + pos.z;
			
			glVertex3f(x1, y1, z1);
			glVertex3f(x2Next, y2Next, z2Next);
			glVertex3f(x1Next, y1Next, z1Next);
			glVertex3f(x1, y1, z1);
		}
	}

	private void setColorForHeight(float scaledY) {
//		System.out.println(scaledY);
		glColor4f((minColor[0] * scaledY + maxColor[0] * (1f - scaledY)),
				(minColor[1] * scaledY + maxColor[1] * (1f - scaledY)),
				(minColor[2] * scaledY + maxColor[2] * (1f - scaledY)),
				(minColor[3] * scaledY + maxColor[3] * (1f - scaledY)));
	}

	protected float heightToRadius(float h, float tubeMaxRadius) {
		if (h < 0 || h > 1) {
			System.err.println(this.getClass().getSimpleName() + " received invalid height: " + h);
		}
		return (1f - h) * tubeMaxRadius;
	}
}
