package agents;

public class NeuralFactors {
	public static int NUM_INPUT_FACTORS  		= 0;
	public static final int HUNGER 				= NUM_INPUT_FACTORS++;
	public static final int AGE 				= NUM_INPUT_FACTORS++;
	public static final int TILE_FIBER 			= NUM_INPUT_FACTORS++;
	public static final int TILE_BLOOD 			= NUM_INPUT_FACTORS++;
	public static final int TILE_FAT			= NUM_INPUT_FACTORS++;
	public static final int TILE_DANGER 		= NUM_INPUT_FACTORS++;
	public static final int TILE_FERTILITY		= NUM_INPUT_FACTORS++;
	public static final int TILE_FRIENDS		= NUM_INPUT_FACTORS++;
	public static final int TILE_HUNT			= NUM_INPUT_FACTORS++;
	public static final int TILE_TERRAIN_HEIGHT	= NUM_INPUT_FACTORS++;
	
	public static int NUM_OUTPUT_FACTORS  	= 0;
	public static final int OUT_AGGRESSIVE		= NUM_OUTPUT_FACTORS++;
	public static final int OUT_HARVEST			= NUM_OUTPUT_FACTORS++;
	public static final int OUT_NODE_GOODNESS	= NUM_OUTPUT_FACTORS++;
	
	public static final String[] NAMES = {
			"HUNGER   ",
			"AGE      ",
			"GRASS    ",
			"BLOOD    ",
			"DANGER   ",
			"FERTILITY",
			"FRIENDS  ",
			"HUNT     ",
			"HEIGHT   ",
//			"OLD_POS  "
	};
	
}
