package plant;

import java.util.ArrayList;

import constants.Constants;
import simulation.Simulation;
import agents.Animal;
import vision.Vision;
import world.World;

public class PlantManager {
	static int MAX_NUM_TREES;

	private Tree[] treePool;

	public ArrayList<Tree> alive = new ArrayList<>(); // TODO: superclass
	public ArrayList<Tree> dead = new ArrayList<>();
	public ArrayList<Tree> toDie = new ArrayList<>();
	public ArrayList<Tree> toLive = new ArrayList<>();
	public boolean killAll;
	int numTrees;

	private Vision vision;

	public PlantManager(Vision vision) {
		this.vision = vision;

		MAX_NUM_TREES = Integer.min(Simulation.WORLD_SIZE, 1000);

		treePool = new Tree[MAX_NUM_TREES];
		for (int i = 0; i < MAX_NUM_TREES; ++i) {
			treePool[i] = new Tree();
			dead.add(treePool[i]);
		}
	}

	public void update() {
		if (numTrees == 0) {
			// Do nothing
		} else if (killAll) { // TODO: superclass
			for (Tree a : alive) {
				a.die();
				someoneDied(a);
			}
			if (numTrees != 0) {
				System.err.println("numTrees " + numTrees + ", should be 0 after killing all");
			}
			numTrees = 0;
		} else {
			for (Tree a : alive) {
				if (a.stepAgent()) {
					// All is well
				} else {
					someoneDied(a);
				}
			}
		}

		killAll = false;
	}

	private void someoneDied(Tree a) { // TODO: superclass
		numTrees--;
		toDie.add(a);

		vision.removeTreeFromZone(a);
	}

	private Tree findFirstAvailablePoolSpot() { // TODO: superclass
		if (dead.size() == 0) {
			System.err.println("Dead pool is empty");
			return null;
		}
		Tree next = dead.get(0);
		dead.remove(next);
		toLive.add(next);
		return next;
	}

	private Tree resurrectTree() { // TODO: superclass
		Tree id = findFirstAvailablePoolSpot();

		if (id == null) {
			System.err.println("did not find pool spot.");
			return null;
		}

		id.reset();

		numTrees++;

		return id;
	}

	public Tree spawnTree() {
		Tree sapling = resurrectTree();
		if (sapling != null) {
			float xPos = Constants.RANDOM.nextFloat() * Simulation.WORLD_SIZE_X;
			float yPos = Constants.RANDOM.nextFloat() * Simulation.WORLD_SIZE_X;

			sapling.resetPos(xPos, yPos);
		}
		return sapling;
	}

	public void synchAliveDead() { // TODO: superclass
		// Remove all dead agents from loop
		for (Tree a : toDie) {
			alive.remove(a);
			dead.add(a);
		}
		toDie.clear();

		// Add all newborn agents to loop
		for (Tree a : toLive) {
			alive.add(a);
			vision.addTreeToZone(a);
		}
		toLive.clear();
	}
}
