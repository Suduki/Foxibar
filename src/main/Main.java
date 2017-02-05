package main;

import constants.Constants;
import display.DisplayHandler;
import simulation.Simulation;
import utils.FPSLimiter;

public class Main
{
	public static void main(String[] args)
	{
		
		Simulation     simulation     = new Simulation();
		DisplayHandler displayHandler = new DisplayHandler(simulation);
		FPSLimiter     fpsLimiter     = new FPSLimiter(Constants.WANTED_FPS);

		try
		{
			while (simulation.handleMessages() && displayHandler.renderThreadThread.isAlive())
			{
				simulation.step();
				fpsLimiter.waitForNextFrame();
			}
		}
		catch ( IllegalStateException e)
		{
			e.printStackTrace();
		}
		finally
		{
			displayHandler.exit();
		}

		System.out.println("Simulation (main) thread finished.");
	}
}