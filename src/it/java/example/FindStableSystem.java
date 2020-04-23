package example;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.jfree.data.statistics.HistogramDataset;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import agents.Animal;
import agents.Bloodling;
import agents.Brainler;
import agents.Giraffe;
import agents.Grassler;
import agents.Randomling;
import constants.Constants;
import plant.Plant;
import plant.PlantTest;
import simulation.Simulation;
import talents.StomachRecommendation;
import talents.Talents;
import testUtils.IntegrationTestWithSimulation;
import testUtils.TestHelper;

public class FindStableSystem extends IntegrationTestWithSimulation {

	private static float WANTED_AMOUNT_OF_TREES() {
		return Plant.WANTED_AVERAGE_AMOUNT_OF_PLANTS();
	}

	private static float WANTED_AMOUNT_OF_RANDOMLING() {
		return simulation.WORLD_SIZE / 20;
	}

	private static float WANTED_AMOUNT_OF_GIRAFFE() {
		return simulation.WORLD_SIZE / 50;
	};

	@Before
	public void init() {
	}

	// NOTE: This is not really a test, it is a program to generate settings for a
	// balanced simulation.
	@Test
	public void findSuitableEnergyGainLevels() {
		Talents.changeTalentMax(Talents.DIGEST_FIBER, 0);
		Talents.changeTalentMax(Talents.DIGEST_GRASS, 0);
		Talents.changeTalentMax(Talents.DIGEST_BLOOD, 0);

		StomachRecommendation fiberRecommendation = findSuitableFiberP();
		fiberRecommendation.printStuff();

		StomachRecommendation grassRecommendation = findSuitableGrassP();
		grassRecommendation.printStuff();

		StomachRecommendation bloodRecommendation = findSuitableBloodP();
		bloodRecommendation.printStuff();

		grassRecommendation.save(StomachRecommendation.grassFile);
		bloodRecommendation.save(StomachRecommendation.bloodFile);
		fiberRecommendation.save(StomachRecommendation.fiberFile);

		betweenTestsResetSimulation();
		simulation = new Simulation((short)6,
				new Class[] { Randomling.class, Bloodling.class, Brainler.class, Grassler.class, Giraffe.class });
		PlantTest.runOneTreeGeneration();
		float[] avgs = testMultipleAgents(new int[] { RANDOMLING, GIRAFFE },
				new int[] { (int) WANTED_AMOUNT_OF_RANDOMLING(), (int) WANTED_AMOUNT_OF_GIRAFFE() }, true);

		TestHelper.assertLessThan(0.3f * WANTED_AMOUNT_OF_RANDOMLING(), avgs[RANDOMLING]);
		TestHelper.assertLessThan(avgs[RANDOMLING], 2f * WANTED_AMOUNT_OF_RANDOMLING());
		TestHelper.assertLessThan(0.3f * WANTED_AMOUNT_OF_GIRAFFE(), avgs[GIRAFFE]);
		TestHelper.assertLessThan(avgs[GIRAFFE], 2f * WANTED_AMOUNT_OF_GIRAFFE());

		avgs = testMultipleAgents(new int[] { RANDOMLING, BLOODLING, GIRAFFE },
				new int[] { (int) WANTED_AMOUNT_OF_RANDOMLING(), simulation.WORLD_SIZE/30, (int) WANTED_AMOUNT_OF_GIRAFFE() }, true);

		assertTrue(simulation.getNumAgents(RANDOMLING) > 0);
		assertTrue(simulation.getNumAgents(BLOODLING) > 0);
		assertTrue(simulation.getNumAgents(GIRAFFE) > 0);

	}

	private StomachRecommendation findSuitableFiberP() {
		float fiberP = 0;
		boolean foundLowG = false;
		float lowFiberP = 0;

		int numInit = (int) WANTED_AMOUNT_OF_GIRAFFE();
		float highestAmountOfAlive = 0;
		
		Animal bestAgent = null;
		Animal bestOfAllTime = null;

		System.out.println("Running multiple simulations to determine proper fiber digestion value.");
		while (highestAmountOfAlive < WANTED_AMOUNT_OF_GIRAFFE() * 3f) {
			Constants.RANDOM = new Random(1);
			fiberP = nextStomachStep(fiberP, foundLowG);
			Talents.changeTalentMax(Talents.DIGEST_FIBER, fiberP);
			if (bestOfAllTime != null) simulation.mAnimalManagers.get(GIRAFFE).bestAgent = bestOfAllTime;

			highestAmountOfAlive = TestHelper.getHighest(testSurvivability(GIRAFFE, numInit, false, false, 300000));

			bestAgent = simulation.mAnimalManagers.get(GIRAFFE).bestAgent;
			if (bestOfAllTime == null || (bestAgent != null && bestAgent.score > bestOfAllTime.score)) {
				bestOfAllTime = bestAgent;
			}
			
			TestHelper.cleanup(simulation);

			if (!foundLowG && highestAmountOfAlive > WANTED_AMOUNT_OF_GIRAFFE() * 1.1f) {
				foundLowG = true;
				lowFiberP = fiberP;

				System.out.println(" Found low limit.");
			}
			System.out.print(".");
		}
		System.out.println(" Done.");
		TestHelper.cleanup(simulation);

		StomachRecommendation fiberThingP = new StomachRecommendation(lowFiberP, fiberP);
		return fiberThingP;
	}

	public StomachRecommendation findSuitableGrassP() {
		float grassP = 0;
		boolean foundLowG = false;
		float lowGrassP = 0;

		int numInitRandomlings = (int) WANTED_AMOUNT_OF_RANDOMLING() / 4;
		float highest = 0;

		System.out.println("Running multiple simulations to determine proper grass digestion value.");
		do {
			grassP = nextStomachStep(grassP);
			Talents.changeTalentMax(Talents.DIGEST_GRASS, grassP);
			highest = TestHelper.getHighest(testSurvivability(RANDOMLING, numInitRandomlings, false, false));

			TestHelper.cleanup(simulation);

			if (!foundLowG && highest > WANTED_AMOUNT_OF_RANDOMLING() / 2) {
				foundLowG = true;
				lowGrassP = grassP;

				System.out.println(" Found low limit.");
			}
			System.out.print(".");
		} while (highest < WANTED_AMOUNT_OF_RANDOMLING() * 3f);

		System.out.println(" Done.");
		TestHelper.cleanup(simulation);

		return new StomachRecommendation(lowGrassP, grassP);
	}

	public StomachRecommendation findSuitableBloodP() {
		StomachRecommendation bloodThingP = new StomachRecommendation();

		float bloodP = 0.1f;
		int numRandomlings;
		boolean foundLowB = false;

		int initNumRandomlings = (int) WANTED_AMOUNT_OF_RANDOMLING();
		int initNumBloodlings = 25;
		int initNumGiraffes = (int) WANTED_AMOUNT_OF_GIRAFFE();

		System.out.println("Running multiple simulations to determine proper blood digestion value.");
		do {
			bloodP = nextStomachStep(bloodP);
			Talents.changeTalentMax(Talents.DIGEST_BLOOD, bloodP);

			int numRandomlingsToSpawn = Integer.max(initNumRandomlings - simulation.getNumAgents(RANDOMLING), 0);
			int numGiraffeToSpawn = Integer.max(initNumGiraffes - simulation.getNumAgents(GIRAFFE), 0);

			float[] avgs = testMultipleAgents(new int[] { RANDOMLING, BLOODLING, GIRAFFE },
					new int[] { numRandomlingsToSpawn, initNumBloodlings, numGiraffeToSpawn }, true);
			numRandomlings = simulation.getNumAgents(RANDOMLING);
			if (!foundLowB && avgs[BLOODLING] > initNumBloodlings) {
				foundLowB = true;
				bloodThingP.lowLimit = bloodP;
				System.out.println(" Found low limit.");
			}
			System.out.print(".");
		} while (numRandomlings > 0.1f * WANTED_AMOUNT_OF_RANDOMLING());

		System.out.println(" Done.");

		bloodThingP.highLimit = bloodP;
		bloodThingP.setMean();

		return bloodThingP;
	}

	@Test
	public void testThatTreesAreAboutRight() {
		PlantTest.runOneTreeGeneration();

		float avg = 0;
		float simulationTime = Plant.MAX_AGE;
		// Run for one generation
		for (int i = 0; i < simulationTime; ++i) {
			simulation.step();
			float numTrees = simulation.mPlantManager.alive.size();
			avg += numTrees / simulationTime;
		}

		checkPercentages(avg, WANTED_AMOUNT_OF_TREES());
	}

	private float nextStomachStep(float old, boolean foundLow) {
		if (foundLow) {
			return old * 1.1f;
		}
		return nextStomachStep(old);
	}
	
	private float nextStomachStep(float old) {
		return old * 1.2f + 0.2f;
	}

	private void checkPercentages(float actualAmountOfThisType, float expectedAmountOfThisType) {

		float delta = Math.abs(actualAmountOfThisType - expectedAmountOfThisType);

		Assert.assertTrue(delta / expectedAmountOfThisType < 0.1f);
	}
}
