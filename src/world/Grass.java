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

	private static final float WATER_LIMIT = 0.8f;
	public void grow(int timeStep, int updateFrequency) {
		for(int i = timeStep%updateFrequency; i < Constants.WORLD_SIZE; i+=updateFrequency) {
			if (toBeUpdated[i]) {
				if (GRASS_MAX_HEIGHT_EQUAL_TO_TERRAIN_HEIGHT) {
					height[i] += Constants.GROWTH * World.terrain.growth[i] * updateFrequency;
					if (height[i] > World.terrain.growth[i]) {
						toBeUpdated[i] = false;
						height[i] = World.terrain.growth[i];
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
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			if (World.terrain.stone[i] || World.terrain.water[i]) {
				this.height[i] = 0;
				toBeUpdated[i] = false;
				continue;
			}
			if (GRASS_MAX_HEIGHT_EQUAL_TO_TERRAIN_HEIGHT) {
				this.height[i] = World.terrain.growth[i];
			}
			else {
				this.height[i] = 1;
			}
			toBeUpdated[i] = false;
		}
	}

	public void killAllGrass() {
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			if (World.terrain.stone[i] || World.terrain.water[i]) {
				toBeUpdated[i] = false;
			}
			else {
				this.height[i] = 0f;
				toBeUpdated[i] = true;
			}
		}
	}

	public float harvest(float grassHarvest, int pos) {
		if (World.terrain.water[pos] || World.terrain.stone[pos]) {
			return 0;
		}
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
