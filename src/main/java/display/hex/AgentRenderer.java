package display.hex;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glVertex3f;

import org.joml.Vector3f;

import agents.Agent;
import agents.AgentManager;
import constants.Constants;
import display.Circle;
import main.Main;
import simulation.Simulation;

public class AgentRenderer {
	
	Circle circle;
	Vector3f renderAt;
	
	public AgentRenderer() {
		super();
		this.circle = new Circle(6, 1, null);
		renderAt = new Vector3f();
	}
	
	public void drawAgents(float heightScale) {
		float x0 = -Simulation.WORLD_SIZE_X/2.0f;
		float z0 = -Simulation.WORLD_SIZE_Y/2.0f;

		float xNudge = (float)(Math.sqrt(3.0f)*0.2f);
		float zNudge = 3.0f/9.0f;

		float xScale = (float)(Math.sqrt(3)*0.5);
		float zScale = 1.5f;
		
		glBegin(GL_TRIANGLES);
		for (AgentManager<?> manager : Main.mSimulation.agentManagers) {
			for (int i = 0; i < manager.alive.size(); ++i) {
				Agent a = manager.alive.get(i);
				if (a == null) break;
				
				int x = (int) a.pos.x;
				int z = (int) a.pos.y;
				
				int hexX = x/2;
				int hexZ = z/2; 
				
				float xpos = x0 + hexX*2*xScale + ((x%2 == 0) ? -xNudge : xNudge);
				float zpos = z0 + hexZ*zScale + ((z%2 == 0) ? -zNudge : zNudge);

				float h = (float)Math.pow(Main.mSimulation.mWorld.terrain.height[x][z], 1.5);
				
				renderAt.set(xpos, h*heightScale, zpos);
				
				renderAgentAt(a);
			}
		}
		glEnd();
	}
	
	void renderAgentAt(Agent a) {

		float[] c2 = a.secondaryColor;
		float[] c = a.color;

		float sizeScale = 0.7f;
		float size = sizeScale + (1f - sizeScale) * a.size;

		float height = size;
		float width = size / 6;

		renderSide(c2, c, height, width);
		renderTop(height, width, c2);
	}

	private void renderTop(float height, float width, float[] c2) {
		glColor3f(c2[0],c2[1],c2[2]);
		for (int i = 0; i < circle.vertices.length; ++i) {
			glVertex3f(renderAt.x + circle.getScaledXAt(i, width), renderAt.y + height, renderAt.z + circle.getScaledZAt(i, width));
			glVertex3f(renderAt.x, renderAt.y + height, renderAt.z);
			glVertex3f(renderAt.x + circle.getScaledXAt(i+1, width), renderAt.y + height, renderAt.z + circle.getScaledZAt(i+1, width));
		}
	}

	private void renderSide(float[] c2, float[] c, float height, float width) {
		for (int i = 0; i < circle.vertices.length; ++i) {
			glColor3f(c2[0], c2[1], c2[2]);
			glVertex3f(renderAt.x + circle.getScaledXAt(i, width), renderAt.y + height, renderAt.z + circle.getScaledZAt(i, width));
			glVertex3f(renderAt.x + circle.getScaledXAt(i+1, width), renderAt.y + height, renderAt.z + circle.getScaledZAt(i+1, width));
			glColor3f(c[0], c[1], c[2]);
			glVertex3f(renderAt.x, renderAt.y, renderAt.z);
		}
	}
}
