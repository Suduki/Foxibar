package plant;

import org.joml.Vector2f;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import testUtils.TestWithSimulation;
import vision.Vision.Zone;

public class PlantManagerTest extends TestWithSimulation {
	
	private PlantManager sut;
	
	@Before
	public void beforeTest() {
		sut = new PlantManager(simulation.vision);
	}

	@Test
	public void testThatTreesSpawnAtProperPlace() {
		sut.spawnTree();
		sut.synchAliveDead();
		
		Zone zone = getVisionZone(sut.alive.get(0));
		
		Assert.assertEquals(1, sut.numTrees);
		Assert.assertEquals(1, zone.treesInZone.size());
	}
	
	@Test
	public void testThatAllTreesDie() {
		for (int i = 0; i < 10; ++i) {
			sut.spawnTree();
		}
		sut.synchAliveDead();
		Assert.assertTrue(sut.numTrees > 5);
		
		sut.killAll = true;
		sut.update();
		Assert.assertEquals(0, sut.numTrees);
	}
	
	@Test
	public void testDeathAndBirthLists() {
		Assert.assertEquals(0, sut.alive.size());
		Assert.assertEquals(PlantManager.MAX_NUM_TREES, sut.dead.size());
		Assert.assertEquals(0, sut.toDie.size());
		Assert.assertEquals(0, sut.toLive.size());
		
		Tree tree = sut.spawnTree();
		sut.spawnTree();
		
		Assert.assertEquals(0, sut.alive.size());
		Assert.assertEquals(PlantManager.MAX_NUM_TREES - 2, sut.dead.size());
		Assert.assertEquals(0, sut.toDie.size());
		Assert.assertEquals(2, sut.toLive.size());
		
		sut.update();
		
		// Alive list should not be modified during update().
		Assert.assertEquals(0, sut.alive.size());
		Assert.assertEquals(PlantManager.MAX_NUM_TREES - 2, sut.dead.size());
		Assert.assertEquals(0, sut.toDie.size());
		Assert.assertEquals(2, sut.toLive.size());
		
		sut.synchAliveDead();
		
		Assert.assertEquals(2, sut.alive.size());
		Assert.assertEquals(PlantManager.MAX_NUM_TREES - 2, sut.dead.size());
		Assert.assertEquals(0, sut.toDie.size());
		Assert.assertEquals(0, sut.toLive.size());
		
		tree.die();
		sut.update();
		
		// Alive list should not be modified during update().
		Assert.assertEquals(2, sut.alive.size());
		Assert.assertEquals(PlantManager.MAX_NUM_TREES - 2, sut.dead.size());
		Assert.assertEquals(1, sut.toDie.size());
		Assert.assertEquals(0, sut.toLive.size());
		
		sut.synchAliveDead();
		
		Assert.assertEquals(1, sut.alive.size());
		Assert.assertEquals(PlantManager.MAX_NUM_TREES - 1, sut.dead.size());
		Assert.assertEquals(0, sut.toDie.size());
		Assert.assertEquals(0, sut.toLive.size());
	}
	
	@Test
	public void testUpperSpawnLimit() {
		for (int i = 0; i < PlantManager.MAX_NUM_TREES; ++i) {
			Assert.assertNotNull(sut.spawnTree());
		}
		
		Assert.assertNull(sut.spawnTree());
		
		sut.synchAliveDead();
		
		sut.alive.get(0).die();
		sut.update();
		sut.synchAliveDead();
		
		Tree tree = sut.spawnTree();
		Assert.assertNotNull(tree);
	}
	
	private Zone getVisionZone(Tree tree) {
		Vector2f pos = tree.pos;
		
		return simulation.vision.getZoneAt((int)pos.x, (int)pos.y);
	}
}
