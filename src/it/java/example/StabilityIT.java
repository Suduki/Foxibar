package example;

import java.util.ArrayList;

import main.TestHelper;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import actions.Action;
import agents.Agent;
import agents.AgentManager;
import agents.Brainler;
import agents.Bloodling;
import agents.Grassler;
import agents.Randomling;
import agents.Stomach;
import constants.Constants;
import simulation.Simulation;
import skills.SkillSet;
import vision.Vision;

public class StabilityIT {
	private static Simulation     simulation;
	
	private static final int RANDOMLING = 0;
	private static final int BLOODLING = 1;
	private static final int BRAINLER = 2;
	private static final int GRASSLER = 3;
	private static final String[] AGENT_TYPES_NAMES = new String[]{"Randomling", "Bloodling", "Brainler", "Grassler"};
	
	private static Integer timeStep = 1;
	
	@BeforeClass
	public static void init() {
		simulation     = new Simulation(Constants.WORLD_MULTIPLIER_TEST, new Class[] {Randomling.class, Bloodling.class, Brainler.class, Grassler.class});
		System.out.println("Before class completed");
	}
	
	private static void sanityCheck() {
		float[][] blood = simulation.mWorld.blood.height;
		float[][] grass = simulation.mWorld.grass.height;
		
		for (int x = 0; x < Simulation.WORLD_SIZE_X; ++x) {
			for (int y = 0; y < Simulation.WORLD_SIZE_Y; ++y) {
				Assert.assertTrue("Expected blood height to be positive and not too large, it was " + blood[x][y] + " at x="+x+" y="+y,
						blood[x][y]>=0 && blood[x][y]<10 && !Float.isNaN(blood[x][y]));
				Assert.assertTrue("Expected grass height to be positive and not too large, it was " + grass[x][y] + " at x="+x+" y="+y,
						grass[x][y]>=0 && grass[x][y]<10 && !Float.isNaN(grass[x][y]));
			}
		}
	}
	private int maxNumType1;

	private int maxNumType2;

	@Test
	public void test1WorldPopulated() {
		System.out.println("Initiating testWorldPopulated");
		System.out.println("Testing Randomling");
		testWorldPopulated(RANDOMLING);
		TestHelper.cleanup(simulation, timeStep);
		System.out.println("Testing Bloodling");
		testWorldPopulated(BLOODLING);
		TestHelper.cleanup(simulation, timeStep);
		System.out.println("Testing Animal");
		testWorldPopulated(BRAINLER);
		TestHelper.cleanup(simulation, timeStep);
		System.out.println("Testing Grassler");
		testWorldPopulated(GRASSLER);
		TestHelper.cleanup(simulation, timeStep);
		System.out.println("Test case testWorldPopulated completed.");
	}
	
	@Test
	public void test2Survivability () {
		System.out.println("Initiating testSurvivability");
		testSurvivability(RANDOMLING, 500, 1000);
		TestHelper.verifyWorldNotEmpty(simulation);
		TestHelper.cleanup(simulation, timeStep);
		
		testSurvivability(BLOODLING, 500, 1000);
		TestHelper.verifyWorldEmpty(simulation);
		TestHelper.cleanup(simulation, timeStep);
		
		testSurvivability(BRAINLER, 500, 1000);
		TestHelper.verifyWorldNotEmpty(simulation);
		TestHelper.cleanup(simulation, timeStep);
		
		testSurvivability(GRASSLER, 500, 1000);
		TestHelper.verifyWorldNotEmpty(simulation);
		TestHelper.cleanup(simulation, timeStep);
		
		System.out.println("Test case testSurvivability completed.");
	}
	
	@Test
	public void testMultipleAgentRandomlingBloodling() {
		int type1 = RANDOMLING;
		int type2 = BLOODLING;
		int initNumAgents1 = 500;
		int initNumAgents2 = 50;
		System.out.println("Initiating testMultipleAgentTypes");
		System.out.println("Testing " + AGENT_TYPES_NAMES[type1] + " and " + AGENT_TYPES_NAMES[type2]);
		testMultipleAgents(type1, type2, initNumAgents1, initNumAgents2);
		Assert.assertTrue("Expected " + AGENT_TYPES_NAMES[type1] + " populations size to increase.", maxNumType1 > initNumAgents1);
		Assert.assertTrue("Expected " + AGENT_TYPES_NAMES[type2] + " populations size to increase.", maxNumType2 > initNumAgents2);
		TestHelper.cleanup(simulation, timeStep);
	}
	
	@Test
	public void testMultipleAgentGrasslerBloodling() {
		int type1 = GRASSLER;
		int type2 = BLOODLING;
		int initNumAgents1 = 500;
		int initNumAgents2 = 50;
		System.out.println("Initiating testMultipleAgentTypes");
		System.out.println("Testing " + AGENT_TYPES_NAMES[type1] + " and " + AGENT_TYPES_NAMES[type2]);
		testMultipleAgents(type1, type2, initNumAgents1, initNumAgents2);
		Assert.assertTrue("Expected " + AGENT_TYPES_NAMES[type1] + " populations size to increase.", maxNumType1 > initNumAgents1);
		Assert.assertTrue("Expected " + AGENT_TYPES_NAMES[type2] + " populations size to increase.", maxNumType2 > initNumAgents2);
		TestHelper.cleanup(simulation, timeStep);
	}
	
	private class StomachRecommendation {
		public StomachRecommendation(float lowLimit, float highLimit) {
			this.lowLimit = lowLimit;
			this.highLimit = highLimit;
			setMean();
		}
		public StomachRecommendation() {
			this.lowLimit = -1;
			this.highLimit = -1;
			setMean();
		}
		
		float lowLimit;
		float highLimit;
		float mean;
		
		void printStuff() {
			System.out.println("lowLimit="+lowLimit + " highLimit=" + highLimit + " mean=" + mean);
		}
		public void setMean() {
			mean = (lowLimit + highLimit)/2;
		}
	}
	
	public StomachRecommendation findSuitableGrassP() {
		float grassP = 0;
		int numAgents;
		boolean foundLowG = false;
		float lowGrassP = 0;
		do {
			grassP += 0.1f;
			SkillSet.changeSkillMax(SkillSet.DIGEST_GRASS, grassP);
			testSurvivability(GRASSLER, 500, 100);
			numAgents = simulation.getNumAgents(GRASSLER);
			TestHelper.cleanup(simulation, timeStep);
			if (!foundLowG && numAgents > 100) {
				foundLowG = true;
				lowGrassP = grassP;
			}
		} while (numAgents < Simulation.WORLD_SIZE/20);
		TestHelper.cleanup(simulation, timeStep);
		StomachRecommendation tmp = new StomachRecommendation(lowGrassP, grassP);
		tmp.printStuff();
		return tmp;
	}
	
	@Test
	public void findSuitableBloodP() {
		StomachRecommendation grassThingP = findSuitableGrassP();
		StomachRecommendation bloodThingP = new StomachRecommendation();
		
		float bloodP = 0f;
		int numGrasslers;
		boolean foundLowB = false;
		
		int type1 = GRASSLER;
		int type2 = BLOODLING;
		int initNumAgents1 = 500;
		int initNumAgents2 = 25;
		
		do {
			bloodP += 0.1f;
			SkillSet.changeSkillMax(SkillSet.DIGEST_GRASS, grassThingP.highLimit);
			SkillSet.changeSkillMax(SkillSet.DIGEST_BLOOD, bloodP);
			testMultipleAgents(type1, type2, initNumAgents1, initNumAgents2);
			numGrasslers = simulation.getNumAgents(GRASSLER);
			TestHelper.cleanup(simulation, timeStep);
			if (!foundLowB && maxNumType2 > initNumAgents2 + 5) {
				foundLowB = true;
				bloodThingP.lowLimit = bloodP;
			}
			Assert.assertTrue("Did not expect bloodP to be this high",  bloodP < 30);
		} while (numGrasslers != 0);
		bloodThingP.highLimit = bloodP;
		bloodThingP.setMean();
		
		grassThingP.printStuff();
		bloodThingP.printStuff();
		
		TestHelper.cleanup(simulation, timeStep);
	}

	@Test //TODO: MOVE; not an 
	public void test4SpeciesTest() {
		Brainler a = new Brainler(0, null, null);
		a.inherit(null);
		Brainler b = new Brainler(0, null, null);
		
		b.inherit(null);
		Assert.assertFalse(a.isCloselyRelatedTo(b));
		
		b.inherit(a);
		Assert.assertTrue(a.isCloselyRelatedTo(b));
		
		boolean relation;
		for (int gen = 0; gen < 25; gen++) {
			b.inherit(b);
			relation = a.isCloselyRelatedTo(b);
			if (!relation) {
				System.out.println("Became non-related after " + gen + " generations");
				break;
			}
		}
		Assert.assertFalse("Expected not related after 25 generations. relation = " + a.findRelationTo(b), a.isCloselyRelatedTo(b));
	}
	
	@After
	public void betweenTests() {
		System.out.println("Between tests cleanup & sanity check");
		sanityCheck();
		TestHelper.cleanup(simulation, timeStep);
	}
	
	@Test
	public void testUsageOfEveryAction() {
		float maxB = Constants.SkillSet.MAX_DIGEST_BLOOD;
		
		SkillSet.changeSkillMax(SkillSet.DIGEST_BLOOD, 0);
		
		TestHelper.verifyWorldEmpty(simulation);
		
		testSurvivability(BRAINLER, 2000, 500);
		
		float numCalls = printActions();
		float huntStrangerProcAtNoBloodGain = ((float) Action.huntStranger.numCalls * 100) / numCalls;
		float seekBloodProcAtNoBloodGain = ((float) Action.seekBlood.numCalls * 100) / numCalls;
		
		TestHelper.cleanup(simulation, timeStep);
		
		SkillSet.changeSkillMax(SkillSet.DIGEST_BLOOD, Constants.SkillSet.MAX_DIGEST_BLOOD * 20);
		
		TestHelper.verifyWorldEmpty(simulation);
		testSurvivability(BRAINLER, 2000, 500);
		
		numCalls = printActions();
		
		
		float huntStrangerProcAtHighBloodGain = ((float) Action.huntStranger.numCalls * 100) / numCalls;
		float seekBloodProcAtHighBloodGain = ((float) Action.seekBlood.numCalls * 100) / numCalls;
		
		
		
		Assert.assertTrue(huntStrangerProcAtHighBloodGain > huntStrangerProcAtNoBloodGain);
		Assert.assertTrue(seekBloodProcAtHighBloodGain > seekBloodProcAtNoBloodGain);
	}


	/////////////
	// HELPERS //
	/////////////
	private int printActions() {
		int numCalls = (int) Action.getTotCalls();
		for (Action act : Action.acts) {
			float perc = ((float) act.numCalls * 100) / numCalls;
			System.out.println(act.getClass().getSimpleName() + ": " + perc + "%");
		}
		return numCalls;
	}

	private void testMultipleAgents(int type1, int type2, int initNumAgents1, int initNumAgents2) {
		testSurvivability(type1, 1000, initNumAgents1);
		maxNumType1 = 0;
		maxNumType2 = 0;
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
	}
	
	private void testSurvivability(int agentType, int simTime, int numInit) {
		System.out.println("Testing survivability of " + AGENT_TYPES_NAMES[agentType]);
		simulation.spawnRandomAgents(agentType, 500);
		int t;
		for (t = 0; t < simTime; t++) {
			simulation.step(timeStep++);
			if (simulation.agentManagers.get(agentType).numAgents == 0) {
				System.out.println(AGENT_TYPES_NAMES[agentType] + "Died after " + t);
				break;
			}
		}
		System.out.println(AGENT_TYPES_NAMES[agentType] + " Survivability test completed after " + t + " time steps, with " + simulation.agentManagers.get(agentType).numAgents + " survivors");
	}
	
	private void testWorldPopulated(int agentType) {
		TestHelper.verifyWorldEmpty(simulation);
		simulation.spawnRandomAgents(agentType, 100);
		simulation.step(timeStep++);
		TestHelper.verifyWorldNotEmpty(simulation);
	}
}