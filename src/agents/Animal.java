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
	public static final int BIRTH_HUNGER_COST = 20;
	
	private int age = 0;
	private int numKids = 0;
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
	
	public Decision decision;
	
	//************ GENETIC STATS ************
	public Species species;
	private boolean isFertile;
	private int timeBetweenBabies = 50;
	
	
	//************ STATIC STUFF ************
	public static Animal[] pool = new Animal[Constants.MAX_NUM_ANIMALS];
	public static int numAnimals = 0;
	public static int numGrasslers = 0;
	public static int numBloodlings = 0;
	public static int[] containsAnimals;
	public static boolean killAll = false;
	
	public static void moveAll() {
		if (killAll) {
			for (Animal a : pool) {
				a.die(0f);
			}
			System.out.println("Num animals alive after killing them all: " + numAnimals);
			System.out.println("Num bloodlings alive after killing them all: " + numBloodlings);
			System.out.println("Num grasslers alive after killing them all: " + numGrasslers);
			numAnimals = 0;
			killAll = false;
		}
		for (Animal a : pool) {
			if (a.isAlive) {
				a.age();
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
	public static int resurrectAnimal(int pos, float hunger, Species speciesMom, Decision decisionMom, Species speciesDad, Decision decisionDad) {
		int id = findFirstAvailablePoolSpot();
		
		if (id == -1) {
			System.err.println("did not find pool spot.");
			return -1;
		}
		
		pool[id].isAlive = true;
		
		pool[id].species.inherit(speciesMom, speciesDad);
		if (decisionMom != null && decisionDad != null) {
			pool[id].decision.inherit(decisionMom, decisionDad);
		}
		else {
			pool[id].decision.initWeightsRandom();
		}
		
		
		
		pool[id].pos = pos;
		pool[id].id = id;
		pool[id].age = 0;
		pool[id].sinceLastBaby = 0;
		pool[id].recover = 0f;
		pool[id].hunger = hunger;
		
		numAnimals++;
		switch (pool[id].species.speciesId) {
			case Constants.SpeciesId.BLOODLING:
				pool[id].color[0] = 1;
				pool[id].color[1] = 0;
				pool[id].color[2] = 0;
				pool[id].size = 3;
				numBloodlings++;
				break;
			case Constants.SpeciesId.GRASSLER:
				pool[id].color[0] = 1;
				pool[id].color[1] = 1;
				pool[id].color[2] = 1;
				pool[id].size = 1;
				numGrasslers++;
				break;
			default:
				System.err.println("aa what is this?");
		}
		containsAnimals[pool[id].pos] = id;
		return id;
	}
	private static int findFirstAvailablePoolSpot() {
		int id = 0;
		while (pool[id].isAlive) {
			id++;
			if (id == Constants.MAX_NUM_ANIMALS) {
				System.err.println("MAX_NUM_ANIMALS reached. Pool full.");
				System.err.println("NUM GRASSLERS = " + numGrasslers + "NUM BLOODLINGS = " + numBloodlings);
				return -1;
			}
		}
		return id;
	}
	
// ************ INSTANCE STUFF ************
	private Animal() {
		this.isAlive = false;
		this.color = new float[3];
		this.nearbyAnimals = new int[Constants.NUM_NEIGHBOURS];
		this.nearbyAnimalsDistance = new int[Constants.NUM_NEIGHBOURS];
		this.decision = new Decision();
		this.species = new Species();
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
		
		hunger = hunger * 0.99f - 1f; //TODO: Why dOlof you do thies?
		if (hunger < 0) {
			die(Constants.Blood.DEATH_FROM_HUNGER_FACTOR);
		}
		
	}
	
	private void age() {
		age++;
		if (species.speciesId == Constants.SpeciesId.BLOODLING) {
			int score = age + numKids*1000;
//			int score = numKids;
			if (score > 10000) {
				if (score > Constants.SpeciesId.bestBloodlingDecisionScore*10) {
					Constants.SpeciesId.bestBloodlingDecision = decision;
					Constants.SpeciesId.bestBloodlingDecisionScore = score;
					color[2] = 0.5f;
				}
				else if (score > Constants.SpeciesId.secondBloodlingDecisionScore*10) {
					Constants.SpeciesId.secondBloodlingDecision = decision;
					Constants.SpeciesId.secondBloodlingDecisionScore = score;
					color[2] = 0.5f;
				}
			}
		}
	}
	
	private boolean findBestDir(short[] bestDir, int[] animalIdToInteractWith) {
		animalIdToInteractWith[0] = -1;
		
		double[] tileGoodness = new double[5];
		for (int tile = 0; tile < 5; ++tile) {
			
			// Reset neural network.
			decision.setZero();
			
			decision.z[0][DecisionFactors.HUNGER] = this.hunger / HUNGRY_HUNGER; //TODO: Rescale this?
			
			if (isFertile) {
				decision.z[0][DecisionFactors.FERTILE] = 1f;
			}
			else {
				decision.z[0][DecisionFactors.FERTILE] = 0;
			}
			
			decision.z[0][DecisionFactors.AGE] = 0;
			
			for (int i = 0; i < decision.z[0].length; ++i) {
				if (i != DecisionFactors.HUNGER && i != DecisionFactors.FERTILE && i != DecisionFactors.AGE) {
					decision.z[0][i] = 0;
				}
			}
			
			int x = World.neighbour[tile][pos] / Constants.WORLD_SIZE_X;
			int y = World.neighbour[tile][pos] % Constants.WORLD_SIZE_X;
			
			decision.z[0][DecisionFactors.TILE_GRASS] = World.grass.height[World.neighbour[tile][pos]];
			decision.z[0][DecisionFactors.TILE_BLOOD] = World.blood.height[World.neighbour[tile][pos]];
			
			decision.z[0][DecisionFactors.TILE_DANGER] = 1;
			decision.z[0][DecisionFactors.TILE_HUNT] = 1;
			decision.z[0][DecisionFactors.TILE_FRIENDS] = 1;
			decision.z[0][DecisionFactors.TILE_FERTILITY] = 1;
			for (int nearbyAnimalId : nearbyAnimals) {
				
				if (nearbyAnimalId == -1) {
					continue;
				}
				
				int xNeigh = pool[nearbyAnimalId].pos / Constants.WORLD_SIZE_X;
				int yNeigh = pool[nearbyAnimalId].pos % Constants.WORLD_SIZE_X;
				
				float distance = ((float)(Math.abs(xNeigh-x) + Math.abs(yNeigh - y)))/Constants.MAX_DISTANCE_AN_ANIMAL_CAN_SEE;
				
				if (looksDangerous(nearbyAnimalId)) {
					decision.z[0][DecisionFactors.TILE_DANGER] = Math.min(distance, decision.z[0][DecisionFactors.TILE_DANGER]);
				}
				if (looksWeak(nearbyAnimalId)) {
					decision.z[0][DecisionFactors.TILE_HUNT] = Math.min(distance, decision.z[0][DecisionFactors.TILE_HUNT]);
					if (distance == 0) {
						animalIdToInteractWith[0] = nearbyAnimalId;
					}
				}
				if (!looksDangerous(nearbyAnimalId)) {
					decision.z[0][DecisionFactors.TILE_FRIENDS] = Math.min(distance, decision.z[0][DecisionFactors.TILE_FRIENDS]);
				}
				if (isFertileWith(nearbyAnimalId)) {
					decision.z[0][DecisionFactors.TILE_FERTILITY] = Math.min(distance, decision.z[0][DecisionFactors.TILE_FERTILITY]) * ((float)sinceLastBaby)/timeBetweenBabies;
					if (distance == 0) {
						animalIdToInteractWith[0] = nearbyAnimalId;
					}
				}
			}
			
			tileGoodness[tile] = decision.neuralMagic();
		}
		bestDir[0] = (short) max(tileGoodness);
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

	private int max(double[] nodeGoodness) {
		int maxI = -1;
		double maxVal = Double.MIN_VALUE;
		for (short i = 0; i < nodeGoodness.length; ++i) {
			if (nodeGoodness[i] > maxVal) {
				maxVal = nodeGoodness[i];
				maxI = i;
			}
		}
		if (maxI == -1 || Double.isNaN(maxVal)) {
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
				resurrectAnimal(pos, BIRTH_HUNGER, pool[id].species, pool[id].decision, pool[id2].species, pool[id2].decision);
				isFertile = false;
				hunger -= BIRTH_HUNGER_COST;
				sinceLastBaby = 0;
				numKids ++;
				pool[id2].isFertile = false;
				pool[id2].hunger -= BIRTH_HUNGER_COST;
				pool[id2].sinceLastBaby = 0;
				pool[id2].numKids ++;
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
			switch (species.speciesId) {
				case Constants.SpeciesId.BLOODLING:
					numBloodlings--;
					break;
				case Constants.SpeciesId.GRASSLER:
					numGrasslers--;
					break;
				default:
					System.err.println("aa what is this?");
			}
			World.blood.append(pos, energyFactor);
		}
		containsAnimals[pos] = -1;
		isAlive = false;
	}
}
