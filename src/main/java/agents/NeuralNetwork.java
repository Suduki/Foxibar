package agents;

import constants.Constants;

public class NeuralNetwork {
	public static final int[] LAYER_SIZES = {NeuralFactors.NUM_INPUT_FACTORS, 8, 4, NeuralFactors.NUM_OUTPUT_FACTORS};
	public static final int NUM_LAYERS = LAYER_SIZES.length;
	public static final int NUM_WEIGHTS = NUM_LAYERS - 1;

	public static final int NUM_OPTIONS = 4;
	
	public float[][][] weights;
	public float[][][] z;
	public int bestDirection;
	public float[][] bias;

	public NeuralNetwork(boolean initZero) {
		weights = new float[NUM_WEIGHTS][][];
		z = new float[NUM_OPTIONS][NUM_LAYERS][];
		bias = new float[NUM_LAYERS-2][]; // Skip input layer and output layer.
		bestDirection = -1;
		
		for (int weight = 0 ; weight < NUM_WEIGHTS; ++weight) {
			weights[weight] = new float[LAYER_SIZES[weight]][LAYER_SIZES[weight+1]];
		}
		for (int direction = 0; direction < NUM_OPTIONS; ++direction) {
			for (int layer = 0 ; layer < NUM_LAYERS; ++layer) {
				z[direction][layer] = new float[LAYER_SIZES[layer]];
			}
		}
		
		if (initZero) {
			initWeightsZero();
		}
		else {
			initWeightsRandom();
		}
		initBias();
		
	}
	
	private void initBias() {
		for (int layer = 0 ; layer < bias.length; ++layer) {
			bias[layer] = new float[LAYER_SIZES[layer]];
			for (int i = 0; i < bias[layer].length; ++i) {
				bias[layer][i] = getRandom();
			}
		}
	}
	
	private float getRandom() {
		return Constants.RANDOM.nextFloat()*2 - 1;
	}

	private void initWeightsZero() {
		for (int weight = 0; weight < weights.length ; ++weight) {
			for (int i = 0; i < weights[weight].length; ++i) {
				for (int j = 0; j < weights[weight][i].length; ++j) {
					weights[weight][i][j] = 0;
				}
			}
		}
	}
	void initWeightsRandom() {
		for (int weight = 0; weight < weights.length ; ++weight) {
			for (int i = 0; i < weights[weight].length; ++i) {
				for (int j = 0; j < weights[weight][i].length; ++j) {
					weights[weight][i][j] = getRandom();
				}
			}
		}
	}

	private void evaluateNeuralNetwork() {
		for (int direction = 0; direction < NUM_OPTIONS; direction++) {
			for (int weightLayer = 0; weightLayer < NUM_WEIGHTS; ++weightLayer) {
				for (int nodeNextLayer = 0; nodeNextLayer < LAYER_SIZES[weightLayer+1]; ++nodeNextLayer) {
					
					// Reset
					z[direction][weightLayer+1][nodeNextLayer] = 0;
					
					// Append from previous node layer
					for (int nodeCurrentLayer = 0; nodeCurrentLayer < LAYER_SIZES[weightLayer]; ++nodeCurrentLayer) {
						z[direction][weightLayer+1][nodeNextLayer] += 
								(z[direction][weightLayer][nodeCurrentLayer] * 
										weights[weightLayer][nodeCurrentLayer][nodeNextLayer]);
					}
					
					// Append from bias
					if (weightLayer < bias.length) {
						z[direction][weightLayer+1][nodeNextLayer] += bias[weightLayer][nodeNextLayer]; 
					}
					
					// Apply sigmoid
					z[direction][weightLayer+1][nodeNextLayer] = sigmoid(z[direction][weightLayer+1][nodeNextLayer]);
				}
			}
		}
	}
	
	public int neuralMagic() {
		evaluateNeuralNetwork();
		double bestVal = Double.NEGATIVE_INFINITY;
		bestDirection = -1;
		for(int direction = 0; direction < NUM_OPTIONS; ++direction) {
			if (z[direction][LAYER_SIZES.length-1][NeuralFactors.OUT_NODE_GOODNESS] > bestVal) {
				bestVal = z[direction][LAYER_SIZES.length-1][NeuralFactors.OUT_NODE_GOODNESS];
				bestDirection = direction;
			}
		}
		if (bestDirection == -1) {
			System.err.println("Found no best direction. Is map crowded?");
			bestDirection = 0;
		}
		return bestDirection;
	}
	
	public float getOutput(int neuralFactorId) {
		return z[bestDirection][LAYER_SIZES.length-1][neuralFactorId];
	}
	
	private float sigmoid(float f) {
		float result = (float) (1d/(1d+Math.exp(-f)));
		return result*2-0.5f;
	}
	
	public void copy(NeuralNetwork d, float mutation) {
		for (int weight = 0; weight < NUM_WEIGHTS; ++weight) {
			for (int i = 0; i < weights[weight].length; ++i) {
				for (int j = 0; j < weights[weight][i].length; ++j) {
					weights[weight][i][j] = d.weights[weight][i][j] + mutation * getRandom();
				}
			}
		}
		for (int layer = 0; layer < bias.length; ++layer) {
			for (int i = 0; i < bias[layer].length; ++i) {
				bias[layer][i] = d.bias[layer][i] + mutation * getRandom();
			}
		}
	}
	
	public void inherit(NeuralNetwork neuralMom) {
		bestDirection = -1;
		float evolution = 0.1f;
		copy(neuralMom, evolution);
	}
}
