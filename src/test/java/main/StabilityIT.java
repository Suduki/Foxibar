package main;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import agents.Agent;
import agents.AgentManager;
import agents.Brainler;
import agents.Bloodling;
import agents.Grassler;
import agents.Randomling;
import constants.Constants;
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
	public static void init() {
		simulation     = new Simulation(new Class[] {Randomling.class, Bloodling.class, Brainler.class, Grassler.class});
		System.out.println("Before class completed");
	}
	
	@AfterClass
	public static void after() {
		sanityCheck();
		System.out.println("Tests finished.");
	}
	
	private static void sanityCheck() {
		float[][] blood = simulation.mWorld.blood.height;
		float[][] grass = simulation.mWorld.grass.height;
		float[][] fat = simulation.mWorld.fat.height;
		
		for (int x = 0; x < Constants.WORLD_SIZE_X; ++x) {
			for (int y = 0; y < Constants.WORLD_SIZE_Y; ++y) {
				Assert.assertTrue("Expected blood height to be positive and not too large, it was " + blood[x][y] + " at x="+x+" y="+y,
						blood[x][y]>=0 && blood[x][y]<10);
				Assert.assertTrue("Expected grass height to be positive and not too large, it was " + grass[x][y] + " at x="+x+" y="+y,
						grass[x][y]>=0 && grass[x][y]<10);
				Assert.assertTrue("Expected fat height to be positive and not too large, it was " + fat[x][y] + " at x="+x+" y="+y,
						fat[x][y]>=0 && fat[x][y]<10);
			}
		}
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
		testSurvivability(RANDOMLING, 500, 1000);
		verifyWorldNotEmpty();
		cleanup();
		
		testSurvivability(BLOODLING, 500, 1000);
		verifyWorldEmpty();
		cleanup();
		
		testSurvivability(BRAINLER, 500, 1000);
		verifyWorldNotEmpty();
		cleanup();
		
		testSurvivability(GRASSLER, 500, 1000);
		verifyWorldNotEmpty();
		cleanup();
		
		System.out.println("Test case testSurvivability completed.");
	}
	
	@Test
	public void testMultipleAgentRandomlingBloodling() {
		testMultipleAgents(RANDOMLING, BLOODLING);
		cleanup();
	}
	@Test
	public void testMultipleAgentBrainlerBloodling() {
		testMultipleAgents(BRAINLER, BLOODLING);
		cleanup();
	}
	@Test
	public void testMultipleAgentGrasslerBloodling() {
		testMultipleAgents(GRASSLER, BLOODLING);
		cleanup();
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
		Assert.assertFalse(a.isCloselyRelatedTo(b));
		
		b.inherit(a);
		Assert.assertTrue(a.isCloselyRelatedTo(b));
		
		for (int gen = 0; gen < 25; gen++) {
			b.inherit(b);
		}
		Assert.assertFalse("Expected not related after 25 generations. relation = " + a.findRelationTo(b), a.isCloselyRelatedTo(b));
	}
	

	/////////////
	// HELPERS //
	/////////////
	private void testMultipleAgents(int type1, int type2) {
		System.out.println("Initiating testMultipleAgentTypes");
		System.out.println("Testing " + AGENT_TYPES_NAMES[type1] + " and " + AGENT_TYPES_NAMES[type2]);
		int initNumAgents1 = 350;
		int initNumAgents2 = 50;
		testSurvivability(type1, 5000, initNumAgents1);
		int maxNumType1 = 0;
		int maxNumType2 = 0;
		simulation.spawnRandomAgents(type2, initNumAgents2);
		int t;
		for (t = 0; t < 6000; t++) {
			simulation.step(timeStep++);
			if (maxNumType2 < simulation.getNumAgents(type2)) {
				maxNumType2 = simulation.getNumAgents(type2);
			}
			if (maxNumType1 < simulation.getNumAgents(type1)) {
				maxNumType1 = simulation.getNumAgents(type1);
			}
			if (simulation.getNumAgents(type1) == 0) {
				System.out.println(AGENT_TYPES_NAMES[type1] + " died after " + t + " time steps.");
				System.out.println("Num " + AGENT_TYPES_NAMES[type2] + " alive = " + simulation.getNumAgents(type2));
				break;
			}
			if (simulation.getNumAgents(type2) == 0) {
				System.out.println(AGENT_TYPES_NAMES[type2] + " died after " + t + " time steps.");
				System.out.println("Num " + AGENT_TYPES_NAMES[type1] + " alive = " + simulation.getNumAgents(type1));
				break;
			}
		}
		System.out.println("Max number of " + AGENT_TYPES_NAMES[type1] + ": " + maxNumType1);
		System.out.println("Max number of " + AGENT_TYPES_NAMES[type2] + ": " + maxNumType2);
		cleanup();
		Assert.assertTrue("Expected " + AGENT_TYPES_NAMES[type1] + " populations size to increase.", maxNumType1 > initNumAgents1);
		Assert.assertTrue("Expected " + AGENT_TYPES_NAMES[type2] + " populations size to increase.", maxNumType2 > initNumAgents2);
		Assert.assertTrue("Expected " + AGENT_TYPES_NAMES[type1] + " to survive longer.", t > 200);
		Assert.assertTrue("Expected " + AGENT_TYPES_NAMES[type2] + " to survive longer.", t > 200);
		
		System.out.println("Test case testMultipleAgentTypes completed.");
	}
	
	private void testSurvivability(int agentType, int simTime, int numInit) {
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