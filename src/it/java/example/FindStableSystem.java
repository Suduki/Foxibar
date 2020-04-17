package example;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import plant.Plant;
import plant.PlantTest;
import talents.StomachRecommendation;
import talents.Talents;
import testUtils.IntegrationTestWithSimulation;
import testUtils.TestHelper;

public class FindStableSystem extends IntegrationTestWithSimulation {
	
	private static float WANTED_AMOUNT_OF_TREES;
	private static float WANTED_AMOUNT_OF_RANDOMLING;
	private static float WANTED_AMOUNT_OF_GIRAFFE;

	@Before
	public  void init() {
		WANTED_AMOUNT_OF_TREES = Plant.WANTED_AVERAGE_AMOUNT_OF_PLANTS;
		WANTED_AMOUNT_OF_RANDOMLING = simulation.WORLD_SIZE / 20;
		WANTED_AMOUNT_OF_GIRAFFE = simulation.WORLD_SIZE / 25;
	}
	
	//NOTE: This is not really a test, it is a thing to generate settings for a balanced simulation.
	@Test
	public void findSuitableEnergyGainLevels() {
		Talents.changeTalentMax(Talents.DIGEST_FIBER, 0);
		Talents.changeTalentMax(Talents.DIGEST_GRASS, 0);
		Talents.changeTalentMax(Talents.DIGEST_BLOOD, 0);
		
		PlantTest.runOneTreeGeneration();
		
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
		PlantTest.runOneTreeGeneration();
		float[] avgs = testMultipleAgents(new int[]{RANDOMLING, GIRAFFE}, 
				new int[]{(int) WANTED_AMOUNT_OF_RANDOMLING, (int) WANTED_AMOUNT_OF_GIRAFFE}, true);
		
		TestHelper.assertLessThan(0.5f * WANTED_AMOUNT_OF_RANDOMLING, avgs[RANDOMLING]);
		TestHelper.assertLessThan(avgs[RANDOMLING], 1.5f * WANTED_AMOUNT_OF_RANDOMLING);
		TestHelper.assertLessThan(0.5f * WANTED_AMOUNT_OF_GIRAFFE, avgs[GIRAFFE]);
		TestHelper.assertLessThan(avgs[GIRAFFE], 1.5f * WANTED_AMOUNT_OF_GIRAFFE);
		
		avgs = testMultipleAgents(new int[]{RANDOMLING, BLOODLING, GIRAFFE}, 
				new int[]{(int) WANTED_AMOUNT_OF_RANDOMLING, 20, (int) WANTED_AMOUNT_OF_GIRAFFE}, true);
		
		TestHelper.assertLessThan(0.1f * WANTED_AMOUNT_OF_RANDOMLING, avgs[RANDOMLING]);
		TestHelper.assertLessThan(10, avgs[BLOODLING]);
		TestHelper.assertLessThan(0.1f * WANTED_AMOUNT_OF_GIRAFFE, avgs[GIRAFFE]);
		
	}
	
	private StomachRecommendation findSuitableFiberP() {
		float fiberP = 0;
		boolean foundLowG = false;
		float lowFiberP = 0;

		int numInit = (int) WANTED_AMOUNT_OF_GIRAFFE / 2;
		testSurvivability(GIRAFFE, numInit, false, true);
		float averageAmountOfAlive = 0;
		
		System.out.println("Running multiple simulations to determine proper fiber digestion value.");
		do {
			fiberP = nextStomachStep(fiberP);
			Talents.changeTalentMax(Talents.DIGEST_FIBER, fiberP);
			averageAmountOfAlive = testSurvivability(GIRAFFE, numInit, false, false);
			
			TestHelper.cleanup(simulation);
			
			if (!foundLowG && averageAmountOfAlive > numInit) {
				foundLowG = true;
				lowFiberP = fiberP;

				System.out.println(" Found low limit.");
			}
			System.out.print(".");
		} while (averageAmountOfAlive < WANTED_AMOUNT_OF_GIRAFFE * 1.5f);
		System.out.println(" Done.");
		TestHelper.cleanup(simulation);
		
		StomachRecommendation fiberThingP = new StomachRecommendation(lowFiberP, fiberP);
		return fiberThingP;
	}

	public StomachRecommendation findSuitableGrassP() {
		float grassP = 0;
		boolean foundLowG = false;
		float lowGrassP = 0;

		int numInitRandomlings = (int) WANTED_AMOUNT_OF_RANDOMLING / 4;
		testSurvivability(RANDOMLING, numInitRandomlings, false, true);
		float average = 0;
		float average1 = 0;
		float average2 = 0;
		float average3 = 0;
		
		System.out.println("Running multiple simulations to determine proper grass digestion value.");
		do {
			grassP += 0.2f;
			grassP = nextStomachStep(grassP);
			Talents.changeTalentMax(Talents.DIGEST_GRASS, grassP);
			average = testSurvivability(RANDOMLING, numInitRandomlings, false, false);
			average1 = testSurvivability(RANDOMLING, numInitRandomlings, false, false);
			average2 = testSurvivability(RANDOMLING, numInitRandomlings, false, false);
			average3 = testSurvivability(RANDOMLING, numInitRandomlings, false, false);
			
			TestHelper.cleanup(simulation);
			
			if (!foundLowG && (average+average1+average2+average3)/4 > WANTED_AMOUNT_OF_RANDOMLING / 2) {
				foundLowG = true;
				lowGrassP = grassP;

				System.out.println(" Found low limit.");
			}
			System.out.print(".");
		} while (average < WANTED_AMOUNT_OF_RANDOMLING * 1.5f);
		
		System.out.println(" Done.");
		TestHelper.cleanup(simulation);
		
		return new StomachRecommendation(lowGrassP, grassP);
	}

	public StomachRecommendation findSuitableBloodP() {
		StomachRecommendation bloodThingP = new StomachRecommendation();

		float bloodP = 0.1f;
		int numRandomlings;
		boolean foundLowB = false;

		int initNumRandomlings = (int) WANTED_AMOUNT_OF_RANDOMLING/2;
		int initNumBloodlings = 25;
		int initNumGiraffes = (int) WANTED_AMOUNT_OF_GIRAFFE/2;

		System.out.println("Running multiple simulations to determine proper blood digestion value.");
		do {
			bloodP = nextStomachStep(bloodP);
			Talents.changeTalentMax(Talents.DIGEST_BLOOD, bloodP);

			int numRandomlingsToSpawn = Integer.max(initNumRandomlings - simulation.getNumAgents(RANDOMLING), 0);
			int numGiraffeToSpawn = Integer.max(initNumGiraffes - simulation.getNumAgents(GIRAFFE), 0);

			float[] avgs = testMultipleAgents(new int[]{RANDOMLING, BLOODLING, GIRAFFE}, new int[]{numRandomlingsToSpawn, initNumBloodlings, numGiraffeToSpawn}, true);
			numRandomlings = simulation.getNumAgents(RANDOMLING);
			if (!foundLowB && avgs[BLOODLING] > initNumBloodlings) {
				foundLowB = true;
				bloodThingP.lowLimit = bloodP;
				System.out.println(" Found low limit.");
			}
			System.out.print(".");
		} while (numRandomlings > 0.1f * WANTED_AMOUNT_OF_RANDOMLING);

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
		
		checkPercentages(avg, WANTED_AMOUNT_OF_TREES);
	}

	private float nextStomachStep(float old) {
		return old * 1.2f + 0.2f;
	}
	
	private void checkPercentages(float actualAmountOfThisType, float expectedAmountOfThisType) {

		float delta = Math.abs(actualAmountOfThisType - expectedAmountOfThisType);
		
		Assert.assertTrue(delta/expectedAmountOfThisType < 0.1f);
	}
}
