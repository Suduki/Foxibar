package main;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import constants.Constants;
import simulation.Simulation;
import agents.Agent;
import agents.Bloodling;
import agents.Brainler;
import agents.Grassler;
import agents.Randomling;

public class VisionTest {
	
	private static Simulation     simulation;
	
	private static final int RANDOMLING = 0;
	private static final int BLOODLING = 1;
	private static final int BRAINLER = 2;
	private static final int GRASSLER = 3;
	private static final String[] AGENT_TYPES_NAMES = new String[]{"Randomling", "Bloodling", "Brainler", "Grassler"};
	
	private static Integer timeStep = 1;

	@BeforeClass
	public static void init() {
		simulation     = new Simulation(new Class[] {Randomling.class, Bloodling.class, Brainler.class, Grassler.class});
		System.out.println("Before class completed");
	}
	

	@Test
	public void thatAnimalsMove() {
		System.out.println("Testing that " + AGENT_TYPES_NAMES[RANDOMLING] + " moves");
		TestHelper.verifyWorldEmpty(simulation);
		simulation.spawnAgent(5, 6, RANDOMLING);
		
		simulation.step(timeStep++);

		Agent a = simulation.agentManagers.get(RANDOMLING).alive.get(0);
		Assert.assertNotEquals(a.pos.x, 5);
		Assert.assertNotEquals(a.pos.y, 6);
		
		TestHelper.cleanup(simulation, timeStep);
		System.out.println("Test Completed");
	}
	
	@Test
	public void thatAnimalsHuntOverEdges() {
		System.out.println("Testing that " + AGENT_TYPES_NAMES[BLOODLING] + " hunts over edges");
		TestHelper.verifyWorldEmpty(simulation);
		simulation.spawnAgent(0, 0, BLOODLING);
		simulation.spawnAgent(Constants.WORLD_SIZE_X - 2, Constants.WORLD_SIZE_Y - 2, GRASSLER);
		
		simulation.step(timeStep++);
		simulation.step(timeStep++);

		Agent a = simulation.agentManagers.get(BLOODLING).alive.get(0);
		Assert.assertTrue(a.pos.x > 5);
		Assert.assertTrue(a.pos.y > 5);
		TestHelper.cleanup(simulation, timeStep);
		System.out.println("Test Completed");
	}
	
	@Test
	public void thatAnimalsFleeOverEdges() {
		System.out.println("Testing that " + AGENT_TYPES_NAMES[GRASSLER] + " flees over edges");
		TestHelper.verifyWorldEmpty(simulation);
		simulation.spawnAgent(2, 2, BLOODLING);
		simulation.spawnAgent(0, 0, GRASSLER);
		
		simulation.step(timeStep++);
		simulation.step(timeStep++);

		Agent a = simulation.agentManagers.get(GRASSLER).alive.get(0);
		Assert.assertTrue(a.pos.x > 5);
		Assert.assertTrue(a.pos.y > 5);
		TestHelper.cleanup(simulation, timeStep);
		System.out.println("Test Completed");
	}
}
