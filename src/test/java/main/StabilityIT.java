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
		System.out.println("Before class completed");
	}
	@Test
	public void singleAnimalTest() {
		System.out.println("Test starts: verifying that alone animals die");
		for (int i = 0; i < 200; ++i) {
			verifyThatAloneAnimalDies();
		}
		System.out.println("Test ends: success");
	}
	public void verifyThatAloneAnimalDies() {
		int timeStep = 0;
		verifyContainsAnimalsEmpty();
		
		Main.spawnRandomAnimal(Constants.Species.GRASSLER, 1);
		Assert.assertTrue(Simulation.animalManager.numAnimals == 1);
		while (simulation.handleMessages() && timeStep <= Animal.MAX_AGE+1)
		{
			timeStep++;
			simulation.step(timeStep);
			if (Simulation.animalManager.numAnimals == 0) {
				break;
			}
		}
		Assert.assertTrue("Expected all animals to be dead. Currently " + 
				Simulation.animalManager.numAnimals + " alive", Simulation.animalManager.numAnimals == 0);
		Assert.assertTrue("There are still animals in the vision zones..." + visionZoneSize(), visionZoneSize() == 0);
		verifyContainsAnimalsEmpty();
		
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
	
	private void verifyContainsAnimalsEmpty() {
		for (int i = 0; i < Simulation.animalManager.containsAnimals.length; ++i) {
			Assert.assertTrue("containsAnimals is not empty even though all animals are dead: at " + i, 
					Simulation.animalManager.containsAnimals[i] == null);
		}
	}
	
	
	private int visionZoneSize() {
		int num = 0;
		for (Vision.Zone[] zi : Simulation.animalManager.vision.zoneGrid) {
			for (Vision.Zone z : zi) {
				num += z.animalsInZone.size();
			}	
		}
		return num;
	}
}