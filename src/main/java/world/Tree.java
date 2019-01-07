package world;

import java.util.Random;

import simulation.Simulation;
import constants.Constants;

public class Tree {

	public static final Random RANDOM = new Random();
	private static final float growth = 0.05f;
	public float[][] height;
	public float[][] health;
	public boolean[][] isAlive;
	public boolean[][] isDamaged;
	
	public Grass grass;
	private float spawnRate = 1f; // TODO: Replace with i++
	private int numTrees = 0;
	
	private Terrain terrain;
	
	public Tree(Terrain terrain, Grass grass) {
		height = new float[Simulation.WORLD_SIZE_X][Simulation.WORLD_SIZE_Y];
		health = new float[Simulation.WORLD_SIZE_X][Simulation.WORLD_SIZE_Y];
		isAlive = new boolean[Simulation.WORLD_SIZE_X][Simulation.WORLD_SIZE_Y];
		isDamaged = new boolean[Simulation.WORLD_SIZE_X][Simulation.WORLD_SIZE_Y];
		this.terrain = terrain;
		this.grass = grass;
	}
	
	public void update() {
		for (int x = 0 ; x < Simulation.WORLD_SIZE_X; ++x) {
			for (int y = 0 ; y < Simulation.WORLD_SIZE_Y; ++y) {
				if (isAlive[x][y]) {
					
					if (grass.toBeUpdated[x][y]) {
						isDamaged[x][y] = true;
						health[x][y] -= 100;
					}
					else {
						isDamaged[x][y] = false;
						height[x][y] += growth * terrain.growth[x][y];
						height[x][y] *= 0.996f;
					}

					if (health[x][y] < 0) {
						die(x, y);
					}
				}
			}
		}
		if (RANDOM.nextFloat() < spawnRate/numTrees) {
			int x = RANDOM.nextInt(Simulation.WORLD_SIZE_X);
			int y = RANDOM.nextInt(Simulation.WORLD_SIZE_Y);
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

		for (int x = 0 ; x < Simulation.WORLD_SIZE_X; ++x) {
			for (int y = 0 ; y < Simulation.WORLD_SIZE_Y; ++y) {
				die(x, y);
			}
		}
	}


}
