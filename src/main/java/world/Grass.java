package world;

import constants.Constants;

public class Grass {

	public float[] height;
	public boolean[] toBeUpdated;
	public float[] color;
	public Tree tree;
	
	public Grass() {
		height = new float[Constants.WORLD_SIZE];
		toBeUpdated = new boolean[Constants.WORLD_SIZE];
		color = Constants.Colors.GRASS;
		tree = new Tree();
	}

	public void grow(int timeStep, int updateFrequency) {
//		if (World.air.getCarbon() < Constants.GROWTH * Constants.WORLD_SIZE) {
////			System.out.println("World.air.getCarbon()=" + World.air.getCarbon());
//			return;
//		}
		for(int i = timeStep%updateFrequency; i < Constants.WORLD_SIZE; i+=updateFrequency) {
//			float totalGrowth = 0; 
			if (toBeUpdated[i]) {
//				totalGrowth -= height[i];
				height[i] += Constants.GROWTH * World.terrain.growth[i] * updateFrequency;
				if (height[i] > World.terrain.growth[i]) {
					toBeUpdated[i] = false;
					height[i] = World.terrain.growth[i];
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
			if (World.terrain.stone[i] || World.terrain.water[i]) {
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
		if (tree.isAlive[pos]) {
			System.err.println("Trying to harvest a tree?");
			return 0;
		}
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
