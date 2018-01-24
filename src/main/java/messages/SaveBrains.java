package main.java.messages;

import java.io.IOException;
import java.io.PrintWriter;

import main.java.agents.Animal;
import main.java.agents.NeuralNetwork;
import main.java.constants.Constants;
import main.java.simulation.Simulation;

public class SaveBrains extends Message {

	@Override
	public String messageName() {
		return "KillAllAnimals";
	}

	@Override
	public void evaluate(Simulation pSimulation)
	{
		Animal.saveBrains = true;
	}
	
	
	public static void saveBrains(final int speciesId) {
		NeuralNetwork best;
		switch (speciesId) {
		case Constants.SpeciesId.BLOODLING:
			best = Animal.pool[Constants.SpeciesId.BEST_BLOODLING_ID].neuralNetwork;
			break;
		case Constants.SpeciesId.GRASSLER:
			best = Animal.pool[Constants.SpeciesId.BEST_GRASSLER_ID].neuralNetwork;
			break;
		default:
			System.err.println("aa what is this2?");
			return;
		}
		try{
			String filename = "brain" + speciesId;
			for (int i : NeuralNetwork.LAYER_SIZES) {
				filename = filename + "_" + i;
			}
			filename = filename + ".txt";
		    PrintWriter writer = new PrintWriter(filename, "UTF-8");
		    writer.println(NeuralNetwork.NUM_LAYERS);
		    for (int i = 0; i < NeuralNetwork.LAYER_SIZES.length; ++i) {
		    	writer.print(NeuralNetwork.LAYER_SIZES[i] + " ");
		    }
		    writer.println();
		    
		    // Write all weights used based on current time step
			for (int weight = 0; weight < NeuralNetwork.NUM_WEIGHTS; ++weight) {
				for (int i = 0; i < best.weights[weight].length; ++i) {
					for (int j = 0; j < best.weights[weight][i].length; ++j) {
						writer.print(best.weights[weight][i][j] + " ");
					}
					writer.println();					
				}
			}
			
			// Write all weights used based on previous time steps
			for (int weight = 1; weight < NeuralNetwork.NUM_WEIGHTS; ++weight) {
				for (int i = 0; i < best.weightsOld[weight].length; ++i) {
					for (int j = 0; j < best.weightsOld[weight][i].length; ++j) {
						writer.print(best.weightsOld[weight][i][j] + " ");
					}
					writer.println();					
				}
			}
		    writer.close();
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean goodTimeToSave(final int speciesId) {
		int bestId;
		switch (speciesId) {
		case Constants.SpeciesId.BLOODLING:
			bestId = Constants.SpeciesId.BEST_BLOODLING_ID;
			break;
		case Constants.SpeciesId.GRASSLER:
			bestId = Constants.SpeciesId.BEST_GRASSLER_ID;
			break;
		default:
			System.err.println("aa what is this3?");
			return false;
		}
		
		if (bestId == -1 || Animal.pool[bestId].score < Animal.AGE_DEATH) {
			return false;
		}
		return true;

	}
}
