package agents;

public class NeuralFactors {
	
	public static final class in {
		public static int NUM_FACTORS  				= 0;
		public static final int HUNGER 				= NUM_FACTORS++;
		public static final int TILE_GRASS 			= NUM_FACTORS++;
		public static final int TILE_BLOOD 			= NUM_FACTORS++;
		public static final int SEEK_GRASS 			= NUM_FACTORS++;
		public static final int SEEK_BLOOD 			= NUM_FACTORS++;
		public static final int TILE_TERRAIN_HEIGHT	= NUM_FACTORS++;
		public static final int FRIENDLER			= NUM_FACTORS++;
		public static final int STRANGER			= NUM_FACTORS++;
	}
}
