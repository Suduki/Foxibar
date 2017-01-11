package constants;

import java.util.Random;

public class Constants {

	public static final short WORLD_MULTIPLIER = 10;
	public static final float INIT_ZOOM = 4;
	public static final int WORLD_SIZE_X = (int) Math.pow(2, WORLD_MULTIPLIER);
	public static final int WORLD_SIZE_Y = (int) Math.pow(2, WORLD_MULTIPLIER);
	public static final int WORLD_SIZE = WORLD_SIZE_X * WORLD_SIZE_Y;
	public static final int WANTED_FPS = 2000;
	public static final int PIXELS_Y = 800;
	public static final int PIXELS_SIDEBOARD = 360;
	public static final int PIXELS_X = 1080 - PIXELS_SIDEBOARD;
	public static final float PIXELS_PER_NODE_X = ((float)PIXELS_X)/WORLD_SIZE_X;
	public static final float PIXELS_PER_NODE_Y = ((float)PIXELS_Y)/WORLD_SIZE_Y;
	public static final Random RANDOM = new Random(1);
	public static final boolean WALK_THROUGH_EDGE = true;
	public static final float GROWTH = 0.01f;
	public static final int MAX_NUM_ANIMALS = 100000;
	public static final short MAX_NUM_ANIMALS_PER_NODE = 10;
	public static final float ZOOM_SPEED = 1.01f;
	
	public static boolean RENDER_TERRAIN = true;
	public static boolean RENDER_ANIMALS = true;
	
	public static class Colors
	{
		public static final float[] RED   = new float[]{1f, 0f, 0f};
		public static final float[] GREEN = new float[]{0f, 1f, 0f};
		public static final float[] BLUE  = new float[]{0f, 0f, 1f};
		public static final float[] DIRT  = new float[]{0.3f, 0.11f, 0.04f};
		public static final float[] SAND  = new float[]{1f, 0.7f, 0.2f};
		public static final float[] WHITE = new float[]{1f, 1f, 1f};
		public static final float[] BLACK = new float[]{0f, 0f, 0f};
		public static final float[] GRASS = new float[]{0f, 0.3f, 0f};
		public static final float[] BLOOD = new float[]{1f, 0f, 0f};
		public static final float[] DARK_RED = new float[]{0.3f, 0f, 0f};
		public static final float[] DARK_BLUE = new float[]{0f, 0f, 0.3f};
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
		public static final float ADDITION_ON_DEATH = 1;
		public static final float DEATH = 100;
		public static final float SPREAD = 0.25f * ADDITION_ON_DEATH;
	}
}
