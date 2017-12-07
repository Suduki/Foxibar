package messages;

import java.io.IOException;
import java.io.PrintWriter;

import agents.AgentHandler;
import agents.Animal;
import agents.NeuralNetwork;
import constants.Constants;
import display.RenderState;

public class SaveBrains extends Message {

	@Override
	public String messageName() {
		return "KillAllAnimals";
	}

	@Override
	public void evaluate(simulation.Simulation pSimulation)
	{
		AgentHandler.saveBrains = true;
	}
	
	public static void saveBrains() {
		int speciesId;
		if (RenderState.FOLLOW_BLOODLING) {
			speciesId = Constants.SpeciesId.BLOODLING;
		}
		else if (RenderState.FOLLOW_GRASSLER) {
			speciesId = Constants.SpeciesId.GRASSLER;
		}
		else {
			System.out.println("Follow the type of animal you want to save");
			return;
		}
		saveBrains(speciesId);
	}
	public static void saveBrains(int speciesId) {
		System.out.println("save brains");
		NeuralNetwork best;
		
		switch (speciesId) {
		case Constants.SpeciesId.BLOODLING:
			best = Constants.SpeciesId.BEST_BLOODLING.neuralNetwork;
			break;
		case Constants.SpeciesId.GRASSLER:
			best = Constants.SpeciesId.BEST_GRASSLER.neuralNetwork;
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
		Animal bestId;
		switch (speciesId) {
		case Constants.SpeciesId.BLOODLING:
			bestId = Constants.SpeciesId.BEST_BLOODLING;
			break;
		case Constants.SpeciesId.GRASSLER:
			bestId = Constants.SpeciesId.BEST_GRASSLER;
			break;
		default:
			System.err.println("aa what is this3?");
			return false;
		}
		
		if (bestId == null || bestId.score < Animal.AGE_DEATH) {
			return false;
		}
		return true;

	}
}
