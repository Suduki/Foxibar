package example;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import actions.Action;
import agents.Animal;
import agents.AnimalManager;
import agents.Brainler;
import agents.Bloodling;
import agents.Grassler;
import agents.Randomling;
import agents.Stomach;
import constants.Constants;
import simulation.Simulation;
import talents.StomachRecommendation;
import talents.Talents;
import testUtils.TestHelper;
import vision.Vision;

public class StabilityIT {
	private static Simulation simulation;

	private static final int RANDOMLING = 0;
	private static final int BLOODLING = 1;
	private static final int BRAINLER = 2;
	private static final int GRASSLER = 3;
	private static final String[] AGENT_TYPES_NAMES = new String[] { "Randomling", "Bloodling", "Brainler",
			"Grassler" };

	@BeforeClass
	public static void init() {
		simulation = new Simulation(Constants.WORLD_MULTIPLIER_TEST,
				new Class[] { Randomling.class, Bloodling.class, Brainler.class, Grassler.class });
		System.out.println("Before class completed");
	}

	private static void sanityCheck() {
		float[][] blood = simulation.mWorld.blood.height;
		float[][] grass = simulation.mWorld.grass.height;

		for (int x = 0; x < Simulation.WORLD_SIZE_X; ++x) {
			for (int y = 0; y < Simulation.WORLD_SIZE_Y; ++y) {
				Assert.assertTrue("Expected blood height to be positive and not too large, it was " + blood[x][y]
						+ " at x=" + x + " y=" + y, blood[x][y] >= 0 && blood[x][y] < 10 && !Float.isNaN(blood[x][y]));
				Assert.assertTrue("Expected grass height to be positive and not too large, it was " + grass[x][y]
						+ " at x=" + x + " y=" + y, grass[x][y] >= 0 && grass[x][y] < 10 && !Float.isNaN(grass[x][y]));
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
		TestHelper.cleanup(simulation);
		System.out.println("Testing Bloodling");
		testWorldPopulated(BLOODLING);
		TestHelper.cleanup(simulation);
		System.out.println("Testing Animal");
		testWorldPopulated(BRAINLER);
		TestHelper.cleanup(simulation);
		System.out.println("Testing Grassler");
		testWorldPopulated(GRASSLER);
		TestHelper.cleanup(simulation);
		System.out.println("Test case testWorldPopulated completed.");
	}

	@Test
	public void test2Survivability() {
		System.out.println("Initiating testSurvivability");
		testSurvivability(RANDOMLING, 5000, 100, false, true);
		TestHelper.verifyWorldNotEmpty(simulation);
		TestHelper.cleanup(simulation);

		testSurvivability(BLOODLING, 5000, 100, false, true);
		TestHelper.verifyWorldEmpty(simulation);
		TestHelper.cleanup(simulation);

		testSurvivability(BRAINLER, 5000, 100, false, true);
		TestHelper.verifyWorldNotEmpty(simulation);
		TestHelper.cleanup(simulation);

		testSurvivability(GRASSLER, 5000, 100, false, true);
		TestHelper.verifyWorldNotEmpty(simulation);
		TestHelper.cleanup(simulation);

		System.out.println("Test case testSurvivability completed.");
	}

	public StomachRecommendation findSuitableGrassP() {
		float grassP = 0;
		int numAgents;
		boolean foundLowG = false;
		float lowGrassP = 0;

		int numInitGrasslers = 100;
		testSurvivability(GRASSLER, 500, numInitGrasslers, false, true);
		System.out.println("Running multiple simulations to determine proper grass digestion value.");
		do {
			grassP += 0.2f;
			Talents.changeTalentMax(Talents.DIGEST_GRASS, grassP);
			testSurvivability(GRASSLER, 500, numInitGrasslers, false, false);
			numAgents = simulation.getNumAgents(GRASSLER);
			TestHelper.cleanup(simulation);
			if (!foundLowG && numAgents > numInitGrasslers + 5) {
				foundLowG = true;
				lowGrassP = grassP;

				System.out.println(" Found low limit.");
			}
			System.out.print(".");
		} while (numAgents < Simulation.WORLD_SIZE / 40);
		System.out.println(" Done.");
		TestHelper.cleanup(simulation);
		StomachRecommendation tmp = new StomachRecommendation(lowGrassP, grassP);
		tmp.printStuff();
		return tmp;
	}

	@Test
	public void findSuitableBloodP() {
		StomachRecommendation grassThingP = findSuitableGrassP();
		StomachRecommendation bloodThingP = new StomachRecommendation();

		float bloodP = 0.1f;
		int numGrasslers;
		boolean foundLowB = false;

		int type1 = GRASSLER;
		int type2 = BLOODLING;
		int initNumAgents1 = 500;
		int initNumAgents2 = 25;

		Talents.changeTalentMax(Talents.DIGEST_GRASS, grassThingP.mean);
		testSurvivability(type1, 500, initNumAgents1, false, true); // Get the first agent type balanced

		System.out.println("Running multiple simulations to determine proper blood digestion value.");
		do {
			maxNumType1 = 0;
			maxNumType2 = 0;
			bloodP += 0.5f;
			Talents.changeTalentMax(Talents.DIGEST_BLOOD, bloodP);

			int numGrasslersToSpawn = simulation.getNumAgents(type1) > 0
					? initNumAgents1 - simulation.getNumAgents(type1)
					: 0;

			testMultipleAgents(type1, type2, numGrasslersToSpawn, initNumAgents2, false);
			numGrasslers = simulation.getNumAgents(type1);
			if (!foundLowB && maxNumType2 > initNumAgents2 + 5) {
				foundLowB = true;
				bloodThingP.lowLimit = bloodP;
				System.out.println(" Found low limit.");
			}
			System.out.print(".");
		} while (numGrasslers != 0);

		System.out.println(" Done.");

		bloodThingP.highLimit = bloodP;
		bloodThingP.setMean();

		grassThingP.printStuff();
		bloodThingP.printStuff();

		grassThingP.save(StomachRecommendation.grassFile);
		bloodThingP.save(StomachRecommendation.bloodFile);

		TestHelper.cleanup(simulation);
	}

	@Test // TODO: MOVE; not an
	public void test4SpeciesTest() {
		Brainler a = new Brainler(null);
		a.inherit(null);
		Brainler b = new Brainler(null);

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
		Assert.assertFalse("Expected not related after 25 generations. relation = " + a.findRelationTo(b),
				a.isCloselyRelatedTo(b));
	}

	@After
	public void betweenTests() {
		System.out.println("Between tests cleanup & sanity check");
		sanityCheck();
		TestHelper.cleanup(simulation);
	}

	@Test
	public void testUsageOfEveryAction() {
		
		System.out.println("testUsageOfEveryAction");

		float maxB = Constants.Talents.MAX_DIGEST_BLOOD;

		int numSimulationIterations = 10;

		Talents.changeTalentMax(Talents.DIGEST_BLOOD, 0);

		System.out.printf("Running %d iterations with low blood gain\n", numSimulationIterations);
		float[] actionPercentagesAtLowBloodGain = new float[Action.numActions];
		runSeveralIterationsAndTrackActions(numSimulationIterations, actionPercentagesAtLowBloodGain);
		printActionsPercentages(actionPercentagesAtLowBloodGain);

		Talents.changeTalentMax(Talents.DIGEST_BLOOD, maxB * 20);

		System.out.printf("Running %d iterations with high blood gain: %f\n", numSimulationIterations, maxB * 20);
		float[] actionPercentagesAtHighBloodGain = new float[Action.numActions];
		runSeveralIterationsAndTrackActions(numSimulationIterations, actionPercentagesAtHighBloodGain);
		printActionsPercentages(actionPercentagesAtHighBloodGain);

		Assert.assertTrue(
				actionPercentagesAtHighBloodGain[Action.huntStranger.id] > actionPercentagesAtLowBloodGain[Action.huntStranger.id]);
		Assert.assertTrue(
				actionPercentagesAtHighBloodGain[Action.harvestBlood.id] > actionPercentagesAtLowBloodGain[Action.harvestBlood.id]);

	}

	private void printActionsPercentages(float[] actionPercentages) {
		for (int i = 0; i < actionPercentages.length; ++i) {
			Action act = Action.acts.get(i);
			float perc = actionPercentages[i];
			System.out.printf("%18s: %2.2f%s\n", act.getClass().getSimpleName(), perc, "%");
		}
	}

	private void runSeveralIterationsAndTrackActions(int numSimulationIterations, float[] actionPercentages) {
		for (int simulationIteration = 0; simulationIteration < numSimulationIterations; simulationIteration++) {
			TestHelper.cleanup(simulation);
			testSurvivability(BRAINLER, 2000, 500, true, false);

			float numCalls = Action.getTotCalls();
			for (int i = 0; i < actionPercentages.length; ++i) {
				actionPercentages[i] += calculateCallPercentage(numCalls, Action.acts.get(i)) / numSimulationIterations;
			}
			System.out.print(".");
		}
		System.out.println(" Done.");

	}

	private float calculateCallPercentage(float numCalls, Action action) {
		return ((float) action.numCommits * 100) / numCalls;
	}

	/////////////
	// HELPERS //
	/////////////
	private void printActions(int numCalls) {

		for (Action act : Action.acts) {
			float perc = ((float) act.numCommits * 100) / numCalls;
			System.out.printf("%18s: %.2f%s\n", act.getClass().getSimpleName(), perc, "%");
		}
	}

	private void testMultipleAgents(int type1, int type2, int initNumAgents1, int initNumAgents2, boolean printStuff) {
		simulation.spawnAgentsAtRandomPosition(type1, initNumAgents1);
		simulation.spawnAgentsAtRandomPosition(type2, initNumAgents2);
		int t = 0;
		while (t < 1000) {
			++t;
			simulation.step();
			if (maxNumType2 < simulation.getNumAgents(type2)) {
				maxNumType2 = simulation.getNumAgents(type2);
			}
			if (maxNumType1 < simulation.getNumAgents(type1)) {
				maxNumType1 = simulation.getNumAgents(type1);
			}
			if (simulation.getNumAgents(type1) == 0) {
				if (printStuff)
					System.out.println(AGENT_TYPES_NAMES[type1] + " died after " + t + " time steps.");
				if (printStuff)
					System.out
							.println("Num " + AGENT_TYPES_NAMES[type2] + " alive = " + simulation.getNumAgents(type2));
				if (printStuff)
					System.out.println("Max number of " + AGENT_TYPES_NAMES[type1] + ": " + maxNumType1);
				return;
			}
			if (simulation.getNumAgents(type2) == 0) {
				if (printStuff)
					System.out.println(AGENT_TYPES_NAMES[type2] + " died after " + t + " time steps.");
				if (printStuff)
					System.out
							.println("Num " + AGENT_TYPES_NAMES[type1] + " alive = " + simulation.getNumAgents(type1));
				if (printStuff)
					System.out.println("Max number of " + AGENT_TYPES_NAMES[type2] + ": " + maxNumType2);
				return;
			}
		}
		if (printStuff)
			System.out.println("Both survived.");
		if (printStuff)
			System.out.println("Num " + AGENT_TYPES_NAMES[type1] + " alive = " + simulation.getNumAgents(type1));
		if (printStuff)
			System.out.println("Num " + AGENT_TYPES_NAMES[type2] + " alive = " + simulation.getNumAgents(type2));

	}

	private void testSurvivability(int agentType, int simTime, int numInit, boolean continuousSpawn, boolean printStuff) {
		if (printStuff)
			System.out.println("Testing survivability of " + AGENT_TYPES_NAMES[agentType]);

		simulation.spawnAgentsAtRandomPosition(agentType, numInit);
		int t;
		for (t = 0; t < simTime; t++) {
			int numActiveAgents = simulation.getNumAgents(agentType);
			if (continuousSpawn && numActiveAgents < numInit) {
				simulation.spawnAgentsAtRandomPosition(agentType, numInit - numActiveAgents);
			}
			simulation.step();
		}
		if (printStuff)
			System.out.println(AGENT_TYPES_NAMES[agentType] + " Survivability test completed after " + t
					+ " time steps, with " + simulation.animalManagers.get(agentType).numAnimals + " survivors");
	}

	private void testWorldPopulated(int agentType) {
		TestHelper.verifyWorldEmpty(simulation);
		simulation.spawnAgentsAtRandomPosition(agentType, 100);
		simulation.step();
		TestHelper.verifyWorldNotEmpty(simulation);
	}
}