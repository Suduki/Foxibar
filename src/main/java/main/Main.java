package main;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import agents.Animal;
import agents.NeuralNetwork;
import agents.NeuralFactors;
import agents.Species;
import constants.Constants;
import display.DisplayHandler;
import display.RenderState;
import messages.LoadBrains;
import messages.SaveBrains;
import simulation.Simulation;
import utils.FPSLimiter;
import world.World;

public class Main
{
	public final static int plottingNumber = 50;
	public static void main(String[] args)
	{
		
		Simulation     simulation     = new Simulation();
		DisplayHandler displayHandler = new DisplayHandler(simulation);
		FPSLimiter     fpsLimiter     = new FPSLimiter(Constants.WANTED_FPS);
		RenderState.activateState(RenderState.RENDER_WORLD_STILL);
		
		spawnRandomAnimal(Constants.Species.BLOODLING, 100);
		
		try {
//			LoadBrains.loadBrains(Constants.Species.BLOODLING); //TODO: Make Animal Serializable
//			LoadBrains.loadBrains(Constants.Species.GRASSLER);
		}
		catch (Exception e ){
			System.err.println("Somethnig wrong with loading files.");
		}

		try
		{
			int timeStep = 0;
			while (simulation.handleMessages() && displayHandler.renderThreadThread.isAlive())
			{
				timeStep++;
				simulation.step(timeStep);
				fpsLimiter.waitForNextFrame();

				if (timeStep % plottingNumber == 0) {
					
					try {
						if (SaveBrains.goodTimeToSave(Constants.Species.BLOODLING)) {
							SaveBrains.saveBrains(Constants.Species.BLOODLING);
							LoadBrains.loadBrains(Constants.Species.BLOODLING);
						}
						if (SaveBrains.goodTimeToSave(Constants.Species.GRASSLER)) {
							SaveBrains.saveBrains(Constants.Species.GRASSLER);
							LoadBrains.loadBrains(Constants.Species.GRASSLER);
						}
					}
					catch (Exception e ){
						System.err.println("Somethnig wrong with loading/saving brain during runtime.");
						e.printStackTrace();
					}			
				}
			}
		}
		catch ( IllegalStateException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Simulation (main) thread finished.");
	}

	
	private void spawnPseudoRandomAnimal(Species species) {
		int pos = Constants.RANDOM.nextInt(Constants.WORLD_SIZE);
		int posX = pos / Constants.WORLD_SIZE_X;
		int posY = pos % Constants.WORLD_SIZE_X;
		
		posX /= Constants.WORLD_MULTIPLIER;
		posY /= Constants.WORLD_MULTIPLIER;
		
		pos = (posX+Constants.WORLD_SIZE_X/2) + Constants.WORLD_SIZE_X * (posY+Constants.WORLD_SIZE_X/2);
		World.animalManager.spawn(pos, species);
	}

	public static void spawnRandomAnimal(Species species, int num) {
		for (int i = 0; i < num; ++i) {
			int pos = Constants.RANDOM.nextInt(Constants.WORLD_SIZE);
			World.animalManager.spawn(pos, species);
		}
	}
}