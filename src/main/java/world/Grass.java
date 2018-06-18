package world;

import constants.Constants;

public class Grass {

	public float[] height;
	public boolean[] toBeUpdated;
	public float[] color;
	public Tree tree;
	private Terrain terrain;

	public Grass(Terrain terrain) {
		height = new float[Constants.WORLD_SIZE];
		toBeUpdated = new boolean[Constants.WORLD_SIZE];
		color = Constants.Colors.GRASS;
		tree = new Tree(terrain);
		this.terrain = terrain;
	}

	public void grow(int timeStep, int updateFrequency) {
//		if (World.air.getCarbon() < Constants.GROWTH * Constants.WORLD_SIZE) { //TODO: Carbon stuff
////			System.out.println("World.air.getCarbon()=" + World.air.getCarbon());
//			return;
//		}
		for(int i = timeStep%updateFrequency; i < Constants.WORLD_SIZE; i+=updateFrequency) {
//			float totalGrowth = 0; 
			if (toBeUpdated[i]) {
//				totalGrowth -= height[i];
				height[i] += Constants.GROWTH * terrain.growth[i] * updateFrequency;
				if (height[i] > terrain.growth[i]) {
					toBeUpdated[i] = false;
					height[i] = terrain.growth[i];
				}
//				totalGrowth += height[i];
			}
//			World.air.harvest(totalGrowth);
		}
		tree.update();
	}

	public void regenerate() {
		tree.killAll();
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			if (terrain.stone[i] || terrain.water[i]) {
				toBeUpdated[i] = false;
				continue;
			}
			this.height[i] = 0;
			toBeUpdated[i] = true;
		}
	}

	public void killAllGrass() {
		tree.killAll();
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			if (terrain.stone[i] || terrain.water[i]) {
				toBeUpdated[i] = false;
			}
			else {
				this.height[i] = 0f;
				toBeUpdated[i] = true;
			}
		}
	}

	public float harvest(float grassHarvest, int pos) {
		if (terrain.water[pos] || terrain.stone[pos]) {
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
