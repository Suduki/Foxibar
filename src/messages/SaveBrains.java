package messages;

import java.io.IOException;
import java.io.PrintWriter;

import agents.Animal;
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
		    PrintWriter writer = new PrintWriter("the-file-name" + speciesId + ".txt", "UTF-8");
		    writer.println(NeuralNetwork.NUM_LAYERS);
		    for (int i = 0; i < NeuralNetwork.LAYER_SIZES.length; ++i) {
		    	writer.print(NeuralNetwork.LAYER_SIZES[i] + " ");
		    }
		    writer.println();
		    
			for (int weight = 0; weight < NeuralNetwork.NUM_WEIGHTS; ++weight) {
				for (int i = 0; i < best.weights[weight].length; ++i) {
					for (int j = 0; j < best.weights[weight][i].length; ++j) {
						writer.print(best.weights[weight][i][j] + " ");
					}
					writer.println();					
				}
			}
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
