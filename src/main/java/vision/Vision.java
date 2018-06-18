package vision;

import java.util.ArrayList;

import agents.Animal;
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
	
	public void updateNearestNeighbours(Animal animal) {
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
				int zoneIX = (zoneX + zonesX + i) % zonesX;
				int zoneIY = (zoneY + zonesY + j) % zonesY;
				for (Animal anI : zoneGrid[zoneIX][zoneIY].animalsInZone) {
					if (anI != animal) {
						float d = calculateDistance(pos, anI.pos);
						if (d > Constants.Vision.MAX_DISTANCE_AN_ANIMAL_CAN_SEE) {
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
	
	private int getZoneXFromPos(int pos) {
		return (pos % Constants.WORLD_SIZE_X) / zoneWidth;
	}
	private int getZoneYFromPos(int pos) {
		return pos / Constants.WORLD_SIZE_X / zoneHeight;
	}
	
	public void updateAnimalZone(Animal id) {
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
	
	public void addAnimalToZone(Animal id) {
		int zoneX = getZoneXFromPos(id.pos);
		int zoneY = getZoneYFromPos(id.pos);
		addAnimalToZone(id, zoneX, zoneY);
	}
	public void removeAnimalFromZone(Animal id) {
		int zoneX = getZoneXFromPos(id.pos);
		int zoneY = getZoneYFromPos(id.pos);
		removeAnimalFromZone(id, zoneX, zoneY);
	}
	
	private void addAnimalToZone(Animal id, int zoneX, int zoneY) {
//		System.out.println("num before add: " + zoneGrid[zoneX][zoneY].animalsInZone.size()
//				+ "zoneX=" + zoneX + "zoneY=" + zoneY);
		zoneGrid[zoneX][zoneY].animalsInZone.add(id);
//		System.out.println("num after add: " + zoneGrid[zoneX][zoneY].animalsInZone.size());
	}
	private void removeAnimalFromZone(Animal id, int zoneX, int zoneY) {
//		System.out.println("num before remove: " + zoneGrid[zoneX][zoneY].animalsInZone.size()
//				+ "zoneX=" + zoneX + "zoneY=" + zoneY);
		zoneGrid[zoneX][zoneY].animalsInZone.remove(id);
//		System.out.println("num after remove: " + zoneGrid[zoneX][zoneY].animalsInZone.size());
	}
	
	public class Zone {
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
