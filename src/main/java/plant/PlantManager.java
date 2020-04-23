package plant;

import java.util.ArrayList;

import constants.Constants;
import simulation.Simulation;
import vision.Vision;
import world.Terrain;

public class PlantManager {
	static int MAX_NUM_TREES;

	private Plant[] pool;

	public ArrayList<Plant> alive = new ArrayList<>(); // TODO: superclass
	public ArrayList<Plant> dead = new ArrayList<>();
	public ArrayList<Plant> toDie = new ArrayList<>();
	public ArrayList<Plant> toLive = new ArrayList<>();
	public boolean killAll;
	int numAlive;

	private Vision vision;
	private Terrain terrain;

	public PlantManager(Vision vision, Terrain terrain) {
		this.vision = vision;
		this.terrain = terrain;

		MAX_NUM_TREES = Integer.min(Simulation.WORLD_SIZE, 10000);

		pool = new Plant[MAX_NUM_TREES];
		for (int i = 0; i < MAX_NUM_TREES; ++i) {
			pool[i] = new Plant();
			dead.add(pool[i]);
		}
	}

	public void update() {
		if (numAlive == 0) {
			// Do nothing
		} else if (killAll) { // TODO: superclass
			for (Plant a : alive) {
				a.die();
				someoneDied(a);
			}
			if (numAlive != 0) {
				System.err.println("numTrees " + numAlive + ", should be 0 after killing all");
			}
			numAlive = 0;
		} else {
			for (Plant a : alive) {
				if (a.stepAgent()) {
					// All is well
				} else {
					someoneDied(a);
				}
			}
		}

		killAll = false;
	}

	private void someoneDied(Plant a) { // TODO: superclass
		numAlive--;
		toDie.add(a);

		vision.removeTreeFromZone(a);
	}

	private Plant findFirstAvailablePoolSpot() { // TODO: superclass
		if (dead.size() == 0) {
			System.err.println("Dead pool is empty");
			return null;
		}
		Plant next = dead.get(0);
		dead.remove(next);
		toLive.add(next);
		return next;
	}

	private Plant resurrect() { // TODO: superclass
		Plant id = findFirstAvailablePoolSpot();

		if (id == null) {
			System.err.println("did not find pool spot.");
			return null;
		}

		id.reset();

		numAlive++;

		return id;
	}
	
	
	private float seeds = 0;
	public void spreadSeed() {
		float luck = Plant.WANTED_AVERAGE_AMOUNT_OF_PLANTS() / Plant.MAX_AGE;
		seeds += luck;
		
		while (seeds > 0) {
			spawn();
			seeds --;
		}
	}

	public Plant spawn() {
		Plant sapling = resurrect();
		if (sapling != null) {
			float xPos = Constants.RANDOM.nextFloat() * Simulation.WORLD_SIZE_X;
			float yPos = Constants.RANDOM.nextFloat() * Simulation.WORLD_SIZE_X;

			sapling.resetPos(xPos, yPos);
			sapling.setGroundGrowth(terrain.growth[(int) xPos][(int) yPos]);
		}
		return sapling;
	}

	public void synchAliveDead() { // TODO: superclass
		// Remove all dead agents from loop
		for (Plant a : toDie) {
			alive.remove(a);
			dead.add(a);
		}
		toDie.clear();

		// Add all newborn agents to loop
		for (Plant a : toLive) {
			alive.add(a);
			vision.addTreeToZone(a);
		}
		toLive.clear();
	}
}
