package display.hex;

import org.joml.Vector3f;

import agents.Agent;
import agents.AgentManager;
import constants.Constants;
import main.Main;
import simulation.Simulation;

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
		super(Constants.Colors.BLACK, Constants.Colors.WHITE, 6, false, 0, true, false, false);
		animalLowerPos = new Vector3f();
		animalUpperPos = new Vector3f();
	}
	
	public void drawAgents(float heightScale) {
		
		for (AgentManager<?> manager : Main.mSimulation.agentManagers) {
			for (int i = 0; i < manager.alive.size(); ++i) {
				Agent a = manager.alive.get(i);
				if (a == null) break;

				float xLow = retrieveXFromWorldPos(a.pos.x - 1f);
				float x = retrieveXFromWorldPos(a.pos.x);
				float xHigh = retrieveXFromWorldPos(a.pos.x + 1f);
				
				float zLow = retrieveZFromWorldPos(a.pos.y - 1f);
				float z = retrieveZFromWorldPos(a.pos.y);
				float zHigh = retrieveZFromWorldPos(a.pos.y + 1f);
				
				xLow = (xLow + x) / 2;
				xHigh = (xHigh + x) / 2;
				zLow = (zLow + z) / 2;
				zHigh = (zHigh + z) / 2;

				float xLowness = 1f - a.pos.x % 1; 
				float zLowness = 1f - a.pos.y % 1;

				float h = (float)Math.pow(Main.mSimulation.mWorld.terrain.height[(int) a.pos.x][(int) a.pos.y], 1.5) * heightScale;

				groundPos.set(xLow * xLowness + xHigh * (1f - xLowness), h, zLow * zLowness + zHigh * (1f - zLowness));
				if (xHigh >= Simulation.WORLD_SIZE_X || xLow < 0) {
					groundPos.x = x;
				}
				if (zHigh >= Simulation.WORLD_SIZE_Y || zLow < 0) {
					groundPos.z = z;
				}
				
				renderAgentAt(a);
			}
		}
	}
	
	@Override
	protected float heightToRadius(float h, float tubeMaxRadius) {
		return h*tubeMaxRadius;
	}
	
	private float retrieveXFromWorldPos(float x) {
		float hexX = (int)x/2;
		float xpos = x0 + hexX*2*xScale + ((((int)x)%2 == 0) ? -xNudge : xNudge);
		
		return xpos;
	}
	
	private float retrieveZFromWorldPos(float z) {
		float hexZ = (int)z/2; 
		float zpos = z0 + hexZ*zScale + ((((int)z)%2 == 0) ? -zNudge : zNudge);
		
		return zpos;
	}

	void renderAgentAt(Agent a) {

		float[] c2 = a.secondaryColor;
		float[] c = a.color;
		
		setColor(c, c2, 1, 1);

		float sizeScale = 0.7f;
		float size = sizeScale + (1f - sizeScale) * a.size;

		float height = size;
		float width = size / 6;

		renderTube(groundPos, height, width, 0);
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
