package constants;

public class RenderState {
	public static boolean RENDER_TERRAIN;
	public static boolean RENDER_ANIMALS;
	public static boolean RENDER_BLOOD;
	public static boolean RENDER_VISION;
	public static boolean RENDER_DIRT;
	public static boolean RENDER_HUNGER;
	public static boolean RENDER_AGE;
	public static boolean PAN_OLD_MAN;
	public static boolean LIMIT_VISION;
	public static boolean FOLLOW_BLOODLING;
	public static boolean FOLLOW_GRASSLER;
	
	private static int renderState;
	private static int NUM_RENDER_STATES = 5;
	public  final static int RENDER_WORLD_STILL								= 0;
	public final static int RENDER_WORLD_FOLLOW_OLDEST						= 1;
	public final static int RENDER_WORLD_FOLLOW_OLDEST_LIMIT_VISION  		= 2;
	public final static int RENDER_WORLD_FOLLOW_BLOODLING_LIMIT_VISION		= 3;
	public final static int RENDER_WORLD_FOLLOW_GRASSLER_LIMIT_VISION		= 4;
	
	public static void stepState() {
		if (++renderState >= NUM_RENDER_STATES) {
			renderState = 0;
		}
		activateState(renderState);
	}

	public static void activateState(int state) {
		renderState = state;
		switch (state) {
			case RENDER_WORLD_STILL:
				RENDER_TERRAIN = true;
				RENDER_ANIMALS = true;
				RENDER_BLOOD = true;
				RENDER_VISION = false;
				RENDER_DIRT = false;
				RENDER_HUNGER = true;
				RENDER_AGE = true;
				PAN_OLD_MAN = false;
				LIMIT_VISION = false;
				FOLLOW_BLOODLING = false;
				FOLLOW_GRASSLER = false;
				break;
			case RENDER_WORLD_FOLLOW_OLDEST:
				RENDER_TERRAIN = true;
				RENDER_ANIMALS = true;
				RENDER_BLOOD = true;
				RENDER_VISION = false;
				RENDER_DIRT = false;
				RENDER_HUNGER = true;
				RENDER_AGE = true;
				PAN_OLD_MAN = true;
				LIMIT_VISION = false;
				FOLLOW_BLOODLING = true;
				FOLLOW_GRASSLER = true;
				break;
			case RENDER_WORLD_FOLLOW_OLDEST_LIMIT_VISION:
				RENDER_TERRAIN = true;
				RENDER_ANIMALS = true;
				RENDER_BLOOD = true;
				RENDER_VISION = false;
				RENDER_DIRT = false;
				RENDER_HUNGER = true;
				RENDER_AGE = true;
				PAN_OLD_MAN = true;
				LIMIT_VISION = true;
				FOLLOW_BLOODLING = true;
				FOLLOW_GRASSLER = true;
				break;
			case RENDER_WORLD_FOLLOW_BLOODLING_LIMIT_VISION:
				RENDER_TERRAIN = true;
				RENDER_ANIMALS = true;
				RENDER_BLOOD = true;
				RENDER_VISION = false;
				RENDER_DIRT = false;
				RENDER_HUNGER = true;
				RENDER_AGE = true;
				PAN_OLD_MAN = true;
				LIMIT_VISION = true;
				FOLLOW_BLOODLING = true;
				FOLLOW_GRASSLER = false;
				break;
			case RENDER_WORLD_FOLLOW_GRASSLER_LIMIT_VISION:
				RENDER_TERRAIN = true;
				RENDER_ANIMALS = true;
				RENDER_BLOOD = true;
				RENDER_VISION = false;
				RENDER_DIRT = false;
				RENDER_HUNGER = true;
				RENDER_AGE = true;
				PAN_OLD_MAN = true;
				LIMIT_VISION = true;
				FOLLOW_BLOODLING = false;
				FOLLOW_GRASSLER = true;
				break;
		}
	}
	
	
	
}
