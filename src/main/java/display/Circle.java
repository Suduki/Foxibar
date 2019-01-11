package display;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Circle {
	public Vector3f position = new Vector3f();
	
	public float[] xVertices;
	public float[] zVertices;
	private final float[] color;
	
	public float radius;
	
	public Circle(int numVertices, float radius, float[] color) {
		initCircle(numVertices);
		this.radius = radius;
		this.color = color;
	}
	
	public void renderCircle(float alpha) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_TRIANGLES);
		glColor4f(color[0], color[1], color[2], alpha);

		for (int i = 0; i < xVertices.length; i++) {
			glVertex2f(position.x, position.z);
			glVertex2f(getXAt(i), getYAt(i));
			if (i+1 < xVertices.length) {
				glVertex2f(getXAt(i+1), getYAt(i+1));
			}
			else {
				glVertex2f(getXAt(0), getYAt(0));
			}
		}
		glEnd();
		glDisable(GL_BLEND);
	}
	
	public float getXAt(int i) {
		return xVertices[i]*radius + position.x;
	}
	
	public float getScaledXAt(int i, float scales) {
		return xVertices[i]*radius * scales + position.x;
	}
	
	public float getYAt(int i) {
		return zVertices[i]*radius + position.z;
	}
	
	public float getScaledZAt(int i, float scales) {
		return zVertices[i]*radius * scales + position.z;
	}
	
	public void renderCircle(float alpha, float[] scales) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_TRIANGLES);
		glColor4f(color[0], color[1], color[2], alpha);

		for (int i = 0; i < xVertices.length; i++) {
			glVertex2f(position.x, position.z);
			glVertex2f(getScaledXAt(i, scales[i]), getScaledZAt(i, scales[i]));
			if (i+1 < xVertices.length) {
				glVertex2f(getScaledXAt(i+1, scales[i+1]), getScaledZAt(i+1, scales[i+1]));
			}
			else {
				glVertex2f(getScaledXAt(0, scales[0]), getScaledZAt(0, scales[0]));
			}
		}
		glEnd();
		glDisable(GL_BLEND);
	}
	
	
	private void initCircle(int numVertices) {
		xVertices = new float[numVertices];
		zVertices = new float[numVertices];
		float angle = 0;
		for (int i = 0; i < numVertices; ++i) {
			angle += Math.PI*2 /numVertices;
			xVertices[i] = (float)Math.cos(angle);
			zVertices[i] = (float)Math.sin(angle);
		}
	}

	public void setPos(float x, float z) {
		position.set(x, 0, z);
	}
	
	public void drawBorder() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_LINES);
		glColor4f(0, 0, 0, 1);
		
		for (int i = 0; i < xVertices.length; i++) {
			glVertex2f(getXAt(i), getYAt(i));
			if (i+1 < xVertices.length) {
				glVertex2f(getXAt(i+1), getYAt(i+1));
			}
			else {
				glVertex2f(getXAt(0), getYAt(0));
			}
		}
		glEnd();
		glDisable(GL_BLEND);
		
	}
	
	public void drawLinesRadial(float[] scales) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_LINES);
		glColor4f(0, 0, 0, 1);

		for (int i = 0; i < xVertices.length; i++) {
			glVertex2f(position.x, position.z);
			glVertex2f(getScaledXAt(i, scales[i]), getScaledZAt(i, scales[i]));
		}
		glEnd();
		glDisable(GL_BLEND);
	}

	public boolean isInside(float x, float z) {
		return position.distance(x, 0, z) <= radius;
	}
}
