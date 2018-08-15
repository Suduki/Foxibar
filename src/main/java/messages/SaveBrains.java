package messages;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import agents.Animal;
import agents.NeuralNetwork;
import agents.Species;
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
//		Animal.saveBrains = true;
	}
	
	
	public static void saveBrains(final Species species) {
		NeuralNetwork best = species.bestBrain.neural;
		try{
			String filename = "brain" + species;
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
