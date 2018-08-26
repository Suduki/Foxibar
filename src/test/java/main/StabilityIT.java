package main;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import agents.Agent;
import agents.Animal;
import agents.Bloodling;
import agents.Randomling;
import simulation.Simulation;
import vision.Vision;

public class StabilityIT {
	private static Simulation     simulation;
	
	private static final int RANDOMLING = 0;
	private static final int BLOODLING = 1;
	private static final int ANIMAL = 2;

	@BeforeClass
	public static <T extends Agent> void init() {
		simulation     = new Simulation(new Class[] {Randomling.class, Bloodling.class, Animal.class});
		System.out.println("Before class completed");
	}
	
	@Test
	public void testWorldPopulated() {
		System.out.println("Initiating test case 1");
		System.out.println("Testing Randomling");
		testWorldPopulated(RANDOMLING);
		System.out.println("Testing Bloodling");
		testWorldPopulated(BLOODLING);
		System.out.println("Testing Animal");
		testWorldPopulated(ANIMAL);
		System.out.println("Test case 1 completed.");
	}
	
	@Test
	public void testSurvivability () {
		System.out.println("Initiating test case 2");
		System.out.println("Testing Randomling");
		testSurvivability(RANDOMLING, true);
		System.out.println("Testing Bloodling");
		testSurvivability(BLOODLING, false);
		System.out.println("Testing Animal");
		testSurvivability(ANIMAL, true);
		System.out.println("Test case 2 completed.");
	}
	
	

	/////////////
	// HELPERS //
	/////////////
	private void testSurvivability(int agentType, boolean expectSurvived) {
		verifyContainsAnimalsEmpty();
		Assert.assertTrue(visionZoneSize() == 0);
		simulation.spawnRandomAgents(agentType, 0, 100);
		for (int timeStep = 0; timeStep < 1000; timeStep++) {
			simulation.step(1);
		}
		if (expectSurvived) {
			verifyContainsAnimalsNotEmpty();
			cleanup();
		}
		else {
			verifyContainsAnimalsEmpty();
		}
	}
	
	private void testWorldPopulated(int agentType) {
		verifyContainsAnimalsEmpty();
		simulation.spawnRandomAgents(agentType, 0, 100);
		simulation.step(1);
		verifyContainsAnimalsNotEmpty();
		
		cleanup();
	}

	private void cleanup() {
		simulation.killAllAgents();
		simulation.step(1);
		verifyContainsAnimalsEmpty();
	}
	
	private void verifyContainsAnimalsEmpty() {
		for (int i = 0; i < simulation.mWorld.containsAgents.length; ++i) {
			Assert.assertTrue("containsAnimals is not empty even though all animals are dead: at " + i, 
					simulation.mWorld.containsAgents[i] == null);
		}
		Assert.assertTrue(visionZoneSize() == 0);
	}
	
	private void verifyContainsAnimalsNotEmpty() {
		boolean contains = false;
		for (int i = 0; i < simulation.mWorld.containsAgents.length; ++i) {
			if (simulation.mWorld.containsAgents[i] != null) {
				contains = true;
				break;
			}
		}
		Assert.assertTrue(visionZoneSize() > 1);
		Assert.assertFalse("containsAnimals is empty even though all animals are alive", contains);
	}
	
	private int visionZoneSize() {
		int num = 0;
		for (Vision.Zone[] zi : simulation.vision.zoneGrid) {
			for (Vision.Zone z : zi) {
				num += z.agentsInZone.size();
			}	
		}
		return num;
	}
}