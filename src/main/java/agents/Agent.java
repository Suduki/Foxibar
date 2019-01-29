package agents;

import java.util.ArrayList;

import org.joml.Vector2f;

import actions.Action;
import constants.Constants;
import talents.Talents;
import vision.Vision;
import world.World;

public abstract class Agent {

	public static final int MAX_AGE = 200;
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
	
	public Talents talents;
	
	public Agent stranger;
	public Agent friendler;

	@SuppressWarnings("rawtypes")
	public Agent(World world, AgentManager agentManager) {
		pos = new Vector2f();
		old = new Vector2f();
		vel = new Vector2f();

		maxAge = MAX_AGE; //TODO: move these constants
		healPower = 0.01f;

		size = 1;
		growth = 0.01f;
		maxSize = 1;

		this.nearbyAgents = new Agent[Constants.Vision.NUM_NEIGHBOURS];
		this.nearbyAgentsDistance = new float[Constants.Vision.NUM_NEIGHBOURS];
		children = new ArrayList<>();
		stomach = new Stomach();

		isAlive = false;

		this.world = world;
		this.agentManager = agentManager;
		
		this.talents = new Talents();

		this.color = new float[3];
		this.secondaryColor = new float[3];
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
		think();
		actionUpdate();
		makeBaby();
		internalOrgansUpdate();

		return isAlive;
	}

	private void think() {
		Action.determineIfPossibleAllActions(this);
	}


	private void makeBaby() {
		if (isFertile && stomach.canHaveBaby(talents.get(Talents.MATE_COST))) {
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
			talents.inheritRandom();
		}
		else if (a.getClass() != this.getClass()){
			System.err.println("inheriting some different class");
		}
		else {
			talents.inherit(a.talents);
		}
		fixAppearance();
	}
	
	protected void fixAppearance() {
		stomach.inherit(talents);
		maxHealth = 100*talents.talentsRelative[Talents.TOUGHNESS];
		size = talents.talentsRelative[Talents.FIGHT]*10+talents.talentsRelative[Talents.TOUGHNESS]*10;
	}

	private void stepScore(int score) {
		this.score += score;
		if (parent != null) {
			parent.stepScore(score+1);
		}
	}

	private void childCost() {
		isFertile = false;
		stomach.energyCost += talents.get(Talents.MATE_COST);
		sinceLastBaby = 0;
	}

	protected float getSpeed() {
		return talents.get(Talents.SPEED);
	}

	private void stepFertility() {
		if (sinceLastBaby++ > timeBetweenBabies) {
			isFertile = true;
		}
	}

	public void move() {
		old.set(pos);
		
		vel.mul(getSpeed());
		vel.add(pos);
		World.wrap(vel);
		
		pos.set(vel);
		
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
		return talents.get(Talents.FIGHT);
	}
	
	public final float harvestSkill = 0.2f;//TODO: Kan en p användas här? Nä?
	
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
			health += maxHealth*0.0001f;
			if (health > maxHealth) {
				health = maxHealth;
			}
		}
	}

	protected void die() {
		world.blood.append((int) pos.x, (int) pos.y, stomach.blood + size, true);
		world.blood.append((int) pos.x, (int) pos.y, stomach.fat / Constants.Talents.MAX_DIGEST_BLOOD, true);
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
		return isFertile && stomach.canHaveBaby(talents.get(Talents.MATE_COST));
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