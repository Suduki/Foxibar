package simulation;

import vision.Vision;
import world.World;

import java.util.ArrayList;

import agents.Agent;
import agents.AgentManager;
import agents.Animal;
import agents.Bloodling;
import agents.Randomling;
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
	
	public void spawnRandomAgents(int id, int num) {
		for (int i = 0; i < num; ++i) {
			int pos = Constants.RANDOM.nextInt(Constants.WORLD_SIZE);
			spawnAgent(pos, id);
		}
	}

	public void resetWorld(boolean b) {
		mWorld.reset(b);
	}

	public void spawnAgent(int pos, int id) {
		if (agentManagers.size() >= id) {
			agentManagers.get(id).spawnAgent(pos, id);
		}
	}
}
