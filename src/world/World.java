package world;

import java.awt.Container;

import agents.Animal;
import constants.Constants;

public class World {

	public static Terrain terrain;
	public static Grass grass;
	public static Blood blood;
	public static int[][] neighbour;
	
	
	private static final int EAST  = 0;
	private static final int NORTH = 1;
	private static final int WEST  = 2;
	private static final int SOUTH = 3;
	private static final int NONE = 4;
	
	
	public World() {
		terrain = new Terrain();
		grass = new Grass();
		blood = new Blood();

		calculateNeighbours();
		regenerate();
	}

	private void calculateNeighbours() {
		neighbour = new int[Constants.WORLD_SIZE][5];
		if (Constants.WALK_THROUGH_EDGE) {
			// EAST
			for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
				neighbour[i][EAST] = i+1;
			}
			for (int i = 0; i < Constants.WORLD_SIZE_Y; ++i) {
				neighbour[(i+1)*Constants.WORLD_SIZE_X - 1][EAST] = i*Constants.WORLD_SIZE_X;
			}
			
			// NORTH
			for (int i = 0; i < Constants.WORLD_SIZE_X; ++i) {
				neighbour[i][NORTH] = i + Constants.WORLD_SIZE - Constants.WORLD_SIZE_X;
			}
			for (int i = Constants.WORLD_SIZE_X; i < Constants.WORLD_SIZE; ++i) {
				neighbour[i][NORTH] = i - Constants.WORLD_SIZE_X;
			}
			
			// WEST
			for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
				neighbour[i][WEST] = i-1;
			}
			for (int i = 0; i < Constants.WORLD_SIZE_Y; ++i) {
				neighbour[i*Constants.WORLD_SIZE_X][WEST] = (i+1)*Constants.WORLD_SIZE_X - 1;
			}
			
			// SOUTH
			for (int i = 0; i < Constants.WORLD_SIZE_X; ++i) {
				neighbour[i + Constants.WORLD_SIZE - Constants.WORLD_SIZE_X][SOUTH] = i;
			}
			for (int i = 0; i < Constants.WORLD_SIZE - Constants.WORLD_SIZE_X; ++i) {
				neighbour[i][SOUTH] = i + Constants.WORLD_SIZE_X;
			}
			
			// NONE
			for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
				neighbour[i][NONE] = i;
			}
		}
	}

	public void update() {
		grass.grow();
	}
	
	public static void regenerate() {
		terrain.regenerate();
		grass.regenerate();
	}
	
	public static void updateColors(float[][] a) {
		float grassness, dirtness;
		float[] dirtColor = new float[3];
		int i = 0;
		for (int y = 0; y < Constants.WORLD_SIZE_Y; ++y) {
			for (int x = 0; x < Constants.WORLD_SIZE_X; ++x, ++i) {
				grassness = grass.height[i];
				dirtness = 1 - grassness;
				a[i][0] = grassness*grass.color[0];
				a[i][1] = grassness*grass.color[1];
				a[i][2] = grassness*grass.color[2];

				terrain.getColor(i, dirtColor);
				a[i][0] += dirtness*dirtColor[0];
				a[i][1] += dirtness*dirtColor[1];
				a[i][2] += dirtness*dirtColor[2];
			}
		}
	}
}
