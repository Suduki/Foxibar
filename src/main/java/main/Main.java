package main;

import messages.SpawnAnimals;
import actions.Action;
import agents.Bloodling;
import agents.Grassler;
import constants.Constants;
import display.DisplayHandler;
import display.RenderState;
import simulation.Simulation;
import utils.FPSLimiter;

public class Main
{
	public final static int plottingNumber = 5000;
	public static Simulation mSimulation;
	public static void main(String[] args)
	{
		
		mSimulation     = new Simulation(Constants.WORLD_MULTIPLIER_MAIN, new Class[] {Grassler.class, Bloodling.class});
		DisplayHandler displayHandler = new DisplayHandler(mSimulation);
		FPSLimiter     fpsLimiter     = new FPSLimiter(Constants.WANTED_FPS);
		RenderState.activateState(RenderState.RENDER_WORLD_STILL);
		
		try
		{
			mSimulation.message(new SpawnAnimals());
			int timeStep = 0;
			while (mSimulation.handleMessages() && displayHandler.renderThreadThread.isAlive())
			{
				timeStep++;
				mSimulation.step(timeStep);
				fpsLimiter.waitForNextFrame();
			}
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
		
		dump();
		System.out.println("Simulation (main) thread finished.");
	}
	
	private static void dump() {
		for (Action act : Action.acts) {
			System.out.println(act.getClass().getSimpleName() + " " + act.numCalls);
		}
	}

	
	
}