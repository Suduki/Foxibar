package constants;

import java.util.Random;

public class Constants {

	public static final short WORLD_MULTIPLIER_MAIN = 7;
	public static final short WORLD_MULTIPLIER_TEST = 5;
	public static final short WORLD_MULTIPLIER_INTEGRATION_TEST = 6;
	public static final float INIT_ZOOM = 1f;
	public static final int WANTED_FPS = 30;
	public static final int PIXELS_Y = 800;
	public static final int WINDOW_WIDTH = 1080;
	public static final int PIXELS_X = WINDOW_WIDTH;
	public static Random RANDOM = new Random(1);
	public static final float TILES_PER_ANIMAL = 20;
	public static final float GROWTH = 0.005f;
	public static int MAX_NUM_ANIMALS = 20000;
	public static final float ZOOM_SPEED = 1.05f;
	
	public static class Talents {
		public static final float MIN_SPEED 		= 0.2f;
		public static final float MAX_SPEED 		= 1f;
		public static final float MIN_TOUGHNESS 	= 0.01f;
		public static final float MAX_TOUGHNESS 	= 1f;
		public static final float MIN_MATE_COST 	= 50f;
		public static final float MAX_MATE_COST 	= 400f;
		public static final float MUTATION = 0.1f;

		public static float MAX_DIGEST_GRASS;
		public static float MAX_DIGEST_FIBER;
		public static float MAX_DIGEST_BLOOD;
	}

	public static class Vision {
		public static final int HEIGHT = 16;
		public static final int WIDTH =  16;
		public static final int MAX_DISTANCE_AN_AGENT_CAN_SEE = HEIGHT;
		public static final int NUM_NEIGHBOURS = 5;
	}
	
	public static class Colors
	{
		public static final float[] RED   = new float[]{1f, 0f, 0f};
		public static final float[] GREEN = new float[]{0f, 1f, 0f};
		public static final float[] BLUE  = new float[]{0f, 0f, 1f};
		public static final float[] DIRT  = new float[]{0.0f, 0.0f, 0.0f};
		public static final float[] SAND  = new float[]{1f, 0.7f, 0.2f};
		public static final float[] WHITE = new float[]{1f, 1f, 1f};
		public static final float[] BLACK = new float[]{0f, 0f, 0f};
		public static final float[] GRASS = new float[]{0f, 1f, 0f};
		public static final float[] BLOOD = new float[]{1f, 0f, 0f};
		public static final float[] DARK_RED = new float[]{0.3f, 0f, 0f};
		public static final float[] DARK_BLUE = new float[]{0f, 0f, 0.3f};
		public static final float[] LIGHT_BLUE = new float[]{0.6f, 0.6f, 0.9f};
		public static final float[] GREY = new float[]{0.5f, 0.5f, 0.7f};
		public static final float[] GRASS_STRAW = new float[]{0.4f, 0.8f, 0f};
		public static final float[] TREE = new float[]{0.5f, 0.2f, 0.1f};
		public static final float[] FIBER = new float[]{0.5f, 0.2f, 0.1f};
		public static final float[] TREE_TOP = new float[]{0.4f, 0.8f, 0f};
		public static final float[] YELLOW = new float[]{1f, 1f, 0f};
		
		public static class DesignYourAnimal {
			public static final float mainColorDominance = 0.7f;
			public static final float secondaryColorDominance = 0.3f;
			public static final float mean = (mainColorDominance + secondaryColorDominance) / 2;
			public static final float[] OUTER = new float[]{mainColorDominance, secondaryColorDominance, secondaryColorDominance};
			public static final float[] INNER= new float[]{0.941f, 0.839f, 0.27f};
			public static final float[] MIDDLE = new float[]{0.843f, 0.545f, 0.392f};
			public static final float[] BUTTON = new float[]{0.8f, 0.8f, 0.8f};
		}
		
	}

	public static class Neighbours {
		public static final short INVALID_DIRECTION = -1;
		public static final short EAST = 0;
		public static final short NORTH = 1;
		public static final short WEST = 2;
		public static final short SOUTH = 3;
		public static final short NONE = 4;
	}

	public static class Blood {
		public static final float SPLASH = 1f;
		public static final float ADDITION_ON_DEATH = 1f;
		public static final float DECAY_FACTOR = 0.999f;
		public static final float DECAY_FACTOR_WATER = 0.9f;
		public static final float DECAY_FACTOR_STONE = 0.9f;
		public static final float DEATH_FROM_HUNGER_FACTOR = 1f;
		public static final float DEATH_FROM_AGE_FACTOR = 1.0f;
		public static final float DEATH_FROM_LOW_HEALTH = 1f;
	}
}
