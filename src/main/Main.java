package main;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import agents.Animal;
import agents.NeuralNetwork;
import agents.NeuralFactors;
import constants.Constants;
import constants.RenderState;
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
		RenderState.activateState(RenderState.RENDER_WORLD_STILL);

		try
		{
			int timeStep = 0;
			while (simulation.handleMessages() && displayHandler.renderThreadThread.isAlive())
			{
				timeStep++;
				simulation.step();
				fpsLimiter.waitForNextFrame();
				
				if (Animal.numAnimals > Constants.WORLD_SIZE/Constants.TILES_PER_ANIMAL/2) {
					while (Animal.numBloodlings < 8) {
						spawnPseudoRandomAnimal(Constants.SpeciesId.BLOODLING);
					}
				}
				while (Animal.numAnimals < 15) {
					spawnRandomAnimal(Constants.SpeciesId.GRASSLER);
				}
				
				if (timeStep % 100 == 0) {
					System.out.format("grasslers = %d, bloodlings = %d\n", Animal.numGrasslers, Animal.numBloodlings);
					System.out.flush();
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

	private static void spawnPseudoRandomAnimal(int speciesId) {
		int pos = Constants.RANDOM.nextInt(Constants.WORLD_SIZE);
		int posX = pos / Constants.WORLD_SIZE_X;
		int posY = pos % Constants.WORLD_SIZE_X;
		
		posX /= Constants.WORLD_MULTIPLIER;
		posY /= Constants.WORLD_MULTIPLIER;
		
		pos = (posX+Constants.WORLD_SIZE_X/2) + Constants.WORLD_SIZE_X * (posY+Constants.WORLD_SIZE_X/2);
		
		switch (speciesId) {
		case Constants.SpeciesId.BLOODLING:
			Animal.resurrectAnimal(pos, 
					Animal.BIRTH_HUNGER, Constants.Species.BLOODLING,  
					null, 
					Constants.Species.BLOODLING, null);
			break;
		case Constants.SpeciesId.GRASSLER:
			Animal.resurrectAnimal(Constants.RANDOM.nextInt(Constants.WORLD_SIZE), 
					Animal.BIRTH_HUNGER, Constants.Species.GRASSLER,  
					null, Constants.Species.GRASSLER, null);
			break;
		}		
	}

	private static void spawnRandomAnimal(int speciesId) {
		switch (speciesId) {
		case Constants.SpeciesId.BLOODLING:
			Animal.resurrectAnimal(Constants.RANDOM.nextInt(Constants.WORLD_SIZE), 
					Animal.BIRTH_HUNGER, Constants.Species.BLOODLING,  
					null, 
					Constants.Species.BLOODLING, null);
			break;
		case Constants.SpeciesId.GRASSLER:
			Animal.resurrectAnimal(Constants.RANDOM.nextInt(Constants.WORLD_SIZE), 
					Animal.BIRTH_HUNGER, Constants.Species.GRASSLER,  
					null, Constants.Species.GRASSLER, null);
			break;
		}
	}
}