package world;

import constants.Constants;

public class Grass {

	public float[] height;
	public boolean[] toBeUpdated;
	public float[] color;
	
	public static final boolean GRASS_MAX_HEIGHT_EQUAL_TO_TERRAIN_HEIGHT = true;
	
	public Grass() {
		height = new float[Constants.WORLD_SIZE];
		toBeUpdated = new boolean[Constants.WORLD_SIZE];
		color = Constants.Colors.GRASS;
	}

	public static final boolean USE_DESSERT = true;
	public void grow(int updateFrequency) {
		for(int i = 0; i < Constants.WORLD_SIZE; ++i) {
			if (toBeUpdated[i]) {
				if (GRASS_MAX_HEIGHT_EQUAL_TO_TERRAIN_HEIGHT) {
					if (!USE_DESSERT || World.terrain.height[i] > 0.3f) {
						height[i] += Constants.GROWTH * World.terrain.height[i] * updateFrequency;
						if (height[i] > World.terrain.height[i]) {
							toBeUpdated[i] = false;
							height[i] = World.terrain.height[i];
						}
					}
				}
				else {
					height[i] += Constants.GROWTH * updateFrequency;
					if (height[i] > 1f) {
						toBeUpdated[i] = false;
						height[i] = 1f;
					}
				}
			}
		}
	}

	public void regenerate() {
		if (GRASS_MAX_HEIGHT_EQUAL_TO_TERRAIN_HEIGHT) {
			for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
				this.height[i] = World.terrain.height[i];
				toBeUpdated[i] = false;
			}
		}
		else {
			for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
				this.height[i] = 1;
				toBeUpdated[i] = false;
			}
		}
	}

	public void killAllGrass() {
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			this.height[i] = 0f;
			toBeUpdated[i] = true;
		}
	}

	public float harvest(float grassHarvest, int pos) {
		float old = height[pos];
		height[pos] -= grassHarvest;
		if (height[pos] < 0) {
			height[pos] = 0;
		}
		toBeUpdated[pos] = true;
		return old - height[pos];
	}

	public double getTotalHeight() {
		double heightTot = 0;
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			heightTot += height[i];
		}
		return heightTot;
	}
}
