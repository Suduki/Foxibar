package vision;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector2i;

import agents.Agent;
import constants.Constants;

public class Vision {
	public Zone[][] zoneGrid;
	
	public final Vector2i zoneSize;
	public final Vector2i zones;
	
	public Vision(int zoneHeight, int zoneWidth) {
		this.zoneSize = new Vector2i(zoneHeight, zoneWidth);
		zones = new Vector2i(Constants.WORLD_SIZE_X/zoneWidth, Constants.WORLD_SIZE_Y/zoneHeight);
		
		zoneGrid = new Zone[zones.x][zones.y];
		for (int i = 0; i < zones.x; ++i) {
			for (int j = 0; j < zones.y; ++j) {
				zoneGrid[i][j] = new Zone();
			}
		}
	}
	
	public void updateNearestNeighbours(Agent a) {
		Vector2f pos = a.pos;
		int zoneX = getZoneXFromPosX(pos.x);
		int zoneY = getZoneYFromPosY(pos.y);
		
		for (int i = 0; i < a.nearbyAgentsDistance.length; ++i) {
			a.nearbyAgentsDistance[i] = -1;
		}
		
		for (int anI = 0; anI < a.nearbyAgents.length; ++anI) {
			a.nearbyAgents[anI] = null;
		}
		
		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j <= 1; ++j) {
				int zoneIX = (zoneX + zones.x + i) % zones.x;
				int zoneIY = (zoneY + zones.y + j) % zones.y;
				for (Agent anI : zoneGrid[zoneIX][zoneIY].agentsInZone) {
					if (anI != a) {
						float d = calculateCircularDistance(pos, anI.pos);
						if (d > Constants.Vision.MAX_DISTANCE_AN_AGENT_CAN_SEE) {
							continue;
						}
						for (int neighId = 0; neighId < a.nearbyAgentsDistance.length; ++neighId) {
							if (a.nearbyAgentsDistance[neighId] == -1) {
								a.nearbyAgentsDistance[neighId] = d;
								a.nearbyAgents[neighId] = anI;
								break;
							}
							else if (d <= a.nearbyAgentsDistance[neighId]) {
								float tmpD = d;
								d = a.nearbyAgentsDistance[neighId];
								a.nearbyAgentsDistance[neighId] = tmpD;
								
								Agent tmpI = anI;
								anI = a.nearbyAgents[neighId];
								a.nearbyAgents[neighId] = tmpI;
							}
						}
					}
				}
			}
			a.closestAgent = a.nearbyAgents[0];
			a.closestAgentDistance = a.nearbyAgentsDistance[0];
		}
	}
	

	public static float calculateCircularDistance(Vector2f pos, Vector2f pos2) {
		return calculateCircularDistance(pos.x, pos.y, pos2.x, pos2.y);
	}

	public static float calculateCircularDistance(float posXFrom, float posYFrom, float posXTo, float posYTo) {
		float xDirect      = Math.abs(posXFrom - posXTo);
		float xThroughWall = Constants.WORLD_SIZE_X - xDirect;
		
		float yDirect      = Math.abs(posYFrom - posYTo);
		float yThroughWall = Constants.WORLD_SIZE_Y - yDirect;
		
		return (float) Math.sqrt(Math.min(xDirect, xThroughWall)*Math.min(xDirect, xThroughWall) +
				Math.min(yDirect, yThroughWall)*Math.min(yDirect, yThroughWall));
	}
	
	private int getZoneXFromPosX(float x) {
		return ((int) x) / zoneSize.x;
	}
	private int getZoneYFromPosY(float y) {
		return ((int) y) / zoneSize.y;
	}
	
	public void updateAgentZone(Agent id) {
		int oldX = (int) id.old.x;
		int oldY = (int) id.old.y;
		int posX = (int) id.pos.x;
		int posY = (int) id.pos.y;
		
		int oldZoneX = getZoneXFromPosX(oldX);
		int oldZoneY = getZoneYFromPosY(oldY);
		int zoneX = getZoneXFromPosX(posX);
		int zoneY = getZoneYFromPosY(posY);
		
		if (oldZoneX != zoneX || oldZoneY != zoneY) {
			removeAgentFromZone(id, oldZoneX, oldZoneY);
			addAgentToZone(id, zoneX, zoneY);
		}
	}
	
	public void addAgentToZone(Agent id) {
		int zoneX = getZoneXFromPosX(id.pos.x);
		int zoneY = getZoneYFromPosY(id.pos.y);
		addAgentToZone(id, zoneX, zoneY);
	}
	public void removeAgentFromZone(Agent id, boolean useOldPos) {
		int zoneX;
		int zoneY;
		if (useOldPos) {
			zoneX = getZoneXFromPosX(id.old.x);
			zoneY = getZoneYFromPosY(id.old.y);
		}
		else {
			zoneX = getZoneXFromPosX(id.pos.x);
			zoneY = getZoneYFromPosY(id.pos.y);
		}
		removeAgentFromZone(id, zoneX, zoneY);
	}
	
	private void addAgentToZone(Agent id, int zoneX, int zoneY) {
//		System.out.println("num before add: " + zoneGrid[zoneX][zoneY].agentsInZone.size()
//				+ "zoneX=" + zoneX + "zoneY=" + zoneY);
		if(!zoneGrid[zoneX][zoneY].agentsInZone.add(id)) {
			System.err.println("Trying to add agent to vision zone, but failed.");
		}
//		System.out.println("num after add: " + zoneGrid[zoneX][zoneY].agentsInZone.size());
	}
	private void removeAgentFromZone(Agent id, int zoneX, int zoneY) {
//		System.out.println("num before remove: " + zoneGrid[zoneX][zoneY].agentsInZone.size()
//				+ "zoneX=" + zoneX + "zoneY=" + zoneY);
		if(!zoneGrid[zoneX][zoneY].agentsInZone.remove(id)) {
			System.err.println("Trying to remove agent from vision zone, but failed.");
		}
//		System.out.println("num after remove: " + zoneGrid[zoneX][zoneY].agentsInZone.size());
	}
	
	public class Zone {
		public ArrayList<Agent> agentsInZone;
		public float[] color;
		
		public Zone() {
			agentsInZone = new ArrayList<>();
			color = new float[3];
			color[0] = Constants.RANDOM.nextFloat();
			color[1] = Constants.RANDOM.nextFloat();
			color[2] = Constants.RANDOM.nextFloat();
		}
	}

	public static void getDirectionOf(Vector2f vel, Vector2f pos, Vector2f pos2) {
		vel.x = pos2.x - pos.x;
		vel.y = pos2.y - pos.y;
		vel.normalize();
	}

}
