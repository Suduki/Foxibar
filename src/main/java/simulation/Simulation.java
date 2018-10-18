package simulation;

import vision.Vision;
import world.World;

import java.util.ArrayList;

import actions.Action;
import agents.Agent;
import agents.AgentManager;
import constants.Constants;
import messages.MessageHandler;
import messages.Message;
import messages.SpawnAnimals;

public class Simulation extends MessageHandler {
	public World mWorld;
	private boolean mPaused = false;
	
	public boolean isPaused() {
		return mPaused;
	}

	public void setPaused(boolean mPaused) {
		this.mPaused = mPaused;
	}
	public Vision vision = new Vision(Constants.Vision.WIDTH, Constants.Vision.HEIGHT);
	
	public ArrayList<AgentManager<?>> agentManagers = new ArrayList<>();
	
	public <T extends Agent> Simulation(Class<T>... classes)
	{
		Action.init();
		mWorld = new World();
		for (Class<T> clazz : classes) {
			agentManagers.add(new AgentManager<T>(mWorld, clazz, Constants.MAX_NUM_ANIMALS, vision));
		}
		this.message(new messages.DummyMessage());
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
			int x = Constants.RANDOM.nextInt(Constants.WORLD_SIZE_V.x);
			int y = Constants.RANDOM.nextInt(Constants.WORLD_SIZE_V.y);
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
