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
		height[pos] = factor*Constants.Blood.ADDITION_ON_DEATH;
		for (short dir = 0; dir < 4; ++dir) {
			height[World.neighbour[dir][pos]] += factor*Constants.Blood.SPREAD;
			if (height[World.neighbour[dir][pos]] > 1f) {
				height[World.neighbour[dir][pos]] = 1f;
			}
		}
	}

	public void decay() {
		for(int i = 0; i < Constants.WORLD_SIZE; ++i) {
			height[i] *= Constants.Blood.DECAY_FACTOR;
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
