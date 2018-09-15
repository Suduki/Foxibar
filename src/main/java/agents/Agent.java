package agents;

import java.util.ArrayList;

import org.joml.Vector2f;

import constants.Constants;
import world.World;

public abstract class Agent {

	public static final int MAX_AGE = 3000;
	public static final float BIRTH_HUNGER_COST = 10/Stomach.FAT_TO_ENERGY_FACTOR;

	public float[] color, secondaryColor;

	public float age;
	public float trueAge;
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

	public Vector2f pos;
	public Vector2f old;
	public Vector2f vel;

	public Agent[] nearbyAgents;
	public float[] nearbyAgentsDistance;
	public Agent closestAgent;
	public float closestAgentDistance;

	protected ArrayList<Agent> children;
	Agent parent;

	float recover;

	public Stomach stomach;
	private int timeBetweenBabies = 40;

	private boolean starving;

	protected World world;
	protected AgentManager<? extends Agent> agentManager;
	public boolean moved;
	public boolean printStuff;

	public Agent(float health, World world, AgentManager agentManager) {
		age = 0;
		trueAge = 0;
		this.health = health;
		
		pos = new Vector2f();
		old = new Vector2f();
		vel = new Vector2f();

		maxAge = MAX_AGE; //TODO: move these constants
		healPower = 0.01f;
		maxHealth = 100;

		size = 1;
		growth = 0.01f;
		maxSize = 1;

		this.nearbyAgents = new Agent[Constants.NUM_NEIGHBOURS];
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
		moved = false;
		if (!isAlive) {
			System.err.println("Trying to step a dead agent.");
			return false;
		}
		if (timeToMove()) {
			moved = true;
			actionUpdate();
			internalOrgansUpdate();
			makeBaby();
		}
		else {
			internalOrgansUpdate();
		}

		return isAlive;
	}

	private void makeBaby() {
		if (isFertile && stomach.canHaveBaby(BIRTH_HUNGER_COST)) {
			mate();
		}
	}


	/**
	 * Step recover. This is to enable different speeds for different agents.
	 * @return
	 */
	private boolean timeToMove() {
		float speed = getSpeed();
		recover += speed;
		stomach.addRecoverCost(speed);
		if (recover < 1f) {
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

	protected void mate() {
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
		stomach.energyCost += BIRTH_HUNGER_COST;
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

	private void move() {
		old.set(pos);
		pos.add(vel);
		World.wrap(pos, Constants.WORLD_SIZE_V);
		
		agentManager.vision.updateAgentZone(this);
	}


	protected void interact() {
		if (closestAgent != null && closestAgent != this && closestAgentDistance < 1f) {
			interactWith(closestAgent);
		}
	}

	private void actionUpdate() {
		think(); // direction points to a tile position where to move.
		interact();
		move();
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

	/**
	 * Sets vel
	 */
	protected abstract void think();

	/**
	 * Increases age. 
	 * Kills agent if too old.
	 */
	public boolean age() {
		age++;
		trueAge++;
		if (age > maxAge) {
			die();
			return false;
		}
		return true;
	}
	
	/**
	 * Updates vel accordingly
	 * @return whether we've found blood
	 */
	protected boolean seekBlood() {
		return world.blood.seekHeight(0, vel, (int)pos.x, (int)pos.y);
	}
	
	/**
	 * Updates vel accordingly
	 * @return whether we've found fat
	 */
	protected boolean seekFat() {
		return world.fat.seekHeight(0, vel, (int)pos.x, (int)pos.y);
	}
	
	/**
	 * Updates vel accordingly
	 * @return whether we've found grass
	 */
	protected boolean seekGrass() {
		return world.grass.seekHeight(0.05f, vel, (int)pos.x, (int)pos.y);
	}
	
	private final static float TWO_PI = (float)Math.PI * 2;
	protected void randomWalk() {
		float angle = Constants.RANDOM.nextFloat() * TWO_PI;
		vel.x = (float) Math.cos(angle);
		vel.y = (float) Math.sin(angle);
	}

	protected abstract float getFightSkill();

	protected float getHarvestRatio() {
		if (stomach.p > 0) {return 1;}
		else { return 0;}
	}

	protected void harvest() {
		float harvestRatio = getHarvestRatio();
		float harvestSkill = 0.5f;//TODO: Kan en p användas här? Nä?
		float amountHarvestedSoFar = world.fat.harvest(harvestSkill, (int) pos.x, (int) pos.y);
		stomach.addFat(amountHarvestedSoFar);
		if (harvestRatio > 0) {
			// Harvest fiber
			if (harvestSkill > amountHarvestedSoFar) {
				float tmp = world.grass.harvest(harvestSkill - amountHarvestedSoFar, (int) pos.x, (int) pos.y);
				stomach.addFiber(tmp);
				amountHarvestedSoFar += tmp;
			}
			// Harvest blood
			if (harvestSkill > amountHarvestedSoFar) {
				float tmp = world.blood.harvest(harvestSkill - amountHarvestedSoFar, (int) pos.x, (int) pos.y);
				stomach.addFiber(tmp);
				amountHarvestedSoFar += tmp;
			}
		}
		else {
			// Harvest blood
			if (harvestSkill > amountHarvestedSoFar) {
				float tmp = world.blood.harvest(harvestSkill - amountHarvestedSoFar, (int) pos.x, (int) pos.y);
				stomach.addBlood(tmp);
				amountHarvestedSoFar += tmp;
			}
			// Harvest fiber
			if (harvestSkill > amountHarvestedSoFar) {
				float tmp = world.grass.harvest(harvestSkill - amountHarvestedSoFar, (int) pos.x, (int) pos.y);
				stomach.addFiber(tmp);
				amountHarvestedSoFar += tmp;
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
		if (age > 100) {
			world.blood.append((int) pos.x, (int) pos.y, stomach.blood + size);
			world.fat.append((int) pos.x, (int) pos.y, stomach.fat);
			world.grass.append((int) pos.x, (int) pos.y, stomach.fiber);
		}
		//		System.out.println("in die(), fat = " + stomach.fat + ", sincelastbaby = " + sinceLastBaby
		//				+ ", age=" + age + ", score = " + score);

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
		return isFertile && stomach.canHaveBaby(BIRTH_HUNGER_COST);
	}

	public void reset() {
		isAlive = true;
		age = 0;
		trueAge = 0;
		score = 0;
		sinceLastBaby = 0;
		recover = 0f;
		health = 0.1f;
		incarnation++;
	}

	/**
	 * 
	 * @return random number between [-1, 1]
	 */
	protected static float rand() {
		return 2*Constants.RANDOM.nextFloat() - 1;
	}


	protected abstract void interactWith(Agent agent);
}