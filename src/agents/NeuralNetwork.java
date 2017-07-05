package agents;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.sun.javafx.geom.Vec2f;

import constants.Constants;

public class NeuralNetwork {
	public static final int[] LAYER_SIZES = {NeuralFactors.NUM_DESICION_FACTORS + NeuralFactors.NUM_HORMONES, 8, 4, 1 + NeuralFactors.NUM_HORMONES};
	public static final int NUM_LAYERS = LAYER_SIZES.length;
	public static final int NUM_WEIGHTS = NUM_LAYERS - 1;

	
	public double[] hormones;
	public float[][][] weights;
	public double[][] z;

	public NeuralNetwork(boolean initZero) {
		hormones = new double[NeuralFactors.NUM_HORMONES];
		weights = new float[NUM_WEIGHTS][][];
		z = new double[NUM_LAYERS][];
		for (int weight = 0 ; weight < NUM_WEIGHTS; ++weight) {
			weights[weight] = new float[LAYER_SIZES[weight]][LAYER_SIZES[weight+1]];
		}
		for (int layer = 0 ; layer < NUM_LAYERS; ++layer) {
			z[layer] = new double[LAYER_SIZES[layer]];
		}
		
		if (initZero) {
			initWeightsZero();
		}
		else {
			initWeightsRandom();
		}
		
	}
	
	public void initWeightsZero() {
		for (int weight = 0; weight < weights.length ; ++weight) {
			for (int i = 0; i < weights[weight].length; ++i) {
				for (int j = 0; j < weights[weight][i].length; ++j) {
					weights[weight][i][j] = 0;
				}
			}
		}
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
		for (int layer = 1; layer < z.length ; ++layer) { // Not resetting first layer.
			for (int i = 0; i < z[layer].length; ++i) {
				z[layer][i] = 0;
			}
		}
	}
	
	private double evaluateNeuralNetwork(final double[][] z, final float[][][] weights) {
		reset();
		for (int weightLayer = 0; weightLayer < NUM_WEIGHTS; ++weightLayer) {
			for (int nodeNextLayer = 0; nodeNextLayer < LAYER_SIZES[weightLayer+1]; ++nodeNextLayer) {
				for (int nodeCurrentLayer = 0; nodeCurrentLayer < LAYER_SIZES[weightLayer]; ++nodeCurrentLayer) {
					z[weightLayer+1][nodeNextLayer] += 
							(z[weightLayer][nodeCurrentLayer] * weights[weightLayer][nodeCurrentLayer][nodeNextLayer]);
				}
				z[weightLayer+1][nodeNextLayer] = sigmoid(z[weightLayer+1][nodeNextLayer]);
			}
		}
		
		// Append hormones
		for (int horm = 0; horm < z[NUM_LAYERS-1].length-1; ++horm) {
			hormones[horm] += z[NUM_LAYERS-1][horm+1];
		}
		
		return z[NUM_LAYERS-1][0]; // Return nodegoodness
	}
	
	public void stepHormones() {
		double decay = 0.8;
		for (int horm = 0; horm < hormones.length; ++horm) {
			z[0][NeuralFactors.NUM_DESICION_FACTORS + horm] += hormones[horm];
			hormones[horm] = 0;
			z[0][NeuralFactors.NUM_DESICION_FACTORS + horm] = sigmoid(z[0][NeuralFactors.NUM_DESICION_FACTORS + horm]);
			z[0][NeuralFactors.NUM_DESICION_FACTORS + horm] *= decay;
//			if (Constants.RANDOM.nextDouble() < 0.001) {
//				System.out.println(z[0][NeuralFactors.NUM_DESICION_FACTORS + horm]);
//			}
		}
	}
	
	private void backPropagationLearning(double prediction, double actual) {
		double l2Error = actual - prediction;
		
		double l2Delta = l2Error * sigmoidPrime(prediction);
		
		int learningRate = 10; // Should be coupled to age differential or something similar.
//		for (int weight = NUM_WEIGHTS - 1; weight >= 0; --weight) {
		int  weight = NUM_WEIGHTS - 1;
		double[] l1Error = new double[LAYER_SIZES[weight]];
		for (int i = 0; i < LAYER_SIZES[weight]; ++i) {
			// l1Error = l2Delta *dot* w1
			l1Error[i] = l2Delta*weights[weight][i][0];

			weights[weight][i][0] += learningRate*z[weight][i]*l2Delta;
		}
		
//		weight = NUM_WEIGHTS - 2;
//		for (int i = 0; i < LAYER_SIZES[weight]; ++i) {
//			for (int j = 0; j < LAYER_SIZES[weight + 1]; ++j) {
//				l1Error[i] = l2Delta*weights[weight][i][j];
//			}
//		}
		
//		}
	}
	
	public static double dot(double[] a, double[] b) {
		if (a.length != b.length) System.err.println("Bad sizes; a = " + a + ", b = " + b);
		double val = 0;
		for (int i = 0; i < a.length; ++i) {
			val += a[i]*b[i];
		}
		return val;
	}
	

	public double neuralMagic(int toLearnFrom) {
		double myGoodness = evaluateNeuralNetwork(this.z, this.weights);
		if (Constants.LEARN_FROM_ELDERS && toLearnFrom != -1) {
			double roleModelGoodness = evaluateNeuralNetwork(this.z, Animal.pool[toLearnFrom].neuralNetwork.weights);
			backPropagationLearning(myGoodness, roleModelGoodness);
			myGoodness = evaluateNeuralNetwork(this.z, this.weights);
			return myGoodness;
		}
		
		return myGoodness;
	}
	
	private double sigmoid(double f) {
		double result = 1d/(1d+Math.exp(-f));
		return result;
	}
	
	private double sigmoidPrime(double f) {
		double expo = Math.exp(-f);
		return expo/((1d+expo)*(1d+expo));
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
		double evolution = 0.1;
		for (int weight = 0; weight < NUM_WEIGHTS; ++weight) {
			for (int i = 0; i < weights[weight].length; ++i) {
				for (int j = 0; j < weights[weight][i].length; ++j) {
//					if (Constants.RANDOM.nextBoolean()) {
//						weights[weight][i][j] = neuralDad.weights[weight][i][j];
//					}
//					else {
						weights[weight][i][j] = neuralMom.weights[weight][i][j];
//					}
					weights[weight][i][j] += evolution * (1f - 2*Constants.RANDOM.nextFloat());
				}
			}
		}
	}
}
