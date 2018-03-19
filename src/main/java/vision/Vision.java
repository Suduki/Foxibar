package vision;

import java.util.ArrayList;

import agents.Animal;
import constants.Constants;

public class Vision {
	public static Zone[][] zoneGrid;
	
	public static final int ZONE_HEIGHT = 16;
	public static final int ZONE_WIDTH  = 16;
	public static final int ZONES_X = Constants.WORLD_SIZE_X/ZONE_WIDTH;
	public static final int ZONES_Y = Constants.WORLD_SIZE_Y/ZONE_HEIGHT;
	
	
	
	public static void init() {
		zoneGrid = new Zone[ZONES_X][ZONES_Y];
		for (int i = 0; i < ZONES_X; ++i) {
			for (int j = 0; j < ZONES_Y; ++j) {
				zoneGrid[i][j] = new Zone();
			}
		}
	}
	
	public static void updateNearestNeighbours(Animal animal) {
		int pos = animal.pos;
		int zoneX = getZoneXFromPos(pos);
		int zoneY = getZoneYFromPos(pos);
		for (int i = 0; i < animal.nearbyAnimalsDistance.length; ++i) {
			animal.nearbyAnimalsDistance[i] = -1;
		}
		
		for (int anI = 0; anI < animal.nearbyAnimals.length; ++anI) {
			animal.nearbyAnimals[anI] = null;
		}
		
		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j <= 1; ++j) {
				int zoneIX = (zoneX + ZONES_X + i) % ZONES_X;
				int zoneIY = (zoneY + ZONES_Y + j) % ZONES_Y;
				for (Animal anI : zoneGrid[zoneIX][zoneIY].animalsInZone) {
					if (anI != animal) {
						float d = calculateDistance(pos, anI.pos);
						if (d > Constants.MAX_DISTANCE_AN_ANIMAL_CAN_SEE) {
							continue;
						}
						for (int neighId = 0; neighId < animal.nearbyAnimalsDistance.length; ++neighId) {
							if (animal.nearbyAnimalsDistance[neighId] == -1) {
								animal.nearbyAnimalsDistance[neighId] = d;
								animal.nearbyAnimals[neighId] = anI;
								break;
							}
							else if (d <= animal.nearbyAnimalsDistance[neighId]) {
								float tmpD = d;
								d = animal.nearbyAnimalsDistance[neighId];
								animal.nearbyAnimalsDistance[neighId] = tmpD;
								
								Animal tmpI = anI;
								anI = animal.nearbyAnimals[neighId];
								animal.nearbyAnimals[neighId] = tmpI;
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
	
	private static int getZoneXFromPos(int pos) {
		return (pos % Constants.WORLD_SIZE_X) / ZONE_WIDTH; 
	}
	private static int getZoneYFromPos(int pos) {
		return pos / Constants.WORLD_SIZE_X / ZONE_HEIGHT;
	}
	
	public static void updateAnimalZone(Animal id) {
		int oldPos = id.oldPos;
		int pos = id.pos;
		
		int oldZoneX = getZoneXFromPos(oldPos);
		int oldZoneY = getZoneYFromPos(oldPos);
		int zoneX = getZoneXFromPos(pos);
		int zoneY = getZoneYFromPos(pos);
		
		if (oldZoneX != zoneX || oldZoneY != zoneY) {
			removeAnimalFromZone(id, oldZoneX, oldZoneY);
			addAnimalToZone(id, zoneX, zoneY);
		}
	}
	
	public static void addAnimalToZone(Animal id) {
		int zoneX = getZoneXFromPos(id.pos);
		int zoneY = getZoneYFromPos(id.pos);
		addAnimalToZone(id, zoneX, zoneY);
	}
	public static void removeAnimalFromZone(Animal id) {
		int zoneX = getZoneXFromPos(id.pos);
		int zoneY = getZoneYFromPos(id.pos);
		removeAnimalFromZone(id, zoneX, zoneY);
	}
	
	private static void addAnimalToZone(Animal id, int zoneX, int zoneY) {
		zoneGrid[zoneX][zoneY].animalsInZone.add(id);
	}
	private static void removeAnimalFromZone(Animal id, int zoneX, int zoneY) {
		zoneGrid[zoneX][zoneY].animalsInZone.remove(id);
	}
	
	private static class Zone {
		public ArrayList<Animal> animalsInZone;
		public float[] color;
		
		public Zone() {
			animalsInZone = new ArrayList<>();
			color = new float[3];
			color[0] = Constants.RANDOM.nextFloat();
			color[1] = Constants.RANDOM.nextFloat();
			color[2] = Constants.RANDOM.nextFloat();
		}
	}

}
