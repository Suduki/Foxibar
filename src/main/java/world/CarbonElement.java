package world;

import org.joml.Vector2f;
import org.joml.Vector2i;

import constants.Constants;

public class CarbonElement extends TileElement{
	
	public float maxHeight, decayFactor;
	
	public CarbonElement(float maxHeight, float[] color, float splash, float decayFactor) {
		this.height = new float[Constants.WORLD_SIZE_V.x][Constants.WORLD_SIZE_V.y];
		this.color = color;
		this.maxHeight = maxHeight;
		this.decayFactor = decayFactor;
	}
	
	private static final float TRUE_DECAY = (float) Math.pow(Constants.Blood.DECAY_FACTOR, World.UPDATE_FREQUENCY);
	public void decay(int timeStep, int updateFrequency) {
		
		for(int i = 0; i < Constants.WORLD_SIZE_V.x; i++) {
			for(int j = 0; j < Constants.WORLD_SIZE_V.y; j++) {
				
				if ((i + j + timeStep) % updateFrequency != 0) {
					continue;
				}
				height[i][j] *= TRUE_DECAY;
			}
		}
	}
	
	public void getColor(int x, int y, float[] temp) {
		for (int c = 0; c < 3; ++c) {
			temp[c] = Math.min(height[x][y], 1f)*color[c];
		}
	}

	public float harvest(float harvest, int x, int y) {
		float old = height[x][y];
		height[x][y] -= harvest;
		if (height[x][y] < 0) {
			height[x][y] = 0;
		}
		return old - height[x][y];
	}

	public void reset() {
		for (int i = 0; i < height.length; ++i) {
			for (int j = 0; j < height[i].length; ++j) {
				height[i][j] = 0;
			}
		}
	}
}

