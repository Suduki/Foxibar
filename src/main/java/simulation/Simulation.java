package simulation;

import world.World;

import agents.AnimalManager;
import agents.Species;
import constants.Constants;
import messages.MessageHandler;
import messages.Message;

public class Simulation extends MessageHandler {
	public World mWorld;
	private boolean mPaused = false;
	
	public AnimalManager animalManager;
	
	public Simulation()
	{
		mWorld = new World();
		this.message(new messages.DummyMessage());
		animalManager = new AnimalManager(mWorld);
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
			animalManager.moveAll();
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

	public void killAllAnimals() {
		animalManager.killAll=true;
		animalManager.moveAll();
	}
	
	public void spawnRandomAnimal(Species species, int num) {
		for (int i = 0; i < num; ++i) {
			int pos = Constants.RANDOM.nextInt(Constants.WORLD_SIZE);
			animalManager.spawn(pos, species);
		}
	}

	public void resetWorld(boolean b) {
		mWorld.reset(b);
	}
}
