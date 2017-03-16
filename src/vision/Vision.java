package vision;

import java.util.ArrayList;

import agents.Animal;
import constants.Constants;

public class Vision {
	public static Zone[][] zoneGrid;
	
	public static final int ZONE_HEIGHT = 4;
	public static final int ZONE_WIDTH = 4;
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
	
	public static void updateAnimalVision() {
		empty();
		fill();
	}
	
	public static void updateNearestNeighbours(int animalId) {
		int pos = Animal.pool[animalId].pos;
		int zoneX = getZoneXFromAnimalPos(pos);
		int zoneY = getZoneYFromAnimalPos(pos);
		for (int i = 0; i < Animal.pool[animalId].nearbyAnimalsDistance.length; ++i) {
			Animal.pool[animalId].nearbyAnimalsDistance[i] = -1;
		}
		
		for (int anI = 0; anI < Animal.pool[animalId].nearbyAnimals.length; ++anI) {
			Animal.pool[animalId].nearbyAnimals[anI] = -1;
		}
		
		for (int i = -1; i <= 1; ++i) {
			for (int j = -1; j <= 1; ++j) {
				int zoneIX = (zoneX + ZONES_X + i) % ZONES_X;
				int zoneIY = (zoneY + ZONES_Y + j) % ZONES_Y;
				for (int anI : zoneGrid[zoneIX][zoneIY].animalsInZone) {
					if (anI != animalId) {
						int d = calculateDistance(pos, Animal.pool[anI].pos);
						if (d > Constants.MAX_DISTANCE_AN_ANIMAL_CAN_SEE) {
							continue;
						}
						for (int neighId = 0; neighId < Animal.pool[animalId].nearbyAnimalsDistance.length; ++neighId) {
							if (Animal.pool[animalId].nearbyAnimalsDistance[neighId] == -1) {
								Animal.pool[animalId].nearbyAnimalsDistance[neighId] = d;
								Animal.pool[animalId].nearbyAnimals[neighId] = anI;
								break;
							}
							else if (d <= Animal.pool[animalId].nearbyAnimalsDistance[neighId]) {
								int tmpD = d;
								d = Animal.pool[animalId].nearbyAnimalsDistance[neighId];
								Animal.pool[animalId].nearbyAnimalsDistance[neighId] = tmpD;
								
								int tmpI = anI;
								anI = Animal.pool[animalId].nearbyAnimals[neighId];
								Animal.pool[animalId].nearbyAnimals[neighId] = tmpI;
							}
						}
//						if (bestDistances[0] == -1 || d <= bestDistances[0]) {
//							bestDistances[0] = d;
//							Animal.pool[animalId].neighbours[0] = anI;
//						}
					}
				}
			}
		}
	}
	
	private static int calculateDistance(int pos, int pos2) {
		return Math.abs(getXFromAnimalPos(pos) - getXFromAnimalPos(pos2)) +
				Math.abs(getYFromAnimalPos(pos) - getYFromAnimalPos(pos2));
	}
	
	private static void empty() {
		for (int i = 0; i < ZONES_X; ++i) {
			for (int j = 0; j < ZONES_Y; ++j) {
				zoneGrid[i][j].animalsInZone.clear();
			}
		}
	}
	
	private static int getXFromAnimalPos(int pos) {
		return pos % Constants.WORLD_SIZE_X;
	}
	private static int getYFromAnimalPos(int pos) {
		return pos / Constants.WORLD_SIZE_X;
	}
	
	private static int getZoneXFromAnimalPos(int pos) {
		return (pos % Constants.WORLD_SIZE_X) / ZONE_WIDTH; 
	}
	private static int getZoneYFromAnimalPos(int pos) {
		return pos / Constants.WORLD_SIZE_X / ZONE_HEIGHT;
	}
	
	private static void fill() {
		for (int i = 0; i < Animal.pool.length; ++i) {
			if (Animal.pool[i].isAlive) {
				int pos = Animal.pool[i].pos;
				if (Constants.RENDER_VISION) {
					int x = (pos % Constants.WORLD_SIZE_X) / ZONE_WIDTH;
					int y = pos / Constants.WORLD_SIZE_X / ZONE_HEIGHT;
//					zoneGrid[zone].animalsInZone.add(i);
					int zoneX = getZoneXFromAnimalPos(pos);
					int zoneY = getZoneYFromAnimalPos(pos);
//					Animal.pool[i].color[0] = zoneGrid[zoneX][zoneY].color[0];
//					Animal.pool[i].color[1] = zoneGrid[zoneX][zoneY].color[1];
//					Animal.pool[i].color[2] = zoneGrid[zoneX][zoneY].color[2];
					zoneGrid[zoneX][zoneY].animalsInZone.add(i);
				}
				else {
					int zoneX = getZoneXFromAnimalPos(pos);
					int zoneY = getZoneYFromAnimalPos(pos);
					zoneGrid[zoneX][zoneY].animalsInZone.add(i);
				}
			}
		}
	}
	
	
	private static class Zone {
		public ArrayList<Integer> animalsInZone;
		public float[] color;
		
		public Zone() {
			animalsInZone = new ArrayList<Integer>();
			color = new float[3];
			color[0] = Constants.RANDOM.nextFloat();
			color[1] = Constants.RANDOM.nextFloat();
			color[2] = Constants.RANDOM.nextFloat();
		}
	}
}
