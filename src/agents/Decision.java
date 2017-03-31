package agents;

import constants.Constants;

public class Decision {
	
	public static int NUM_WEIGHTS = 0;
	
	public static final int WANT_TO_MATE		= NUM_WEIGHTS++;
	public static final int WANT_TO_FLEE 		= NUM_WEIGHTS++;
	public static final int WANT_TO_HUNT 		= NUM_WEIGHTS++;
	public static final int WANT_TO_BE_ALONE 	= NUM_WEIGHTS++;
	public static final int WANT_HIGH_GRASS 	= NUM_WEIGHTS++;
	public static final int WANT_HIGH_BLOOD 	= NUM_WEIGHTS++;
	
	public static final String[] FACTOR_NAMES = new String[] {"MATE", "FLEE", "HUNT", "ALONE", "GRASS", "BLOOD"};
	
	public static final Decision STANDARD_GRASSLER = new Decision(new float[]{1f, 0f, 0.0f, -1f, 5.0f, 0.0f});
	public static final Decision STANDARD_BLOODLING = new Decision(new float[]{5f, 0.0f, 5f, -1f, 0.0f, 1.0f});
	
	public static double[] GRASSLER_SUM = new double[NUM_WEIGHTS];
	public static double[] BLOODLING_SUM = new double[NUM_WEIGHTS];
	public static int GRASSLER_TOTAL = 0;
	public static int BLOODLING_TOTAL = 0;
	
	public float[] decisionFactors = new float[NUM_WEIGHTS];
	
	
	public Decision(float[] factors) {
		for (int i = 0; i < NUM_WEIGHTS; ++i) {
			this.decisionFactors[i] = factors[i];
		}
		normalize(decisionFactors);
	}
	
	public Decision(Decision d) {
		this.inherit(d, d);
	}
	public Decision() {
	}

	public void inherit(Decision mom, Decision dad) {
		float evolution = 0.01f;
		for (int i = 0; i < NUM_WEIGHTS; ++i) {
			decisionFactors[i] = (mom.decisionFactors[i] + dad.decisionFactors[i])*0.5f + evolution*(0.5f - Constants.RANDOM.nextFloat());
		}
		normalize(decisionFactors);
	}
	
	public void randomize() {
		for (int i = 0; i < NUM_WEIGHTS; ++i) {
			decisionFactors[i] = Constants.RANDOM.nextFloat();
		}
		normalize(decisionFactors);
	}
	
	public static void register(int animalType, Decision d) {
		switch(animalType) {
		case Constants.SpeciesId.GRASSLER:
			for (int i = 0; i < NUM_WEIGHTS; ++i) {
				GRASSLER_SUM[i] += d.decisionFactors[i];
			}
			GRASSLER_TOTAL ++;
			break;
		case Constants.SpeciesId.BLOODLING:
			for (int i = 0; i < NUM_WEIGHTS; ++i) {
				BLOODLING_SUM[i] += d.decisionFactors[i];
			}
			BLOODLING_TOTAL ++;
			break;
		}
	}
	
	public static void unregister(int animalType, Decision d) {
		switch(animalType) {
		case Constants.SpeciesId.GRASSLER:
			for (int i = 0; i < NUM_WEIGHTS; ++i) {
				GRASSLER_SUM[i] -= d.decisionFactors[i];
			}
			GRASSLER_TOTAL --;
			break;
		case Constants.SpeciesId.BLOODLING:
			for (int i = 0; i < NUM_WEIGHTS; ++i) {
				BLOODLING_SUM[i] -= d.decisionFactors[i];
			}
			BLOODLING_TOTAL --;
			break;
		}
	}
	
	public void normalize(float[] f) {
		float tot = 0;
		for(int i = 0; i < NUM_WEIGHTS; ++i) {
			tot += f[i];
		}
		for(int i = 0; i < NUM_WEIGHTS; ++i) {
			f[i] /= tot;
		}
	}

	
	private static final int NUM_HIDDEN_LAYERS = 1;
	private static final int HIDDEN_LAYER_NUM_NODES = 1;
	
	public int neuralMagic(float[] inputData) {
		float[] weights = new float[NUM_HIDDEN_LAYERS];
		for (int layers= 0; layers < NUM_HIDDEN_LAYERS; layers++) {
			for (int i = 0; i < inputData.length; ++i) {
				
			}
		}
		return 0;
	}
	
	
}
