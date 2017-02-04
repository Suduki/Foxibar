package world;

import java.awt.Container;

import agents.Animal;
import constants.Constants;
import static constants.Constants.Neighbours.*;

public class World {

	public static Terrain terrain;
	public static Grass grass;
	public static Blood blood;
	
	public static int[] east;
	public static int[] north;
	public static int[] west;
	public static int[] south;
	public static int[] none;
	public static int[][] neighbour;
	
	
	public World() {
		terrain = new Terrain();
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
		terrain.regenerate();
		grass.regenerate();
	}
	
	public static void updateColor(float[][] a, int pos) {
		float grassness, dirtness;
		float[] temp = new float[3]; //TODO remove new somehow.
		
		grassness = grass.height[pos];
		a[pos][0] = grassness*grass.color[0];
		a[pos][1] = grassness*grass.color[1];
		a[pos][2] = grassness*grass.color[2];

		terrain.getColor(pos, temp);
		dirtness = 1 - grassness;
		a[pos][0] += dirtness*temp[0];
		a[pos][1] += dirtness*temp[1];
		a[pos][2] += dirtness*temp[2];
		
		if (Constants.RENDER_BLOOD) {
			blood.getColor(pos, temp);
			a[pos][0] += temp[0];
			a[pos][1] += temp[1];
			a[pos][2] += temp[2];
		}
	}
}
