package display;

public class RenderState {
	public static boolean RENDER_TERRAIN;
	public static boolean RENDER_ANIMALS;
	public static boolean RENDER_DIRT;
	public static boolean RENDER_HUNGER;
	public static boolean RENDER_AGE;
	public static boolean RENDER_HEALTH;
	public static boolean PAN_OLD_MAN;
	public static boolean LIMIT_VISION;
	public static boolean FOLLOW_BLOODLING;
	public static boolean FOLLOW_GRASSLER;
	public static boolean DRAW_VISION_CIRCLE;
	
	private static int renderState;
	private static int NUM_RENDER_STATES = 4;
	public final static int RENDER_WORLD_STILL								= 0;
	public final static int RENDER_WORLD_FOLLOW_BLOODLING_LIMIT_VISION		= 1;
	public final static int RENDER_WORLD_FOLLOW_GRASSLER_LIMIT_VISION		= 2;
	public final static int RENDER_WORLD_DONT_RENDER_ANIMALS 				= 3;
	
	public static void stepState() {
		if (++renderState >= NUM_RENDER_STATES) {
			renderState = 0;
		}
		activateState(renderState);
	}

	public static void activateState(int state) {
		RENDER_TERRAIN = true;
		RENDER_ANIMALS = true;
		RENDER_DIRT = true;
		RENDER_HUNGER = true;
		RENDER_AGE = true;
		RENDER_HEALTH = false;
		renderState = state;
		switch (state) {
			case RENDER_WORLD_STILL:
				PAN_OLD_MAN = false;
				LIMIT_VISION = false;
				FOLLOW_BLOODLING = false;
				FOLLOW_GRASSLER = false;
				DRAW_VISION_CIRCLE = false;
				RENDER_ANIMALS = true;
				break;
			case RENDER_WORLD_FOLLOW_BLOODLING_LIMIT_VISION:
				PAN_OLD_MAN = true;
				LIMIT_VISION = true;
				FOLLOW_BLOODLING = true;
				FOLLOW_GRASSLER = false;
				DRAW_VISION_CIRCLE = true;
				RENDER_ANIMALS = true;
				break;
			case RENDER_WORLD_FOLLOW_GRASSLER_LIMIT_VISION:
				PAN_OLD_MAN = true;
				LIMIT_VISION = true;
				FOLLOW_BLOODLING = false;
				FOLLOW_GRASSLER = true;
				DRAW_VISION_CIRCLE = true;
				RENDER_ANIMALS = true;
				break;
			case RENDER_WORLD_DONT_RENDER_ANIMALS:
				PAN_OLD_MAN = false;
				LIMIT_VISION = false;
				FOLLOW_BLOODLING = false;
				FOLLOW_GRASSLER = false;
				DRAW_VISION_CIRCLE = false;
				RENDER_ANIMALS = false;
				break;	
		}
	}
	
	
	
}
