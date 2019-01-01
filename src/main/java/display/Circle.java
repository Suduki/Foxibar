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

public class Circle {
	public Vector2f position = new Vector2f();
	
	private float[] circleVerticesX;
	private float[] circleVerticesY;
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

		for (int i = 0; i < circleVerticesX.length; i++) {
			glVertex2f(position.x, position.y);
			glVertex2f(getXAt(i), getYAt(i));
			if (i+1 < circleVerticesX.length) {
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
		return circleVerticesX[i]*radius + position.x;
	}
	
	public float getScaledXAt(int i, float scales) {
		return circleVerticesX[i]*radius * (scales/2 + 1f/2) + position.x;
	}
	
	public float getYAt(int i) {
		return circleVerticesY[i]*radius + position.y;
	}
	
	public float getScaledYAt(int i, float scales) {
		return circleVerticesY[i]*radius* (scales/2 + 1f/2) + position.y;
	}
	
	public void renderCircle(float alpha, float[] scales) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_TRIANGLES);
		glColor4f(color[0], color[1], color[2], alpha);

		for (int i = 0; i < circleVerticesX.length; i++) {
			glVertex2f(position.x, position.y);
			glVertex2f(getScaledXAt(i, scales[i]), getScaledYAt(i, scales[i]));
			if (i+1 < circleVerticesX.length) {
				glVertex2f(getScaledXAt(i+1, scales[i+1]), getScaledYAt(i+1, scales[i+1]));
			}
			else {
				glVertex2f(getScaledXAt(0, scales[0]), getScaledYAt(0, scales[0]));
			}
		}
		glEnd();
		glDisable(GL_BLEND);
	}
	
	
	public void initCircle(int numVertices) {
		circleVerticesX = new float[numVertices];
		circleVerticesY = new float[numVertices];
		float angle = 0;
		for (int i = 0; i < numVertices; ++i) {
			angle += Math.PI*2 /numVertices;
			circleVerticesX[i] = (float)Math.cos(angle);
			circleVerticesY[i] = (float)Math.sin(angle);
		}
	}

	public void setPos(float x, float y) {
		position.set(x, y);
	}
	
	public void drawBorder() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_LINES);
		glColor4f(0, 0, 0, 1);
		
		for (int i = 0; i < circleVerticesX.length; i++) {
			glVertex2f(getXAt(i), getYAt(i));
			if (i+1 < circleVerticesX.length) {
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

		for (int i = 0; i < circleVerticesX.length; i++) {
			glVertex2f(position.x, position.y);
			glVertex2f(getScaledXAt(i, scales[i]), getScaledYAt(i, scales[i]));
		}
		glEnd();
		glDisable(GL_BLEND);
	}

	public boolean isInside(float x, float y) {
		return position.distance(x, y) <= radius;
	}
}
