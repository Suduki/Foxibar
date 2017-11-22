package agents;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.sun.javafx.geom.Vec2f;

import constants.Constants;

public class NeuralNetwork {
	public static final int[] LAYER_SIZES = {NeuralFactors.NUM_DESICION_FACTORS, 8, 4, 2};
	public static final int NUM_LAYERS = LAYER_SIZES.length;
	public static final int NUM_WEIGHTS = NUM_LAYERS - 1;

	public static final int NUM_OPTIONS = 5;
	
	public float[][][] weights;
	public float[][][] weightsOld;
	public double[][][] z;
	public double[][][] zOld;
	public int bestDirection;
	public double[][] bias;

	public NeuralNetwork(boolean initZero) {
		weights = new float[NUM_WEIGHTS][][];
		weightsOld = new float[NUM_WEIGHTS][][];
		z = new double[NUM_OPTIONS][NUM_LAYERS][];
		zOld = new double[NUM_OPTIONS][NUM_LAYERS][];
		bias = new double[NUM_LAYERS-2][]; // Skip input layer and output layer.
		bestDirection = -1;
		
		for (int weight = 0 ; weight < NUM_WEIGHTS; ++weight) {
			weights[weight] = new float[LAYER_SIZES[weight]][LAYER_SIZES[weight+1]];
			if (weight > 0) {
				weightsOld[weight] = new float[LAYER_SIZES[weight+1]][LAYER_SIZES[weight+1]];
			}
		}
		for (int direction = 0; direction < NUM_OPTIONS; ++direction) {
			for (int layer = 0 ; layer < NUM_LAYERS; ++layer) {
				z[direction][layer] = new double[LAYER_SIZES[layer]];

				if (layer != 0) { // We do not use these at the first layer.	
					zOld[direction][layer] = new double[LAYER_SIZES[layer]];
				}
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
			bias[layer] = new double[LAYER_SIZES[layer]];
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
		for (int weight = 1; weight < weightsOld.length ; ++weight) {
			for (int i = 0; i < weightsOld[weight].length; ++i) {
				for (int j = 0; j < weightsOld[weight][i].length; ++j) {
					weightsOld[weight][i][j] = 0;
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
		for (int weight = 1; weight < weightsOld.length ; ++weight) {
			for (int i = 0; i < weightsOld[weight].length; ++i) {
				for (int j = 0; j < weightsOld[weight][i].length; ++j) {
					weightsOld[weight][i][j] = getRandom();
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
					
					// Append from previous time step
					if (weightLayer != 0 && bestDirection != -1) {
						for (int oldNodeLayer = 0; oldNodeLayer < LAYER_SIZES[weightLayer+1]; oldNodeLayer++) {
							z[direction][weightLayer+1][nodeNextLayer] += 
									(zOld[direction][weightLayer+1][oldNodeLayer] * 
											weightsOld[weightLayer][oldNodeLayer][nodeNextLayer]);
						}
					}
					
					// Append from bias
					if (weightLayer < bias.length) {
						z[direction][weightLayer+1][nodeNextLayer] += bias[weightLayer][nodeNextLayer]; 
					}
					
					// Apply sigmoid
					z[direction][weightLayer+1][nodeNextLayer] = sigmoid(z[direction][weightLayer+1][nodeNextLayer]);
				}
			}
			for (int weightLayer = 0; weightLayer < NUM_WEIGHTS; ++weightLayer) {
				for (int nodeNextLayer = 0; nodeNextLayer < LAYER_SIZES[weightLayer+1]; ++nodeNextLayer) {
					zOld[direction][weightLayer+1][nodeNextLayer] = z[direction][weightLayer+1][nodeNextLayer];
				}
			}
		}
	}
	
	public int neuralMagic(boolean[] directionWalkable, float[] speed) {
		evaluateNeuralNetwork();
		double bestVal = Double.NEGATIVE_INFINITY;
		bestDirection = -1;
		for(int direction = 0; direction < NUM_OPTIONS; ++direction) {
			if (!directionWalkable[direction]) {
				continue;
			}
			if (z[direction][LAYER_SIZES.length-1][0] > bestVal) {
				bestVal = z[direction][LAYER_SIZES.length-1][0];
				bestDirection = direction;
			}
		}
		if (bestDirection == -1) {
			System.err.println("Found no best direction. Is map crowded?");
			bestDirection = 0;
		}
		speed[0] = (float) z[bestDirection][LAYER_SIZES.length-1][1];
		return bestDirection;
	}
	
	private double sigmoid(double f) {
		double result = 1d/(1d+Math.exp(-f));
		return result;
	}
	
	private double sigmoidPrime(double f) {
		double expo = Math.exp(-f);
		return expo/((1d+expo)*(1d+expo));
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
	
	public void inherit(NeuralNetwork neuralMom, NeuralNetwork neuralDad) {
		bestDirection = -1;
		float evolution = 0.1f;
		if (Constants.RANDOM.nextBoolean()) {
			copy(neuralMom, evolution);
		}
		else {
			copy(neuralDad, evolution);
		}
	}
}
