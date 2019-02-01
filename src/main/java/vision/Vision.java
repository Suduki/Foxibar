package vision;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector2i;

import simulation.Simulation;
import agents.Agent;
import agents.Animal;
import constants.Constants;
import plant.Tree;

public class Vision {
	public Zone[][] zoneGrid;
	
	public final Vector2i zoneSize;
	public final Vector2i zones;
	
	public Vision(int zoneHeight, int zoneWidth) {
		System.out.println("Initializing Vision with Height = " + zoneHeight + " Width = " + zoneHeight);
		System.out.println("WORLD_SIZE_X = " + Simulation.WORLD_SIZE_X + " WORLD_SIZE_Y = " + Simulation.WORLD_SIZE_Y);
		if (zoneHeight < 4) zoneHeight = 4;
		if (zoneWidth < 4) zoneWidth = 4;
		this.zoneSize = new Vector2i(zoneHeight, zoneWidth);
		zones = new Vector2i(Simulation.WORLD_SIZE_X/zoneWidth, Simulation.WORLD_SIZE_Y/zoneHeight);
		
		zoneGrid = new Zone[zones.x][zones.y];
		for (int i = 0; i < zones.x; ++i) {
			for (int j = 0; j < zones.y; ++j) {
				zoneGrid[i][j] = new Zone();
			}
		}
	}
	
	public void updateNearestNeighbours(Animal a) {
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
				Zone zone = zoneGrid[zoneIX][zoneIY];
				
				updateNearestAnimalNeighboursInZone(a, zone);
				updateTreeScoresInZone(a, zone);
			}
		}
		
		updateStrangerAndFriendler(a);
	}

	private void updateTreeScoresInZone(Animal a, Zone zone) {
		for (Tree tree : zone.treesInZone) {
			float score = tree.leafness / (1f + calculateCircularDistance(a.pos, tree.pos));
			if (score > Constants.Vision.MAX_DISTANCE_AN_AGENT_CAN_SEE) {
				continue;
			}
			for (int neighId = 0; neighId < a.nearbyTreesScore.length; ++neighId) {
				if (a.nearbyTreesScore[neighId] == -1) {
					a.nearbyTreesScore[neighId] = score;
					a.nearbyTrees[neighId] = tree;
					break;
				} else if (score <= a.nearbyTreesScore[neighId]) {
					float tmpD = score;
					score = a.nearbyTreesScore[neighId];
					a.nearbyTreesScore[neighId] = tmpD;

					Tree tmpTree = tree;
					tree = a.nearbyTrees[neighId];
					a.nearbyTrees[neighId] = tmpTree;
				}
			}
		}
	}

	private void updateNearestAnimalNeighboursInZone(Animal a, Zone zone) {
		for (Animal anI : zone.agentsInZone) {
			if (anI != a) {
				float d = calculateCircularDistance(a.pos, anI.pos);
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
						
						Animal tmpI = anI;
						anI = a.nearbyAgents[neighId];
						a.nearbyAgents[neighId] = tmpI;
					}
				}
			}
		}
	}

	private void updateStrangerAndFriendler(Animal a) {
		a.stranger = null;
		a.friendler = null;
		
		for (int i = 0; i < a.nearbyAgents.length; ++i) {
			Animal n = a.nearbyAgents[i];
			if (n == null || (a.stranger != null && a.friendler == null)) {
				break;
			}
			if (a.nearbyAgentsDistance[i] < 0 || Float.isNaN(a.nearbyAgentsDistance[i])) {
				continue;
			}
			if (a.stranger == null) {
				if (!a.isCloselyRelatedTo(n)) {
					a.stranger = n;
				}
			}
			if (a.friendler == null) {
				if (a.isCloselyRelatedTo(n)) {
					a.friendler = n;
				}
			}
		}
	}

	public static void getDirectionOf(Vector2f vel, Vector2f pos, Vector2f pos2) {
		float xDirect      = Math.abs(pos2.x - pos.x);
		float xThroughWall = Simulation.WORLD_SIZE_X - xDirect;
		
		float yDirect      = Math.abs(pos2.y - pos.y);
		float yThroughWall = Simulation.WORLD_SIZE_Y - yDirect;
		
		vel.x = pos2.x - pos.x;
		vel.y = pos2.y - pos.y;
		if (xDirect > xThroughWall) vel.x = - vel.x;
		if (yDirect > yThroughWall) vel.y = - vel.y;
		if (vel.lengthSquared() > 0) vel.normalize();
	}

	public static float calculateCircularDistance(Vector2f pos, Vector2f pos2) {
		return calculateCircularDistance(pos.x, pos.y, pos2.x, pos2.y);
	}

	public static float calculateCircularDistance(float posXFrom, float posYFrom, float posXTo, float posYTo) {
		float xDirect      = Math.abs(posXFrom - posXTo);
		float xThroughWall = Simulation.WORLD_SIZE_X - xDirect;
		
		float yDirect      = Math.abs(posYFrom - posYTo);
		float yThroughWall = Simulation.WORLD_SIZE_Y - yDirect;
		
		return (float) Math.sqrt(Math.min(xDirect, xThroughWall)*Math.min(xDirect, xThroughWall) +
				Math.min(yDirect, yThroughWall)*Math.min(yDirect, yThroughWall));
	}
	
	private int getZoneXFromPosX(float x) {
		return ((int) x) / zoneSize.x;
	}
	private int getZoneYFromPosY(float y) {
		return ((int) y) / zoneSize.y;
	}
	
	public void updateAgentZone(Animal id) {
		int oldX = (int) id.oldPos.x;
		int oldY = (int) id.oldPos.y;
		int posX = (int) id.pos.x;
		int posY = (int) id.pos.y;
		
		int oldZoneX = getZoneXFromPosX(oldX);
		int oldZoneY = getZoneYFromPosY(oldY);
		int zoneX = getZoneXFromPosX(posX);
		int zoneY = getZoneYFromPosY(posY);
		
		if (oldZoneX != zoneX || oldZoneY != zoneY) {
			removeAnimalFromZone(id, oldZoneX, oldZoneY);
			addAnimalToZone(id, zoneX, zoneY);
		}
	}
	
	public void addAgentToZone(Agent id) {
		if (id instanceof Animal) {
			addAgentToZone((Animal) id);
			return;
		}
		if (id instanceof Tree) {
			addAgentToZone((Tree) id);
			return;
		}
		System.err.println("Trying to add weird agent to zone?");
	}
	
	public void addAgentToZone(Animal id) {
		int zoneX = getZoneXFromPosX(id.pos.x);
		int zoneY = getZoneYFromPosY(id.pos.y);
		addAnimalToZone(id, zoneX, zoneY);
	}
	
	public void removeAgentFromZone(Animal id, boolean useOldPos) {
		int zoneX;
		int zoneY;
		if (useOldPos) {
			zoneX = getZoneXFromPosX(id.oldPos.x);
			zoneY = getZoneYFromPosY(id.oldPos.y);
		}
		else {
			zoneX = getZoneXFromPosX(id.pos.x);
			zoneY = getZoneYFromPosY(id.pos.y);
		}
		removeAnimalFromZone(id, zoneX, zoneY);
	}
	
	public void addTreeToZone(Tree id) {
		int zoneX = getZoneXFromPosX(id.pos.x);
		int zoneY = getZoneYFromPosY(id.pos.y);
		addTreeToZone(id, zoneX, zoneY);
	}
	
	public void removeTreeFromZone(Tree id) {
		int zoneX = getZoneXFromPosX(id.pos.x);
		int zoneY = getZoneYFromPosY(id.pos.y);
		removeTreeFromZone(id, zoneX, zoneY);
	}
	
	private void addAnimalToZone(Animal id, int zoneX, int zoneY) {
		if(!zoneGrid[zoneX][zoneY].agentsInZone.add(id)) {
			System.err.println("Trying to add agent to vision zone, but failed.");
		}
	}
	
	private void removeAnimalFromZone(Animal id, int zoneX, int zoneY) {
		if(!zoneGrid[zoneX][zoneY].agentsInZone.remove(id)) {
			System.err.println("Trying to remove agent from vision zone, but failed.");
		}
	}
	
	private void addTreeToZone(Tree id, int zoneX, int zoneY) {
		if(!zoneGrid[zoneX][zoneY].treesInZone.add(id)) {
			System.err.println("Trying to add agent to vision zone, but failed.");
		}
	}
	
	private void removeTreeFromZone(Tree id, int zoneX, int zoneY) {
		if(!zoneGrid[zoneX][zoneY].treesInZone.remove(id)) {
			System.err.println("Trying to remove agent from vision zone, but failed.");
		}
	}
	
	public class Zone {
		public ArrayList<Animal> agentsInZone;
		public ArrayList<Tree> treesInZone;
		public float[] color;
		
		public Zone() {
			agentsInZone = new ArrayList<>();
			treesInZone = new ArrayList<>();
			color = new float[3];
			color[0] = Constants.RANDOM.nextFloat();
			color[1] = Constants.RANDOM.nextFloat();
			color[2] = Constants.RANDOM.nextFloat();
		}
	}

	public float[] getColorAt(int x, int y) {
		int zx = getZoneXFromPosX(x);
		int zy = getZoneYFromPosY(y);
		return zoneGrid[zx][zy].color;
	}

}
