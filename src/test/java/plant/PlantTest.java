package plant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import testUtils.TestWithSimulation;
import static testUtils.TestHelper.ERROR_DELTA;

public class PlantTest extends TestWithSimulation {
	
	private Plant sut;
	private PlantManager manager;
	
	@Before
	public void beforeTest() {
		manager = new PlantManager(simulation.vision, simulation.mWorld.terrain);
		sut = manager.spawn();
	}

	@Test
	public void testThatTreesGrow() {
		Assert.assertEquals(sut.size, 0, ERROR_DELTA);
		Assert.assertEquals(sut.health, 0.1f, ERROR_DELTA);
		Assert.assertEquals(sut.leafness(), 0f, ERROR_DELTA);
		
		sut.stepAgent();
		
		Assert.assertTrue(sut.health > 0.1f);
		Assert.assertEquals(sut.leafness(), 0f, ERROR_DELTA);
		Assert.assertEquals(sut.size, 0, ERROR_DELTA);
		
		while (sut.health < 1f) {
			sut.stepAgent();
		}
		
		sut.stepAgent();
		
		Assert.assertEquals(sut.health, 1f, ERROR_DELTA);
		Assert.assertTrue(sut.leafness() > 0f);
		Assert.assertTrue(sut.size > 0);
	}
	
	@Test
	public void testThatTreesDieFromAge() {
		Assert.assertEquals(sut.age, 0);
		Assert.assertEquals(sut.trueAge, 0);
		
		sut.stepAgent();
		
		Assert.assertEquals(sut.age, 1);
		Assert.assertEquals(sut.trueAge, 1);
		Assert.assertTrue(sut.isAlive);
		
		sut.maxAge = 1;
		sut.stepAgent();
		Assert.assertFalse(sut.isAlive);
	}

}
