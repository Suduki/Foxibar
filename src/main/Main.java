package main;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import agents.Animal;
import agents.NeuralNetwork;
import agents.NeuralFactors;
import constants.Constants;
import dataPlotting.XYPlotThingVersusTime;
import dataPlotting.XYPlotThingVersusTime2;
import display.DisplayHandler;
import display.RenderState;
import messages.LoadBrains;
import messages.SaveBrains;
import simulation.Simulation;
import utils.FPSLimiter;

public class Main
{
	public final static int plottingNumber = 50;
	public static boolean plotStuff = true;
	public static boolean plotStuff2 = false;
	public static void main(String[] args)
	{
		
		if (plotStuff) {
//			XYPlotThingVersusTime.plotStuff();
		}
		if (plotStuff2) {
			//XYPlotThingVersusTime2.plotStuff();
		}
		
		Simulation     simulation     = new Simulation();
		DisplayHandler displayHandler = new DisplayHandler(simulation);
		FPSLimiter     fpsLimiter     = new FPSLimiter(Constants.WANTED_FPS);
		RenderState.activateState(RenderState.RENDER_WORLD_STILL);
		
		try {
			LoadBrains.loadBrains(Constants.SpeciesId.BLOODLING);
			LoadBrains.loadBrains(Constants.SpeciesId.GRASSLER);
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

				if (Animal.numAnimals > Constants.WORLD_SIZE/Constants.TILES_PER_ANIMAL/2) {
					while (Animal.numBloodlings < 15) {
						spawnPseudoRandomAnimal(Constants.SpeciesId.BLOODLING);
					}
				}
				while (Animal.numAnimals < 1) {
					spawnRandomAnimal(Constants.SpeciesId.GRASSLER);
				}
				
				if (plotStuff2) {
					XYPlotThingVersusTime2.myInstance.step();
				}
				if (timeStep % plottingNumber == 0) {
					if (plotStuff) {
						XYPlotThingVersusTime.myInstance.step();
					}
					
					try {
						if (SaveBrains.goodTimeToSave(Constants.SpeciesId.BLOODLING)) {
							SaveBrains.saveBrains(Constants.SpeciesId.BLOODLING);
							LoadBrains.loadBrains(Constants.SpeciesId.BLOODLING);
						}
						if (SaveBrains.goodTimeToSave(Constants.SpeciesId.GRASSLER)) {
							SaveBrains.saveBrains(Constants.SpeciesId.GRASSLER);
							LoadBrains.loadBrains(Constants.SpeciesId.GRASSLER);
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