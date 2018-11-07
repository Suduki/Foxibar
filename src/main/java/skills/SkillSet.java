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

	public float[] skillsRelative;
	public float[] skillsActual;

	public static SkillSet BLOODLING_SKILL_SET;
	public static SkillSet GRASSLER_SKILL_SET;
	public static SkillSet RANDOMLING_SKILL_SET;

	public SkillSet() {
		super();
		this.skillsRelative = new float[NUM_SKILLS];
		this.skillsActual = new float[NUM_SKILLS];
	}

	public SkillSet(float[] skills) {
		super();
		this.skillsRelative = skills;
		this.skillsActual = new float[NUM_SKILLS];
		normalizeAndCalculateActuals();
	}

	public static void init() {
		if (RANGES == null) {
			System.out.println("Initializing " + SkillSet.class.getSimpleName());
		} // Was already initialized.

		RANGES = new float[NUM_SKILLS][];
		RANGES[FIGHT] 			= new float[] {0, 1};
		RANGES[SPEED] 			= new float[] {MIN_SPEED, MAX_SPEED};
		RANGES[TOUGHNESS] 		= new float[] {MIN_TOUGHNESS, MAX_TOUGHNESS};// TODO
		RANGES[DIGEST_BLOOD] 	= new float[] {0, MAX_DIGEST_BLOOD};
		RANGES[DIGEST_GRASS] 	= new float[] {0, MAX_DIGEST_GRASS};
		RANGES[MATE_COST] 		= new float[] {MAX_MATE_COST, MIN_MATE_COST};

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
			skillsRelative[i] = randPositive(1f);
		}
		normalizeAndCalculateActuals();
	}

	public void inherit(SkillSet ancestor) {
		for (int i = 0; i < NUM_SKILLS; ++i) {
			skillsRelative[i] = ancestor.skillsRelative[i] + rand(MUTATION);
			if (skillsRelative[i] < 0) skillsRelative[i] = 0;
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
		for (int i = 0; i < NUM_SKILLS; ++i) {
			skillsActual[i] = RANGES[i][0] + (RANGES[i][1] - RANGES[i][0]) * skillsRelative[i];
		}
	}

	public void normalize() {
		float sum = sum();
		if (sum > 0.0001f) {
			for (int i = 0; i < NUM_SKILLS; ++i) {
				skillsRelative[i] = skillsRelative[i] / sum;
			}
		}
		else {
			for (int i = 0; i < NUM_SKILLS; ++i) {
				skillsRelative[i] = 1f / sum;
			}
		}
	}

	public float sum() {
		float tot = 0;
		for (int i = 0; i < NUM_SKILLS; ++i) {
			if (skillsRelative[i] < 0) {
				System.err.println(SkillSet.class.getSimpleName() + ": skills[i] < 0");
			}
			tot += skillsRelative[i];
		}
		return tot;
	}


	public float get(int skillId) {
		return skillsActual[skillId];
	}

	public static void changeSkillMax(int skill, float value) {
		RANGES[skill][1] = value;
	}
}
