package vision;

import java.util.ArrayList;

import constants.Constants;

public class Vision {
	public static Zone[] zoneGrid;
	
	
	public static final int ZONE_WIDTH = 8;
	public static final int ZONE_HEIGHT = 8;
	public static final int ZONES = Constants.WORLD_SIZE_X/ZONE_WIDTH * Constants.WORLD_SIZE_Y/ZONE_HEIGHT;
	
	public void init() {
		zoneGrid = new Zone[ZONES];
		for (int i = 0; i < ZONES; ++i) {
			zoneGrid[i] = new Zone();
		}
	}
	
	public void updateAnimalVision(int animalId) {
		
	}
	
	
	
	public class Zone {
		public ArrayList<Integer> animalsInZone;
		
		public Zone() {
			animalsInZone = new ArrayList<Integer>();
		}
		
	}
}
