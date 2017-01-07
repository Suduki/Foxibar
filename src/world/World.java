package world;

import java.awt.Container;

import agents.Animal;
import constants.Constants;
import static constants.Constants.Neighbours.*;

public class World {

	public static Terrain growth;
	public static Grass grass;
	public static Blood blood;
	
	public static int[] east;
	public static int[] north;
	public static int[] west;
	public static int[] south;
	public static int[] none;
	public static int[][] neighbour;
	
	
	public World() {
		growth = new Terrain();
		grass = new Grass();
		blood = new Blood();

		calculateNeighbours();
		regenerate();
	}

	private void calculateNeighbours() {
		neighbour = new int[5][];
		east = new int[Constants.WORLD_SIZE];
		north = new int[Constants.WORLD_SIZE];
		west = new int[Constants.WORLD_SIZE];
		south = new int[Constants.WORLD_SIZE];
		none = new int[Constants.WORLD_SIZE];
		
		neighbour[EAST] = east;
		neighbour[NORTH] = north;
		neighbour[WEST] = west;
		neighbour[SOUTH] = south;
		neighbour[NONE] = none;
		
		if (Constants.WALK_THROUGH_EDGE) {
			// EAST
			for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
				east[i] = i+1;
			}
			for (int i = 0; i < Constants.WORLD_SIZE_Y; ++i) {
				east[(i+1)*Constants.WORLD_SIZE_X - 1] = i*Constants.WORLD_SIZE_X;
			}
			
			// NORTH
			for (int i = 0; i < Constants.WORLD_SIZE_X; ++i) {
				north[i] = i + Constants.WORLD_SIZE - Constants.WORLD_SIZE_X;
			}
			for (int i = Constants.WORLD_SIZE_X; i < Constants.WORLD_SIZE; ++i) {
				north[i] = i - Constants.WORLD_SIZE_X;
			}
			
			// WEST
			for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
				west[i] = i-1;
			}
			for (int i = 0; i < Constants.WORLD_SIZE_Y; ++i) {
				west[i*Constants.WORLD_SIZE_X] = (i+1)*Constants.WORLD_SIZE_X - 1;
			}
			
			// SOUTH
			for (int i = 0; i < Constants.WORLD_SIZE_X; ++i) {
				south[i + Constants.WORLD_SIZE - Constants.WORLD_SIZE_X] = i;
			}
			for (int i = 0; i < Constants.WORLD_SIZE - Constants.WORLD_SIZE_X; ++i) {
				south[i] = i + Constants.WORLD_SIZE_X;
			}
			
			// NONE
			for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
				none[i] = i;
			}
		}
	}

	public void update() {
		grass.grow();
	}
	
	public static void regenerate() {
		growth.regenerate();
		grass.regenerate();
	}
	
	public static void updateColor(float[][] a, int pos) {
		float grassness, dirtness;
		float[] dirtColor = new float[3]; //TODO remove new somehow.
		grassness = grass.height[pos];
		a[pos][0] = grassness*Grass.colors[grass.type[pos]][0];
		a[pos][1] = grassness*Grass.colors[grass.type[pos]][1];
		a[pos][2] = grassness*Grass.colors[grass.type[pos]][2];

		growth.getColor(pos, dirtColor);
		dirtness = 1 - grassness;
		a[pos][0] += dirtness*dirtColor[0];
		a[pos][1] += dirtness*dirtColor[1];
		a[pos][2] += dirtness*dirtColor[2];		
	}
}
