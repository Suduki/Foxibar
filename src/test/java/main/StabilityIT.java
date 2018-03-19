package main;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import agents.Animal;
import agents.AnimalManager;
import agents.Species;
import constants.Constants;
import simulation.Simulation;
import world.World;

public class StabilityIT {
	private static Simulation     simulation;

	@BeforeClass
	public static void init() {
		simulation     = new Simulation();

		System.out.println("before");
	}
	@Test
	public void hum() {
		int timeStep = 0;
		

		System.out.println("Test step 1, verify that mr. lonely dies");
		spawnRandomAnimal(Constants.Species.GRASSLER, 1);
		Assert.assertTrue(World.animalManager.numAnimals == 1);
		while (simulation.handleMessages() && timeStep <= 10000)
		{
			timeStep++;
			simulation.step(timeStep);
			if (World.animalManager.numAnimals == 0) {
				break;
			}
		}
		Assert.assertTrue("Expected all animals to be dead. Currently " + 
				World.animalManager.numAnimals + " alive", World.animalManager.numAnimals == 0);
		
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

	public static void spawnRandomAnimal(Species species, int num) {
		for (int i = 0; i < num; ++i) {
			int pos = Constants.RANDOM.nextInt(Constants.WORLD_SIZE);
			World.animalManager.spawn(pos, species);
		}
	}
}