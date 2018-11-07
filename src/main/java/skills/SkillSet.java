package skills;

import constants.Constants;
import agents.Agent;
import agents.Stomach;
import static constants.Constants.SkillSet.*;

public class SkillSet {
	
	public static int NUM_SKILLS = 0;
	public static final int FIGHT 			= NUM_SKILLS++;
	public static final int SPEED 			= NUM_SKILLS++;
	public static final int TOUGHNESS 		= NUM_SKILLS++;
	public static final int DIGEST_BLOOD 	= NUM_SKILLS++;
	public static final int DIGEST_GRASS 	= NUM_SKILLS++;
	public static final int MATE_COST 		= NUM_SKILLS++;
	
	public static final float MUTATION = 0.1f;
	
	public static float[][] RANGES;
	
	public float[] skills;
	public float[] skillsAbsolute;
	
	public static SkillSet BLOODLING_SKILL_SET;
	public static SkillSet GRASSLER_SKILL_SET;
	public static SkillSet RANDOMLING_SKILL_SET;

	public SkillSet() {
		super();
		this.skills = new float[NUM_SKILLS];
		this.skillsAbsolute = new float[NUM_SKILLS];
	}
	
	public SkillSet(float[] skills) {
		super();
		this.skills = skills;
		this.skillsAbsolute = new float[NUM_SKILLS];
		normalizeAndCalculateAbsolutes();
	}


	public static void init() {
		if (RANGES != null) return; // Was already initialized.
		
		
		System.out.println("Initializing " + SkillSet.class.getSimpleName());
		RANGES = new float[NUM_SKILLS][];
		RANGES[FIGHT] 			= new float[] {0, 1};
		RANGES[SPEED] 			= new float[] {MIN_SPEED, MAX_SPEED};
		RANGES[TOUGHNESS] 		= new float[] {MIN_TOUGHNESS, MAX_TOUGHNESS};// TODO
		RANGES[DIGEST_BLOOD] 	= new float[] {0, Stomach.MAX_B};
		RANGES[DIGEST_GRASS] 	= new float[] {0, Stomach.MAX_G};
		RANGES[MATE_COST] 		= new float[] {Agent.BIRTH_HUNGER_COST * 2, Agent.BIRTH_HUNGER_COST / 2};
		
		BLOODLING_SKILL_SET = new SkillSet(new float[]{
				1f, 1f, 1f, 1f, 0, 1f
		});
		GRASSLER_SKILL_SET = new SkillSet(new float[]{
				0, 0.5f, 0, 0, 1f, 1f
		});
		RANDOMLING_SKILL_SET = new SkillSet(new float[]{
				0, 0, 0, 0, 1f, 1f
		});
	}


	public void inheritRandom() {
		for (int i = 0; i < NUM_SKILLS; ++i) {
			skills[i] = randPositive(1f);
		}
		normalizeAndCalculateAbsolutes();
	}
	
	public void inherit(SkillSet ancestor) {
		for (int i = 0; i < NUM_SKILLS; ++i) {
			skills[i] = ancestor.skills[i] + rand(MUTATION);
			if (skills[i] < 0) skills[i] = 0;
		}
		normalizeAndCalculateAbsolutes();
	}
	
	private float rand(float size) {
		return size * (Constants.RANDOM.nextFloat()*2 - 1f);
	}
	private float randPositive(float size) {
		return size * (Constants.RANDOM.nextFloat());
	}



	public void normalizeAndCalculateAbsolutes() {
		normalize();
		for (int i = 0; i < NUM_SKILLS; ++i) {
			skillsAbsolute[i] = RANGES[i][0]gångra ,ed nåt här + RANGES[i][1] * skills[i];
		}
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


	public float get(int skillId) {
		return skillsAbsolute[skillId];
	}
}
