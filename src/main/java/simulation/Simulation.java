package simulation;

import vision.Vision;
import world.World;

import java.util.ArrayList;

import agents.Agent;
import agents.AgentManager;
import constants.Constants;
import messages.MessageHandler;
import messages.Message;

public class Simulation extends MessageHandler {
	public World mWorld;
	private boolean mPaused = false;
	public Vision vision = new Vision(Constants.Vision.WIDTH, Constants.Vision.HEIGHT);
	
	public ArrayList<AgentManager<?>> agentManagers = new ArrayList<>();
	
	public <T extends Agent> Simulation(Class<T>... classes)
	{
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
			for (AgentManager<?> aM : agentManagers) {
				aM.moveAll();
			}
		}
	}
	
	public World getWorld()
	{
		return mWorld;
	}
	
	public void setPause(boolean pPaused)
	{
		mPaused = pPaused;
	}

	public void killAllAgents() {
		for (AgentManager<?> aM : agentManagers) {
			aM.killAll = true;
			aM.moveAll();
		}
	}
	
	public void spawnRandomAgents(int managerId, int speciesId, int num) {
		for (int i = 0; i < num; ++i) {
			int pos = Constants.RANDOM.nextInt(Constants.WORLD_SIZE);
			spawnAgent(pos, managerId, speciesId);
		}
	}

	public void resetWorld(boolean b) {
		mWorld.reset(b);
	}

	public void spawnAgent(int pos, int managerId, int speciesId) {
		if (agentManagers.size() >= managerId) {
			agentManagers.get(managerId).spawnAgent(pos, speciesId);
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
}
