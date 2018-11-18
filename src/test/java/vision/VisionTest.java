package vision;

import main.TestHelper;

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
		simulation     = new Simulation(Constants.WORLD_MULTIPLIER_TEST, new Class[] {Randomling.class, Bloodling.class, Brainler.class, Grassler.class});
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
		simulation.spawnAgent(Simulation.WORLD_SIZE_X - 2, Simulation.WORLD_SIZE_Y - 2, GRASSLER);
		
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

		Agent a = simulation.agentManagers.get(GRASSLER).alive.get(0);
		Assert.assertTrue(a.pos.x > 5);
		Assert.assertTrue(a.pos.y > 5);
		TestHelper.cleanup(simulation, timeStep);
		System.out.println("Test Completed");
	}
	
	@Test
	public void thatAnimalsFindFood() {
		System.out.println("Testing that " + AGENT_TYPES_NAMES[GRASSLER] + " finds food");
		TestHelper.verifyWorldEmpty(simulation);
		
		int startPosX = 10;
		int startPosY = 1;
		
		int foodPosX = 10;
		int foodPosY = 0;
		
		simulation.mWorld.grass.killAllGrass();
		simulation.mWorld.grass.append(foodPosX, foodPosY, 10, false);
		simulation.spawnAgent(startPosX, startPosY, GRASSLER);
		
		simulation.step(timeStep++);
		
		Agent a = simulation.agentManagers.get(GRASSLER).alive.get(0);
		Assert.assertTrue("Expecting animal to be at x = " + foodPosX + ", it was x=" + a.pos.x + " y=" + a.pos.y, ((int)a.pos.x) == foodPosX);
		Assert.assertTrue("Expecting animal to be at y = " + foodPosY + ", it was x=" + a.pos.x + " y=" + a.pos.y, ((int)a.pos.y) == foodPosY);
		TestHelper.cleanup(simulation, timeStep);
		System.out.println("Test Completed");
	}
	
	@Test //TODO Currently not working, this is a bug!
	public void thatAnimalsFindFoodOverEdges() {
		System.out.println("Testing that " + AGENT_TYPES_NAMES[GRASSLER] + " finds food over edges");
		TestHelper.verifyWorldEmpty(simulation);
		
		int startPosX = 10;
		int startPosY = 0;
		
		int foodPosX = 10;
		int foodPosY = Simulation.WORLD_SIZE_Y - 1;
		
		simulation.mWorld.grass.killAllGrass();
		simulation.mWorld.grass.append(foodPosX, foodPosY, 10, false);
		simulation.spawnAgent(startPosX, startPosY, GRASSLER);
		
		simulation.step(timeStep++);
		
		Agent a = simulation.agentManagers.get(GRASSLER).alive.get(0);
		Assert.assertTrue("Expecting animal to be at x = " + foodPosX + ", it was x=" + a.pos.x + " y=" + a.pos.y, ((int)a.pos.x) == foodPosX);
		Assert.assertTrue("Expecting animal to be at y = " + foodPosY + ", it was x=" + a.pos.x + " y=" + a.pos.y, ((int)a.pos.y) == foodPosY);
		TestHelper.cleanup(simulation, timeStep);
		System.out.println("Test Completed");
	}
	
	
}
