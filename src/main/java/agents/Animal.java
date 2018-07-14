package agents;

import constants.Constants;
import main.Main;
import simulation.Simulation;
import vision.Vision;
import world.World;

public class Animal extends Agent {
	public int incarnation = 0;
	public int score = 0;
	
	public int sinceLastBaby = 0;
	boolean isFertile;
	
	public float size;
	
	public int pos;
	public int oldPos;
	public int oldX;
	public int oldY;
	
	public Animal[] nearbyAnimals;
	public float[] nearbyAnimalsDistance;
	
	float recover;
	
	public Stomach stomach;
	public Brain brain;
	
	public Species species;
	private int timeBetweenBabies = 10;

	private boolean starving;
	
	private World world;
	private AnimalManager animalManager;
	
	public Animal(float health, Species species, World world, AnimalManager animalManager) {
		super(health);
		this.species = species;
		this.nearbyAnimals = new Animal[Constants.NUM_NEIGHBOURS];
		this.nearbyAnimalsDistance = new float[Constants.NUM_NEIGHBOURS];
		this.brain = new Brain(false);
		
		stomach = new Stomach();
		
		isAlive = false;
		
		this.world = world;
		this.animalManager = animalManager;
	}

	/**
	 * Steps the animal one time step.
	 * @return whether the animal is alive or not.
	 */
	@Override
	public boolean stepAgent() {
		if (!isAlive) {
			System.err.println("Trying to step a dead agent.");
			return false;
		}
		if (!timeToMove()) return isAlive;
		
		actionUpdate();
		internalOrgansUpdate();
		
		return isAlive;
	}
	/**
	 * Step recover. This is to enable different speeds for different agents.
	 * @return
	 */
	private boolean timeToMove() {
		recover += getSpeed();
		if (recover < 1f){
			return false;
		}
		recover--;
		return true;
	}

	private void actionUpdate() {
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
			animalManager.mate(animal, this);
			this.childCost();
			animal.childCost();
			this.score++;
			animal.score++;
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
	 * TODO: Reduced by agent mass.
	 * TODO: Increased by agent muscle.
	 * @return speed
	 */
	private float getSpeed() {
//		float totalMass = (stomach.getMass());
//		return 1f / totalMass;
		return 1f;
	}
	
	private void stepFertility() {
		if (sinceLastBaby++ > timeBetweenBabies) {
			isFertile = true;
		}
	}

	private void move(int tileDir) {
		oldPos = pos;
		int tilePos = World.neighbour[tileDir][pos];
		if (world.containsAnimals[tilePos] == null) {
			pos = tilePos;
		}
	}

	/**
	 * Interacts with the agent at direction
	 * @param tileToInteractWith
	 */
	private void interact(int tileToInteractWith) {
		int tilePos = World.neighbour[tileToInteractWith][pos];
		Agent agent = world.getAgentAt(tilePos);
		if (agent != null && agent != this && agent.getClass() == Animal.class) {
			if (brain.neural.getOutput(NeuralFactors.OUT_AGGRESSIVE) > 0) {
				fightWith((Animal) agent);
			}
			else if (((Animal)agent).species == species){
				mateWith((Animal) agent);
			}
		}
	}

	
	private void fightWith(Animal agent) {
		agent.health -= getFightSkill();
	}

	/**
	 * Decides where to go.
	 * @return 
	 */
	private int think() {
		for (int tile = 0; tile < 4; ++tile) {
			int tilePos = World.neighbour[tile][pos];
			
			brain.neural.z[tile][0][NeuralFactors.HUNGER] = stomach.getRelativeFullness();
			if (brain.neural.z[tile][0][NeuralFactors.HUNGER] > 1) {
				brain.neural.z[tile][0][NeuralFactors.HUNGER] = 1; 
				System.out.println("brain.neural.z[tile][0][NeuralFactors.HUNGER] > 1:  " + brain.neural.z[tile][0][NeuralFactors.HUNGER]);
			}
			
			
			brain.neural.z[tile][0][NeuralFactors.AGE] = ((float)age)/maxAge;
			
//			brain.neural.z[tile][0][NeuralFactors.TILE_FIBER] = World.grass.height[tilePos] + 
//					World.fiber.height[tilePos] - 
//					World.grass.height[pos] - World.fiber.height[pos];
			brain.neural.z[tile][0][NeuralFactors.TILE_FAT] = world.fat.height[tilePos] - world.fat.height[pos];
			brain.neural.z[tile][0][NeuralFactors.TILE_BLOOD] = world.blood.height[tilePos] - world.blood.height[pos];
			brain.neural.z[tile][0][NeuralFactors.TILE_TERRAIN_HEIGHT] = 
					world.terrain.height[tilePos] - world.terrain.height[pos];
			
			brain.neural.z[tile][0][NeuralFactors.TILE_DANGER] = 0;
			brain.neural.z[tile][0][NeuralFactors.TILE_HUNT] = 0;
			brain.neural.z[tile][0][NeuralFactors.TILE_FRIENDS] = 0;
			brain.neural.z[tile][0][NeuralFactors.TILE_FERTILITY] = 0;
			
			// Loop through the sighted animals to determine tile goodnesses
			for (Animal nearbyAnimalId : nearbyAnimals) {
				
				if (nearbyAnimalId == null) {
					continue;
				}
				
				float distance = (float) Vision.calculateCircularDistance(tilePos, nearbyAnimalId.pos);
				if (distance > Constants.Vision.MAX_DISTANCE_AN_ANIMAL_CAN_SEE) {
					continue;
				}
				float distanceFactor = 1f-distance/Constants.Vision.MAX_DISTANCE_AN_ANIMAL_CAN_SEE;
				
				// 0 distanceF means the animal is too far away
				if (distanceFactor < 0  || distanceFactor > 1) {
					System.err.println("distanceFactor = " + distanceFactor);
				}
				
				if (looksDangerous(nearbyAnimalId)) {
//					brain.neural.z[tile][0][NeuralFactors.TILE_DANGER] = Math.max(distanceFactor, brain.neural.z[tile][0][NeuralFactors.TILE_DANGER]);
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
		float neuralOutput = brain.neural.getOutput(NeuralFactors.OUT_HARVEST);//TODO
		float harvestSkill = 0.5f;//TODO: Kan en p användas här? Nä?
		float amount = world.fat.harvest(harvestSkill, pos);
		stomach.addFat(amount);
		if (neuralOutput > 0) {
			// Harvest fiber
//			if (harvestSkill > amount) {
//				float tmp = World.fiber.harvest(harvestSkill - amount, pos);
//				stomach.addFiber(tmp);
//				amount += tmp;
//			}
			if (harvestSkill > amount) {
				float tmp = world.grass.harvest(harvestSkill - amount, pos);
				stomach.addFiber(tmp);
				amount += tmp;
			}
		}
		else {
			// Harvest blood
			if (harvestSkill > amount) {
				float tmp = world.blood.harvest(harvestSkill - amount, pos);
				stomach.addBlood(tmp);
				amount += tmp;
			}
		}
	}

	@Override
	protected void grow() {
		if (size < maxSize) {
			size += growth;
			if (size > maxSize) {
				size = maxSize;
			}
		}
	}

	@Override
	protected void heal() {
		if (health < maxHealth) {
			health += healPower;
			if (health > maxHealth) {
				health = maxHealth;
			}
		}
	}

	@Override
	protected void die() {
		world.blood.append(pos, stomach.blood);
//		world.blood.append(pos, size);
//		world.fiber.append(pos, stomach.fiber);
		world.fat.append(pos, stomach.fat);
		System.out.println("in die(), fat = " + stomach.fat + "sincelastbaby = " + sinceLastBaby
				+ "age=" + age);
		stomach.empty();
		species.someoneDied(this);
		
		isAlive = false;
		
	}
	
	
	private boolean looksDangerous(Animal animal) {
		return getFightSkill() < animal.getFightSkill();
	}

	private float getFightSkill() {
		return species.fightSkill;
	}

	private boolean isFertileWith(Animal animal) {
		return isFriendWith(animal) && isFertileAndNotHungry() && animal.isFertileAndNotHungry();
	}

	private boolean isFriendWith(Animal animal) {
		return species.speciesId == animal.species.speciesId;
	}
	
	private boolean isFertileAndNotHungry() {
		
		return isFertile && stomach.canHaveBaby(Species.BIRTH_HUNGER_COST);
	}

	public void reset() {
		isAlive = true;
		age = 0;
		score = 0;
		sinceLastBaby = 0;
		recover = 0f;
		health = 0.1f;
		incarnation++;
	}
}
