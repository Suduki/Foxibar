package agents;

public class NeuralFactors {
	public static int 		NUM_INPUT_FACTORS  		= 0;
	public static final int IN_STOMACH_FULLNESS		= NUM_INPUT_FACTORS++;
	public static final int IN_AGE 					= NUM_INPUT_FACTORS++;
	public static final int IN_STAMINA				= NUM_INPUT_FACTORS++;
	public static final int IN_TILE_GRASS 			= NUM_INPUT_FACTORS++;
	public static final int IN_TILE_BLOOD 			= NUM_INPUT_FACTORS++;
	public static final int IN_TILE_DANGER 			= NUM_INPUT_FACTORS++;
	public static final int IN_TILE_FERTILITY		= NUM_INPUT_FACTORS++;
	public static final int IN_TILE_FRIENDS			= NUM_INPUT_FACTORS++;
	public static final int IN_TILE_HUNT			= NUM_INPUT_FACTORS++;
	public static final int IN_TILE_TERRAIN_HIGHT	= NUM_INPUT_FACTORS++;
//	public static final int TILE_OLD_POSITION	= NUM_DESICION_FACTORS++;
	
	
	public static int 		NUM_OUTPUT_FACTORS 		= 0;
	public static final int OUT_TILE_GOODNESS 		= NUM_OUTPUT_FACTORS++;
	public static final int OUT_SPEED 				= NUM_OUTPUT_FACTORS++;
	public static final int OUT_GRASS_EAT 			= NUM_OUTPUT_FACTORS++;
	public static final int OUT_BLOOD_EAT 			= NUM_OUTPUT_FACTORS++;
}
