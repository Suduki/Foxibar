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
	public static final int AGE_DEATH = 1000;
	
	private int age = 0;
	private int numKids = 0;
	private int sinceLastBaby = 0;
	private int id;
	public float size = 3f;
	public float[] color;
	public int pos;
	public boolean isAlive;
	public float hunger;
	public int[] nearbyAnimals;
	public int[] nearbyAnimalsDistance;
	
	private float recover = 0f;
	
	public NeuralNetwork neuralNetwork;
	
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
				if (!a.age()) {
					continue;
				}
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
	public static int resurrectAnimal(int pos, float hunger, Species speciesMom, NeuralNetwork neuralMom, Species speciesDad, NeuralNetwork neuralDad) {
		int id = findFirstAvailablePoolSpot();
		
		if (id == -1) {
			System.err.println("did not find pool spot.");
			return -1;
		}
		
		pool[id].isAlive = true;
		
		pool[id].species.inherit(speciesMom, speciesDad);
		if (neuralMom != null && neuralDad != null) {
			pool[id].neuralNetwork.inherit(neuralMom, neuralDad);
		}
		else {
			pool[id].neuralNetwork.initWeightsRandom();
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
		this.neuralNetwork = new NeuralNetwork();
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
		
		hunger = hunger * 0.999f - 1f; //TODO: Why dOlof you do thies?
		if (hunger < 0) {
			die(Constants.Blood.DEATH_FROM_HUNGER_FACTOR);
		}
		
	}
	
	private boolean age() {
		age++;
		
		if (age > AGE_DEATH) {
			die(0f);
			return false;
		}
		
		if (species.speciesId == Constants.SpeciesId.BLOODLING) {
			int score = age + numKids*1000;
//			int score = numKids;
			if (score > 10000) {
				if (score > Constants.SpeciesId.bestBloodlingNeuralScore*10) {
					Constants.SpeciesId.bestBloodlingNeural = neuralNetwork;
					Constants.SpeciesId.bestBloodlingNeuralScore = score;
					color[2] = 0.5f;
				}
				else if (score > Constants.SpeciesId.secondBloodlingNeuralScore*10) {
					Constants.SpeciesId.secondBloodlingNeural = neuralNetwork;
					Constants.SpeciesId.secondBloodlingNeuralScore = score;
					color[2] = 0.5f;
				}
			}
		}
		return true;
	}
	
	private double[] tileGoodness = new double[5];
	private boolean findBestDir(short[] bestDir, int[] animalIdToInteractWith) {
		animalIdToInteractWith[0] = -1;
		
		for (int tile = 0; tile < 5; ++tile) {
			
			// Reset neural network.
			neuralNetwork.reset();
			
			neuralNetwork.z[0][NeuralFactors.HUNGER] = hunger / HUNGRY_HUNGER; //TODO: Rescale this?
			
			if (isFertile) {
				neuralNetwork.z[0][NeuralFactors.FERTILE] = 1f;
			}
			else {
				neuralNetwork.z[0][NeuralFactors.FERTILE] = 0;
			}
			
			neuralNetwork.z[0][NeuralFactors.AGE] = ((float)age)/AGE_DEATH;
			
			for (int i = 0; i < neuralNetwork.z[0].length; ++i) {
				if (i != NeuralFactors.HUNGER && i != NeuralFactors.FERTILE && i != NeuralFactors.AGE) {
					neuralNetwork.z[0][i] = 0;
				}
			}
			
			int x = World.neighbour[tile][pos] / Constants.WORLD_SIZE_X;
			int y = World.neighbour[tile][pos] % Constants.WORLD_SIZE_X;
			
			neuralNetwork.z[0][NeuralFactors.TILE_GRASS] = World.grass.height[World.neighbour[tile][pos]];
			neuralNetwork.z[0][NeuralFactors.TILE_BLOOD] = World.blood.height[World.neighbour[tile][pos]];
			
			neuralNetwork.z[0][NeuralFactors.TILE_DANGER] = 0;
			neuralNetwork.z[0][NeuralFactors.TILE_HUNT] = 0;
			neuralNetwork.z[0][NeuralFactors.TILE_FRIENDS] = 0;
			neuralNetwork.z[0][NeuralFactors.TILE_FERTILITY] = 0;
			for (int nearbyAnimalId : nearbyAnimals) {
				
				if (nearbyAnimalId == -1) {
					continue;
				}
				
				int xNeigh = pool[nearbyAnimalId].pos / Constants.WORLD_SIZE_X;
				int yNeigh = pool[nearbyAnimalId].pos % Constants.WORLD_SIZE_X;
				
				float distance = 1f-((float)(Math.abs(xNeigh-x) + Math.abs(yNeigh - y)))/Constants.MAX_DISTANCE_AN_ANIMAL_CAN_SEE;
				
				if (looksDangerous(nearbyAnimalId)) {
					neuralNetwork.z[0][NeuralFactors.TILE_DANGER] = Math.min(distance, neuralNetwork.z[0][NeuralFactors.TILE_DANGER]);
				}
				if (looksWeak(nearbyAnimalId)) {
					neuralNetwork.z[0][NeuralFactors.TILE_HUNT] = Math.min(distance, neuralNetwork.z[0][NeuralFactors.TILE_HUNT]);
					if (distance == 1) {
						animalIdToInteractWith[0] = nearbyAnimalId;
					}
				}
				if (!looksDangerous(nearbyAnimalId)) {
					neuralNetwork.z[0][NeuralFactors.TILE_FRIENDS] = Math.min(distance, neuralNetwork.z[0][NeuralFactors.TILE_FRIENDS]);
				}
				if (isFertileWith(nearbyAnimalId)) {
					neuralNetwork.z[0][NeuralFactors.TILE_FERTILITY] = Math.min(distance, neuralNetwork.z[0][NeuralFactors.TILE_FERTILITY]) * ((float)sinceLastBaby)/timeBetweenBabies;
					if (distance == 1) {
						animalIdToInteractWith[0] = nearbyAnimalId;
					}
				}
			}
			
			tileGoodness[tile] = neuralNetwork.neuralMagic();
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
				resurrectAnimal(pos, BIRTH_HUNGER, pool[id].species, pool[id].neuralNetwork, pool[id2].species, pool[id2].neuralNetwork);
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
