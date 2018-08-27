package main;

import agents.Bloodling;
import agents.Randomling;
import agents.Species;
import constants.Constants;
import display.DisplayHandler;
import display.RenderState;
import simulation.Simulation;
import utils.FPSLimiter;

public class Main
{
	public final static int plottingNumber = 5000;
	public static Simulation simulation;
	public static void main(String[] args)
	{
		
		simulation     = new Simulation(new Class[] {Randomling.class, Bloodling.class});
		DisplayHandler displayHandler = new DisplayHandler(simulation);
		FPSLimiter     fpsLimiter     = new FPSLimiter(Constants.WANTED_FPS);
		RenderState.activateState(RenderState.RENDER_WORLD_STILL);
		
//		spawnRandomAnimal(Constants.Species.BLOODLING, 100);
		
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

//				if (timeStep % plottingNumber == 0) {
//					
//					try {
//						if (SaveBrains.goodTimeToSave(Constants.Species.BLOODLING)) {
//							SaveBrains.saveBrains(Constants.Species.BLOODLING);
//							LoadBrains.loadBrains(Constants.Species.BLOODLING);
//						}
//						if (SaveBrains.goodTimeToSave(Constants.Species.GRASSLER)) {
//							SaveBrains.saveBrains(Constants.Species.GRASSLER);
//							LoadBrains.loadBrains(Constants.Species.GRASSLER);
//						}
//					}
//					catch (Exception e ){
//						System.err.println("Somethnig wrong with loading/saving brain during runtime.");
//						e.printStackTrace();
//					}			
//				}
			}
		}
		catch ( IllegalStateException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Simulation (main) thread finished.");
	}

	
	
}