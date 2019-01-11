package world;

import simulation.Simulation;
import constants.Constants;

public class Grass extends TileElement {

	public boolean[][] growing;
	
	public Tree tree;
	private Terrain terrain;

	public Grass(Terrain terrain) {
		height = new float[Simulation.WORLD_SIZE_X][Simulation.WORLD_SIZE_Y];
		growing = new boolean[Simulation.WORLD_SIZE_X][Simulation.WORLD_SIZE_Y];
		color = Constants.Colors.GRASS;
		tree = new Tree(terrain, this);
		this.terrain = terrain;
		regenerate(true);
	}

	public void grow(int timeStep, int updateFrequency) {
		for(int i = 0; i < Simulation.WORLD_SIZE_X; i++) {
			for(int j = 0; j < Simulation.WORLD_SIZE_Y; j++) {
				if ((i + j + timeStep) % updateFrequency != 0) {
					continue;
				}
				if (growing[i][j]) {
					height[i][j] += Constants.GROWTH * terrain.growth[i][j] * updateFrequency;
					if (height[i][j] > terrain.growth[i][j]) {
						growing[i][j] = false;
						height[i][j] = terrain.growth[i][j];
					}
				}
			}
		}
		tree.update();
	}
	
	public float getHealth(int x, int y) {
		if (!growing[x][y]) return 1f;
		if (height[x][y] < 0) System.err.println("Grass height is negative.");
		
		return height[x][y] / terrain.growth[x][y];
	}

	public void regenerate(boolean isFullyGrown) {
		tree.killAll();
		for (int x = 0; x < Simulation.WORLD_SIZE_X; ++x) {
			for (int y = 0; y < Simulation.WORLD_SIZE_Y; ++y) {
				if (terrain.stone[x][y] || terrain.water[x][y]) {
					growing[x][y] = false;
					continue;
				}
				if (isFullyGrown) {
					this.height[x][y] = terrain.growth[x][y];
					growing[x][y] = false;
				}
				else {
					this.height[x][y] = 0;
					growing[x][y] = true;
				}
			}
		}
	}

	public void killAllGrass() {
		tree.killAll();
		for (int x = 0; x < Simulation.WORLD_SIZE_X; ++x) {
			for (int y = 0; y < Simulation.WORLD_SIZE_Y; ++y) {
				
				if (terrain.stone[x][y] || terrain.water[x][y]) {
					growing[x][y] = false;
				}
				else {
					this.height[x][y] = 0f;
					growing[x][y] = true;
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
		growing[x][y] = true;
		return old - height[x][y];
	}

	public double getTotalHeight() {
		double heightTot = 0;
		for (int x = 0; x < Simulation.WORLD_SIZE_X; ++x) {
			for (int y = 0; y < Simulation.WORLD_SIZE_Y; ++y) {
				heightTot += height[x][y];
			}
		}
		return heightTot;
	}

}
