package agents;

import constants.Constants;
import vision.Vision;
import world.World;

public class Animal extends Agent {
	
	public int score = 0;
	
	public int sinceLastBaby = 0;
	boolean isFertile;
	
	Integer id;
	
	public float size;
	
	public int pos;
	public int oldPos;
	public int oldX;
	public int oldY;
	
	public float hunger;
	public Animal[] nearbyAnimals;
	public float[] nearbyAnimalsDistance;
	
	float recover;
	
	public Stomach stomach;
	public Brain brain;
	
	public Species species;
	private int timeBetweenBabies = 10;

	public float muscle;

	private boolean starving;
	
	public Animal(float health, int id, Species species) {
		super(health);
		this.species = species;
		this.nearbyAnimals = new Animal[Constants.NUM_NEIGHBOURS];
		this.nearbyAnimalsDistance = new float[Constants.NUM_NEIGHBOURS];
		this.brain = new Brain(false);
		this.id = id;
		
		stomach = new Stomach();
		
		isAlive = false;
	}

	@Override
	public boolean stepAgent() {
		// Step recover. This is to enable different speeds for different agents.
		recover += getSpeed();
		if (recover < 1f){
			return isAlive;
		}
		recover--;
		
		if (!isAlive) {
			System.out.println("Trying to step a dead agent.");
			return false;
		}
		
		actionUpdate();
		internalOrgansUpdate();
		
		return isAlive;
	}
	private void actionUpdate() {
		look();
		int direction = think(); // direction points to a tile position where to move.
		interact(direction);
		move(direction);
		harvest();
	}
	private void internalOrgansUpdate() {
		if (stomach.stepStomach(species)) {
			starving = false;
		}
		else {
			starving = true;
		}
		if(!age()) {
			return;
		}
		stepFertility();
		
		if (!starving) {
			grow();
			heal();
		}
		else {
			starve();
		}
		checkHealth();
		
	}
	
	private void checkHealth() {
		if (health < 0) {
			die();
		}
	}

	private void starve() {
		health --;
	}
	
	private void mateWith(Animal animal) {
		if (isFertileWith(animal)) {
			World.animalManager.mate(animal, this);
			this.childCost();
			animal.childCost();
		}
	}
	
	private void childCost() {
		isFertile = false;
		stomach.energyCost += Species.BIRTH_HUNGER_COST;
		sinceLastBaby = 0;
		
		// This will cause the mating animals to continue living, which is what we want in the end.
		// A bit unconventional and forced.
		age = 0;
	}

	/**
	 * Speed is a value between (0, speed, 1]
	 * Recommended between (0.5, speed, 1]
	 * Used to step initiative (recover)
	 * 
	 * Reduced by agent mass.
	 * Increased by agent muscle.
	 * @return speed
	 */
	private float getSpeed() {
		float totalMass = (muscle + stomach.getMass())*size;
		return muscle / totalMass;
	}
	
	private void look() {
		Vision.updateNearestNeighbours(this);
	}

	private void stepFertility() {
		if (sinceLastBaby++ > timeBetweenBabies) {
			isFertile = true;
		}
	}

	private void move(int direction) {
		oldPos = pos;
		if (World.animalManager.containsAnimals[World.neighbour[direction][pos]] == null) {
			pos = World.neighbour[direction][pos];
			Vision.updateAnimalZone(this);
		}
	}

	/**
	 * Interacts with the agent at direction
	 * @param positionToInteractWith
	 */
	private void interact(int positionToInteractWith) {
		Agent agent = getAgentAt(positionToInteractWith);
		if (agent != null && agent.getClass() == Animal.class) {
			mateWith((Animal) agent);
		}
	}

	
	private Agent getAgentAt(int position) {
		return World.animalManager.getAgentAt(position);
	}

	private double[] tileGoodness = new double[5];

	/**
	 * Decides where to go.
	 * @return 
	 */
	private int think() {
		for (int tile = 0; tile < 4; ++tile) {
			int tilePos = World.neighbour[tile][pos];
			Agent animalOnTile = getAgentAt(tilePos); 
			
			brain.neural.z[tile][0][NeuralFactors.HUNGER] = stomach.getRelativeFullness();
			if (brain.neural.z[tile][0][NeuralFactors.HUNGER] > 1) {
				brain.neural.z[tile][0][NeuralFactors.HUNGER] = 1; 
				System.out.println("brain.neural.z[tile][0][NeuralFactors.HUNGER] > 1:  " + brain.neural.z[tile][0][NeuralFactors.HUNGER]);
			}
			
			
			brain.neural.z[tile][0][NeuralFactors.AGE] = ((float)age)/Species.AGE_DEATH;
			
			brain.neural.z[tile][0][NeuralFactors.TILE_FIBER] = World.grass.height[tilePos] + 
					World.fiber.height[tilePos] - 
					World.grass.height[pos] - World.fiber.height[pos];
			brain.neural.z[tile][0][NeuralFactors.TILE_FAT] = World.fat.height[tilePos] - World.fat.height[pos];
			brain.neural.z[tile][0][NeuralFactors.TILE_BLOOD] = World.blood.height[tilePos] - World.blood.height[pos];
			brain.neural.z[tile][0][NeuralFactors.TILE_TERRAIN_HEIGHT] = 
					World.terrain.height[tilePos] - World.terrain.height[pos];
			
			brain.neural.z[tile][0][NeuralFactors.TILE_DANGER] = animalOnTile == null ? -1 : 1;
			brain.neural.z[tile][0][NeuralFactors.TILE_HUNT] = 0;
			brain.neural.z[tile][0][NeuralFactors.TILE_FRIENDS] = 0;
			brain.neural.z[tile][0][NeuralFactors.TILE_FERTILITY] = 0;
			
			// Loop through the sighted animals to determine tile goodnesses
			for (Animal nearbyAnimalId : nearbyAnimals) {
				
				if (nearbyAnimalId == null) {
					continue;
				}
				
				float distance = (float) Vision.calculateCircularDistance(tilePos, nearbyAnimalId.pos);
				if (distance > Constants.MAX_DISTANCE_AN_ANIMAL_CAN_SEE) {
					continue;
				}
				float distanceFactor = 1f-distance/Constants.MAX_DISTANCE_AN_ANIMAL_CAN_SEE;
				
				// 0 distanceF means the animal is too far away
				if (distanceFactor < 0  || distanceFactor > 1) {
					System.err.println("distanceFactor = " + distanceFactor);
				}
				
				if (looksDangerous(nearbyAnimalId)) {
					brain.neural.z[tile][0][NeuralFactors.TILE_DANGER] = Math.max(distanceFactor, brain.neural.z[tile][0][NeuralFactors.TILE_DANGER]);
				}
				else {
					brain.neural.z[tile][0][NeuralFactors.TILE_HUNT] = Math.max(distanceFactor, brain.neural.z[tile][0][NeuralFactors.TILE_HUNT]);
					brain.neural.z[tile][0][NeuralFactors.TILE_FRIENDS] = Math.max(distanceFactor, brain.neural.z[tile][0][NeuralFactors.TILE_FRIENDS]);
				}
				
				if (isFertileWith(nearbyAnimalId)) {
					brain.neural.z[tile][0][NeuralFactors.TILE_FERTILITY] = Math.max(distanceFactor, brain.neural.z[tile][0][NeuralFactors.TILE_FERTILITY]);
				}
				
			}
		}
		return brain.neural.neuralMagic();
	}

	@Override
	protected void harvest() {
		float neuralOutput = 1f;//TODO
		float harvestSkill = 0.5f;//TODO: Kan en p användas här? Nä?
		float amount = World.fat.harvest(harvestSkill, pos);
		stomach.addFat(amount);
		if (neuralOutput > 0) {
			// Harvest fiber
			if (harvestSkill > amount) {
				float tmp = World.fiber.harvest(harvestSkill - amount, pos);
				stomach.addFiber(tmp);
				amount += tmp;
			}
			if (harvestSkill > amount) {
				float tmp = World.grass.harvest(harvestSkill - amount, pos);
				stomach.addFiber(tmp);
				amount += tmp;
			}
		}
		else {
			// Harvest blood
			if (harvestSkill > amount) {
				float tmp = World.blood.harvest(harvestSkill - amount, pos);
				stomach.addBlood(tmp);
				amount += tmp;
			}
		}
	}

	@Override
	protected void grow() {
		if (size < maxSize) {
			float oldSize = size;
			size += growth;
			if (size > maxSize) {
				size = maxSize;
			}
			stomach.addGrowCost(size - oldSize); 
		}
	}

	@Override
	protected void heal() {
		if (health < maxHealth) {
			float oldHealth = health;
			health += healPower;
			if (health > maxHealth) {
				health = maxHealth;
			}
			stomach.addHealCost(health - oldHealth);
		}
	}

	@Override
	protected void die() {
		Vision.removeAnimalFromZone(this);
		species.someoneDied(this);
		World.animalManager.someoneDied(this);
		World.blood.append(pos, stomach.blood);
		World.fiber.append(pos, stomach.fiber);
		World.fat.append(pos, stomach.fat);
		stomach.empty();
		
		isAlive = false;
		
	}
	
	
	private boolean looksDangerous(Animal animal) {
		return getFightSkill() < animal.getFightSkill();
	}

	private float getFightSkill() {
		return muscle * size;
	}

	private boolean isFertileWith(Animal animal) {
		return isFriendWith(animal) && isFertile() && animal.isFertile();
	}

	private boolean isFriendWith(Animal animal) {
		return species.speciesId == animal.species.speciesId;
	}
	
	private boolean isFertile() {
		return isFertile;
	}
}
