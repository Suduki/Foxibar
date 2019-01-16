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

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Circle {
	public Vector3f position = new Vector3f();
	public Vector3f originalTilt = new Vector3f(0, 1f, 0);
	
	public Vector3f[] vertices;
	
	private final float[] color;
	
	public float radius;
	
	public Circle(int numVertices, float radius, float[] color) {
		vertices = new Vector3f[numVertices];
		for (int i = 0; i < vertices.length; ++i) {
			vertices[i] = new Vector3f();
		}
		resetCircle();
		this.radius = radius;
		this.color = color;
	}
	
	public void renderCircle(float alpha) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_TRIANGLES);
		glColor4f(color[0], color[1], color[2], alpha);

		for (int i = 0; i < vertices.length; i++) {
			glVertex2f(position.x, position.z);
			glVertex2f(getXAt(i), getYAt(i));
			if (i+1 < vertices.length) {
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
		i = i % vertices.length;
		return vertices[i].x*radius + position.x;
	}
	
	public float getScaledXAt(int i, float scales) {
		i = i % vertices.length;
		return vertices[i].x*radius * scales + position.x;
	}
	
	public float getScaledYAt(int i, float scales) {
		i = i % vertices.length;
		return vertices[i].y*radius * scales + position.y;
	}
	
	public float getScaledZAt(int i, float scales) {
		i = i % vertices.length;
		return vertices[i].z*radius * scales + position.z;
	}
	
	public float getYAt(int i) {
		i = i % vertices.length;
		return vertices[i].z*radius + position.z;
	}
	
	
	public void renderCircle(float alpha, float[] scales) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_TRIANGLES);
		glColor4f(color[0], color[1], color[2], alpha);

		for (int i = 0; i < vertices.length; i++) {
			glVertex2f(position.x, position.z);
			glVertex2f(getScaledXAt(i, scales[i]), getScaledZAt(i, scales[i]));
			if (i+1 < vertices.length) {
				glVertex2f(getScaledXAt(i+1, scales[i+1]), getScaledZAt(i+1, scales[i+1]));
			}
			else {
				glVertex2f(getScaledXAt(0, scales[0]), getScaledZAt(0, scales[0]));
			}
		}
		glEnd();
		glDisable(GL_BLEND);
	}
	
	public void resetCircle() {
		float angle = 0;
		for (int i = 0; i < vertices.length; ++i) {
			angle += Math.PI*2 / vertices.length;
			
			vertices[i].set((float)Math.cos(angle), 0f, (float)Math.sin(angle));
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
		
		for (int i = 0; i < vertices.length; i++) {
			glVertex2f(getXAt(i), getYAt(i));
			if (i+1 < vertices.length) {
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

		for (int i = 0; i < vertices.length; i++) {
			glVertex2f(position.x, position.z);
			glVertex2f(getScaledXAt(i, scales[i]), getScaledZAt(i, scales[i]));
		}
		glEnd();
		glDisable(GL_BLEND);
	}

	public boolean isInside(float x, float z) {
		return position.distance(x, 0, z) <= radius;
	}

	public void rotateTowards(Vector3f dir) {
		for (int i = 0; i < vertices.length; ++i) {
			Quaternionf quat = originalTilt.rotationTo(dir, new Quaternionf());
			vertices[i].rotate(quat);
		}
	}
}
