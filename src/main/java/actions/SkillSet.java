package actions;

public class SkillSet {
	
	private static int NUM_SKILLS = 0;
	public static final int FIGHT = NUM_SKILLS++; // DAMAGE
	public static final int MAX_SPEED = NUM_SKILLS++; // SPEED
	public static final int MAX_SIZE = NUM_SKILLS++; // TOUGHNESS
	public static final int HARVEST_BLOOD = NUM_SKILLS++;
	public static final int HARVEST_GRASS = NUM_SKILLS++;
	
	public static float[][] RANGES;
	
	private float[] skills;
	
	
	public static void init() {
		int low = 0, high = 1;
		RANGES = new float[NUM_SKILLS][];
		RANGES[FIGHT] = new float[] {0, 1};
		RANGES[MAX_SPEED] = new float[] {0, 1};
		RANGES[FIGHT] = new float[] {0, 1};
		RANGES[FIGHT] = new float[] {0, 1};
	}
}
