package world;

import constants.Constants;

public class Grass {

	private static int numGrassTypes = 3;
	public static float[][] colors;
	
	private Terrain terrain;
	public float[] height;
	public boolean[] toBeUpdated;
	public short[] type;
	
	public Grass() {
		height = new float[Constants.WORLD_SIZE];
		toBeUpdated = new boolean[Constants.WORLD_SIZE];
		colors = new float[numGrassTypes][3];
		type = new short[Constants.WORLD_SIZE];
		
		for (int c = 0; c < 3 ; ++c) {
			colors[0][c] = Constants.Colors.GRASS[c];
			colors[1][c] = Constants.Colors.DARK_RED[c];
			colors[2][c] = Constants.Colors.DARK_BLUE[c];
		}
		
		terrain = new Terrain();
	}

	public void grow() {
		for(int i = 0; i < Constants.WORLD_SIZE; ++i) {
			if (toBeUpdated[i]) {
				height[i] += Constants.GROWTH * World.growth.height[i];
				if (height[i] > World.growth.height[i]) {
					toBeUpdated[i] = false;
				}
			}
		}
	}

	public void regenerate() {
		this.terrain.regenerate();
		determineType();
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			this.height[i] = World.growth.height[i];
			toBeUpdated[i] = false;
		}
	}
	
	private void determineType() {
		for(int i = 0; i < Constants.WORLD_SIZE; ++i) {
			if (terrain.height[i]<0.4f) type[i]=0;
			else if (terrain.height[i]<0.6f) type[i]=1;
			else if (terrain.height[i]<1f) type[i]=2;
		}
	}

	public void killAllGrass() {
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			this.height[i] = 0f;
			toBeUpdated[i] = true;
		}
	}
}
