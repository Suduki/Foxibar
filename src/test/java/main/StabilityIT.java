package main;

import java.util.Random;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import agents.Animal;
import constants.Constants;
import simulation.Simulation;
import vision.Vision;

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
		for (int i = 0; i < 20; ++i) {
			verifyThatAloneAnimalDies();
		}
		System.out.println("Test ends: success");
	}
	@Test
	public void multipleAnimalSingleSpeciesTest() {
		int numSuccess = 0;
		int numTries = 20;
		for (int i = 0; i < numTries; ++i) {
			numSuccess += multipleAnimalsSingleSpeciesSimulation();
		}
		System.out.println("successRate = " + ((float)numSuccess)/numTries);
	}
	
	/**
	 * 
	 * @return 0 on death of species, 1 on survival of species
	 */
	private int multipleAnimalsSingleSpeciesSimulation() {
		int timeStep = 0;
		int populationSurvived = 0;
		verifyContainsAnimalsEmpty();
		Constants.RANDOM = new Random(1);
		simulation.resetWorld(true);
		simulation.spawnRandomAnimal(Constants.Species.GRASSLER, 100);
		Assert.assertTrue(simulation.animalManager.numAnimals == 100);
		while (simulation.handleMessages() && timeStep <= Animal.MAX_AGE*2+1)
		{
			timeStep++;
			simulation.step(timeStep);
			if (simulation.animalManager.numAnimals == 0) {
				break;
			}
		}
		if (simulation.animalManager.numAnimals > 0) {
			populationSurvived = 1;
		}
		simulation.killAllAnimals();
		Assert.assertTrue("There are still animals in the vision zones..." + visionZoneSize(), visionZoneSize() == 0);
		verifyContainsAnimalsEmpty();
		
		return populationSurvived;
	}
	
	//================= HELPERS =======================
	public void verifyThatAloneAnimalDies() {
		int timeStep = 0;
		verifyContainsAnimalsEmpty();
		
		simulation.spawnRandomAnimal(Constants.Species.GRASSLER, 1);
		Assert.assertTrue(simulation.animalManager.numAnimals == 1);
		while (simulation.handleMessages() && timeStep <= Animal.MAX_AGE+1)
		{
			timeStep++;
			simulation.step(timeStep);
			if (simulation.animalManager.numAnimals == 0) {
				break;
			}
		}
		Assert.assertTrue("Expected all animals to be dead. Currently " + 
				simulation.animalManager.numAnimals + " alive", simulation.animalManager.numAnimals == 0);
		Assert.assertTrue("There are still animals in the vision zones..." + visionZoneSize(), visionZoneSize() == 0);
		verifyContainsAnimalsEmpty();
	}
	
	private void verifyContainsAnimalsEmpty() {
		for (int i = 0; i < simulation.mWorld.containsAnimals.length; ++i) {
			Assert.assertTrue("containsAnimals is not empty even though all animals are dead: at " + i, 
					simulation.mWorld.containsAnimals[i] == null);
		}
	}
	
	
	private int visionZoneSize() {
		int num = 0;
		for (Vision.Zone[] zi : simulation.animalManager.vision.zoneGrid) {
			for (Vision.Zone z : zi) {
				num += z.animalsInZone.size();
			}	
		}
		return num;
	}
}