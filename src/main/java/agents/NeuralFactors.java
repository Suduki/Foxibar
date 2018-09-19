package agents;

public class NeuralFactors {
	public static final class in {
		public static int NUM_FACTORS  				= 0;
		public static final int HUNGER 				= NUM_FACTORS++;
		public static final int TILE_GRASS 			= NUM_FACTORS++;
		public static final int TILE_BLOOD 			= NUM_FACTORS++;
		public static final int TILE_FAT			= NUM_FACTORS++;
		public static final int TILE_TERRAIN_HEIGHT	= NUM_FACTORS++;
		public static final int FRIENDLER			= NUM_FACTORS++;
		public static final int STRANGER			= NUM_FACTORS++;
	}

	public static final class out {
		public static final int HARVEST_GRASS		= 0;
		public static final int HARVEST_BLOOD		= 1;
		public static final int FLEE_FROM_STRANGER	= 2;
		public static final int FLEE_FROM_FRIENDLER = 3;
		public static final int HUNT_STRANGER		= 4;
		public static final int HUNT_FRIENDLER		= 5;
		public static final int SPEED 				= 6;
		public static final int NUM_FACTORS  		= 7;
	}
}
