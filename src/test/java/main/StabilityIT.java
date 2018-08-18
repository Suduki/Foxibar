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
		simulation.spawnRandomAgent(1, 100);
		Assert.assertTrue(simulation.agentManager.numAgents == 100);
		while (simulation.handleMessages() && timeStep <= Animal.MAX_AGE*2+1)
		{
			timeStep++;
			simulation.step(timeStep);
			if (simulation.agentManager.numAgents == 0) {
				break;
			}
		}
		if (simulation.agentManager.numAgents > 0) {
			populationSurvived = 1;
		}
		simulation.killAllAgents();
		Assert.assertTrue("There are still animals in the vision zones..." + visionZoneSize(), visionZoneSize() == 0);
		verifyContainsAnimalsEmpty();
		
		return populationSurvived;
	}
	
	//================= HELPERS =======================
	public void verifyThatAloneAnimalDies() {
		int timeStep = 0;
		verifyContainsAnimalsEmpty();
		
		simulation.spawnRandomAgent(1, 1);
		Assert.assertTrue(simulation.agentManager.numAgents == 1);
		while (simulation.handleMessages() && timeStep <= Animal.MAX_AGE+1)
		{
			timeStep++;
			simulation.step(timeStep);
			if (simulation.agentManager.numAgents == 0) {
				break;
			}
			verifyContainsAnimalsNotEmpty();
		}
		Assert.assertTrue("Expected all animals to be dead. Currently " + 
				simulation.agentManager.numAgents + " alive", simulation.agentManager.numAgents == 0);
		Assert.assertTrue("There are still animals in the vision zones..." + visionZoneSize(), visionZoneSize() == 0);
		verifyContainsAnimalsEmpty();
	}
	
	private void verifyContainsAnimalsEmpty() {
		for (int i = 0; i < simulation.mWorld.containsAgents.length; ++i) {
			Assert.assertTrue("containsAnimals is not empty even though all animals are dead: at " + i, 
					simulation.mWorld.containsAgents[i] == null);
		}
	}
	
	private void verifyContainsAnimalsNotEmpty() {
		boolean contains = false;
		for (int i = 0; i < simulation.mWorld.containsAgents.length; ++i) {
			if (simulation.mWorld.containsAgents[i] != null) {
				contains = true;
				break;
			}
		}
		Assert.assertFalse("containsAnimals is empty even though all animals are alive", contains);
	}
	
	private int visionZoneSize() {
		int num = 0;
		for (Vision.Zone[] zi : simulation.agentManager.vision.zoneGrid) {
			for (Vision.Zone z : zi) {
				num += z.agentsInZone.size();
			}	
		}
		return num;
	}
}