package agents;

import constants.Constants;

public class Decision {
	
	public static int NUM_FACTORS = 6;
	
	public static final int WANT_TO_MATE = 0;
	public static final int WANT_TO_FLEE = 1;
	public static final int WANT_TO_HUNT = 2;
	public static final int WANT_TO_BE_ALONE = 3;
	public static final int WANT_HIGH_GRASS = 4;
	public static final int WANT_HIGH_BLOOD = 5;
	
	public static final String[] FACTOR_NAMES = new String[] {"MATE", "FLEE", "HUNT", "ALONE", "GRASS", "BLOOD"};
	
	public static final Decision STANDARD_GRASSLER = new Decision(new float[]{1f, 0f, 0.0f, -1f, 5.0f, 0.0f});
	public static final Decision STANDARD_BLOODLING = new Decision(new float[]{5f, 0.0f, 5f, -1f, 0.0f, 1.0f});
	
	public static double[] GRASSLER_SUM = new double[NUM_FACTORS];
	public static double[] BLOODLING_SUM = new double[NUM_FACTORS];
	public static int GRASSLER_TOTAL = 0;
	public static int BLOODLING_TOTAL = 0;
	
	public float[] decisionFactors = new float[NUM_FACTORS];
	
	
	public Decision(float[] factors) {
		for (int i = 0; i < NUM_FACTORS; ++i) {
			this.decisionFactors[i] = factors[i];
		}
		normalize();
	}
	
	public Decision(Decision d) {
		this.inherit(d, d);
	}

	public void inherit(Decision mom, Decision dad) {
		float evolution = 0.01f;
		for (int i = 0; i < NUM_FACTORS; ++i) {
			decisionFactors[i] = (mom.decisionFactors[i] + dad.decisionFactors[i])*0.5f + evolution*(0.5f - Constants.RANDOM.nextFloat());
		}
		normalize();
	}
	
	public void randomize() {
		for (int i = 0; i < NUM_FACTORS; ++i) {
			decisionFactors[i] = Constants.RANDOM.nextFloat();
		}
		normalize();
	}
	
	public static void register(int animalType, Decision d) {
		switch(animalType) {
		case Constants.SpeciesId.GRASSLER:
			for (int i = 0; i < NUM_FACTORS; ++i) {
				GRASSLER_SUM[i] += d.decisionFactors[i];
			}
			GRASSLER_TOTAL ++;
			break;
		case Constants.SpeciesId.BLOODLING:
			for (int i = 0; i < NUM_FACTORS; ++i) {
				BLOODLING_SUM[i] += d.decisionFactors[i];
			}
			BLOODLING_TOTAL ++;
			break;
		}
	}
	
	public static void unregister(int animalType, Decision d) {
		switch(animalType) {
		case Constants.SpeciesId.GRASSLER:
			for (int i = 0; i < NUM_FACTORS; ++i) {
				GRASSLER_SUM[i] -= d.decisionFactors[i];
			}
			GRASSLER_TOTAL --;
			break;
		case Constants.SpeciesId.BLOODLING:
			for (int i = 0; i < NUM_FACTORS; ++i) {
				BLOODLING_SUM[i] -= d.decisionFactors[i];
			}
			BLOODLING_TOTAL --;
			break;
		}
	}
	
	public void normalize() {
		float tot = 0;
		for(int i = 0; i < NUM_FACTORS; ++i) {
			tot += decisionFactors[i];
		}
		for(int i = 0; i < NUM_FACTORS; ++i) {
			decisionFactors[i] /= tot;
		}
	}

}
