package talents;

import constants.Constants;
import agents.Animal;
import agents.Stomach;
import static constants.Constants.Talents.*;

public class Talents {

	public static int NUM_TALENTS = 0;
	public static final int FIGHT 			= NUM_TALENTS++;
	public static final int SPEED 			= NUM_TALENTS++;
	public static final int TOUGHNESS 		= NUM_TALENTS++;
	public static final int DIGEST_BLOOD 	= NUM_TALENTS++;
	public static final int DIGEST_GRASS 	= NUM_TALENTS++;
	public static final int MATE_COST 		= NUM_TALENTS++;

	public static final String[] names = {
			"Damage",
			"Speed",
			"Toughness",
			"Digest Blood",
			"Digest Grass",
			"Fertility"
	};
	
	public float mutation;

	public static float[][] RANGES;

	public float[] talentsRelative;
	public float[] talentsActual;

	public Talents() {
		super();
		this.talentsRelative = new float[NUM_TALENTS];
		this.talentsActual = new float[NUM_TALENTS];
	}

	public Talents(float[] talents) {
		super();
		this.talentsRelative = talents;
		this.talentsActual = new float[NUM_TALENTS];
		normalizeAndCalculateActuals();
	}

	public static void init() {
		if (RANGES == null) {
			System.out.println("Initializing " + Talents.class.getSimpleName());
		}

		RANGES = new float[NUM_TALENTS][];
		RANGES[FIGHT] 			= new float[] {0, 1};
		RANGES[SPEED] 			= new float[] {MIN_SPEED, MAX_SPEED};
		RANGES[TOUGHNESS] 		= new float[] {MIN_TOUGHNESS, MAX_TOUGHNESS};// TODO
		RANGES[DIGEST_BLOOD] 	= new float[] {0, MAX_DIGEST_BLOOD};
		RANGES[DIGEST_GRASS] 	= new float[] {0, MAX_DIGEST_GRASS};
		RANGES[MATE_COST] 		= new float[] {MAX_MATE_COST, MIN_MATE_COST};
	}


	public void inheritRandom() {
		mutation = Constants.Talents.MUTATION;
		for (int i = 0; i < NUM_TALENTS; ++i) {
			talentsRelative[i] = randPositive(1f);
		}
		normalizeAndCalculateActuals();
	}

	public void inherit(Talents ancestor) {
		mutation = ancestor.mutation;
		for (int i = 0; i < NUM_TALENTS; ++i) {
			talentsRelative[i] = ancestor.talentsRelative[i] + rand(mutation);
			if (talentsRelative[i] < 0) talentsRelative[i] = 0;
		}
		normalizeAndCalculateActuals();
	}

	private float rand(float size) {
		return size * (Constants.RANDOM.nextFloat()*2 - 1f);
	}
	private float randPositive(float size) {
		return size * (Constants.RANDOM.nextFloat());
	}



	public void normalizeAndCalculateActuals() {
		normalize();
		for (int i = 0; i < NUM_TALENTS; ++i) {
			talentsActual[i] = RANGES[i][0] + (RANGES[i][1] - RANGES[i][0]) * talentsRelative[i];
		}
	}

	public void normalize() {
		float sum = sum();
		if (sum > 0.0001f) {
			for (int i = 0; i < NUM_TALENTS; ++i) {
				talentsRelative[i] = talentsRelative[i] / sum;
			}
		}
		else {
			for (int i = 0; i < NUM_TALENTS; ++i) {
				talentsRelative[i] = 1f / sum;
			}
		}
	}

	public float sum() {
		float tot = 0;
		for (int i = 0; i < NUM_TALENTS; ++i) {
			if (talentsRelative[i] < 0) {
				System.err.println(Talents.class.getSimpleName() + ": talentsRelative[i] < 0");
			}
			tot += talentsRelative[i];
		}
		return tot;
	}


	public float get(int talentId) {
		return talentsActual[talentId];
	}
	
	public float getRelative(int talentId) {
		return talentsRelative[talentId];
	}

	public static void changeTalentMax(int talentId, float value) {
		RANGES[talentId][1] = value;
	}
	
	public void print() {
		for (int i = 0; i < NUM_TALENTS; ++i) {
			System.out.println(talentsRelative[i] + "  " + talentsActual[i]);
		}
	}

	public void normalizeWithRespectTo(int talentId) {
		float sum = sum();
		float sumExcluding = sum - talentsRelative[talentId];
		if (sum > 0.0001f) {
			for (int i = 0; i < NUM_TALENTS; ++i) {
				talentsRelative[i] = talentsRelative[i] / sum;
			}
		}
		else {
			for (int i = 0; i < NUM_TALENTS; ++i) {
				talentsRelative[i] = 1f / sum;
			}
		}
	}

}
