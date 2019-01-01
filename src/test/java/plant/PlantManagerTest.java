package plant;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import simulation.Simulation;

public class PlantManagerTest {
	
	int worldSize = 16;
	PlantManager plantManager;
	
	@Before
	public void setupTests() {
		Simulation.WORLD_SIZE_X = worldSize;
		Simulation.WORLD_SIZE_Y = worldSize;
		plantManager = new PlantManager();
	}
	
	@Test
	public void testThatCorrectNumberOfZonesAreCreated() {
		assertEquals(plantManager.plantZones.size(), worldSize / PlantZone.zoneSizeX * worldSize / PlantZone.zoneSizeY);
	}

	@Test
	public void testThatCorrectZoneCanBeFetched() {
		assertEquals(plantManager.getZoneAt(0, 0),
				plantManager.getZoneAt(2, 4));
		assertEquals(plantManager.getZoneAt(0 + PlantZone.zoneSizeX, 0),
				plantManager.getZoneAt(2 + PlantZone.zoneSizeX, 4));
		assertEquals(plantManager.getZoneAt(0 + PlantZone.zoneSizeX, 0 + PlantZone.zoneSizeY),
				plantManager.getZoneAt(2 + PlantZone.zoneSizeX, 4 + PlantZone.zoneSizeY));
		assertNotEquals(plantManager.getZoneAt(0, 0),
				plantManager.getZoneAt(2 + PlantZone.zoneSizeX, 4));
	}
	
	@Test 
	public void testThatAllZonesAreUpdated() {
		PlantZone mocked = Mockito.spy(plantManager.plantZones.get(0));
		plantManager.update();
		
		Mockito.verify(mocked).update();
	}
}
