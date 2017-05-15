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
	public static final int BIRTH_HUNGER_COST = 60;
	public static final int AGE_DEATH = 100;
	
	public int age = 0;
	private int numKids = 0;
	private int sinceLastBaby = 0;
	private Integer id;
	public float size = 3f;
	public float[] color;
	public int pos;
	public int oldPos;
	public int oldX;
	public int oldY;
	
	public boolean isAlive;
	public float hunger;
	public int[] nearbyAnimals;
	public int[] nearbyAnimalsDistance;
	
	private float recover = 0f;
	
	public NeuralNetwork neuralNetwork;
	
	//************ GENETIC STATS ************
	public Species species;
	private boolean isFertile;
	private int timeBetweenBabies = 3;
	
	
	//************ STATIC STUFF ************
	public static Animal[] pool = new Animal[Constants.MAX_NUM_ANIMALS];
	public static ArrayList<Integer> alive = new ArrayList<>();
	public static ArrayList<Integer> dead = new ArrayList<>();
	public static int numAnimals = 0;
	public static int numGrasslers = 0;
	public static int numBloodlings = 0;
	public static int[] containsAnimals;
	public static boolean killAll = false;
	
	public static void moveAll() {
		if (killAll) {
			while (!alive.isEmpty()) {
				pool[alive.get(0)].die(0f);
			}
			System.out.println("Num animals alive after killing them all: " + numAnimals);
			System.out.println("Num bloodlings alive after killing them all: " + numBloodlings);
			System.out.println("Num grasslers alive after killing them all: " + numGrasslers);
			numAnimals = 0;
			killAll = false;
		}
		for (Animal a : pool) {
			if (a.isAlive) {
				a.sinceLastBaby++;
				a.recover += a.species.speed;
				if (a.recover > 1f) {
					if (!a.age()) {
						continue;
					}
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
		
		for (int pos = 0; pos < Constants.WORLD_SIZE; ++pos) {
			containsAnimals[pos] = -1;
		}
		
		for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
			pool[id] = new Animal();
			dead.add(id);
		}
	}
	public static int resurrectAnimal(int pos, float hunger, Species speciesMom, NeuralNetwork neuralMom, Species speciesDad, NeuralNetwork neuralDad) {
		int id = findFirstAvailablePoolSpot();
		
		if (id == -1) {
			System.err.println("did not find pool spot.");
			return -1;
		}
		alive.add(id);
		
		pool[id].isAlive = true;
		
		pool[id].species.inherit(speciesMom, speciesDad);
		if (neuralMom != null && neuralDad != null) {
			pool[id].neuralNetwork.inherit(neuralMom, neuralDad);
		}
		else {
			pool[id].neuralNetwork.initWeightsRandom();
		}
		
		
		
		pool[id].pos = pos;
		pool[id].oldPos = pos;
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
				pool[id].size = 4;
				numBloodlings++;
				break;
			case Constants.SpeciesId.GRASSLER:
				pool[id].color[0] = 1;
				pool[id].color[1] = 1;
				pool[id].color[2] = 1;
				pool[id].size = 2;
				numGrasslers++;
				break;
			default:
				System.err.println("aaaaa what is this?");
		}
		containsAnimals[pool[id].pos] = id;
		Vision.addAnimalToZone(id);
		return id;
	}
	
	private static int findFirstAvailablePoolSpot() {
		if (dead.size() == 0) {
			return -1;
		}
		int poolSpot = dead.get(0);
		dead.remove(0);
		return poolSpot;
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
	private short bestDir;
	private int animalIdToHunt;
	private int animalIdToMateWith;
	private void move() {

		// Remove animal from the world temporarily :F
		containsAnimals[pos] = -1;
		
		Vision.updateNearestNeighbours(id);
		
		// Calculate to where we want to move
		if (findBestDir()) {
			moveTo(bestDir);
			if (animalIdToHunt != -1) {
				interactWith(animalIdToHunt);
			}
			if (animalIdToMateWith != -1) {
				interactWith(animalIdToMateWith);
			}
			harvestBlood();
			harvestGrass();
		}
		else {
			System.err.println("Warning: found no best dir, should not happen?");
			moveRandom();
		}
		
		
		// Add animal to the world again :)
		containsAnimals[pos] = id;
	}
	
	private boolean age() {
		age++;
		
		if (age > AGE_DEATH) {
			die(Constants.Blood.DEATH_FROM_AGE_FACTOR);
			return false;
		}
		hunger = hunger * 0.999f - 1f;
		if (hunger < 0) {
			die(Constants.Blood.DEATH_FROM_HUNGER_FACTOR);
			return false;
		}
		
		return true;
	}
	
	private double[] tileGoodness = new double[5];
	private boolean findBestDir() {
		animalIdToHunt = -1;
		animalIdToMateWith = -1;
		
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
			
			int x = World.neighbour[tile][pos] / Constants.WORLD_SIZE_X;
			int y = World.neighbour[tile][pos] % Constants.WORLD_SIZE_X;
			
			neuralNetwork.z[0][NeuralFactors.TILE_GRASS] = World.grass.height[World.neighbour[tile][pos]];
			neuralNetwork.z[0][NeuralFactors.TILE_BLOOD] = World.blood.height[World.neighbour[tile][pos]];
			
			neuralNetwork.z[0][NeuralFactors.TILE_DANGER] = 0;
			neuralNetwork.z[0][NeuralFactors.TILE_HUNT] = 0;
			neuralNetwork.z[0][NeuralFactors.TILE_FRIENDS] = 0;
			neuralNetwork.z[0][NeuralFactors.TILE_FERTILITY] = 0;
			
			// Calculate tile in relation to old position.
//			neuralNetwork.z[0][NeuralFactors.TILE_OLD_POSITION] = (float)Vision.calculateDistance(pos, oldPos);
//			if (neuralNetwork.z[0][NeuralFactors.TILE_OLD_POSITION] != 0) {
//				neuralNetwork.z[0][NeuralFactors.TILE_OLD_POSITION] = 1f;
//			}
			neuralNetwork.z[0][NeuralFactors.TILE_OLD_POSITION] = Math.max(Math.abs(x-oldX), Math.abs(y-oldY))/2;
			if (neuralNetwork.z[0][NeuralFactors.TILE_OLD_POSITION] > 1) {
				neuralNetwork.z[0][NeuralFactors.TILE_OLD_POSITION] = 1f;
			}
			
			// Loop through the sighted animals to determine tile goodnesses
			for (int nearbyAnimalId : nearbyAnimals) {
				
				if (nearbyAnimalId == -1) {
					continue;
				}
				
				int xNeigh = pool[nearbyAnimalId].pos / Constants.WORLD_SIZE_X;
				int yNeigh = pool[nearbyAnimalId].pos % Constants.WORLD_SIZE_X;
				
				float distance = 1f-((float)(Math.abs(xNeigh-x) + Math.abs(yNeigh - y)))/Constants.MAX_DISTANCE_AN_ANIMAL_CAN_SEE;
				
				// 0 distance means the animal is too far away
				if (distance < 0) {
					distance = 0;
				}
				
				if (looksDangerous(nearbyAnimalId)) {
					neuralNetwork.z[0][NeuralFactors.TILE_DANGER] = Math.max(distance, neuralNetwork.z[0][NeuralFactors.TILE_DANGER]);
				}
				if (looksWeak(nearbyAnimalId)) {
					neuralNetwork.z[0][NeuralFactors.TILE_HUNT] = Math.max(distance, neuralNetwork.z[0][NeuralFactors.TILE_HUNT]);
					if (distance == 1) {
						animalIdToHunt = nearbyAnimalId;
					}
				}
				if (!looksDangerous(nearbyAnimalId)) {
					neuralNetwork.z[0][NeuralFactors.TILE_FRIENDS] = Math.max(distance, neuralNetwork.z[0][NeuralFactors.TILE_FRIENDS]);
				}
				if (isFertileWith(nearbyAnimalId)) {
					neuralNetwork.z[0][NeuralFactors.TILE_FERTILITY] = Math.max(distance, neuralNetwork.z[0][NeuralFactors.TILE_FERTILITY]);
					if (distance == 1) {
						animalIdToMateWith = nearbyAnimalId;
					}
				}
			}
			
			tileGoodness[tile] = neuralNetwork.neuralMagic();
		}
		bestDir = (short) max(tileGoodness);
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
		oldPos = pos;
		oldX = pos / Constants.WORLD_SIZE_X;
		oldY = pos % Constants.WORLD_SIZE_X;
		pos = World.neighbour[to][pos];
		Vision.updateAnimalZone(id);
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
				
				
				// This will cause the mating animals to continue living, which is what we want in the end.
				// A bit unconventional and forced.
				age = 0;
				pool[id2].age = 0;
			}
			else if (looksWeak(id2)) {
				pool[id2].die(1f);
			}
		}
	}
	private void moveRandom() {
		pos = World.neighbour[Constants.RANDOM.nextInt(5)][pos];
	}
	
	private void die(float bloodFactor) {
		if (!this.isAlive) {
			System.err.println("Trying to kill what is already dead.");
		}
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
		Vision.removeAnimalFromZone(id);
		World.blood.append(pos, bloodFactor);
		containsAnimals[pos] = -1;
		isAlive = false;
		alive.remove(alive.indexOf(id));
		dead.add(id);
		
		pos = -1;
		oldPos = -1;
	}
}
