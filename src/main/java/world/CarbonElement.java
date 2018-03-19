package world;

import constants.Constants;

public class CarbonElement {
	public float[] height;
	public float[] color;
	
	public float maxHeight, splash, decayFactor;
	
	public CarbonElement(float maxHeight, float[] color, float splash, float decayFactor) {
		this.height = new float[Constants.WORLD_SIZE];
		this.color = color;
		this.maxHeight = maxHeight;
		this.splash = splash;
		this.decayFactor = decayFactor;
	}
	
	public void append(int pos, float amount) {
		height[pos] += amount;
		for (short dir = 0; dir < 4; ++dir) {
			height[World.neighbour[dir][pos]] += amount * splash;
			if (height[World.neighbour[dir][pos]] > 1f) {
				height[World.neighbour[dir][pos]] = 1f;
			}
		}
	}

	private static final float TRUE_DECAY = (float) Math.pow(Constants.Blood.DECAY_FACTOR, World.UPDATE_FREQUENCY);
	public void decay(int timeStep, int updateFrequency) {
		
		for(int i = timeStep%updateFrequency; i < Constants.WORLD_SIZE; i+=updateFrequency) {
			float oldH = height[i];
			height[i] *= TRUE_DECAY;
			World.air.addCarbon(oldH - height[i]);
		}
	}
	
	public void getColor(int pos, float[] temp) {
		for (int c = 0; c < 3; ++c) {
			temp[c] = Math.min(height[pos], 1f)*color[c];
		}
	}

	public float harvest(float harvest, int pos) {
		float old = height[pos];
		height[pos] -= harvest;
		if (height[pos] < 0) {
			height[pos] = 0;
		}
		return old - height[pos];
	}
}

