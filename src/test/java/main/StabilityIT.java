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
	
	private static int timeStep = 1;

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
		cleanup();
		System.out.println("Testing Bloodling");
		testWorldPopulated(BLOODLING);
		cleanup();
		System.out.println("Testing Animal");
		testWorldPopulated(ANIMAL);
		cleanup();
		System.out.println("Test case 1 completed.");
	}
	
	@Test
	public void testSurvivability () {
		System.out.println("Initiating test case 2");
		System.out.println("Testing Randomling");
		testSurvivability(RANDOMLING, true);
		cleanup();
		System.out.println("Testing Bloodling");
		testSurvivability(BLOODLING, false);
		cleanup();
		System.out.println("Testing Animal");
		testSurvivability(ANIMAL, false);
		cleanup();
		System.out.println("Test case 2 completed.");
	}
	
	

	/////////////
	// HELPERS //
	/////////////
	private void testSurvivability(int agentType, boolean expectSurvived) {
		verifyWorldEmpty();
		Assert.assertTrue(visionZoneSize() == 0);
		simulation.spawnRandomAgents(agentType, 0, 100);
		for (int t = 0; t < 10000; t++) {
			simulation.step(timeStep++);
		}
		if (expectSurvived) {
			verifyWorldNotEmpty();
		}
		else {
			verifyWorldEmpty();
		}
	}
	
	private void testWorldPopulated(int agentType) {
		verifyWorldEmpty();
		simulation.spawnRandomAgents(agentType, 0, 100);
		simulation.step(timeStep++);
		verifyWorldNotEmpty();
	}

	private void cleanup() {
		simulation.killAllAgents();
		simulation.step(timeStep++);
		verifyWorldEmpty();
		simulation.mWorld.reset(true);
	}
	
	private void verifyWorldEmpty() {
		Assert.assertTrue(simulation.getNumAgents() == 0);
		Assert.assertTrue(visionZoneSize() == 0);
		for (int i = 0; i < simulation.mWorld.containsAgents.length; ++i) {
			Assert.assertTrue("containsAnimals is not empty even though all animals are dead: at " + i, 
					simulation.mWorld.containsAgents[i] == null);
		}
	}
	
	private void verifyWorldNotEmpty() {
		Assert.assertTrue(simulation.getNumAgents() > 0);
		Assert.assertTrue(visionZoneSize() > 1);
		boolean contains = false;
		for (int i = 0; i < simulation.mWorld.containsAgents.length; ++i) {
			if (simulation.mWorld.containsAgents[i] != null) {
				contains = true;
				break;
			}
		}
		Assert.assertTrue("containsAgents should contain living Agents, but does not", contains);
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