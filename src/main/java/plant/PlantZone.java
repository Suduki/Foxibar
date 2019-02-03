package plant;

import java.util.ArrayList;

import constants.Constants;
import agents.Animal;
import vision.Vision;

public class PlantZone {
	static final int zoneSizeX = 8;
	static final int zoneSizeY = 8;
	static final int MAX_NUM_TREES_IN_ZONE = zoneSizeX * zoneSizeY;
	
	private Tree[] treePool = new Tree[MAX_NUM_TREES_IN_ZONE];
	
	public ArrayList<Tree> alive = new ArrayList<>(); //TODO: superclass
	public ArrayList<Tree> dead = new ArrayList<>();
	public ArrayList<Tree> toDie = new ArrayList<>();
	public ArrayList<Tree> toLive = new ArrayList<>();
	public boolean killAll;
	int numTrees;
	
	private Vision vision;
	private int x0, y0;
	
	public PlantZone(Vision vision, int startX, int startY) {
		this.vision = vision;
		x0 = startX * zoneSizeX;
		y0 = startY * zoneSizeY;
		
		for (int i = 0; i < MAX_NUM_TREES_IN_ZONE; ++i) {
			treePool[i] = new Tree();
			dead.add(treePool[i]);
		}
	}
	
	public void update() {
		if (numTrees == 0) {
			// Do nothing
		}
		else if (killAll) { //TODO: superclass
			for (Tree a : alive) {
				a.die();
				someoneDied(a);
			}
			if (numTrees != 0) {
				System.err.println("numTrees " + numTrees + ", should be 0 after killing all");
			}
			numTrees = 0;
		}
		else {
			for (Tree a : alive) {
				if (a.stepAgent()) {
					// All is well
				}
				else {
					someoneDied(a);
				}
			}
		}

		killAll = false;
	}

	private void someoneDied(Tree a) { //TODO: superclass
		numTrees--;
		toDie.add(a);
		
		vision.removeTreeFromZone(a);
	}
	
	private Tree findFirstAvailablePoolSpot() { //TODO: superclass
		if (dead.size() == 0) {
			System.err.println("Dead pool is empty");
			return null;
		}
		Tree next = dead.get(0);
		dead.remove(next);
		toLive.add(next);
		return next;
	}
	
	public Tree resurrectTree() { //TODO: superclass
		Tree id = findFirstAvailablePoolSpot();

		if (id == null) {
			System.err.println("did not find pool spot.");
			return null;
		}

		id.reset();

		numTrees++;

		return id;
	}

	public void spawnTree() {
		Tree sapling = resurrectTree();
		float xPos = x0 + Constants.RANDOM.nextFloat() * zoneSizeX;
		float yPos = y0 + Constants.RANDOM.nextFloat() * zoneSizeY;
		sapling.resetPos(xPos, yPos);
		
		vision.addTreeToZone(sapling);
	}
	
	public void synchAliveDead() { //TODO: superclass
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
