package main;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import agents.Animal;
import agents.Decision;
import agents.DecisionFactors;
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
			int timeStep = 0;
			while (simulation.handleMessages() && displayHandler.renderThreadThread.isAlive())
			{
				simulation.step();
				fpsLimiter.waitForNextFrame();
				
				timeStep++;
				if (timeStep % 1000 == 0) {
					System.out.format("BLOODLING ("+Decision.BLOODLING_TOTAL+"): ");
					for (int i = 0; i < Decision.NUM_WEIGHTS; ++i) {
						System.out.format(Decision.FACTOR_NAMES[i] + ": %.2f, ", Decision.BLOODLING_SUM[i]/Decision.BLOODLING_TOTAL);
					}
					System.out.println("");
					System.out.print("GRASSLER ("+Decision.GRASSLER_TOTAL+"): ");
					for (int i = 0; i < Decision.NUM_WEIGHTS; ++i) {
						System.out.format(Decision.FACTOR_NAMES[i] + ": %.2f, ", Decision.GRASSLER_SUM[i]/Decision.GRASSLER_TOTAL);
					}
					System.out.println("");
				}
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