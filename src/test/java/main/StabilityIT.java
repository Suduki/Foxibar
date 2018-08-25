package main;

import java.util.Random;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import agents.Agent;
import agents.AgentManager;
import agents.Animal;
import agents.Bloodling;
import agents.Randomling;
import constants.Constants;
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
		System.out.println("Initiating test case");
		testWorldPopulated(RANDOMLING);
		testWorldPopulated(BLOODLING);
		testWorldPopulated(ANIMAL);
		System.out.println("Test case completed.");
	}
	
	private void testWorldPopulated(int agentType) {
		verifyContainsAnimalsEmpty();
		Assert.assertTrue(visionZoneSize() == 0);
		simulation.spawnRandomAgents(agentType, 100);
		simulation.step(1);
		verifyContainsAnimalsNotEmpty();
		Assert.assertTrue(visionZoneSize() > 1);
		
		simulation.killAllAgents();
		simulation.step(1);
		verifyContainsAnimalsEmpty();
		Assert.assertTrue(visionZoneSize() == 0);
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
		for (Vision.Zone[] zi : simulation.vision.zoneGrid) {
			for (Vision.Zone z : zi) {
				num += z.agentsInZone.size();
			}	
		}
		return num;
	}
}