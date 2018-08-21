package vision;

import java.util.ArrayList;

import agents.Agent;
import constants.Constants;

public class Vision {
	public Zone[][] zoneGrid;
	
	public final int zoneHeight;
	public final int zoneWidth;
	public final int zonesX;
	public final int zonesY;
	
	public Vision(int zoneHeight, int zoneWidth) {
		this.zoneHeight = zoneHeight;
		this.zoneWidth = zoneWidth;
		zonesX = Constants.WORLD_SIZE_X/zoneWidth;
		zonesY = Constants.WORLD_SIZE_Y/zoneHeight;
		
		zoneGrid = new Zone[zonesX][zonesY];
		for (int i = 0; i < zonesX; ++i) {
			for (int j = 0; j < zonesY; ++j) {
				zoneGrid[i][j] = new Zone();
			}
		}
	}
	
	public void updateNearestNeighbours(Agent a) {
		int pos = a.pos;
		int zoneX = getZoneXFromPos(pos);
		int zoneY = getZoneYFromPos(pos);
		for (int i = 0; i < a.nearbyAgentsDistance.length; ++i) {
			a.nearbyAgentsDistance[i] = -1;
		}
		
		for (int anI = 0; anI < a.nearbyAgents.length; ++anI) {
			a.nearbyAgents[anI] = null;
		}
		
		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j <= 1; ++j) {
				int zoneIX = (zoneX + zonesX + i) % zonesX;
				int zoneIY = (zoneY + zonesY + j) % zonesY;
				for (Agent anI : zoneGrid[zoneIX][zoneIY].agentsInZone) {
					if (anI != a) {
						float d = calculateDistance(pos, anI.pos);
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
		}
	}
	
	private static boolean WARNING_PRINTED = false;
	public static double calculateCircularDistance(int pos, int pos2) {
		if (!WARNING_PRINTED) {
			System.err.println("USING CIRCULAR DISTANCE. NOT OPTIMAL FOR SIMULATIONS (I THINK)");
			WARNING_PRINTED = true;
		}
		int xDirect      = Math.abs(getXFromPos(pos) - getXFromPos(pos2));
		int xThroughWall = Constants.WORLD_SIZE_X - xDirect;
		
		int yDirect      = Math.abs(getYFromPos(pos) - getYFromPos(pos2));
		int yThroughWall = Constants.WORLD_SIZE_Y - yDirect;
		
		return Math.sqrt(Math.min(xDirect, xThroughWall)*Math.min(xDirect, xThroughWall) +
				Math.min(yDirect, yThroughWall)*Math.min(yDirect, yThroughWall));
	}
	
	public static int calculateDistance(int pos, int pos2) {
		int xDirect      = Math.abs(getXFromPos(pos) - getXFromPos(pos2));
		int xThroughWall = Constants.WORLD_SIZE_X - xDirect;
		
		int yDirect      = Math.abs(getYFromPos(pos) - getYFromPos(pos2));
		int yThroughWall = Constants.WORLD_SIZE_Y - yDirect;
		
		return Math.min(xDirect, xThroughWall) +
				Math.min(yDirect, yThroughWall);
	}
	
	private static int getXFromPos(int pos) {
		return pos % Constants.WORLD_SIZE_X;
	}
	private static int getYFromPos(int pos) {
		return pos / Constants.WORLD_SIZE_X;
	}
	
	private int getZoneXFromPos(int pos) {
		return (pos % Constants.WORLD_SIZE_X) / zoneWidth;
	}
	private int getZoneYFromPos(int pos) {
		return pos / Constants.WORLD_SIZE_X / zoneHeight;
	}
	
	public void updateAgentZone(Agent id) {
		int oldPos = id.oldPos;
		int pos = id.pos;
		
		int oldZoneX = getZoneXFromPos(oldPos);
		int oldZoneY = getZoneYFromPos(oldPos);
		int zoneX = getZoneXFromPos(pos);
		int zoneY = getZoneYFromPos(pos);
		
		if (oldZoneX != zoneX || oldZoneY != zoneY) {
			removeAgentFromZone(id, oldZoneX, oldZoneY);
			addAgentToZone(id, zoneX, zoneY);
		}
	}
	
	public void addAgentToZone(Agent id) {
		int zoneX = getZoneXFromPos(id.pos);
		int zoneY = getZoneYFromPos(id.pos);
		addAgentToZone(id, zoneX, zoneY);
	}
	public void removeAgentFromZone(Agent id, boolean useOldPos) {
		int zoneX;
		int zoneY;
		if (useOldPos) {
			zoneX = getZoneXFromPos(id.oldPos);
			zoneY = getZoneYFromPos(id.oldPos);
		}
		else {
			zoneX = getZoneXFromPos(id.pos);
			zoneY = getZoneYFromPos(id.pos);
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

	public static int getDirectionOf(int pos, int pos2) {
		int pX = getXFromPos(pos) -  getXFromPos(pos2); 
		int pY = getYFromPos(pos) -  getYFromPos(pos2);
		if (Math.abs(pX) > Math.abs(pY)) {
			// Move in x-dir
			if (pX > 0) {
				return Constants.Neighbours.WEST;
			} 
			else {
				return Constants.Neighbours.EAST;
			}
		}
		else {
			if (pY > 0) {
				return Constants.Neighbours.NORTH;
			} 
			else {
				return Constants.Neighbours.SOUTH;
			}
		}
	}

}
