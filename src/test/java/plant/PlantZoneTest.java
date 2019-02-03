package plant;

import org.joml.Vector2f;
import org.junit.Assert;
import org.junit.Test;

import testUtils.TestWithSimulation;

public class PlantZoneTest extends TestWithSimulation {

	@Test
	public void testThatTreesSpawnAtProperPlace() {
		int startX = 4;
		int startY = 2;
		
		PlantZone sut = new PlantZone(simulation.vision, startX, startY);
		sut.spawnTree();
		sut.synchAliveDead();
		
		Assert.assertEquals(sut.numTrees, 1);
		
		Vector2f pos = sut.alive.get(0).pos; 
		Assert.assertTrue(pos.x >= startX * PlantZone.zoneSizeX);
		Assert.assertTrue(pos.x < (startX+1) * PlantZone.zoneSizeX);
		Assert.assertTrue(pos.y >= startY * PlantZone.zoneSizeY);
		Assert.assertTrue(pos.y < (startY+1) * PlantZone.zoneSizeY);
	}
	
	@Test
	public void testThatAllTreesDie() {
		PlantZone sut = new PlantZone(simulation.vision, 0, 0);
		sut.spawnTree();
		
		Assert.assertEquals(sut.numTrees, 1);
		
		sut.killAll = true;
		sut.update();
		Assert.assertEquals(sut.numTrees, 0);
	}

}
