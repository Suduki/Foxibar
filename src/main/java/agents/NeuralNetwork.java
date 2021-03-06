package agents;

import java.io.Serializable;
import java.util.ArrayList;

import actions.Action;
import constants.Constants;

public class NeuralNetwork implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int[] LAYER_SIZES = {NeuralFactors.in.NUM_FACTORS, 4, Action.numActions + 1 /* speed */};
	public static final int NUM_LAYERS = LAYER_SIZES.length;
	public static final int NUM_WEIGHTS = NUM_LAYERS - 1;

	public float[][][] weights;
	public float[][] z;
	public int bestDirection;
	public float[][] bias;

	public NeuralNetwork(boolean initZero) {
		weights = new float[NUM_WEIGHTS][][];
		z = new float[NUM_LAYERS][];
		bias = new float[NUM_LAYERS-2][]; // Skip input layer and output layer. TODO: WHY?!
		bestDirection = 0;

		for (int weight = 0 ; weight < NUM_WEIGHTS; ++weight) {
			weights[weight] = new float[LAYER_SIZES[weight]][LAYER_SIZES[weight+1]];
		}
		for (int layer = 0 ; layer < NUM_LAYERS; ++layer) {
			z[layer] = new float[LAYER_SIZES[layer]];
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
		for (int weightLayer = 0; weightLayer < NUM_WEIGHTS; ++weightLayer) {
			for (int nodeNextLayer = 0; nodeNextLayer < LAYER_SIZES[weightLayer+1]; ++nodeNextLayer) {

				// Reset
				z[weightLayer+1][nodeNextLayer] = 0;

				// Append from previous node layer
				for (int nodeCurrentLayer = 0; nodeCurrentLayer < LAYER_SIZES[weightLayer]; ++nodeCurrentLayer) {
					z[weightLayer+1][nodeNextLayer] += 
							(z[weightLayer][nodeCurrentLayer] * 
									weights[weightLayer][nodeCurrentLayer][nodeNextLayer]);
				}

				// Append from bias
				if (weightLayer < bias.length) {
					z[weightLayer+1][nodeNextLayer] += bias[weightLayer][nodeNextLayer]; 
				}

				// Apply sigmoid
				z[weightLayer+1][nodeNextLayer] = sigmoid(z[weightLayer+1][nodeNextLayer]);
			}
		}
	}

	public int neuralMagic(final ArrayList<Action> actions) {
		evaluateNeuralNetwork();
		float bestVal = Float.NEGATIVE_INFINITY;
		int selection = -1;
		for (int i = 0; i < Action.numActions; ++i) {
			if (actions.get(i).isPossible && z[LAYER_SIZES.length-1][i] > bestVal) {
				
				bestVal = z[LAYER_SIZES.length-1][i];
				selection = i;
			}
		}
		return selection;
	}


	private float sigmoid(float f) {
		return (float) (1d/(1d+Math.exp(-f)));
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
		bestDirection = 0;
		float evolution = 0.01f;
		copy(neuralMom, evolution);
	}

	public float getOutput(int neuralFactorId) {
		return z[LAYER_SIZES.length-1][neuralFactorId];
	}
	public float getScaledOutput(int neuralFactorId) {
		return sigmoid(z[LAYER_SIZES.length-1][neuralFactorId]);
	}

	public float getSpeed() {
		// A bit ugly, but what to do...
		return z[LAYER_SIZES.length-1][Action.numActions];
	}
}
