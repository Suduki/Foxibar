package skills;

import agents.Agent;
import agents.Stomach;
import constants.Constants;

public class SkillSet {
	
	public static int NUM_SKILLS = 0;
	public static final int FIGHT 			= NUM_SKILLS++;
	public static final int MAX_SPEED 		= NUM_SKILLS++;
	public static final int TOUGHNESS 		= NUM_SKILLS++;
	public static final int DIGEST_BLOOD 	= NUM_SKILLS++;
	public static final int DIGEST_GRASS 	= NUM_SKILLS++;
	public static final int MATE 			= NUM_SKILLS++;
	
	public static final float MUTATION = 0.1f;
	
	public static float[][] RANGES;
	
	public float[] skills;

	public SkillSet() {
		super();
		this.skills = new float[NUM_SKILLS];
	}


	public static void init() {
		if (RANGES != null) return; // Was already initialized.
		System.out.println("Initializing " + SkillSet.class.getSimpleName());
		RANGES = new float[NUM_SKILLS][];
		RANGES[FIGHT] 			= new float[] {0, 1};
		RANGES[MAX_SPEED] 		= new float[] {Constants.SkillSet.MIN_SPEED, 1};
		RANGES[TOUGHNESS] 		= new float[] {Constants.SkillSet.MIN_TOUGHNESS, 1};
		RANGES[DIGEST_BLOOD] 	= new float[] {0, Stomach.MAX_B};
		RANGES[DIGEST_GRASS] 	= new float[] {0, Stomach.MAX_G};
		RANGES[MATE] 			= new float[] {Agent.BIRTH_HUNGER_COST / 2, Agent.BIRTH_HUNGER_COST * 2};
	}


	public void inheritRandom() {
		for (int i = 0; i < NUM_SKILLS; ++i) {
			skills[i] = randPos(1f);
		}
		normalize();
	}
	
	public void inherit(SkillSet ancestor) {
		for (int i = 0; i < NUM_SKILLS; ++i) {
			skills[i] = ancestor.skills[i] + rand(MUTATION);
			if (skills[i] < 0) skills[i] = 0;
		}
		normalize();
	}
	
	private float rand(float size) {
		return size * (Constants.RANDOM.nextFloat()*2 - 1f);
	}
	private float randPos(float size) {
		return size * (Constants.RANDOM.nextFloat());
	}



	public void normalize() {
		float sum = sum();
		for (int i = 0; i < NUM_SKILLS; ++i) {
			skills[i] = skills[i] / sum;
		}
	}
	
	public float sum() {
		float tot = 0;
		for (int i = 0; i < NUM_SKILLS; ++i) {
			if (skills[i] < 0) {
				System.err.println(SkillSet.class.getSimpleName() + ": skills[i] < 0");
			}
			tot += skills[i];
		}
		return tot;
	}
}
