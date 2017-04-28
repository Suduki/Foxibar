package agents;

import constants.Constants;

public class NeuralNetwork {
	
	private static final int[] LAYERS = {NeuralFactors.NUM_DESICION_FACTORS, 7, 5, 2};
	private static final int NUM_LAYERS = LAYERS.length;
	private static final int NUM_WEIGHTS = NUM_LAYERS - 1;

	private float[][][] weights;
	double[][] z;
	
	public NeuralNetwork() {
		weights = new float[NUM_WEIGHTS][][];
		z = new double[NUM_LAYERS][];
		for (int weight = 0 ; weight < NUM_WEIGHTS; ++weight) {
			weights[weight] = new float[LAYERS[weight]][LAYERS[weight+1]];
		}
		for (int layer = 0 ; layer < NUM_LAYERS; ++layer) {
			z[layer] = new double[LAYERS[layer]];
		}
		
		initWeightsRandom();
	}
	
	public void initWeightsRandom() {
		for (int weight = 0; weight < weights.length ; ++weight) {
			for (int i = 0; i < weights[weight].length; ++i) {
				for (int j = 0; j < weights[weight][i].length; ++j) {
					weights[weight][i][j] = 1 - 2*Constants.RANDOM.nextFloat();
				}
			}
		}
	}

	public void reset() {
		for (int layer = 0; layer < z.length ; ++layer) {
			for (int i = 0; i < z[layer].length; ++i) {
				z[layer][i] = 0;
			}
		}
	}
	
	public double neuralMagic() {
		for (int weight = 0; weight < NUM_WEIGHTS; ++weight) {
			for (int i = 0; i < LAYERS[weight+1]; ++i) {
				for (int j = 0; j < LAYERS[weight]; ++j) {
					z[weight+1][i] += z[weight][j]*weights[weight][j][i];
				}
				z[weight+1][i] = sigmoid(z[weight+1][i]); 
			}
		}
		return z[z.length-1][z[z.length-1].length-1];
	}
	
	private double sigmoid(double f) {
		return 1d/(1d+Math.exp(-f));
	}
	
	public void copy(NeuralNetwork d) {
		for (int weight = 0; weight < NUM_WEIGHTS; ++weight) {
			for (int i = 0; i < weights[weight].length; ++i) {
				for (int j = 0; j < weights[weight][i].length; ++j) {
					weights[weight][i][j] = d.weights[weight][i][j];
				}
			}
		}
	}
	
	public void inherit(NeuralNetwork neuralMom, NeuralNetwork neuralDad) {
		double diff = 0.5;
		for (int weight = 0; weight < NUM_WEIGHTS; ++weight) {
			for (int i = 0; i < weights[weight].length; ++i) {
				for (int j = 0; j < weights[weight][i].length; ++j) {
					if (Constants.RANDOM.nextBoolean()) {
						weights[weight][i][j] = neuralDad.weights[weight][i][j];
					}
					else {
						weights[weight][i][j] = neuralMom.weights[weight][i][j];
					}
					weights[weight][i][j] += diff * (1f - 2*Constants.RANDOM.nextFloat());
				}
			}
		}
	}
}
