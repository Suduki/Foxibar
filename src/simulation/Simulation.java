package simulation;

import utils.PerformanceMonitor;
import vision.Vision;
import world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import agents.Animal;
import messages.MessageHandler;
import messages.Message;

public class Simulation extends MessageHandler {
	private World mWorld;
	private boolean mPaused = false;
	
	public Simulation()
	{
		mWorld = new World();
		Animal.init();
		Vision.init();
		this.message(new messages.DummyMessage());
	}
	
	protected void evaluateMessage(Message pMessage)
	{
		pMessage.evaluate(this);
	}
	
	public void step()
	{
		if (!mPaused)
		{
			PerformanceMonitor.instance.ToggleMonitoring("WorldUpdate");
			mWorld.update();
			PerformanceMonitor.instance.ToggleMonitoring("WorldUpdate");
			PerformanceMonitor.instance.ToggleMonitoring("AnimalUpdate");
			Animal.moveAll();
			PerformanceMonitor.instance.ToggleMonitoring("AnimalUpdate");

			PerformanceMonitor.instance.ToggleMonitoring("AnimalVisionUpdate");
			Vision.updateAnimalVision();
			PerformanceMonitor.instance.ToggleMonitoring("AnimalVisionUpdate");
			//PerformanceMonitor.instance.printPercentagesOnAll();
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
