package world;

import constants.Constants;

public class Grass extends TileElement {

	public boolean[][] toBeUpdated;
	
	public Tree tree;
	private Terrain terrain;

	public Grass(Terrain terrain) {
		height = new float[Constants.WORLD_SIZE_V.x][Constants.WORLD_SIZE_V.y];
		toBeUpdated = new boolean[Constants.WORLD_SIZE_V.x][Constants.WORLD_SIZE_V.y];
		color = Constants.Colors.GRASS;
		tree = new Tree(terrain);
		this.terrain = terrain;
		regenerate(true);
	}

	public void grow(int timeStep, int updateFrequency) {
		for(int i = 0; i < Constants.WORLD_SIZE_V.x; i++) {
			for(int j = 0; j < Constants.WORLD_SIZE_V.y; j++) {
				if ((i + j + timeStep) % updateFrequency != 0) {
					continue;
				}
				if (toBeUpdated[i][j]) {
					height[i][j] += Constants.GROWTH * terrain.growth[i][j] * updateFrequency;
					if (height[i][j] > terrain.growth[i][j]) {
						toBeUpdated[i][j] = false;
						height[i][j] = terrain.growth[i][j];
					}
				}
			}
		}
		tree.update();
	}

	public void regenerate(boolean fullyGrown) {
		tree.killAll();
		for (int x = 0; x < Constants.WORLD_SIZE_V.x; ++x) {
			for (int y = 0; y < Constants.WORLD_SIZE_V.y; ++y) {
				if (terrain.stone[x][y] || terrain.water[x][y]) {
					toBeUpdated[x][y] = false;
					continue;
				}
				if (fullyGrown) {
					this.height[x][y] = terrain.growth[x][y];
					toBeUpdated[x][y] = false;
				}
				else {
					this.height[x][y] = 0;
					toBeUpdated[x][y] = true;
				}
			}
		}
	}

	public void killAllGrass() {
		tree.killAll();
		for (int x = 0; x < Constants.WORLD_SIZE_V.x; ++x) {
			for (int y = 0; y < Constants.WORLD_SIZE_V.y; ++y) {
				
				if (terrain.stone[x][y] || terrain.water[x][y]) {
					toBeUpdated[x][y] = false;
				}
				else {
					this.height[x][y] = 0f;
					toBeUpdated[x][y] = true;
				}
			}
		}
	}

	public float harvest(float grassHarvest, int x, int y) {
		if (terrain.water[x][y] || terrain.stone[x][y]) {
			return 0;
		}
		float old = height[x][y];
		height[x][y] -= grassHarvest;
		if (height[x][y] < 0) {
			height[x][y] = 0;
		}
		toBeUpdated[x][y] = true;
		return old - height[x][y];
	}

	public double getTotalHeight() {
		double heightTot = 0;
		for (int x = 0; x < Constants.WORLD_SIZE_V.x; ++x) {
			for (int y = 0; y < Constants.WORLD_SIZE_V.y; ++y) {
				heightTot += height[x][y];
			}
		}
		return heightTot;
	}

}
