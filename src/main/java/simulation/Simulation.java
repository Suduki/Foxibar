package main.java.simulation;

import main.java.agents.Animal;
import main.java.messages.DummyMessage;
import main.java.messages.Message;
import main.java.messages.MessageHandler;
import main.java.vision.Vision;
import main.java.world.World;

public class Simulation extends MessageHandler {
	private World mWorld;
	private boolean mPaused = false;
	
	public Simulation()
	{
		mWorld = new World();
		Animal.init();
		Vision.init();
		this.message(new DummyMessage());
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
			Animal.moveAll();
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
