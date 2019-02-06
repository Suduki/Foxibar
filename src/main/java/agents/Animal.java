package agents;

import java.util.ArrayList;

import org.joml.Vector2f;

import actions.Action;
import constants.Constants;
import plant.Tree;
import talents.Talents;
import vision.Vision;
import world.World;

public abstract class Animal extends Agent {

	public static final int MAX_AGE = 200;
	protected static final float REACH = 1;

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
	
	public Tree[] nearbyTrees;
	public float[] nearbyTreesScore;

	protected ArrayList<Animal> children;
	Animal parent;

	public Stomach stomach;
	private int timeBetweenBabies = 80;

	private boolean starving;

	public boolean printStuff;
	
	public Talents talents;
	
	public Animal stranger;
	public Animal friendler;
	public boolean didMate;
	public boolean didMove;
	
	protected World world; // TODO: Remove this dependency. only die() uses it, could be moved to manager.
	
	public Animal(World world) {
		super();
		
		this.world = world;
		oldPos = new Vector2f();
		vel = new Vector2f();

		maxAge = MAX_AGE; //TODO: move these constants
		healPower = 0.01f;

		growth = 0.01f;
		maxSize = 1;

		this.nearbyAgents = new Animal[Constants.Vision.NUM_NEIGHBOURS];
		this.nearbyAgentsDistance = new float[Constants.Vision.NUM_NEIGHBOURS];
		children = new ArrayList<>();
		stomach = new Stomach();

		isAlive = false;

		this.talents = new Talents();

		this.color = new float[3];
		this.secondaryColor = new float[3];
	}

	@Override
	public boolean stepAgent() {
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
			didMate = true;
			this.childCost();
			this.stepScore(1);
		}
	}

	protected void inherit(Animal a) {
		if (a == null) {
			talents.inheritRandom();
		}
		else if (a.getClass() != this.getClass()){
			System.err.println("inheriting some different class");
		}
		else {
			talents.inherit(((Animal) a).talents);
		}
		fixAppearance();
	}
	
	protected void fixAppearance() {
		stomach.inherit(talents);
		maxHealth = 100*talents.talentsRelative[Talents.TOUGHNESS];
		maxSize = talents.talentsRelative[Talents.TOUGHNESS];
		maxTall = talents.talentsRelative[Talents.FIGHT];
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
		oldPos.set(pos);
		
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
		
		didMove = true;
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
	public void attack(Animal a) {
		if (Vision.calculateCircularDistance(pos, a.pos) < REACH) {
			fightWith(a);
		}
	}
	
	protected void fightWith(Animal agent) {
		agent.health -= getFightSkill();
	}

	public void turnAwayFrom(Animal a) {
		Vision.getDirectionOf(vel, a.pos, pos);
	}
	
	public void turnTowards(Animal a) {
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

	@Override
	public void die() {
		super.die();
		world.blood.append((int) pos.x, (int) pos.y, stomach.blood + size, true);
		world.blood.append((int) pos.x, (int) pos.y, stomach.fat / Constants.Talents.MAX_DIGEST_BLOOD, true);
		world.grass.append((int) pos.x, (int) pos.y, stomach.fiber, true);
		//		System.out.println("in die(), fat = " + stomach.fat + ", sincelastbaby = " + sinceLastBaby
		//				+ ", age=" + age + ", score = " + score);

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
	protected static float rand() { //TODO: Move to util class
		return 2*Constants.RANDOM.nextFloat() - 1;
	}
	
	protected void updateNearestNeighbours(Vision vision) {
		vision.updateNearestNeighbours(this);
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