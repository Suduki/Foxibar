package agents;

import java.util.ArrayList;

import org.joml.Vector2f;

import actions.Action;
import actions.ActionI;
import constants.Constants;
import skills.SkillSet;
import vision.Vision;
import world.World;

public abstract class Agent {

	public static final int MAX_AGE = 3000;
	public static final float BIRTH_HUNGER_COST = 10/Stomach.FAT_TO_ENERGY_FACTOR;
	protected static final float REACH = 1;

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

	protected ArrayList<Agent> children;
	Agent parent;

	public Stomach stomach;
	private int timeBetweenBabies = 80;

	private boolean starving;

	public World world;
	protected AgentManager<? extends Agent> agentManager;
	public boolean printStuff;
	
	protected SkillSet skillSet;
	
	public Agent stranger;
	public Agent friendler;

	public Agent(float health, World world, AgentManager agentManager) {
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
		
		this.skillSet = new SkillSet();
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
		actionUpdate();
		makeBaby();
		internalOrgansUpdate();

		return isAlive;
	}

	private void makeBaby() {
		if (isFertile && stomach.canHaveBaby(skillSet.get(SkillSet.MATE_COST))) {
			mate();
		}
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

	protected void inherit(Agent a) {
		if (a == null) {
			skillSet.inheritRandom();
		}
		else if (a.getClass() != this.getClass()){
			System.err.println("inheriting some different class");
		}
		stomach.inherit(a.skillSet);
	}

	private void stepScore(int score) {
		this.score += score;
		if (parent != null) {
			parent.stepScore(score+1);
		}
	}

	private void childCost() {
		isFertile = false;
		stomach.energyCost += skillSet.get(SkillSet.MATE_COST);
		sinceLastBaby = 0;

		// This will cause the mating animals to continue living, which is what we want in the end.
		// A bit unconventional and forced.
		age = 0;
	}

	protected float getSpeed() {
		return skillSet.get(SkillSet.SPEED);
	}

	private void stepFertility() {
		if (sinceLastBaby++ > timeBetweenBabies) {
			isFertile = true;
		}
	}

	public void move() {
		old.set(pos);
		vel.mul(getSpeed());
		pos.add(vel);
		World.wrap(pos);
		
		if (pos.x == Float.NaN) {
			System.err.println("NaN position!!! What did you do!");
		}
		if (pos.x < 0 || pos.y < 0) {
			System.err.println("Negative position!!! What did you do!");
		}
		
		agentManager.vision.updateAgentZone(this);
	}

	
	protected abstract void actionUpdate();
	
	private void internalOrgansUpdate() {
		starving = !stomach.stepStomach();
		if(!age()) {
			return;
		}

		if (!starving) {
			stepFertility();
			grow();
			heal();
		}
		else {
			starve();
		}
		checkHealth();
	}

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
	public void attack(Agent a) {
		if (Vision.calculateCircularDistance(pos, a.pos) < REACH) {
			fightWith(a);
		}
	}
	
	protected void fightWith(Agent agent) {
		agent.health -= getFightSkill();
	}

	public void turnAwayFrom(Agent a) {
		Vision.getDirectionOf(vel, a.pos, pos);
	}
	
	public void turnTowards(Agent a) {
		Vision.getDirectionOf(vel, pos, a.pos);
	}
	
	private final static float TWO_PI = (float)Math.PI * 2;
	public void randomWalk() {
		float angle = Constants.RANDOM.nextFloat() * TWO_PI;
		vel.x = (float) Math.cos(angle);
		vel.y = (float) Math.sin(angle);
	}

	protected float getFightSkill() {
		return skillSet.get(SkillSet.FIGHT);
	}
	
	protected final float harvestSkill = 0.5f;//TODO: Kan en p användas här? Nä?
	
	public void harvestBlood() {
		stomach.addBlood(world.blood.harvest(harvestSkill, (int) pos.x, (int) pos.y));
	}
	public void harvestGrass() {
		stomach.addFiber(world.grass.harvest(harvestSkill, (int) pos.x, (int) pos.y));
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
		world.blood.append((int) pos.x, (int) pos.y, stomach.blood + size, true);
		world.blood.append((int) pos.x, (int) pos.y, stomach.fat / Stomach.getMAX_B(), true);
		world.grass.append((int) pos.x, (int) pos.y, stomach.fiber, true);
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
	
	public abstract boolean isCloselyRelatedTo(Agent a);

	protected boolean isSameClassAs(Agent a) {
		return a != null && a.getClass() == this.getClass();
	}

	protected boolean isFertileAndNotHungry() {
		return isFertile && stomach.canHaveBaby(skillSet.get(SkillSet.MATE_COST));
	}

	public void reset() {
		isAlive = true;
		age = 0;
		trueAge = 0;
		score = 0;
		sinceLastBaby = 0;
		health = 0.1f;
		incarnation++;
	}

	/**
	 * 
	 * @return random number between [-1, 1]
	 */
	protected static float rand() { //TODO: Move to util class
		return 2*Constants.RANDOM.nextFloat() - 1;
	}
}