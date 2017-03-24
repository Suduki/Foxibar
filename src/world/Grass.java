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

	public void grow() {
		for(int i = 0; i < Constants.WORLD_SIZE; ++i) {
			if (toBeUpdated[i]) {
				if (GRASS_MAX_HEIGHT_EQUAL_TO_TERRAIN_HEIGHT) {
					height[i] += Constants.GROWTH * World.terrain.height[i];
					if (height[i] > World.terrain.height[i]) {
						toBeUpdated[i] = false;
					}
				}
				else {
					height[i] += Constants.GROWTH;
					if (height[i] > 1f) {
						toBeUpdated[i] = false;
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
}
