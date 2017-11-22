package agents;

import java.util.ArrayList;

import vision.Vision;
import world.World;
import constants.Constants;
import display.RenderState;
import messages.LoadBrains;
import messages.SaveBrains;

public class Animal {
	
	public static final int BIRTH_HUNGER = 60;
	public static final int HUNGRY_HUNGER = 100;
	public static final int BIRTH_HUNGER_COST = 80;
	public static final int AGE_DEATH = 1000;
	
	public int age = 0;
	public int score = 0;
	private int sinceLastBaby = 0;
	private Integer id;
	public float size = 3f;
	public float[] secondaryColor;
	public float[] primaryColor;
	public int pos;
	public int oldPos;
	public int oldX;
	public int oldY;
	
	public boolean isAlive;
	public float hunger;
	public float health;
	public int[] nearbyAnimals;
	public int[] nearbyAnimalsDistance;
	
	public NeuralNetwork neuralNetwork;
	
	//************ GENETIC STATS ************
	public Species species;
	private boolean isFertile;
	private int timeBetweenBabies = 10;
	
	
	//************ STATIC STUFF ************
	public static Animal[] pool = new Animal[Constants.MAX_NUM_ANIMALS];
	public static ArrayList<Integer> alive = new ArrayList<>();
	public static ArrayList<Integer> dead = new ArrayList<>();
	public static int numAnimals = 0;
	public static int numGrasslers = 0;
	public static int numBloodlings = 0;
	public static int[] containsAnimals;
	public static boolean killAll = false;
	public static boolean saveBrains = false;
	public static boolean loadBrains = false;
	
	
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
		if (saveBrains) {
			System.out.println("Save brains");
			if (RenderState.FOLLOW_BLOODLING) {
				SaveBrains.saveBrains(Constants.SpeciesId.BLOODLING);
			}
			else if (RenderState.FOLLOW_GRASSLER) {
				SaveBrains.saveBrains(Constants.SpeciesId.GRASSLER);
			}
			else {
				System.err.println("You need to follow the species you want to save, press the render animal button!");
			}
			saveBrains = false;
		}
		if (loadBrains) {
			System.out.println("Load brains");
			if (RenderState.FOLLOW_BLOODLING) {
				LoadBrains.loadBrains(Constants.SpeciesId.BLOODLING);
			}
			else if (RenderState.FOLLOW_GRASSLER) {
				LoadBrains.loadBrains(Constants.SpeciesId.GRASSLER);
			}
			else {
				System.err.println("You need to follow the species you want to load, press the render animal button!");
			}
			loadBrains = false;
		}
		
		for (Animal a : pool) {
			if (a.isAlive) {
				a.sinceLastBaby++;
				if (a.energy.canMove(a.speed[0], a.species.speed)) {
					if (!a.age()) { 
						// Died from hunger or age
						continue;
					}
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
			if (LoadBrains.bestBloodling != null && pool[id].species.speciesId == Constants.SpeciesId.BLOODLING) {
				pool[id].neuralNetwork.inherit(LoadBrains.bestBloodling, LoadBrains.bestBloodling);
			}
			else if (LoadBrains.bestGrassler != null && pool[id].species.speciesId == Constants.SpeciesId.GRASSLER) {
				pool[id].neuralNetwork.inherit(LoadBrains.bestGrassler, LoadBrains.bestGrassler);
			}
			else {
				pool[id].neuralNetwork.initWeightsRandom();
			}
		}
		
		
		
		pool[id].pos = pos;
		pool[id].oldPos = pos;
		pool[id].id = id;
		pool[id].age = 0;
		pool[id].score = 0;
		pool[id].sinceLastBaby = 0;
		pool[id].hunger = hunger;
		pool[id].health = 0.1f;
		pool[id].energy.init();
		
		numAnimals++;
		switch (pool[id].species.speciesId) {
			case Constants.SpeciesId.BLOODLING:
				pool[id].secondaryColor[0] = 1;
				pool[id].secondaryColor[1] = 0;
				pool[id].secondaryColor[2] = 0;
				
				pool[id].size = 2;
				numBloodlings++;
				break;
			case Constants.SpeciesId.GRASSLER:
				pool[id].secondaryColor[0] = 1;
				pool[id].secondaryColor[1] = 1;
				pool[id].secondaryColor[2] = 1;
				pool[id].size = 1;
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
		this.secondaryColor = new float[3];
		this.primaryColor = new float[3];
		this.primaryColor[0] = Constants.RANDOM.nextFloat();
		this.primaryColor[1] = Constants.RANDOM.nextFloat();
		this.primaryColor[2] = Constants.RANDOM.nextFloat();
		
		this.nearbyAnimals = new int[Constants.NUM_NEIGHBOURS];
		this.nearbyAnimalsDistance = new int[Constants.NUM_NEIGHBOURS];
		this.neuralNetwork = new NeuralNetwork(false);
		this.species = new Species();
		this.energy = new Energy();
	}
	private short bestDir;
	private int animalIdToHunt;
	private int animalIdToMateWith;
	public Energy energy;
	private void move() {

		// Remove animal from the world temporarily :F
		containsAnimals[pos] = -1;
		
		Vision.updateNearestNeighbours(id);
		
		// Calculate to where we want to move
		if (findBestDir()) {
			moveTo(bestDir);
			if (animalIdToHunt != -1) {
				fightWith(animalIdToHunt);
			}
			if (animalIdToMateWith != -1) {
				mateWith(animalIdToMateWith);
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
		score++;
		
		if (this.species.speciesId == Constants.SpeciesId.GRASSLER) {
			if (score > Constants.SpeciesId.BEST_GRASSLER_SCORE) {
				Constants.SpeciesId.BEST_GRASSLER_ID = id;
				Constants.SpeciesId.BEST_GRASSLER_SCORE = score;
			}
		}
		if (this.species.speciesId == Constants.SpeciesId.BLOODLING) {
			if (score > Constants.SpeciesId.BEST_BLOODLING_SCORE) {
				Constants.SpeciesId.BEST_BLOODLING_ID = id;
				Constants.SpeciesId.BEST_BLOODLING_SCORE = score;
			}
		}
		
		if (age > AGE_DEATH) {
			die(Constants.Blood.DEATH_FROM_AGE_FACTOR);
			return false;
		}
		hunger = hunger - 1f;
		if (hunger < 0) {
			die(Constants.Blood.DEATH_FROM_HUNGER_FACTOR);
			return false;
		}
		if (health < 0) {
			die(Constants.Blood.DEATH_FROM_LOW_HEALTH);
			return false;
		}
		if (health < 1f) {
			health = health + species.healing;
			if (health > 1f) {
				health = 1f;
			}
		}
		
		return true;
	}
	
	private static boolean[] directionWalkable = new boolean[5];
	public float[] speed = new float[1];
	
	private boolean findBestDir() {
		animalIdToHunt = -1;
		animalIdToMateWith = -1;
		
		for (int tile = 0; tile < 5; ++tile) {
			
			if (containsAnimals[World.neighbour[tile][pos]] == -1 || containsAnimals[World.neighbour[tile][pos]] == id) {
				directionWalkable[tile] = true;
			}
			else {
				directionWalkable[tile] = false;
			}
			
			neuralNetwork.z[tile][0][NeuralFactors.HUNGER] = hunger / HUNGRY_HUNGER;
			if (neuralNetwork.z[tile][0][NeuralFactors.HUNGER] > 1) {
				neuralNetwork.z[tile][0][NeuralFactors.HUNGER] = 1; 
			}
			
			neuralNetwork.z[tile][0][NeuralFactors.AGE] = ((float)age)/AGE_DEATH;
			
			if(neuralNetwork.z[tile][0][NeuralFactors.AGE] > 1) {
				System.out.println(age + "  " + AGE_DEATH + "   " + neuralNetwork.z[0][NeuralFactors.AGE]);
			}
			
			neuralNetwork.z[tile][0][NeuralFactors.TILE_GRASS] = World.grass.height[World.neighbour[tile][pos]];
			neuralNetwork.z[tile][0][NeuralFactors.TILE_BLOOD] = World.blood.height[World.neighbour[tile][pos]];
			neuralNetwork.z[tile][0][NeuralFactors.TILE_TERRAIN_HIGHT] = 
					Math.abs(World.terrain.height[World.neighbour[tile][pos]]*2-1f);
			
			neuralNetwork.z[tile][0][NeuralFactors.TILE_DANGER] = 0;
			neuralNetwork.z[tile][0][NeuralFactors.TILE_HUNT] = 0;
			neuralNetwork.z[tile][0][NeuralFactors.TILE_FRIENDS] = 0;
			neuralNetwork.z[tile][0][NeuralFactors.TILE_FERTILITY] = 0;
			
			// Calculate tile in relation to old position.
//			neuralNetwork.z[tile][0][NeuralFactors.TILE_OLD_POSITION] =  (float)Vision.calculateCircularDistance(World.neighbour[tile][pos], oldPos);
			
			// Loop through the sighted animals to determine tile goodnesses
			for (int nearbyAnimalId : nearbyAnimals) {
				
				if (nearbyAnimalId == -1) {
					continue;
				}
				
				float distance = (float) Vision.calculateCircularDistance(World.neighbour[tile][pos], pool[nearbyAnimalId].pos);
				if (distance > Constants.MAX_DISTANCE_AN_ANIMAL_CAN_SEE) {
					continue;
				}
				float distanceFactor = 1f-distance/Constants.MAX_DISTANCE_AN_ANIMAL_CAN_SEE;
				
				// 0 distanceF means the animal is too far away
				if (distanceFactor < 0  || distanceFactor > 1) {
					System.err.println("distanceFactor = " + distanceFactor);
				}
				
				if (looksDangerous(nearbyAnimalId)) {
					neuralNetwork.z[tile][0][NeuralFactors.TILE_DANGER] = Math.max(distanceFactor, neuralNetwork.z[tile][0][NeuralFactors.TILE_DANGER]);
				}
				if (looksWeak(nearbyAnimalId)) {
					neuralNetwork.z[tile][0][NeuralFactors.TILE_HUNT] = Math.max(distanceFactor, neuralNetwork.z[tile][0][NeuralFactors.TILE_HUNT]);
					if (distanceFactor == 1) {
						animalIdToHunt = nearbyAnimalId;
					}
				}
				if (!looksDangerous(nearbyAnimalId)) {
					neuralNetwork.z[tile][0][NeuralFactors.TILE_FRIENDS] = Math.max(distanceFactor, neuralNetwork.z[tile][0][NeuralFactors.TILE_FRIENDS]);
//					neuralNetwork.z[0][NeuralFactors.TILE_FRIENDS] += distanceFactor / Constants.NUM_NEIGHBOURS;
				}
				if (isFertileWith(nearbyAnimalId)) {
					neuralNetwork.z[tile][0][NeuralFactors.TILE_FERTILITY] = Math.max(distanceFactor, neuralNetwork.z[tile][0][NeuralFactors.TILE_FERTILITY]);
					if (distanceFactor == 1) {
						animalIdToMateWith = nearbyAnimalId;
					}
				}
				
			}
		}
		bestDir = (short) neuralNetwork.neuralMagic(directionWalkable, speed);
		return true;
	}
	
	private boolean looksDangerous(int nearbyAnimalId) {
		return species.fight < pool[nearbyAnimalId].species.fight;
	}

	private boolean looksWeak(int nearbyAnimalId) {
		return species.fight > pool[nearbyAnimalId].species.fight;
	}

	private boolean isFertileWith(int nearbyAnimalId) {
		return isFriendWith(nearbyAnimalId) && isFertile() && pool[nearbyAnimalId].isFertile();
	}

	private boolean isFriendWith(int nearbyAnimalId) {
		return species.speciesId == pool[nearbyAnimalId].species.speciesId;
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
			System.err.println("maxVal isNaN? " + maxVal);
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
		if (containsAnimals[World.neighbour[to][pos]] == -1) {
			pos = World.neighbour[to][pos];
			Vision.updateAnimalZone(id);
		}
	}
	
	private void mateWith(int id2) {
		resurrectAnimal(pos, BIRTH_HUNGER, pool[id].species, pool[id].neuralNetwork, pool[id2].species, pool[id2].neuralNetwork);
		isFertile = false;
		hunger -= BIRTH_HUNGER_COST;
		sinceLastBaby = 0;
		
		pool[id2].isFertile = false;
		pool[id2].hunger -= BIRTH_HUNGER_COST;
		pool[id2].sinceLastBaby = 0;
		
		
		// This will cause the mating animals to continue living, which is what we want in the end.
		// A bit unconventional and forced.
		age = 0;
		pool[id2].age = 0;
	}
	private void fightWith(int id2) {
		health -= pool[id2].species.fight;
		pool[id2].health -= species.fight;
	}
	private void moveRandom() {
		pos = World.neighbour[Constants.RANDOM.nextInt(5)][pos];
	}
	
	private void die(float bloodFactor) {
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
		
		if (Constants.SpeciesId.BEST_BLOODLING_ID == id) {
			Constants.SpeciesId.BEST_BLOODLING_ID = -1;
			Constants.SpeciesId.BEST_BLOODLING_SCORE = 0;
		}
		if (Constants.SpeciesId.BEST_GRASSLER_ID == id) {
			Constants.SpeciesId.BEST_GRASSLER_ID = -1;
			Constants.SpeciesId.BEST_GRASSLER_SCORE = 0;
		}
		
		pos = -1;
		oldPos = -1;
	}
}
