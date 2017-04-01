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
			while (simulation.handleMessages() && displayHandler.renderThreadThread.isAlive())
			{
				simulation.step();
				fpsLimiter.waitForNextFrame();
				
				while (Animal.numBloodlings < 10) {
					spawnRandomAnimal(Constants.SpeciesId.BLOODLING);
				}
				while (Animal.numGrasslers < 50) {
					spawnRandomAnimal(Constants.SpeciesId.GRASSLER);
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

	private static void spawnRandomAnimal(int speciesId) {
		switch (speciesId) {
		case Constants.SpeciesId.BLOODLING:
			boolean useBestAnimal = true;
			if (useBestAnimal) {
				Animal.resurrectAnimal(Constants.RANDOM.nextInt(Constants.WORLD_SIZE), 
						Animal.BIRTH_HUNGER, Constants.Species.BLOODLING,  
						Constants.SpeciesId.bestBloodlingDecision, 
						Constants.Species.BLOODLING, Constants.SpeciesId.secondBloodlingDecision);
			}
			else {
				int i =	Animal.resurrectAnimal(Constants.RANDOM.nextInt(Constants.WORLD_SIZE), 
						Animal.BIRTH_HUNGER, Constants.Species.BLOODLING,  
						Constants.SpeciesId.bestBloodlingDecision, 
						Constants.Species.BLOODLING, Constants.SpeciesId.secondBloodlingDecision);
				Animal.pool[i].decision.initWeightsRandom();
			}
			break;
		case Constants.SpeciesId.GRASSLER:
			Animal.resurrectAnimal(Constants.RANDOM.nextInt(Constants.WORLD_SIZE), 
					Animal.BIRTH_HUNGER, Constants.Species.GRASSLER,  
					null, Constants.Species.GRASSLER, null);
			break;
		}
	}
}