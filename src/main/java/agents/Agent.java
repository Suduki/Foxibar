package agents;

import java.util.ArrayList;

import constants.Constants;
import world.World;

public abstract class Agent {

	public static final int MAX_AGE = 3000;

	public float age;
	public float maxAge;

	public float health;
	protected float healPower;
	protected float maxHealth;

	public float size;
	protected float growth;
	protected float maxSize;

	public boolean isAlive;
	
	public int incarnation = 0;
	public int score = 0;
	
	public int sinceLastBaby = 0;
	boolean isFertile;
	
	public int pos;
	public int oldPos;
	public int oldX;
	public int oldY;
	
	public Agent[] nearbyAgents;
	public float[] nearbyAgentsDistance;
	
	protected ArrayList<Agent> children;
	Agent parent;
	
	float recover;
	
	public Stomach stomach;
	private int timeBetweenBabies = 10;

	private boolean starving;
	
	protected World world;
	protected AgentManager agentManager;

	public Agent(float health, World world, AgentManager agentManager) {
		age = 0;
		this.health = health;

		maxAge = MAX_AGE; //TODO: move these
		healPower = 0.01f;
		maxHealth = 100;

		size = 1;
		growth = 0.01f;
		maxSize = 1;
		
		this.nearbyAgents = new Animal[Constants.NUM_NEIGHBOURS];
		this.nearbyAgentsDistance = new float[Constants.NUM_NEIGHBOURS];
		children = new ArrayList<>();
		stomach = new Stomach();
		
		isAlive = false;
		
		this.world = world;
		this.agentManager = agentManager;
	}


	/**
	 * Steps the animal one time step.
	 * @return whether the animal is alive or not.
	 */
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
	private void checkHealth() {
		if (health < 0) {
			die();
		}
	}

	private void starve() {
		health --;
	}
	
	private void mate() {
		if (isFertileAndNotHungry()) {
			children.add(agentManager.mate(this));
			this.childCost();
			this.stepScore(1);
		}
	}
	
	public abstract void inherit(Agent a);
	
	private void stepScore(int score) {
		this.score += score;
		if (parent != null) {
			parent.stepScore(score+1);
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

	protected abstract float getSpeed() ;
	
	private void stepFertility() {
		if (sinceLastBaby++ > timeBetweenBabies) {
			isFertile = true;
		}
	}

	private void move(int tileDir) {
		oldPos = pos;
		int tilePos = World.neighbour[tileDir][pos];
		if (world.containsAgents[tilePos] == null) {
			pos = tilePos;
		}
	}
	
	/**
	 * Interacts with the agent at direction
	 * @param tileToInteractWith
	 */
	protected abstract void interact(int tileToInteractWith);
	
	
	private void actionUpdate() {
		int direction = think(); // direction points to a tile position where to move.
		interact(direction);
		move(direction);
		harvest();
	}
	private void internalOrgansUpdate() {
		if (stomach.stepStomach()) {
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

	protected abstract int think();

	/**
	 * Increases age. 
	 * Kills agent if too old.
	 */
	public boolean age() {
		age++;
		if (age > maxAge) {
			die();
			return false;
		}
		return true;
	}
	
	protected abstract float getFightSkill();

	protected abstract float getHarvestRatio();

	protected void harvest() {
		float neuralOutput = getHarvestRatio();
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

	protected void grow() {
		if (size < maxSize) {
			size += growth;
			if (size > maxSize) {
				size = maxSize;
			}
		}
	}

	protected void heal() {
		if (health < maxHealth) {
			health += healPower;
			if (health > maxHealth) {
				health = maxHealth;
			}
		}
	}

	protected void die() {
		world.blood.append(pos, stomach.blood);
//		world.blood.append(pos, size);
		world.grass.append(pos, stomach.fiber);
		world.fat.append(pos, stomach.fat);
		System.out.println("in die(), fat = " + stomach.fat + ", sincelastbaby = " + sinceLastBaby
				+ ", age=" + age + ", score = " + score);
		stomach.empty();
		
		if (this.getClass() == Animal.class) {
			((Animal)this).species.someoneDied((Animal)this); //TODO: UGLY; Override
		}
		
		for (Agent child : children) {
			child.parentDied();
		}
		
		isAlive = false;
		
	}
	
	
	private void parentDied() {
		parent = null;
	}

	protected boolean looksDangerous(Agent nearbyAnimalId) {
		return getFightSkill() < nearbyAnimalId.getFightSkill();
	}

	protected boolean isFriendWith(Agent animal) {
		return this.getClass() == animal.getClass();
	}
	
	protected boolean isFertileAndNotHungry() {
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


