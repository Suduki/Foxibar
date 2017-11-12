package world;

import constants.Constants;

public class Blood {
	public float[] height;
	public final float[] color;
	
	public Blood() {
		this.height = new float[Constants.WORLD_SIZE];
		this.color = Constants.Colors.BLOOD;
	}
	
	public void append(int pos, float factor) {
		height[pos] += factor*Constants.Blood.ADDITION_ON_DEATH;
		for (short dir = 0; dir < 4; ++dir) {
			height[World.neighbour[dir][pos]] += factor*Constants.Blood.SPLASH*Constants.Blood.ADDITION_ON_DEATH;
			if (height[World.neighbour[dir][pos]] > 1f) {
				height[World.neighbour[dir][pos]] = 1f;
			}
		}
	}

	private static final float TRUE_DECAY = (float) Math.pow(Constants.Blood.DECAY_FACTOR, World.UPDATE_FREQUENCY);
	private static final float TRUE_DECAY_WATER = (float) Math.pow(Constants.Blood.DECAY_FACTOR_WATER, World.UPDATE_FREQUENCY);
	private static final float TRUE_DECAY_STONE = (float) Math.pow(Constants.Blood.DECAY_FACTOR_STONE, World.UPDATE_FREQUENCY);
	
	public void decay(int timeStep, int updateFrequency) {
		for(int i = timeStep%updateFrequency; i < Constants.WORLD_SIZE; i+=updateFrequency) {
			if (World.terrain.water[i]) {
				height[i] *= TRUE_DECAY_WATER;
			}
			else if (World.terrain.stone[i]) {
				height[i] *= TRUE_DECAY_STONE;
			}
			else {
				height[i] *= TRUE_DECAY;
			}
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
