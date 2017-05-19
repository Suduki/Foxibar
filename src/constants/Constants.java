package constants;

import java.util.Random;

import agents.Animal;
import agents.NeuralNetwork;
import vision.Vision;

public class Constants {

	public static final short WORLD_MULTIPLIER = 7;
	public static final float INIT_ZOOM = 1.0f;
	public static final int WORLD_SIZE_X = (int) Math.pow(2, WORLD_MULTIPLIER);
	public static final int WORLD_SIZE_Y = (int) Math.pow(2, WORLD_MULTIPLIER);
	public static final int WORLD_SIZE = WORLD_SIZE_X * WORLD_SIZE_Y;
	public static final int WANTED_FPS = 20;
	public static final int PIXELS_Y = 800;
	public static final int PIXELS_SIDEBOARD = 360;
	public static final int WINDOW_WIDTH = 1080;
	public static final int PIXELS_X = WINDOW_WIDTH - PIXELS_SIDEBOARD;
	public static final float PIXELS_PER_NODE_X = ((float)PIXELS_X)/WORLD_SIZE_X;
	public static final float PIXELS_PER_NODE_Y = ((float)PIXELS_Y)/WORLD_SIZE_Y;
	public static final Random RANDOM = new Random(1);
	public static final float TILES_PER_ANIMAL = 5;
	public static final float GROWTH = Species.GRASSLER_SPEED / Species.GRASS_GAIN / TILES_PER_ANIMAL;
	public static final int MAX_NUM_ANIMALS = 50000;
	public static final float ZOOM_SPEED = 1.05f;
	public static final int NUM_NEIGHBOURS = 10;
	
	public static boolean RENDER_TERRAIN = true;
	public static boolean RENDER_ANIMALS = true;
	public static boolean RENDER_BLOOD = true;
	public static boolean RENDER_VISION = false;
	public static boolean RENDER_DIRT = false;
	public static boolean RENDER_HUNGER = true;
	public static boolean RENDER_AGE = true;
	public static final int MAX_DISTANCE_AN_ANIMAL_CAN_SEE = Vision.ZONE_HEIGHT;
	
	public static class Colors
	{
		public static final float[] RED   = new float[]{1f, 0f, 0f};
		public static final float[] GREEN = new float[]{0f, 1f, 0f};
		public static final float[] BLUE  = new float[]{0f, 0f, 1f};
		public static final float[] DIRT  = new float[]{0.3f, 0.11f, 0.04f};
		public static final float[] SAND  = new float[]{1f, 0.7f, 0.2f};
		public static final float[] WHITE = new float[]{1f, 1f, 1f};
		public static final float[] BLACK = new float[]{0f, 0f, 0f};
		public static final float[] GRASS = new float[]{0f, 1f, 0f};
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
		public static final float SPREAD = 0.0f * ADDITION_ON_DEATH;
		public static final float DECAY_FACTOR = 0.99f;
		public static final float DEATH_FROM_HUNGER_FACTOR = 0.0f;
		public static final float DEATH_FROM_AGE_FACTOR = 0.5f;
	}
	public static class Species {
		public static final float GRASSLER_SPEED = 0.9f;
		public static final float GRASS_GAIN = 20f;
		public static final agents.Species GRASSLER = new agents.Species(
				SpeciesId.GRASSLER, 0.3f, GRASS_GAIN, 0f, 0f, GRASSLER_SPEED, 0f);
//				SpeciesId.GRASSLER, 1f, 20f, 0f, 0f, 0.8f, 0f);
		public static final agents.Species BLOODLING = new agents.Species(
//				SpeciesId.BLOODLING, 0f, 0f, 1f, 30f, 1f, 1f);
				SpeciesId.BLOODLING, 0f, 0f, 0.02f, 100f, 1f, 1f);
	}
	public static class SpeciesId {
		public static final int BLOODLING = 1;
		public static final int GRASSLER = 2;
	}
}
