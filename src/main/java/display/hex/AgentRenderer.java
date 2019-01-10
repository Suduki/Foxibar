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

import agents.Agent;
import agents.AgentManager;
import display.Circle;
import main.Main;
import simulation.Simulation;

public class AgentRenderer {
	
	Circle circle;
	
	public AgentRenderer() {
		super();
		this.circle = new Circle(6, 1, null);
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
				renderAgentAt(a, h, xpos, zpos, heightScale);
			}
		}
		glEnd();
	}
	
	void renderAgentAt(Agent a, float h, float x, float z, float heightScale) {

		float[] c2 = a.secondaryColor;
		float[] c = a.color;
		h *= heightScale;

		float sizeScale = 0.7f;
		float size = sizeScale + (1f - sizeScale) * a.size;

		float height = size;
		float width = size / 6;

		renderSide(h, x, z, c2, c, height, width);
		renderTop(h, x, z, height, width, c2);
	}

	private void renderTop(float h, float x, float z, float height, float width, float[] c2) {
		glColor3f(c2[0],c2[1],c2[2]);
		int i;
		for (i = 0; i < circle.xVertices.length-1; ++i) {
			glVertex3f(x + width * circle.xVertices[i], h + height, z + width * circle.zVertices[i]);
			glVertex3f(x, h + height, z);
			glVertex3f(x + width * circle.xVertices[i+1], h + height, z + width * circle.zVertices[i+1]);
		}
		
		glVertex3f(x + width * circle.xVertices[i], h + height, z + width * circle.zVertices[i]);
		glVertex3f(x, h + height, z);
		glVertex3f(x + width * circle.xVertices[0], h + height, z + width * circle.zVertices[0]);
	}

	private void renderSide(float h, float x, float z, float[] c2, float[] c, float height, float width) {
		int i;
		for (i = 0; i < circle.xVertices.length-1; ++i) {
			glColor3f(c2[0],c2[1],c2[2]);
			glVertex3f(x + width * circle.xVertices[i], h + height, z + width * circle.zVertices[i]);
			glVertex3f(x + width * circle.xVertices[i+1], h + height, z + width * circle.zVertices[i+1]);
			glColor3f(c[0],c[1],c[2]);
			glVertex3f(x,h,z);
		}
		
		glColor3f(c2[0],c2[1],c2[2]);
		glVertex3f(x + width * circle.xVertices[i], h + height, z + width * circle.zVertices[i]);
		glVertex3f(x + width * circle.xVertices[0], h + height, z + width * circle.zVertices[0]);
		glColor3f(c[0],c[1],c[2]);
		glVertex3f(x,h,z);
	}
}
