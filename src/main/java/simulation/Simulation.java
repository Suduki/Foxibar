package simulation;

import world.World;
import agents.AgentManager;
import agents.Animal;
import agents.Randomling;
import agents.Species;
import constants.Constants;
import messages.MessageHandler;
import messages.Message;

public class Simulation extends MessageHandler {
	public World mWorld;
	private boolean mPaused = false;
	
	public AgentManager<Randomling> agentManager;
	
	public Simulation()
	{
		mWorld = new World();
		this.message(new messages.DummyMessage());
		agentManager = new AgentManager(mWorld, Randomling.class);
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
			agentManager.moveAll();
			mWorld.wind.stepWind();
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
		agentManager.killAll = true;
		agentManager.moveAll();
	}
	
	public void spawnRandomAgent(int id, int num) {
		for (int i = 0; i < num; ++i) {
			int pos = Constants.RANDOM.nextInt(Constants.WORLD_SIZE);
			agentManager.spawnAgent(pos, id);
		}
	}

	public void resetWorld(boolean b) {
		mWorld.reset(b);
	}
}
