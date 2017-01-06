package world;

import constants.Constants;

public class Blood {
	public float[] height;
	public float[] color;
	
	public Blood() {
		this.height = new float[Constants.WORLD_SIZE];
		this.color = Constants.Colors.BLOOD;
	}
	
	public void append(int pos) {
		height[pos] += Constants.Blood.ADDITION_ON_DEATH;
	}
}
