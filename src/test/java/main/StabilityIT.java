package main;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import agents.Agent;
import agents.AgentManager;
import agents.Brainler;
import agents.Bloodling;
import agents.Grassler;
import agents.Randomling;
import simulation.Simulation;
import vision.Vision;

public class StabilityIT {
	private static Simulation     simulation;
	
	private static final int RANDOMLING = 0;
	private static final int BLOODLING = 1;
	private static final int BRAINLER = 2;
	private static final int GRASSLER = 3;
	private static final String[] AGENT_TYPES_NAMES = new String[]{"Randomling", "Bloodling", "Brainler", "Grassler"};
	
	private static int timeStep = 1;

	@BeforeClass
	public static <T extends Agent> void init() {
		simulation     = new Simulation(new Class[] {Randomling.class, Bloodling.class, Brainler.class, Grassler.class});
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
		testWorldPopulated(BRAINLER);
		cleanup();
		System.out.println("Testing Grassler");
		testWorldPopulated(GRASSLER);
		cleanup();
		System.out.println("Test case testWorldPopulated completed.");
	}
	
	@Test
	public void test2Survivability () {
		System.out.println("Initiating testSurvivability");
		testSurvivability(RANDOMLING, 1000);
		verifyWorldNotEmpty();
		cleanup();
		
		testSurvivability(BLOODLING, 1000);
		verifyWorldEmpty();
		cleanup();
		
		testSurvivability(BRAINLER, 1000);
		verifyWorldNotEmpty();
		cleanup();
		
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
		int maxNumRandomlings = 0;
		int maxNumBloodlings = 0;
		simulation.spawnRandomAgents(BLOODLING, 100);
		for (int t = 0; t < 6000; t++) {
			simulation.step(timeStep++);
			if (maxNumBloodlings < simulation.getNumAgents(BLOODLING)) {
				maxNumBloodlings = simulation.getNumAgents(BLOODLING);
			}
			if (maxNumRandomlings < simulation.getNumAgents(RANDOMLING)) {
				maxNumRandomlings = simulation.getNumAgents(RANDOMLING);
			}
			if (simulation.getNumAgents(BLOODLING) == 0) {
				System.out.println("Bloodlings died after " + t + " time steps.");
				System.out.println("Num Randomlings alive = " + simulation.getNumAgents(RANDOMLING));
				break;
			}
		}
		System.out.println("Max number of Randomlings: " + maxNumRandomlings);
		System.out.println("Max number of Bloodlings: " + maxNumBloodlings);
		cleanup();
		System.out.println("Test case testMultipleAgentTypes completed.");
	}
	
	@Test
	public void walkTest() {
		verifyWorldEmpty();
		simulation.spawnAgent(5, 6, RANDOMLING);
		
		simulation.step(timeStep++);
		simulation.step(timeStep++);

		Agent a = simulation.agentManagers.get(RANDOMLING).alive.get(0);
		Assert.assertNotEquals(a.pos.x, 5);
		Assert.assertNotEquals(a.pos.y, 6);
	}
	
	@Test //TODO: MOVE
	public void test4SpeciesTest() {
		Brainler a = new Brainler(0, null, null);
		a.inherit(null);
		Brainler b = new Brainler(0, null, null);
		
		b.inherit(null);
		Assert.assertFalse(a.isCloselyRelated(b));
		
		b.inherit(a);
		Assert.assertTrue(a.isCloselyRelated(b));
		
		for (int gen = 0; gen < 20; gen++) {
			b.inherit(b);
		}
		Assert.assertFalse(a.isCloselyRelated(b));
	}


	/////////////
	// HELPERS //
	/////////////
	
	private void testSurvivability(int agentType, int simTime) {
		System.out.println("Testing survivability of " + AGENT_TYPES_NAMES[agentType]);
		simulation.spawnRandomAgents(agentType, 500);
		int t;
		for (t = 0; t < simTime; t++) {
			simulation.step(timeStep++);
			if (simulation.agentManagers.get(agentType).numAgents == 0) break;
		}
		System.out.println(AGENT_TYPES_NAMES[agentType] + " Survivability test completed after " + t + " time steps");
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
	}
	
	private void verifyWorldNotEmpty() {
		Assert.assertTrue(simulation.getNumAgents() > 0);
		Assert.assertTrue(visionZoneSize() > 1);
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