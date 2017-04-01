package agents;

import constants.Constants;

public class Decision {
	
	private static final int NUM_NODES_LAYER1 = 20;
	float[][] weights1;
	float[] weights2;
	
	public Decision() {
		weights1 = new float[DecisionFactors.NUM_DESICION_FACTORS][NUM_NODES_LAYER1];
		weights2 = new float[NUM_NODES_LAYER1];
		initWeightsRandom();
	}
	
	public void initWeightsRandom() {
		for (int i = 0; i < weights1.length; ++i) {
			for (int j = 0; j < weights1[i].length; ++j) {
				weights1[i][j] = 1 - 2*Constants.RANDOM.nextFloat();
			}
		}
		for (int j = 0; j < weights2.length; ++j) {
			weights2[j] = 1 - 2*Constants.RANDOM.nextFloat();
		}
	}
	public double neuralMagic(float[] inputData) {

		double[] z1 = new double[NUM_NODES_LAYER1];
		
		for (int i = 0; i < z1.length; ++i) {
			for (int j = 0; j < inputData.length; ++j) {
				z1[i] += weights1[j][i]*inputData[j];
			}
			z1[i] = sigmoid(z1[i]);
		}
		double result = 0;
		for (int j = 0; j < weights2.length; ++j) {
			result += weights2[j]*z1[j];
		}
		
		return sigmoid(result);
	}
	
	private double sigmoid(double f) {
		return 1d/(1d+Math.exp(f));
	}
	
	
	public void inherit(Decision decisionMom, Decision decisionDad) {
		// First decide which parent to mostly resemble.
		Decision toInheritFrom;
		Decision other;
		if (Constants.RANDOM.nextBoolean()) {
			toInheritFrom = decisionMom;
			other = decisionDad;
		}
		else {
			toInheritFrom = decisionDad;
			other = decisionMom;
		}
		
		// Loop through all weights.
		for (int i = 0; i < weights1.length; ++i) {
			for (int j = 0; j < weights1[i].length; ++j) {
				weights1[i][j] = toInheritFrom.weights1[i][j];
				// Add a randomness depending on the difference between mom and dad.
				double diff = Math.abs(weights1[i][j] - other.weights1[i][j]);
				weights1[i][j] += diff * (1 - 2*Constants.RANDOM.nextDouble());
			}
		}
		
		// Same for layer 2
		for (int j = 0; j < weights2.length; ++j) {
			weights2[j] = toInheritFrom.weights2[j];
			
			double diff = Math.abs(weights2[j] - other.weights2[j]);
			weights2[j] += diff * (1 - 2*Constants.RANDOM.nextDouble());
			
		}
	}
}
