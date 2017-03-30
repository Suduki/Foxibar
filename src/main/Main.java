package main;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import agents.Animal;
import agents.Decision;
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

		System.out.println(Animal.numVals);
		System.out.println(Animal.HUNGER);
		System.out.println(Animal.FERTILE);
		System.out.println(Animal.AGE);
		System.out.println(Animal.TILE_GRASS);
		System.out.println(Animal.TILE_BLOOD);
		System.out.println(Animal.TILE_DANGER);
		System.out.println(Animal.TILE_FERTILITY);
		System.out.println(Animal.TILE_FRIENDS);
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
					for (int i = 0; i < Decision.NUM_FACTORS; ++i) {
						System.out.format(Decision.FACTOR_NAMES[i] + ": %.2f, ", Decision.BLOODLING_SUM[i]/Decision.BLOODLING_TOTAL);
					}
					System.out.println("");
					System.out.print("GRASSLER ("+Decision.GRASSLER_TOTAL+"): ");
					for (int i = 0; i < Decision.NUM_FACTORS; ++i) {
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