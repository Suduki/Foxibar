package world;

import noise.Noise;
import constants.Constants;

public class Terrain {
	public float[] height;
	
	public Terrain() {
		height = new float[Constants.WORLD_SIZE];
	}
	
	public float[] getColor(int i, float[] color) {
		color[0] = Constants.Colors.DIRT[0]*height[i] + Constants.Colors.SAND[0]*(1-height[i]);
		color[1] = Constants.Colors.DIRT[1]*height[i] + Constants.Colors.SAND[1]*(1-height[i]);
		color[2] = Constants.Colors.DIRT[2]*height[i] + Constants.Colors.SAND[2]*(1-height[i]);
		return color;
	}

	public void regenerate() {
		float[][][] noise = Noise.generate(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y);
		
		int i = 0;
		for(int x = 0; x < Constants.WORLD_SIZE_X; ++x) {
			for(int y = 0; y < Constants.WORLD_SIZE_Y; ++y, ++i) {
				height[i] = noise[0][x][y];
				
			}
		}
	}
}
