package main;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import agents.Animal;
import agents.AnimalManager;
import agents.Species;
import constants.Constants;
import simulation.Simulation;
import vision.Vision;
import world.World;

public class StabilityIT {
	private static Simulation     simulation;

	@BeforeClass
	public static void init() {
		simulation     = new Simulation();
		System.out.println("before");
	}
	@Test
	public void singleAnimalTest() {
		for (int i = 0; i < 2000; ++i) {
			verifyThatAloneAnimalDies();
		}
	}
	public void verifyThatAloneAnimalDies() {
		int timeStep = 0;
		
		Main.spawnRandomAnimal(Constants.Species.GRASSLER, 1);
		Assert.assertTrue(World.animalManager.numAnimals == 1);
		while (simulation.handleMessages() && timeStep <= 300)
		{
			timeStep++;
			simulation.step(timeStep);
			if (World.animalManager.numAnimals == 0) {
				break;
			}
		}
		Assert.assertTrue("Expected all animals to be dead. Currently " + 
				World.animalManager.numAnimals + " alive", World.animalManager.numAnimals == 0);
		Assert.assertTrue("zoneSize() was " + zoneSize(), zoneSize() == 0);
		
//		System.out.println("Test step 2, verify that a population of Grasslers survive");
//		spawnRandomAnimal(Constants.Species.GRASSLER, 100);
//		
//		timeStep = 0;
//		while (simulation.handleMessages() && timeStep <= 1001)
//		{
//			timeStep++;
//			simulation.step(timeStep);
//		}
//		Assert.assertTrue(World.animalManager.species[0].numAlive < 10000);
//		Assert.assertTrue(World.animalManager.species[0].numAlive > 10);
//		System.out.println("num animals after test step 2: Grasslers: " + Animal.numGrasslers + ", Bloodlings: " + Animal.numBloodlings);
//		
//		spawnRandomAnimal(Constants.SpeciesId.BLOODLING, 100);
//		timeStep = 0;
//		while (simulation.handleMessages() && timeStep <= (Animal.AGE_DEATH) * 10)
//		{
//			timeStep++;
//			simulation.step(timeStep);
//		}
//		Assert.assertTrue(Animal.numGrasslers < 10000);
//		Assert.assertTrue(Animal.numGrasslers > 10);
//		Assert.assertTrue(Animal.numBloodlings < 10000);
//		Assert.assertTrue(Animal.numBloodlings > 10);
//		System.out.println("num animals after test step 3: Grasslers: " + Animal.numGrasslers + ", Bloodlings: " + Animal.numBloodlings);
	}
	
	
	private int zoneSize() {
		int num = 0;
		for (Vision.Zone[] zi : Vision.zoneGrid) {
			for (Vision.Zone z : zi) {
				num += z.animalsInZone.size();
			}	
		}
		return num;
	}
}