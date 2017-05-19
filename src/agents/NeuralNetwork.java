package agents;

import constants.Constants;

public class NeuralNetwork {
	
	private static final int[] LAYER_SIZES = {NeuralFactors.NUM_DESICION_FACTORS, 5, 3, 1};
	private static final int NUM_LAYERS = LAYER_SIZES.length;
	private static final int NUM_WEIGHTS = NUM_LAYERS - 1;

	private float[][][] weights;
	double[][] z;
	
	public NeuralNetwork() {
		weights = new float[NUM_WEIGHTS][][];
		z = new double[NUM_LAYERS][];
		for (int weight = 0 ; weight < NUM_WEIGHTS; ++weight) {
			weights[weight] = new float[LAYER_SIZES[weight]][LAYER_SIZES[weight+1]];
		}
		for (int layer = 0 ; layer < NUM_LAYERS; ++layer) {
			z[layer] = new double[LAYER_SIZES[layer]];
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
		for (int layer = 1; layer < z.length ; ++layer) {
			for (int i = 0; i < z[layer].length; ++i) {
				z[layer][i] = 0;
			}
		}
	}
	
	private double evaluateNeuralNetwork(final double[][] z, final float[][][] weights) {
		reset();
		for (int weight = 0; weight < NUM_WEIGHTS; ++weight) {
			for (int i = 0; i < LAYER_SIZES[weight+1]; ++i) {
				for (int j = 0; j < LAYER_SIZES[weight]; ++j) {
					z[weight+1][i] += z[weight][j]*weights[weight][j][i];
				}
				z[weight+1][i] = sigmoid(z[weight+1][i]); 
			}
		}
		return z[z.length-1][z[z.length-1].length-1];
	}
	
	private void backPropagationLearning(double output, double facit) {
		
	}
	
	public double neuralMagic(int roleModel) {
		double myGoodness = evaluateNeuralNetwork(this.z, this.weights);
		if (roleModel != -1) {
			double roleModelGoodness = evaluateNeuralNetwork(this.z, Animal.pool[roleModel].neuralNetwork.weights);
			backPropagationLearning(myGoodness, roleModelGoodness);
		}
		
		return myGoodness;
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
		double evolution = 0.1;
		for (int weight = 0; weight < NUM_WEIGHTS; ++weight) {
			for (int i = 0; i < weights[weight].length; ++i) {
				for (int j = 0; j < weights[weight][i].length; ++j) {
					if (Constants.RANDOM.nextBoolean()) {
						weights[weight][i][j] = neuralDad.weights[weight][i][j];
					}
					else {
						weights[weight][i][j] = neuralMom.weights[weight][i][j];
					}
					weights[weight][i][j] += evolution * (1f - 2*Constants.RANDOM.nextFloat());
				}
			}
		}
	}
	
	public void train(NeuralNetwork roleModel) {
		
	}
}
