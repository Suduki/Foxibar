package main;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import agents.Agent;
import agents.AgentManager;
import agents.Animal;
import agents.Bloodling;
import agents.Grassler;
import agents.Randomling;
import simulation.Simulation;
import vision.Vision;

public class StabilityIT {
	private static Simulation     simulation;
	
	private static final int RANDOMLING = 0;
	private static final int BLOODLING = 1;
	private static final int ANIMAL = 2;
	private static final int GRASSLER = 3;
	
	private static int timeStep = 1;

	@BeforeClass
	public static <T extends Agent> void init() {
		simulation     = new Simulation(new Class[] {Randomling.class, Bloodling.class, Animal.class, Grassler.class});
		System.out.println("Before class completed");
	}
	
	@Test
	public void test1WorldPopulated() {
		System.out.println("Initiating testWorldPopulated");
		System.out.println("Testing Randomling");
		testWorldPopulated(RANDOMLING);
		cleanup();
		System.out.println("Testing Bloodling");
		testWorldPopulated(BLOODLING);
		cleanup();
		System.out.println("Testing Animal");
		testWorldPopulated(ANIMAL);
		cleanup();
		System.out.println("Testing Grassler");
		testWorldPopulated(GRASSLER);
		cleanup();
		System.out.println("Test case testWorldPopulated completed.");
	}
	
	@Test
	public void test2Survivability () {
		System.out.println("Initiating testSurvivability");
		System.out.println("Testing Randomling");
		testSurvivability(RANDOMLING, 1000);
		verifyWorldNotEmpty();
		cleanup();
		
		System.out.println("Testing Bloodling");
		testSurvivability(BLOODLING, 1000);
		verifyWorldEmpty();
		cleanup();
		
		System.out.println("Testing Animal");
		testSurvivability(ANIMAL, 1000);
		verifyWorldNotEmpty();
		cleanup();
		
		System.out.println("Testing Grassler");
		testSurvivability(GRASSLER, 1000);
		verifyWorldNotEmpty();
		cleanup();
		
		System.out.println("Test case testSurvivability completed.");
	}
	
	@Test
	public void test3MultipleAgentTypes() {
		System.out.println("Initiating testMultipleAgentTypes");
		System.out.println("Testing Randomling and Bloodling");
		testSurvivability(RANDOMLING, 1000);
		simulation.spawnRandomAgents(BLOODLING, 100);
		for (int t = 0; t < 6000; t++) {
			simulation.step(timeStep++);
			if (simulation.getNumAgents(BLOODLING) == 0) {
				System.out.println("Bloodlings died after " + t + " time steps.");
				break;
			}
		}
		
		// Expect either both specieses survived or none.
		if (simulation.getNumAgents(BLOODLING) == 0) {
			verifyWorldEmpty();
		}
		else {
			verifyWorldNotEmpty();
		}
		
		cleanup();
		System.out.println("Test case testMultipleAgentTypes completed.");
	}
	
	@Test
	public void test4SpeciesTest() {
		testRelated();
	}


	/////////////
	// HELPERS //
	/////////////
	private void testRelated() {
		Animal a = new Animal(0, null, null);
		a.inherit(null);
		Animal b = new Animal(0, null, null);

		b.inherit(null);
		Assert.assertFalse(a.isCloselyRelated(b));
		
		b.inherit(a);
		Assert.assertTrue(a.isCloselyRelated(b));
		
		for (int gen = 0; gen < 20; gen++) {
			b.inherit(b);
			System.out.println(a.findRelationTo(b));
		}
		Assert.assertFalse(a.isCloselyRelated(b));
		
	}
	
	private void testSurvivability(int agentType, int simTime) {
		simulation.spawnRandomAgents(agentType, 500);
		for (int t = 0; t < simTime; t++) {
			simulation.step(timeStep++);
			if (simulation.agentManagers.get(agentType).numAgents == 0) break;
		}
	}
	
	private void testWorldPopulated(int agentType) {
		verifyWorldEmpty();
		simulation.spawnRandomAgents(agentType, 100);
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