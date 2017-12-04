package messages;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import agents.Animal2;
import agents.NeuralNetwork;
import constants.Constants;
import world.World;

public class SaveBrains extends Message {

	@Override
	public String messageName() {
		return "KillAllAnimals";
	}

	@Override
	public void evaluate(simulation.Simulation pSimulation)
	{
		Animal2.saveBrains = true;
	}
	
	
	public static void saveBrains(final int speciesId) {
		NeuralNetwork best;
		switch (speciesId) {
		case Constants.SpeciesId.BLOODLING:
			best = Constants.SpeciesId.BEST_BLOODLING.neuralNetwork;
			break;
		case Constants.SpeciesId.GRASSLER:
			best = Animal2.pool[Constants.SpeciesId.BEST_GRASSLER_ID].neuralNetwork;
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
		
		if (bestId == -1 || Animal2.pool[bestId].score < Animal2.AGE_DEATH) {
			return false;
		}
		return true;

	}
}
