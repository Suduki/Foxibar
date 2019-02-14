package example;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import agents.Bloodling;
import agents.Brainler;
import agents.Giraffe;
import agents.Grassler;
import agents.Randomling;
import constants.Constants;
import plant.Plant;
import simulation.Simulation;
import talents.StomachRecommendation;
import talents.Talents;
import testUtils.IntegrationTestWithSimulation;
import testUtils.TestHelper;
import testUtils.TestWithSimulation;

public class FindStableSystem extends IntegrationTestWithSimulation {
	
	private static float WANTED_AMOUNT_OF_TREES;
	private static float WANTED_AMOUNT_OF_GRASSLER;
	private static float WANTED_AMOUNT_OF_GIRAFFE;

	@Before
	public  void init() {
		WANTED_AMOUNT_OF_TREES = Plant.WANTED_AVERAGE_AMOUNT_OF_PLANTS;
		WANTED_AMOUNT_OF_GRASSLER = simulation.WORLD_SIZE / 20;
		WANTED_AMOUNT_OF_GIRAFFE = simulation.WORLD_SIZE / 50;
	}
	
	//NOTE: This is not really a test, it is a thing to generate settings for a balanced simulation.
	@Test
	public void findSuitableEnergyGainLevels() {
		StomachRecommendation fiberRecommendation = findSuitableFiberP();
		
		fiberRecommendation.printStuff();
		Talents.changeTalentMax(Talents.DIGEST_FIBER, fiberRecommendation.mean);
		
		StomachRecommendation grassRecommendation = findSuitableGrassP();
		
		Talents.changeTalentMax(Talents.DIGEST_GRASS, grassRecommendation.mean);
		StomachRecommendation bloodRecommendation = findSuitableBloodP();

		grassRecommendation.printStuff();
		bloodRecommendation.printStuff();
		
		grassRecommendation.save(StomachRecommendation.grassFile);
		bloodRecommendation.save(StomachRecommendation.bloodFile);
		fiberRecommendation.save(StomachRecommendation.fiberFile);
	}
	
	private StomachRecommendation findSuitableFiberP() {
		float fiberP = 0;
		int numAgents;
		boolean foundLowG = false;
		float lowFiberP = 0;

		int numInit = (int) WANTED_AMOUNT_OF_GIRAFFE / 2;
		testSurvivability(GIRAFFE, 500, numInit, false, true);
		System.out.println("Running multiple simulations to determine proper fiber digestion value.");
		do {
			fiberP = nextStomachStep(fiberP);
			Talents.changeTalentMax(Talents.DIGEST_FIBER, fiberP);
			testSurvivability(GIRAFFE, 500, numInit, false, false);
			numAgents = simulation.getNumAgents(GIRAFFE);
			
			TestHelper.cleanup(simulation);
			
			if (!foundLowG && numAgents > WANTED_AMOUNT_OF_GIRAFFE) {
				foundLowG = true;
				lowFiberP = fiberP;

				System.out.println(" Found low limit.");
			}
			System.out.print(".");
		} while (numAgents < WANTED_AMOUNT_OF_GIRAFFE * 1.5f);
		System.out.println(" Done.");
		TestHelper.cleanup(simulation);
		
		StomachRecommendation fiberThingP = new StomachRecommendation(lowFiberP, fiberP);
		return fiberThingP;
	}

	public StomachRecommendation findSuitableGrassP() {
		float grassP = 0;
		int numAgents;
		boolean foundLowG = false;
		float lowGrassP = 0;

		int numInitGrasslers = (int) WANTED_AMOUNT_OF_GRASSLER / 4;
		testSurvivability(GRASSLER, 500, numInitGrasslers, false, true);
		System.out.println("Running multiple simulations to determine proper grass digestion value.");
		do {
			grassP += 0.2f;
			grassP = nextStomachStep(grassP);
			Talents.changeTalentMax(Talents.DIGEST_GRASS, grassP);
			testSurvivability(GRASSLER, 500, numInitGrasslers, false, false);
			numAgents = simulation.getNumAgents(GRASSLER);
			
			TestHelper.cleanup(simulation);
			
			if (!foundLowG && numAgents > WANTED_AMOUNT_OF_GRASSLER / 2) {
				foundLowG = true;
				lowGrassP = grassP;

				System.out.println(" Found low limit.");
			}
			System.out.print(".");
		} while (numAgents < WANTED_AMOUNT_OF_GRASSLER * 1.5f);
		System.out.println(" Done.");
		TestHelper.cleanup(simulation);
		StomachRecommendation tmp = new StomachRecommendation(lowGrassP, grassP);
		tmp.printStuff();
		return tmp;
	}

	public StomachRecommendation findSuitableBloodP() {
		StomachRecommendation bloodThingP = new StomachRecommendation();

		float bloodP = 0.1f;
		int numGrasslers;
		boolean foundLowB = false;

		int initNumGrasslers = (int) WANTED_AMOUNT_OF_GRASSLER/2;
		int initNumBloodlings = 25;
		int initNumGiraffes = (int) WANTED_AMOUNT_OF_GIRAFFE/2;

		testMultipleAgents(new int[]{GRASSLER, GIRAFFE}, new int[]{initNumGrasslers, initNumGiraffes}, false);

		System.out.println("Running multiple simulations to determine proper blood digestion value.");
		do {
			for (int i = 0; i < maxNumType.length; ++i) {
				maxNumType[i] = 0;
			}
			
			bloodP = nextStomachStep(bloodP);
			Talents.changeTalentMax(Talents.DIGEST_BLOOD, bloodP);

			int numGrasslersToSpawn = Integer.max(initNumGrasslers - simulation.getNumAgents(GRASSLER), 0);
			int numGiraffeToSpawn = Integer.max(initNumGiraffes - simulation.getNumAgents(GIRAFFE), 0);

			testMultipleAgents(new int[]{GRASSLER, BLOODLING, GIRAFFE}, new int[]{numGrasslersToSpawn, initNumBloodlings, numGiraffeToSpawn}, false);
			numGrasslers = simulation.getNumAgents(GRASSLER);
			if (!foundLowB && maxNumType[BLOODLING] > initNumBloodlings + 5) {
				foundLowB = true;
				bloodThingP.lowLimit = bloodP;
				System.out.println(" Found low limit.");
			}
			System.out.print(".");
		} while (numGrasslers != 0);

		System.out.println(" Done.");

		bloodThingP.highLimit = bloodP;
		bloodThingP.setMean();
		
		return bloodThingP;
	}
	
	@Test
	public void testThatTreesAreAboutRight() {
		// Run for one generation
		for (int i = 0; i < Plant.MAX_AGE; ++i) {
			simulation.step();
		}
		
		long sum = 0;
		int simulationTime = (int) Plant.MAX_AGE;
		// Run for one generation
		for (int i = 0; i < simulationTime; ++i) {
			simulation.step();
			int numTrees = simulation.plantManager.alive.size();
			sum += numTrees;
		}
		
		float avg = sum / simulationTime;
		checkPercentages(avg, WANTED_AMOUNT_OF_TREES);
	}

	private float nextStomachStep(float old) {
		return old * 1.2f + 0.2f;
	}
	
	private void checkPercentages(float actualAmountOfThisType, float expectedAmountOfThisType) {

		float delta = Math.abs(actualAmountOfThisType - expectedAmountOfThisType);
		
		Assert.assertTrue(delta/expectedAmountOfThisType > 0.9f);
	}
}
