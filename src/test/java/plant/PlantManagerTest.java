package plant;

import org.joml.Vector2f;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import testUtils.TestWithSimulation;
import vision.Vision.Zone;
import static plant.PlantManager.*;

public class PlantManagerTest extends TestWithSimulation {
	
	private PlantManager sut;
	
	@Before
	public void beforeTest() {
		sut = new PlantManager(simulation.vision, simulation.mWorld.terrain);
	}

	@Test
	public void testThatTreesSpawnAtProperPlace() {
		sut.spawn();
		sut.synchAliveDead();
		
		Zone zone = getVisionZone(sut.alive.get(0));
		
		Assert.assertEquals(1, sut.numAlive);
		Assert.assertEquals(1, zone.treesInZone.size());
	}
	
	@Test
	public void testThatAllTreesDie() {
		for (int i = 0; i < 10; ++i) {
			sut.spawn();
		}
		sut.synchAliveDead();
		Assert.assertTrue(sut.numAlive == 10);
		
		sut.killAll = true;
		sut.update();
		Assert.assertEquals(0, sut.numAlive);
	}
	
	@Test
	public void testListStateAtStart() {
		Assert.assertEquals(0, sut.alive.size());
		Assert.assertEquals(MAX_NUM_TREES, sut.dead.size());
		Assert.assertEquals(0, sut.toDie.size());
		Assert.assertEquals(0, sut.toLive.size());
	}
	
	@Test
	public void testListStateAfterSpawn() {
		sut.spawn();
		sut.spawn();
		sut.update();
		
		Assert.assertEquals(0, sut.alive.size());
		Assert.assertEquals(MAX_NUM_TREES - 2, sut.dead.size());
		Assert.assertEquals(0, sut.toDie.size());
		Assert.assertEquals(2, sut.toLive.size());
	}
	
	@Test
	public void testListStateSpawnAfterSynch() {
		sut.spawn();
		sut.spawn();
		sut.update();
		sut.synchAliveDead();
		
		Assert.assertEquals(2, sut.alive.size());
		Assert.assertEquals(MAX_NUM_TREES - 2, sut.dead.size());
		Assert.assertEquals(0, sut.toDie.size());
		Assert.assertEquals(0, sut.toLive.size());
	}
	
	@Test
	public void testListStateDie() {
		Plant tree = sut.spawn();
		sut.spawn();
		sut.update();
		sut.synchAliveDead();
		
		tree.die();
		sut.update();
		
		Assert.assertEquals(2, sut.alive.size());
		Assert.assertEquals(MAX_NUM_TREES - 2, sut.dead.size());
		Assert.assertEquals(1, sut.toDie.size());
		Assert.assertEquals(0, sut.toLive.size());
	}
	
	@Test
	public void testListState() {
		Plant tree = sut.spawn();
		sut.spawn();
		sut.update();
		sut.synchAliveDead();
		
		tree.die();
		sut.update();
		sut.synchAliveDead();
		
		Assert.assertEquals(1, sut.alive.size());
		Assert.assertEquals(MAX_NUM_TREES - 1, sut.dead.size());
		Assert.assertEquals(0, sut.toDie.size());
		Assert.assertEquals(0, sut.toLive.size());
	}
	
	@Test
	public void testUpperSpawnLimit() {
		for (int i = 0; i < MAX_NUM_TREES; ++i) {
			Assert.assertNotNull(sut.spawn());
		}
		
		Assert.assertNull(sut.spawn());
		
		sut.synchAliveDead();
		
		sut.alive.get(0).die();
		sut.update();
		sut.synchAliveDead();
		
		Plant tree = sut.spawn();
		Assert.assertNotNull(tree);
		Assert.assertEquals(2, tree.incarnation);
	}
	
	private Zone getVisionZone(Plant tree) {
		Vector2f pos = tree.pos;
		
		return simulation.vision.getZoneAt((int)pos.x, (int)pos.y);
	}
}
