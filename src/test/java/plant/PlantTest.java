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
	public void testThatPlantsGrow() {
		Assert.assertEquals(sut.size, 0, ERROR_DELTA);
		Assert.assertEquals(sut.health, 0.1f, ERROR_DELTA);
		Assert.assertEquals(sut.leafness(), 0f, ERROR_DELTA);

		sut.stepAgent();

		Assert.assertTrue(sut.health > 0.1f);
		Assert.assertEquals(sut.leafness(), 0f, ERROR_DELTA);
		Assert.assertEquals(sut.size, 0, ERROR_DELTA);

		growPlantFully();

		Assert.assertEquals(sut.health, 1f, ERROR_DELTA);
		Assert.assertTrue(sut.size > 0); 
		Assert.assertTrue(sut.leafness() > 0f);
	}

	@Test
	public void testThatPlantsDieFromAge() {
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

	@Test
	public void testThatLeaflessPlantsCannotBeHarvested() {
		float amount = sut.harvest(0.1f);

		Assert.assertEquals(amount, 0f, ERROR_DELTA);
	}
	
	@Test
	public void testThatPlantsCanBeHarvested() {
		growPlantFully();
		
		float leafness = sut.leafness();

		float harvestSkill = 0.1f;
		float expectedHarvest = harvestSkill * sut.size;

		float actual = sut.harvest(harvestSkill);

		Assert.assertEquals(actual, expectedHarvest, ERROR_DELTA);
		Assert.assertTrue(leafness > sut.leafness());
	}

	private void growPlantFully() {
		float oldSize;
		float oldHealth;
		do {
			oldSize = sut.size;
			oldHealth = sut.health;
			sut.stepAgent();
		}
		while ((sut.size - oldSize) > ERROR_DELTA || (sut.health - oldHealth) > ERROR_DELTA);
	}

}
