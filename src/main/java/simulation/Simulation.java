package simulation;

import vision.Vision;
import world.World;
import agents.AgentManager;
import agents.Bloodling;
import agents.Randomling;
import constants.Constants;
import messages.MessageHandler;
import messages.Message;

public class Simulation extends MessageHandler {
	public World mWorld;
	private boolean mPaused = false;
	public Vision vision = new Vision(Constants.Vision.WIDTH, Constants.Vision.HEIGHT);
	
	public AgentManager<Randomling> randomlingManager;
	public AgentManager<Bloodling> bloodlingManager;
	
	public Simulation()
	{
		mWorld = new World();
		this.message(new messages.DummyMessage());
		randomlingManager = new AgentManager(mWorld, Randomling.class, Constants.MAX_NUM_ANIMALS, vision);
		bloodlingManager = new AgentManager(mWorld, Bloodling.class, Constants.MAX_NUM_ANIMALS, vision);
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
			randomlingManager.moveAll();
			bloodlingManager.moveAll();
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
		randomlingManager.killAll = true;
		randomlingManager.moveAll();
		bloodlingManager.killAll = true;
		bloodlingManager.moveAll();
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
		if (id == 0) {
			randomlingManager.spawnAgent(pos, id);
		}
		else {
			bloodlingManager.spawnAgent(pos, id);
		}
	}
}
