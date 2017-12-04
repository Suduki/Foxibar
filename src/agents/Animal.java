package agents;

import messages.LoadBrains;
import simulation.Simulation;
import vision.Vision;
import world.World;
import constants.Constants;

public class Animal extends Agent {
	
	public static final int HUNGRY_HUNGER = 100;
	private static final float HUNGER_AT_BIRTH = 60;
	public static final int AGE_DEATH = 1000;
	public static final int BIRTH_HUNGER_COST = 80;


	public Species species = new Species();
	
	
	public boolean isAlive = false;
	private int timeBetweenBabies = 10;


	private int age;
	public int oldPos;
	private NeuralNetwork neuralNetwork = new NeuralNetwork(false);


	private int score;
	private int sinceLastBaby;
	private float hunger;
	private float health;
	private float[] primaryColor = new float[3];
	private float[] secondaryColor = new float[3];
	private int size;
	
	// The animals that can be seen are stored here
	public Animal[] nearbyAnimals = new Animal[Constants.NUM_NEIGHBOURS];
	// Distances to seen animals
	public float[] nearbyAnimalsDistance = new float[Constants.NUM_NEIGHBOURS];
	
	@Override
	public float update(float passedTime) {
		look();
		think();
		move();
		interact();
		stepTime(passedTime);
		
		return speed;
	}
	


	private short bestDir;
	private Animal animalToHunt;
	private Animal animalToMateWith;
	private boolean[] directionWalkable = new boolean[5];
	private int oldX;
	private int oldY;
	@Override
	public void die(float bloodFactor) {
		isAlive = false;
		World.blood.append(pos, bloodFactor);
	}
	
	/**
	 * Called from agentHandler when waking one animal from pool of dead, should not be used elsewhere.
	 * @param mom
	 * @param dad
	 */
	public void init(Animal mom, Animal dad) {
		isAlive = true;
		pos = mom.pos;
		age = 0;
		score = 0;
		oldPos = pos;
		sinceLastBaby = 0;
		hunger = HUNGER_AT_BIRTH;
		health = 0.1f;
		speed = species.speed;
		
		species.inherit(mom.species, dad.species);
		neuralNetwork.inherit(mom.neuralNetwork, dad.neuralNetwork);

		primaryColor[0] = Constants.RANDOM.nextFloat();
		primaryColor[1] = Constants.RANDOM.nextFloat();
		primaryColor[2] = Constants.RANDOM.nextFloat();
		
		switch (mom.species.speciesId) {
			case Constants.SpeciesId.BLOODLING:
				secondaryColor [0] = 1;
				secondaryColor[1] = 0;
				secondaryColor[2] = 0;
				AgentHandler.numBloodlings++;
				size = 2;
				break;
			case Constants.SpeciesId.GRASSLER:
				secondaryColor[0] = 1;
				secondaryColor[1] = 1;
				secondaryColor[2] = 1;
				AgentHandler.numGrasslers++;
				size = 1;
				break;
			default:
				System.err.println("aaaaa what is this?");
		}
	}
	
	
	// *************** PRIVATE HELPERS ************** \\
	private void interact() {
		if (animalToHunt != null) {
			fight();
		}
		if (animalToMateWith != null) {
			mate();
		}
		harvestBlood();
		harvestGrass();
	}
	private void think() {
		if (!findBestDir()) {
			System.err.println("Warning: found no best dir, should not happen?");
		}
	}
	private void look() {
		Vision.updateNearestNeighbours(this);		
	}
	private void stepTime(float passedTime) {
		hunger -= passedTime;
		if (hunger < 0) {
			die(Constants.Blood.DEATH_FROM_HUNGER_FACTOR);
		}
		sinceLastBaby += passedTime;
		age += passedTime;
		if (age > AGE_DEATH) {
			die(Constants.Blood.DEATH_FROM_AGE_FACTOR);
		}
		health += passedTime * species.healing;
	}
	private void move() {
		moveTo(bestDir);
	}
	private boolean findBestDir() {
		animalToHunt = null;
		animalToMateWith = null;
		
		for (int tile = 0; tile < 5; ++tile) {
			
			if (Simulation.agentHandler.containsAgents[World.neighbour[tile][pos]] == -1 || 
					Simulation.agentHandler.containsAgents[World.neighbour[tile][pos]] == id) {
				directionWalkable [tile] = true;
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
			for (Animal nearbyAnimalId : nearbyAnimals) {
				
				if (nearbyAnimalId == null) {
					continue;
				}
				
				float distance = (float) Vision.calculateCircularDistance(World.neighbour[tile][pos], nearbyAnimalId.pos);
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
						animalToHunt = nearbyAnimalId;
					}
				}
				if (!looksDangerous(nearbyAnimalId)) {
					neuralNetwork.z[tile][0][NeuralFactors.TILE_FRIENDS] = Math.max(distanceFactor, neuralNetwork.z[tile][0][NeuralFactors.TILE_FRIENDS]);
//					neuralNetwork.z[0][NeuralFactors.TILE_FRIENDS] += distanceFactor / Constants.NUM_NEIGHBOURS;
				}
				if (isFertileWith(nearbyAnimalId)) {
					neuralNetwork.z[tile][0][NeuralFactors.TILE_FERTILITY] = Math.max(distanceFactor, neuralNetwork.z[tile][0][NeuralFactors.TILE_FERTILITY]);
					if (distanceFactor == 1) {
						animalToMateWith = nearbyAnimalId;
					}
				}
				
			}
		}
		bestDir = (short) neuralNetwork.neuralMagic(directionWalkable);
		return true;
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
		Vision.updateAnimalZone(this);
	}
	
	private void mate() {
		Simulation.agentHandler.spawnAnimal(this, animalToMateWith);
		hunger -= BIRTH_HUNGER_COST;
		sinceLastBaby = 0;
		
		animalToMateWith.hunger -= BIRTH_HUNGER_COST;
		animalToMateWith.sinceLastBaby = 0;
		
		
		// This will cause the mating animals to continue living, which is what we want in the end.
		// A bit unconventional and forced.
		age = 0;
		animalToMateWith.age = 0;
	}
	private void fight() {
		health -= animalToHunt.species.fight;
		animalToHunt.health -= species.fight;
	}
	private boolean looksDangerous(Animal a) {
		return species.fight < a.species.fight;
	}

	private boolean looksWeak(Animal a) {
		return species.fight > a.species.fight;
	}

	private boolean isFertileWith(Animal a) {
		return isFriendWith(a) && isFertile() && a.isFertile();
	}

	private boolean isFriendWith(Animal a) {
		return species.speciesId == a.species.speciesId;
	}
	private boolean isHungry() {
		return hunger < HUNGRY_HUNGER;
	}
	private boolean isFertile() {
		return sinceLastBaby > timeBetweenBabies && !isHungry();
	}

}

