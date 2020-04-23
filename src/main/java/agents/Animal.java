package agents;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector2i;

import actions.ActionManager;
import constants.Constants;
import plant.Plant;
import talents.Talents;
import vision.Vision;
import world.World;

public abstract class Animal extends Agent {

	public static final int MAX_AGE = 200;
	public static final float REACH = 1;

	public static final float HARVEST_GRASS = 1f;
	public static final float HARVEST_BLOOD = 0.1f;
	public static final float HARVEST_FIBER = 0.1f;

	public float[] color, secondaryColor;

	protected float healPower;

	protected float growth;
	public float maxTall;
	public float maxSize;

	public int score = 0;
	public int sinceLastBaby = 0;

	boolean isFertile;

	public Vector2f oldPos;
	public Vector2f vel;

	public Animal[] nearbyAgents;
	public float[] nearbyAgentsDistance;

	public Plant nearbyPlant;
	public float nearbyPlantScore;

	protected ArrayList<Animal> children;
	Animal parent;
	
	public Stomach stomach;
	private int timeBetweenBabies = 20;

	private boolean starving;

	public boolean printStuff;

	public Talents talents;

	public Animal stranger;
	public Animal friendler;
	public boolean didMate;
	public boolean didMove;

	public Vector2i visionZone = new Vector2i(-1, -1);

	protected World world; // TODO: Remove this dependency. only die() uses it, could be moved to manager.
	protected ActionManager actionManager;

	public Animal(World world, ActionManager actionManager) {
		super();

		this.actionManager = actionManager;
		this.world = world;
		oldPos = new Vector2f();
		vel = new Vector2f();

		maxAge = MAX_AGE; // TODO: move these constants
		healPower = 0.01f;

		growth = 0.01f;
		maxSize = 1;

		this.nearbyAgents = new Animal[Constants.Vision.NUM_NEIGHBOURS];
		this.nearbyAgentsDistance = new float[Constants.Vision.NUM_NEIGHBOURS];
		this.nearbyPlant = null;
		this.nearbyPlantScore = 0;

		children = new ArrayList<>();
		stomach = new Stomach();

		isAlive = false;

		this.talents = new Talents();

		this.color = new float[3];
		this.secondaryColor = new float[3];
	}

	@Override
	public boolean stepAgent() {
		oldPos.set(pos);

		internalOrgansUpdate();

		if (!isAlive) {
			return false;
		}
		think();
		actionUpdate();
		makeBaby();

		return isAlive;
	}

	private void think() {
		actionManager.determineIfPossibleAllActions(this);
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
		health--;
	}

	protected void mate() {
		if (isFertileAndNotHungry()) {
			didMate = true;
			this.childCost();
			this.stepScore(1);
		}
	}

	protected void inherit(Animal a) {
		if (a == null) {
			talents.inheritRandom();
		} else if (a.getClass() != this.getClass()) {
			System.err.println("inheriting some different class");
		} else {
			talents.inherit(((Animal) a).talents);
		}
		fixAppearance();
	}

	protected void fixAppearance() {
		stomach.inherit(talents);
		maxHealth = 100 * talents.getRelative(Talents.TOUGHNESS);
		maxSize = talents.getRelative(Talents.TOUGHNESS);
		maxTall = talents.getRelative(Talents.DIGEST_FIBER);
	}

	private void stepScore(int score) {
		this.score += score;
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
		vel.mul(getSpeed());
		vel.add(pos);
		World.wrap(vel);

		pos.set(vel);
		didMove = true;

		if (pos.x == Float.NaN) {
			System.err.println("NaN position!!! What did you do!");
		}
		if (pos.x < 0 || pos.y < 0) {
			System.err.println("Negative position!!! What did you do!");
		}
	}

	private static float COLLISION = 0.8f;

	public void collide(Animal a) {
		float distance = Vision.calculateCircularDistance(pos, a.pos);
		if (distance >= COLLISION) {
			return;
		}

		didMove = true;
		a.didMove = true;

		float velX = 0;
		float velY = 0;
		if (distance < 0.00001f) {
			velX = COLLISION / 2;
		} else {
			float distanceFactor = (COLLISION - distance) / (distance * 2);
			velX = (a.pos.x - pos.x) * distanceFactor;
			velY = (a.pos.y - pos.y) * distanceFactor;
		}

		pos.add(-velX, -velY);
		a.pos.add(velX, velY);

		World.wrap(pos);
		World.wrap(a.pos);

		if (pos.x == Float.NaN) {
			System.err.println("NaN position!!! What did you do!");
		}
		if (pos.x < 0 || pos.y < 0) {
			System.err.println("Negative position!!! What did you do!");
		}
	}

	protected abstract void actionUpdate();

	private void internalOrgansUpdate() {
		starving = !stomach.stepStomach();
		if (!age()) {
			return;
		}

		if (!starving) {
			stepFertility();
			grow();
			heal();
		} else {
			starve();
		}
		checkHealth();
	}

	public void attack(Animal a) {
		if (Vision.calculateCircularDistance(pos, a.pos) < REACH) {
			fightWith(a);
		}
	}

	protected void fightWith(Animal agent) {
		agent.health -= getFightSkill();
	}

	public void turnAwayFrom(Agent a) {
		Vision.getDirectionOf(vel, a.pos, pos);
	}

	public void turnTowards(Agent a) {
		Vision.getDirectionOf(vel, pos, a.pos);
	}

	private final static float TWO_PI = (float) Math.PI * 2;

	public void randomWalk() {
		float angle = Constants.RANDOM.nextFloat() * TWO_PI;
		vel.x = (float) Math.cos(angle);
		vel.y = (float) Math.sin(angle);
	}

	protected float getFightSkill() {
		return talents.get(Talents.FIGHT);
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
			health += maxHealth * 0.0001f;
			if (health > maxHealth) {
				health = maxHealth;
			}
		}
	}

	@Override
	public void die() {
		super.die();
		float bloodToAdd = stomach.blood + size + stomach.fat / (Constants.Talents.MAX_DIGEST_BLOOD + 1f);
		world.blood.append((int) pos.x, (int) pos.y, bloodToAdd, true);
		world.grass.append((int) pos.x, (int) pos.y, stomach.grass, true);
		// System.out.println("in die(), fat = " + stomach.fat + ", sincelastbaby = " +
		// sinceLastBaby
		// + ", age=" + age + ", score = " + score);

		for (Animal child : children) {
			child.parentDied();
		}
	}

	private void parentDied() {
		parent = null;
	}

	protected boolean looksDangerous(Animal nearbyAnimalId) {
		return getFightSkill() < nearbyAnimalId.getFightSkill();
	}

	public abstract boolean isCloselyRelatedTo(Animal a);

	protected boolean isSameClassAs(Animal a) {
		return a != null && a.getClass() == this.getClass();
	}

	protected boolean isFertileAndNotHungry() {
		return isFertile && stomach.canHaveBaby(talents.get(Talents.MATE_COST));
	}

	@Override
	public void reset() {
		super.reset();

		score = 0;
		sinceLastBaby = 0;
	}

	/**
	 * 
	 * @return random number between [-1, 1]
	 */
	protected static float rand() { // TODO: Move to util class
		return 2 * Constants.RANDOM.nextFloat() - 1;
	}

	protected void addToChildren(Agent a) {
		this.children.add((Animal) a);
	}

	protected void addParent(Agent a) {
		this.parent = (Animal) a;
	}

	@Override
	public void resetPos(float x, float y) {
		super.resetPos(x, y);
		oldPos.x = x;
		oldPos.y = y;
	}
}