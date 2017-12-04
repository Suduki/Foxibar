package simulation;

import vision.Vision;
import world.World;

import java.util.concurrent.ConcurrentLinkedQueue;

import agents.AgentHandler;
import agents.Animal;
import agents.Animal2;
import agents.NeuralNetwork;
import messages.MessageHandler;
import messages.Message;

public class Simulation extends MessageHandler {
	private World mWorld;
	private boolean mPaused = false;
	public static AgentHandler agentHandler;
	
	public Simulation()
	{
		mWorld = new World();
		agentHandler.init();
		Vision.init();
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
			Animal2.moveAll();
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
