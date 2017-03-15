package agents;

import java.util.ArrayList;
import java.util.Collections;

import vision.Vision;
import world.World;
import constants.Constants;
import constants.Constants.Neighbours;
import static constants.Constants.Neighbours.*;

public class Animal {
	
	private int age = 0;
	private int sinceLastBaby = 0;
	private int id;
	public float size = 3f;
	public float[] color;
	public int pos;
	public boolean isAlive;
	private float hunger;
	public int[] nearbyAnimals;
	public int[] nearbyAnimalsDistance;
	
	private float recover = 0f;
	
	private enum Activity {
		DO_NOTHING, HARVEST_GRASS, HARVEST_BLOOD, MATE, FIGHT;
		
		public final static int GRASS = 0;
		public final static int BLOOD = 1;
		public final static int FERTILE = 2;
		public final static int DANGER = 3;
	};
	
	//************ GENETIC STATS ************
	private Skill skill;
	private boolean isFertile;
	private int timeBetweenBabies = 50;
	
	
	//************ STATIC STUFF ************
	public static Animal[] pool = new Animal[Constants.MAX_NUM_ANIMALS];
	public static int numAnimals = 0;
	public static int[] containsAnimals;
	public static boolean killAll = false;
	
	public static void moveAll() {
		if (killAll) {
			for (Animal a : pool) {
				a.die();
			}
			for (int i = 0; i < containsAnimals.length; ++i) {
				containsAnimals[i] = -1;
			}
			System.out.println("Num animals alive after killing them all: " + numAnimals);
			numAnimals = 0;
			killAll = false;
		}
		for (Animal a : pool) {
			if (a.isAlive) {
				a.age++;
				a.sinceLastBaby++;
				a.recover += a.skill.speed;
				if (a.recover > 1f) {
					a.recover--;
					a.move();
				}
				if (!a.isFertile && a.sinceLastBaby > a.timeBetweenBabies) {
					a.isFertile = true;
				}
			}
		}
	}
	
	public static void init() {
		containsAnimals = new int[Constants.WORLD_SIZE];
		
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			containsAnimals[i] = -1;
		}
		
		for(int i = 0; i < Constants.MAX_NUM_ANIMALS; ++i) {
			pool[i] = new Animal();
		}
	}
	
	public static int resurrectAnimal(int pos, float g, float b, float hunger) {
		int id = 0;
		while (pool[id].isAlive) {
			id++;
			if (id == Constants.MAX_NUM_ANIMALS) {
				System.err.println("MAX_NUM_ANIMALS reached. Pool full.");
				return -1;
			}
		}
		pool[id].isAlive = true;
		
		for (int i = 0; i < pool[id].nearbyAnimals.length; ++i) {
			pool[id].nearbyAnimals[i] = -1;
		}
		
//		pool[id].skill.fight = Constants.RANDOM.nextFloat();
		if (true || Constants.RANDOM.nextBoolean()) { // TODO: This is quick hack fix yolo (2)
			pool[id].skill.inherit(Constants.Skill.GRASSLER);
		}
		else {
			pool[id].skill.inherit(Constants.Skill.BLOODLING);
		}
		
		pool[id].color[0] = pool[id].skill.bloodDigestion*pool[id].skill.bloodHarvest;
		pool[id].color[1] = pool[id].skill.grassDigestion*pool[id].skill.grassHarvest;
		pool[id].color[2] = 0;
		
		
		pool[id].pos = pos;
		pool[id].id = id;
		pool[id].age = 0;
		pool[id].sinceLastBaby = 0;
		pool[id].recover = 0f;
		pool[id].hunger = hunger;
		
		numAnimals++;
		containsAnimals[pool[id].pos] = id;
		
		return id;
	}
	
// ************ INSTANCE STUFF ************
	private Animal() {
		this.isAlive = false;
		this.color = new float[3];
		this.skill = new Skill();
		this.nearbyAnimals = new int[Constants.NUM_NEIGHBOURS];
		this.nearbyAnimalsDistance = new int[Constants.NUM_NEIGHBOURS];
	}
	private void move() {

		// Remove animal from the world temporarily :F
		containsAnimals[pos] = -1;
		
		// Calculate to where we want to move
		short[] bestDir = new short[1];
		Activity[] bestChoice = new Activity[1];
		int[] animalIdToInteractWith = new int[1];
		if (findBestDir(bestDir, bestChoice, animalIdToInteractWith)) {
			moveTo(bestDir[0]);
			switch (bestChoice[0]) {
			case HARVEST_BLOOD:
				harvestBlood();
				break;
			case HARVEST_GRASS:
				harvestGrass();
				break;
			case MATE:
				interactWith(animalIdToInteractWith[0]);
				break;
			default:
				System.out.println("ERROR: invalid choice!");
				break;
			}
		}
		else {
			moveRandom();
		}
		
		// Add animal to the world again :)
		containsAnimals[pos] = id;
		
		Vision.updateNearestNeighbours(id);
		
		hunger--;
		if (hunger < 0) {
			die();
		}
		
	}
	private boolean findBestDir(short[] bestDir, Activity[] bestChoice, int[] animalIdToInteractWith) {
		animalIdToInteractWith[0] = -1;
		bestDir[0] = Constants.Neighbours.NORTH;
		bestChoice[0] = Activity.HARVEST_GRASS;
		
		float[] nodeGoodness = new float[5];
		for (float f : nodeGoodness) {
			f = 0;
		}
		
		for (int nearbyAnimalId : nearbyAnimals) {
			if (nearbyAnimalId == -1) {
				continue;
			}
			int xNeigh = pool[nearbyAnimalId].pos / Constants.WORLD_SIZE_X;
			int yNeigh = pool[nearbyAnimalId].pos % Constants.WORLD_SIZE_X;
			
			for (int nodeNeighbour = 0; nodeNeighbour < 5; ++nodeNeighbour) {
				int x = World.neighbour[nodeNeighbour][pos] / Constants.WORLD_SIZE_X;
				int y = World.neighbour[nodeNeighbour][pos] % Constants.WORLD_SIZE_X;
				int distance = Math.abs(xNeigh-x) + Math.abs(yNeigh - y) + 1;
				if (isFertileWith(nearbyAnimalId)) {
					nodeGoodness[nodeNeighbour] += 1f/distance*1000;
					if (distance == 1) {
						bestChoice[0] = Activity.MATE;
						animalIdToInteractWith[0] = nearbyAnimalId;
					}
				}
				else {
					nodeGoodness[nodeNeighbour] -= 10f/distance;
				}
			}
		}
		for (int nodeNeighbour = 0; nodeNeighbour < 5; ++nodeNeighbour) {
			nodeGoodness[nodeNeighbour] += World.grass.height[World.neighbour[nodeNeighbour][pos]];
		}
		
		bestDir[0] = max(nodeGoodness);
		return true;
	}
	
	private boolean isFertileWith(int nearbyAnimalId) {
		return skill.speciesId == pool[nearbyAnimalId].skill.speciesId && isFertile() && pool[nearbyAnimalId].isFertile();
	}

	private boolean isFertile() {
		return isFertile && !isHungry();
	}

	private short max(float[] array) {
		short maxI = 0;
		for (short i = 1; i < array.length; ++i) {
			if (array[i] > array[maxI]) {
				maxI = i;
			}
		}
		return maxI;
	}

	private boolean isHungry() {
		return hunger < 50;
	}
	private void harvestGrass() {
		this.hunger += World.grass.harvest(skill.grassHarvest, pos) * skill.grassDigestion;
	}
	private void harvestBlood() {
		this.hunger += World.blood.harvest(skill.bloodHarvest, pos) * skill.bloodDigestion;
	}
	
	private void moveTo(short to) {
		pos = World.neighbour[to][pos];
	}
	private void interactWith(int id2) {
		if (id2 != id) {
			if (isFertile && pool[id2].isFertile) {
				resurrectAnimal(pos, 0f, 0f, 3);
				isFertile = false;
				hunger -= 2f; //TODO: ...
				sinceLastBaby = 0;
				pool[id2].isFertile = false;
				pool[id2].hunger -= 2f;
				pool[id2].sinceLastBaby = 0;
			}
		}
	}
	private void moveRandom() {
		pos = World.neighbour[Constants.RANDOM.nextInt(5)][pos];
	}
	
	private void die() {
		if (this.isAlive) {
			numAnimals--;
		}
		containsAnimals[pos] = -1;
		isAlive = false;
		World.blood.append(pos);
	}
	
	public short oppositeDirection(short d) {
		if (d == EAST) {
			return WEST;
		}
		else if (d == WEST) {
			return EAST;
		}
		else if (d == NORTH) {
			return SOUTH;
		}
		else if (d == SOUTH) {
			return NORTH;
		}
		else {
			return NONE;
		}
	}
}
