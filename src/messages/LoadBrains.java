package messages;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import agents.Animal;
import agents.NeuralNetwork;
import constants.Constants;
import world.World;

public class LoadBrains extends Message {

	@Override
	public String messageName() {
		return "KillAllAnimals";
	}

	@Override
	public void evaluate(simulation.Simulation pSimulation)
	{
		Animal.loadBrains = true;
	}
	
	public static NeuralNetwork bestBloodling;
	public static NeuralNetwork bestGrassler;
	
	public static void loadBrains(final int speciesId) {
		
		NeuralNetwork best = new NeuralNetwork(true);
		BufferedReader br = null;
		try {
			String filename = "brain" + speciesId;
			for (int i : NeuralNetwork.LAYER_SIZES) {
				filename = filename + "_" + i;
			}
			filename = filename + ".txt";
			br = new BufferedReader(new FileReader(filename));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();
		    String[] a = everything.split("\n");
		    for (int i = 0; i < a.length; ++i) {
		    	a[i] = a[i].substring(0, a[i].length() - 1);
		    }
		    int numLayers = Integer.valueOf(a[0]);
		    String[] a1 = a[1].split(" ");
		    
		    if (a1.length != numLayers || numLayers != NeuralNetwork.NUM_LAYERS) {
		    	System.err.println("INVALID SHIT1!");
		    }
		    
		    int[] a1Int = new int[a1.length];
		    
		    for (int i = 0; i <0 ; ++i) {
		    	a1Int[i] = Integer.valueOf(a1[0]);
		    }
		    
		    int fileRow = 2;
		    
			for (int weight = 0; weight < NeuralNetwork.NUM_WEIGHTS; ++weight) {
				for (int i = 0; i < best.weights[weight].length; ++i) {
					String[] tmpDataFromFile = a[fileRow++].split(" ");
					for (int j = 0; j < best.weights[weight][i].length; ++j) {
//						System.out.print(tmpDataFromFile[j] + " ");
						best.weights[weight][i][j] = Float.valueOf(tmpDataFromFile[j]); 
					}
//					System.out.println();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		switch (speciesId) {
		case Constants.SpeciesId.BLOODLING:
			System.out.println("Loading bloodling brain");
			bestBloodling = best;
			break;
		case Constants.SpeciesId.GRASSLER:
			System.out.println("Loading grassler brain");
			bestGrassler = best;
			break;
		default:
			System.err.println("aa what is this2?");
		}
	}
}
