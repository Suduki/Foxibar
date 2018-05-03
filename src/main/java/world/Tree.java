package world;

import java.util.ArrayList;

import constants.Constants;

public class Tree {

	private static final float growth = 0.05f;
	public float[] height;
	public float[] health;
	public boolean[] isAlive;
	private float spawnRate = 1f; // TODO: Replace with i++
	private int numTrees = 0;
	
	public Tree() {
		height = new float[Constants.WORLD_SIZE];
		health = new float[Constants.WORLD_SIZE];
		isAlive = new boolean[Constants.WORLD_SIZE];
	}
	
	public void update() {
		for (int pos = 0 ; pos < Constants.WORLD_SIZE; ++pos) {
			if (isAlive[pos]) {
				height[pos] += growth;
				height[pos] *= 0.996f;
				health[pos] -= 1;

				if (health[pos] < 0) {
					die(pos);
				}
			}
		}
		if (Constants.RANDOM.nextFloat() < spawnRate/numTrees) {
			int pos = Constants.RANDOM.nextInt(Constants.WORLD_SIZE);
			if (!isAlive[pos] && !World.terrain.water[pos] && !World.terrain.stone[pos]) {
				resurrect(pos);
			}
		}
	}
	
	private void die(int pos) {
		if (isAlive[pos]) {
			height[pos] = 0;
			health[pos] = 0;
			numTrees --;
		}
		isAlive[pos] = false;
	}

	public void resurrect(int pos) {
		isAlive[pos] = true;
		height[pos] = 1;
		health[pos] = 100000;
		numTrees  ++;
	}

	public void killAll() {
		for (int pos = 0; pos < Constants.WORLD_SIZE; ++pos) {
			die(pos);
		}
	}


}
