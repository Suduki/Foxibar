package world;

import constants.Constants;

public class Tree {

	private static final float growth = 0.05f;
	public float[][] height;
	public float[][] health;
	public boolean[][] isAlive;
	public Grass grass;
	private float spawnRate = 1f; // TODO: Replace with i++
	private int numTrees = 0;
	
	private Terrain terrain;
	
	public Tree(Terrain terrain, Grass grass) {
		height = new float[Constants.WORLD_SIZE_V.x][Constants.WORLD_SIZE_V.y];
		health = new float[Constants.WORLD_SIZE_V.x][Constants.WORLD_SIZE_V.y];
		isAlive = new boolean[Constants.WORLD_SIZE_V.x][Constants.WORLD_SIZE_V.y];
		this.terrain = terrain;
		this.grass = grass;
	}
	
	public void update() {
		for (int x = 0 ; x < Constants.WORLD_SIZE_V.x; ++x) {
			for (int y = 0 ; y < Constants.WORLD_SIZE_V.y; ++y) {
				if (isAlive[x][y]) {
					
					if (grass.toBeUpdated[x][y]) {
						health[x][y] -= 100;
					}
					else {
						height[x][y] += growth * terrain.growth[x][y];
						height[x][y] *= 0.996f;
					}

					if (health[x][y] < 0) {
						die(x, y);
					}
				}
			}
		}
		if (Constants.RANDOM.nextFloat() < spawnRate/numTrees) {
			int x = Constants.RANDOM.nextInt(Constants.WORLD_SIZE_V.x);
			int y = Constants.RANDOM.nextInt(Constants.WORLD_SIZE_V.y);
			if (!isAlive[x][y] && !terrain.water[x][y] && !terrain.stone[x][y]) {
				resurrect(x, y);
			}
		}
	}
	
	private void die(int x, int y) {
		if (isAlive[x][y]) {
			height[x][y] = 0;
			health[x][y] = 0;
			numTrees --;
		}
		isAlive[x][y] = false;
	}

	public void resurrect(int x, int y) {
		isAlive[x][y] = true;
		height[x][y] = 1;
		health[x][y] = 100000;
		numTrees  ++;
	}

	public void killAll() {

		for (int x = 0 ; x < Constants.WORLD_SIZE_V.x; ++x) {
			for (int y = 0 ; y < Constants.WORLD_SIZE_V.y; ++y) {
				die(x, y);
			}
		}
	}


}
