package agents;

import java.util.ArrayList;
import java.util.Collections;

import vision.Vision;
import world.World;
import constants.Constants;
import constants.Constants.Neighbours;
import static constants.Constants.Neighbours.*;

public class Animal {
	
	public static final int BIRTH_HUNGER = 20;
	public static final int HUNGRY_HUNGER = 100;
	public static final int BIRTH_HUNGER_COST = 40;
	
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
	
	//************ GENETIC STATS ************
	public Species species;
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
				a.die(0f);
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
				a.recover += a.species.speed;
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
	public static int resurrectAnimal(int pos, float hunger, Species mom, Species dad) {
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
		pool[id].species.inherit(mom, dad);
		Decision.register(pool[id].species.speciesId, pool[id].species.decision);
		
		pool[id].color[0] = pool[id].species.bloodDigestion*pool[id].species.bloodHarvest;
		pool[id].color[1] = pool[id].species.grassDigestion*pool[id].species.grassHarvest;
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
		this.species = new Species();
		this.nearbyAnimals = new int[Constants.NUM_NEIGHBOURS];
		this.nearbyAnimalsDistance = new int[Constants.NUM_NEIGHBOURS];
	}
	private void move() {

		// Remove animal from the world temporarily :F
		containsAnimals[pos] = -1;
		
		// Calculate to where we want to move
		short[] bestDir = new short[1];
		int[] animalIdToInteractWith = new int[1];
		if (findBestDir(bestDir, animalIdToInteractWith)) {
			moveTo(bestDir[0]);
			harvestBlood();
			harvestGrass();
			if(animalIdToInteractWith[0] != -1) {
				interactWith(animalIdToInteractWith[0]);
			}
		}
		else {
			moveRandom();
		}
		
		// Add animal to the world again :)
		containsAnimals[pos] = id;
		
		Vision.updateNearestNeighbours(id);
		
		hunger = hunger * 0.98f - 1f; //TODO: Why dOlof you do thies?
		if (hunger < 0) {
			die(Constants.Blood.DEATH_FROM_HUNGER_FACTOR);
		}
		
	}
	
	public static int numVals = 0;
	public static final int HUNGER = numVals++;
	public static final int FERTILE = numVals++;
	public static final int AGE = numVals++;
	public static final int TILE_GRASS = numVals++;
	public static final int TILE_BLOOD = numVals++;
	public static final int TILE_DANGER = numVals++;
	public static final int TILE_FERTILITY = numVals++;
	public static final int TILE_FRIENDS = numVals++;
	public static final int TILE_HUNT = numVals;
	
	private boolean findBestDir(short[] bestDir, int[] animalIdToInteractWith) {
		animalIdToInteractWith[0] = -1;
		
		float[] nodeGoodness = new float[5];
		float[] inputData = new float[numVals];
		
		inputData[HUNGER] = this.hunger / HUNGRY_HUNGER; //TODO: Rescale this?
		
		if (isFertile) {
			inputData[FERTILE] = 1f;
		}
		else {
			inputData[FERTILE] = 0;
		}
		
		inputData[AGE] = 0;
		
		for (int nodeNeighbour = 0; nodeNeighbour < 5; ++nodeNeighbour) {
			
			for (int i = 0; i < inputData.length; ++i) {
				inputData[i] = 0;
			}
			
			int x = World.neighbour[nodeNeighbour][pos] / Constants.WORLD_SIZE_X;
			int y = World.neighbour[nodeNeighbour][pos] % Constants.WORLD_SIZE_X;
			
			inputData[TILE_GRASS] = World.grass.height[World.neighbour[nodeNeighbour][pos]];
			inputData[TILE_BLOOD] = World.blood.height[World.neighbour[nodeNeighbour][pos]];
			
			for (int nearbyAnimalId : nearbyAnimals) {
				
				if (nearbyAnimalId == -1) {
					continue;
				}
				
				int xNeigh = pool[nearbyAnimalId].pos / Constants.WORLD_SIZE_X;
				int yNeigh = pool[nearbyAnimalId].pos % Constants.WORLD_SIZE_X;
				
				int distance = Math.abs(xNeigh-x) + Math.abs(yNeigh - y) + 1;
				
			}
			
			
		}
//		for (int nearbyAnimalId : nearbyAnimals) {
//			if (nearbyAnimalId == -1) {
//				continue;
//			}
//			
//			
//			int xNeigh = pool[nearbyAnimalId].pos / Constants.WORLD_SIZE_X;
//			int yNeigh = pool[nearbyAnimalId].pos % Constants.WORLD_SIZE_X;
//			
//			for (int nodeNeighbour = 0; nodeNeighbour < 5; ++nodeNeighbour) {
//				int x = World.neighbour[nodeNeighbour][pos] / Constants.WORLD_SIZE_X;
//				int y = World.neighbour[nodeNeighbour][pos] % Constants.WORLD_SIZE_X;
//				int distance = Math.abs(xNeigh-x) + Math.abs(yNeigh - y) + 1;
//				if (isFertileWith(nearbyAnimalId)) {
//					nodeGoodness[nodeNeighbour] += species.decision.decisionFactors[Decision.WANT_TO_MATE]/distance; //TODO: *distance? investigate different varianter
//					if (distance == 1) {
//						animalIdToInteractWith[0] = nearbyAnimalId;
//					}
//				}
//				else {
//					if (looksDangerous(nearbyAnimalId)) {
//						// Yelp! Run!
//						nodeGoodness[nodeNeighbour] += species.decision.decisionFactors[Decision.WANT_TO_FLEE]/distance;
//					}
//					else if (looksWeak(nearbyAnimalId)) {
//						nodeGoodness[nodeNeighbour] += species.decision.decisionFactors[Decision.WANT_TO_HUNT]/distance;
//						if (distance == 1) {
//							animalIdToInteractWith[0] = nearbyAnimalId;
//						}
//					}
//					else {
//						// This is a non-dangerous dude, lets move away a li'l bit
//						nodeGoodness[nodeNeighbour] += species.decision.decisionFactors[Decision.WANT_TO_BE_ALONE]/distance;
//					}
//				}
//			}
//		}
//		for (int nodeNeighbour = 0; nodeNeighbour < 5; ++nodeNeighbour) {
//			nodeGoodness[nodeNeighbour] += species.decision.decisionFactors[Decision.WANT_HIGH_GRASS]*World.grass.height[World.neighbour[nodeNeighbour][pos]];
//			nodeGoodness[nodeNeighbour] += species.decision.decisionFactors[Decision.WANT_HIGH_BLOOD]*World.blood.height[World.neighbour[nodeNeighbour][pos]];
//		}
//		
		bestDir[0] = (short) max(nodeGoodness);
		return true;
	}
	
	private boolean looksDangerous(int nearbyAnimalId) {
		return species.fight < pool[nearbyAnimalId].species.fight;
	}

	private boolean looksWeak(int nearbyAnimalId) {
		return species.fight > pool[nearbyAnimalId].species.fight;
	}

	private boolean isFertileWith(int nearbyAnimalId) {
		return species.speciesId == pool[nearbyAnimalId].species.speciesId && isFertile() && pool[nearbyAnimalId].isFertile();
	}

	private boolean isFertile() {
		return isFertile && !isHungry();
	}

	private int max(float[] array) {
		int maxI = -1;
		float maxVal = -100;
		for (short i = 0; i < array.length; ++i) {
			if (array[i] > maxVal) {
				maxVal = array[i];
				maxI = i;
			}
		}
		if (maxI == -1) {
			maxI = Constants.RANDOM.nextInt(5);
		}
		return maxI;
	}

	private boolean isHungry() {
		return hunger < HUNGRY_HUNGER;
	}
	private void harvestGrass() {
		this.hunger += World.grass.harvest(species.grassHarvest, pos) * species.grassDigestion;
	}
	private void harvestBlood() {
		this.hunger += World.blood.harvest(species.bloodHarvest, pos) * species.bloodDigestion;
	}
	
	private void moveTo(short to) {
		pos = World.neighbour[to][pos];
	}
	private void interactWith(int id2) {
		if (id2 != id) {
			if (isFertileWith(id2)) {
				resurrectAnimal(pos, BIRTH_HUNGER, species, pool[id2].species);
				isFertile = false;
				hunger -= BIRTH_HUNGER_COST;
				sinceLastBaby = 0;
				pool[id2].isFertile = false;
				pool[id2].hunger -= BIRTH_HUNGER_COST;
				pool[id2].sinceLastBaby = 0;
			}
			else if (looksWeak(id2)) {
				pool[id2].die(1f);
			}
		}
	}
	private void moveRandom() {
		pos = World.neighbour[Constants.RANDOM.nextInt(5)][pos];
	}
	
	private void die(float energyFactor) {
		if (this.isAlive) {
			numAnimals--;
			Decision.unregister(species.speciesId, species.decision);
			World.blood.append(pos, energyFactor);
		}
		containsAnimals[pos] = -1;
		isAlive = false;
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
