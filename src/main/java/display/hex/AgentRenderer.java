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
import world.World;

public class AgentRenderer extends TubeRenderer {
	
	Vector3f animalLowerPos;
	Vector3f animalUpperPos;
	
	private final float x0 = -Simulation.WORLD_SIZE_X/2.0f;
	private final float z0 = -Simulation.WORLD_SIZE_Y/2.0f;
	
	private final float xNudge = (float)(Math.sqrt(3.0f)*0.2f);
	private final float zNudge = 3.0f/9.0f;
	
	private final float xScale = (float)(Math.sqrt(3)*0.5);
	private final float zScale = 1.5f;
	
	public AgentRenderer() {
		super(Constants.Colors.BLACK, Constants.Colors.WHITE, 6, false, 0, true, false);
		animalLowerPos = new Vector3f();
		animalUpperPos = new Vector3f();
	}
	
	public void drawAgents(float heightScale) {
		
		for (AgentManager<?> manager : Main.mSimulation.agentManagers) {
			for (int i = 0; i < manager.alive.size(); ++i) {
				Agent a = manager.alive.get(i);
				if (a == null) break;


				float xLow = a.pos.x - 0.5f;
				float zLow = a.pos.y - 0.5f;
				
				float xHigh = a.pos.x + 0.5f;
				float zHigh = a.pos.y + 0.5f;
				
				if (xHigh >= Simulation.WORLD_SIZE_X || xLow < 0 || zHigh >= Simulation.WORLD_SIZE_Y || zLow < 0) {
					findPixelPosition(pos, a.pos.x, a.pos.y, heightScale);
				}
				else {
					
					findPixelPosition(animalLowerPos, World.wrapX(xLow), World.wrapY(zLow), heightScale);
					findPixelPosition(animalUpperPos, World.wrapX(xHigh), World.wrapY(zHigh), heightScale);
					
					animalLowerPos.mul((1f - (a.pos.x % 1f)), 0.5f, (1f - (a.pos.y % 1f)));
					animalUpperPos.mul((a.pos.x % 1f), 0.5f, (a.pos.y % 1f));
					
					pos.set(animalLowerPos);
					pos.add(animalUpperPos);
				}
				
				renderAgentAt(a);
			}
		}
	}
	
	@Override
	protected float heightToRadius(float h, float tubeMaxRadius) {
		return h;
	}
	
	private void findPixelPosition(Vector3f vec, float x, float z, float heightScale) {
		
		float hexX = (int)x/2;
		float hexZ = (int)z/2; 
		
		float xpos = x0 + hexX*2*xScale + ((((int)x)%2 == 0) ? -xNudge : xNudge);
		float zpos = z0 + hexZ*zScale + ((((int)z)%2 == 0) ? -zNudge : zNudge);

		float h = (float)Math.pow(Main.mSimulation.mWorld.terrain.height[(int) x][(int) z], 1.5);
		
		vec.set(xpos, h*heightScale, zpos);
	}

	void renderAgentAt(Agent a) {

		float[] c2 = a.secondaryColor;
		float[] c = a.color;
		
		setColor(c, c2, 1, 1);

		float sizeScale = 0.7f;
		float size = sizeScale + (1f - sizeScale) * a.size;

		float height = size;
		float width = size / 6;

		renderTube(pos, height, width, 0);
	}

//	private void renderTop(float height, float width, float[] c2) {
//		glColor3f(c2[0],c2[1],c2[2]);
//		for (int i = 0; i < circle.vertices.length; ++i) {
//			glVertex3f(pos.x + circle.getScaledXAt(i, width), pos.y + height, pos.z + circle.getScaledZAt(i, width));
//			glVertex3f(pos.x, pos.y + height, pos.z);
//			glVertex3f(pos.x + circle.getScaledXAt(i+1, width), pos.y + height, pos.z + circle.getScaledZAt(i+1, width));
//		}
//	}
//
//	private void renderSide(float[] c2, float[] c, float height, float width) {
//		for (int i = 0; i < circle.vertices.length; ++i) {
//			glColor3f(c2[0], c2[1], c2[2]);
//			glVertex3f(pos.x + circle.getScaledXAt(i, width), pos.y + height, pos.z + circle.getScaledZAt(i, width));
//			glVertex3f(pos.x + circle.getScaledXAt(i+1, width), pos.y + height, pos.z + circle.getScaledZAt(i+1, width));
//			glColor3f(c[0], c[1], c[2]);
//			glVertex3f(pos.x, pos.y, pos.z);
//		}
//	}
}
