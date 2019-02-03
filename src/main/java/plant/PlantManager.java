package plant;

import java.util.ArrayList;

import simulation.Simulation;
import vision.Vision;

public class PlantManager {
	ArrayList<PlantZone> plantZones;
	
	int numZonesX = Simulation.WORLD_SIZE_X / PlantZone.zoneSizeX;
	int numZonesY = Simulation.WORLD_SIZE_Y / PlantZone.zoneSizeY;
	
	public PlantManager(Vision vision) {
		super();
		plantZones = new ArrayList<PlantZone>();
		for (int x = 0; x < numZonesX; ++x) {
			for (int y = 0; y < numZonesY; ++y) {
				plantZones.add(new PlantZone(vision, x, y));
			}
		}
	}
	
	public PlantZone getZoneAt(int worldX, int worldY) {
		int scaledX = worldX / PlantZone.zoneSizeX;
		int scaledY = worldY / PlantZone.zoneSizeY;
		
		return plantZones.get(scaledX * numZonesY + scaledY);
	}
	
	public void update() {
		for (PlantZone pZ : plantZones) {
			pZ.update();
		}
	}
}
