package agents;

public class NeuralFactors {
	public static int NUM_DESICION_FACTORS  	= 0;
	public static final int HUNGER 				= NUM_DESICION_FACTORS++;
	public static final int FERTILE 			= NUM_DESICION_FACTORS++;
	public static final int AGE 				= NUM_DESICION_FACTORS++;
	public static final int TILE_GRASS 			= NUM_DESICION_FACTORS++;
	public static final int TILE_BLOOD 			= NUM_DESICION_FACTORS++;
	public static final int TILE_DANGER 		= NUM_DESICION_FACTORS++;
	public static final int TILE_FERTILITY		= NUM_DESICION_FACTORS++;
	public static final int TILE_FRIENDS		= NUM_DESICION_FACTORS++;
	public static final int TILE_HUNT			= NUM_DESICION_FACTORS++;
	public static final int TILE_OLD_POSITION	= NUM_DESICION_FACTORS++;
	
	public static final String[] NAMES = {
			"HUNGER   ",
			"FERTILE  ",
			"AGE      ",
			"GRASS    ",
			"BLOOD    ",
			"DANGER   ",
			"FERTILITY",
			"FRIENDS  ",
			"HUNT     ",
			"OLD_POS  "
	};
}
