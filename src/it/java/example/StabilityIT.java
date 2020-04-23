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
import actions.ActionManager;
import actions.ActionManager.Actions;
import agents.Animal;
import agents.AnimalManager;
import agents.Brainler;
import agents.Bloodling;
import agents.Grassler;
import agents.Giraffe;
import agents.Randomling;
import agents.Stomach;
import constants.Constants;
import simulation.Simulation;
import talents.StomachRecommendation;
import talents.Talents;
import testUtils.IntegrationTestWithSimulation;
import testUtils.TestHelper;
import vision.Vision;

public class StabilityIT extends IntegrationTestWithSimulation {

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
		System.out.println("Testing Giraffe");
		testWorldPopulated(GIRAFFE);
		TestHelper.cleanup(simulation);
		System.out.println("Test case testWorldPopulated completed.");
	}

	@Test
	public void test2Survivability() {
		System.out.println("Initiating testSurvivability");
		testSurvivability(RANDOMLING, 100, false, true);
		TestHelper.verifyWorldNotEmpty(simulation);
		TestHelper.cleanup(simulation);

		testSurvivability(BLOODLING, 100, false, true);
		TestHelper.verifyWorldEmpty(simulation);
		TestHelper.cleanup(simulation);

		testSurvivability(BRAINLER, 100, false, true);
		TestHelper.verifyWorldNotEmpty(simulation);
		TestHelper.cleanup(simulation);

		testSurvivability(GRASSLER, 100, false, true);
		TestHelper.verifyWorldNotEmpty(simulation);
		TestHelper.cleanup(simulation);
		
		testSurvivability(GIRAFFE, 100, false, true);
		TestHelper.verifyWorldNotEmpty(simulation);
		TestHelper.cleanup(simulation);

		System.out.println("Test case testSurvivability completed.");
	}

	@Test // TODO: MOVE; not an IT
	public void test4SpeciesTest() {
		Brainler a = new Brainler(null, null);
		a.inherit(null);
		Brainler b = new Brainler(null, null);

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

	private void printActionsPercentages(float[] actionPercentages) {
		for (int i = 0; i < actionPercentages.length; ++i) {
			Action act = simulation.mActionManager.acts.get(i);
			float perc = actionPercentages[i];
			System.out.printf("%18s: %2.2f%s\n", act.getClass().getSimpleName(), perc, "%");
		}
	}

	private void runSeveralIterationsAndTrackActions(int numSimulationIterations, float[] actionPercentages) {
		for (int simulationIteration = 0; simulationIteration < numSimulationIterations; simulationIteration++) {
			TestHelper.cleanup(simulation);
			testSurvivability(BRAINLER, 500, true, false);

			float numCalls = simulation.mActionManager.getTotCalls();
			for (int i = 0; i < actionPercentages.length; ++i) {
				actionPercentages[i] += calculateCallPercentage(numCalls, simulation.mActionManager.acts.get(i)) / numSimulationIterations;
			}
			System.out.print(".");
		}
		System.out.println(" Done.");

	}

	private float calculateCallPercentage(float numCalls, Action action) {
		return ((float) action.numCommits * 100) / numCalls;
	}
}