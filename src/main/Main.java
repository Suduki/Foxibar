package main;

import constants.Constants;
import display.DisplayHandler;
import simulation.Simulation;
import utils.CLI;
import utils.FPSLimiter;
import utils.PerformanceMonitor;

import java.util.Map;

public class Main
{
	public static void main(String[] args)
	{
		
		Simulation     simulation     = new Simulation();
		DisplayHandler displayHandler = new DisplayHandler(simulation);
		FPSLimiter     fpsLimiter     = new FPSLimiter(Constants.WANTED_FPS);

		//CLI
		new Thread(CLI.instance).start();

		try
		{
			while (simulation.handleMessages() && displayHandler.renderThreadThread.isAlive())
			{
				simulation.step();
				PerformanceMonitor.instance.ToggleMonitoring("FrameWaiting");
				fpsLimiter.waitForNextFrame();
				PerformanceMonitor.instance.ToggleMonitoring("FrameWaiting");
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