package simulation;

import vision.Vision;
import world.World;

import java.util.concurrent.ConcurrentLinkedQueue;

import agents.Animal;
import agents.AnimalManager;
import agents.NeuralNetwork;
import messages.MessageHandler;
import messages.Message;

public class Simulation extends MessageHandler {
	public World mWorld;
	private boolean mPaused = false;
	
	public static  AnimalManager animalManager;
	
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
}
