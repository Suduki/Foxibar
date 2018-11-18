package simulation;

import vision.Vision;
import world.World;

import java.util.ArrayList;

import actions.Action;
import agents.Agent;
import agents.AgentManager;
import constants.Constants;
import main.StomachRecommendation;
import messages.MessageHandler;
import messages.Message;
import messages.SpawnAnimals;
import talents.Talents;

public class Simulation extends MessageHandler {
	public World mWorld;
	private boolean mPaused = false;
	
	public static int WORLD_SIZE;
	public static int WORLD_SIZE_X;
	public static int WORLD_SIZE_Y;
	
	public boolean isPaused() {
		return mPaused;
	}

	public void setPaused(boolean mPaused) {
		this.mPaused = mPaused;
	}
	public Vision vision;
	
	public ArrayList<AgentManager<?>> agentManagers = new ArrayList<>();
	
	public <T extends Agent> Simulation(short worldMultiplier, Class<T>... classes)
	{
		loadStomachRecommendation();
		WORLD_SIZE_X = (int) Math.pow(2, worldMultiplier);
		WORLD_SIZE_Y = (int) Math.pow(2, worldMultiplier);
		WORLD_SIZE = WORLD_SIZE_X * WORLD_SIZE_Y;
		vision = new Vision(Constants.Vision.WIDTH, Constants.Vision.HEIGHT);
		Action.init();
		Talents.init();
		mWorld = new World(vision);
		for (Class<T> clazz : classes) {
			agentManagers.add(new AgentManager<T>(mWorld, clazz, Constants.MAX_NUM_ANIMALS, vision));
		}
		this.message(new messages.DummyMessage());
	}
	
	private void loadStomachRecommendation() {
		StomachRecommendation recommendation = StomachRecommendation.load(StomachRecommendation.bloodFile); 
		if (recommendation != null) {
			Constants.Talents.MAX_DIGEST_BLOOD = recommendation.mean;
			System.out.println("Found Blood file, will use that for the simulation");
		}
		recommendation = StomachRecommendation.load(StomachRecommendation.grassFile); 
		if (recommendation != null) {
			Constants.Talents.MAX_DIGEST_GRASS = recommendation.mean;
			System.out.println("Found Grass file, will use that for the simulation");
		}
	}

	protected void evaluateMessage(Message pMessage)
	{
		pMessage.evaluate(this);
	}
	
	public void step(int timeStep)
	{
		if (!mPaused)
		{
			mWorld.update(timeStep);
			SpawnAnimals.step();
			for (AgentManager<?> aM : agentManagers) {
				aM.synchAliveDead();
				aM.moveAll();
			}
		}
	}
	
	public World getWorld()
	{
		return mWorld;
	}
	
	public void killAllAgents() {
		for (AgentManager<?> aM : agentManagers) {
			aM.killAll = true;
			aM.synchAliveDead();
			aM.moveAll();
			aM.synchAliveDead();
		}
	}
	
	public void spawnRandomAgents(int managerId, int num) {
		
		for (int i = 0; i < num; ++i) {
			int x = Constants.RANDOM.nextInt(WORLD_SIZE_X);
			int y = Constants.RANDOM.nextInt(WORLD_SIZE_Y);
			spawnAgent(x, y, managerId);
		}
	}

	public void resetWorld(boolean b) {
		mWorld.reset(b);
	}

	public void spawnAgent(int x, int y, int managerId) {
		if (agentManagers.size() >= managerId) {
			agentManagers.get(managerId).spawnAgent(x, y);
		}
		else {
			System.err.println("Trying to spawn agents in a non-existing manager?");
		}
	}

	public int getNumAgents() {
		int numAgents = 0;
		for (AgentManager<?> aM : agentManagers) {
			numAgents += aM.numAgents;
		}
		return numAgents;
	}
	public int getNumAgents(int agentType) {
		AgentManager<?> aM = agentManagers.get(agentType);
		return aM.numAgents;
	}
}
