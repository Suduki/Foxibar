package agents;


import org.joml.Vector2f;

import constants.Constants;
import vision.Vision;
import world.World;

public class Brainler extends Agent {
	public Brain brain;
	
	public float[] appearanceFactors;
	public static final int NUM_APPEARANCE_FACTORS = 6;
	
	public Brainler(float health, World world, AgentManager<Agent> agentManager) {
		super(health, world, agentManager);
		this.brain = new Brain(false);
		this.color = new float[3];
		this.secondaryColor = new float[3];
		this.appearanceFactors = new float[NUM_APPEARANCE_FACTORS];
	}

	// static to save memory. Don't know correct WoW, just guessing.
	private static Brainler friendler;
	private static Agent stranger;
	private static Vector2f grassDir = new Vector2f();
	private static Vector2f bloodDir = new Vector2f();
	private static Vector2f fatDir = new Vector2f();
	
	@Override
	protected void actionUpdate() {
		int action = think();
		switch (action) {
		case NeuralFactors.out.HARVEST_GRASS:
			actHarvestGrass();
			break;
		case NeuralFactors.out.HARVEST_BLOOD:
			actHarvestBlood();
			break;
		case NeuralFactors.out.FLEE_FROM_STRANGER:
			fleeFrom(stranger);
			break;
		case NeuralFactors.out.FLEE_FROM_FRIENDLER:
			fleeFrom(friendler);
			break;
		case NeuralFactors.out.HUNT_STRANGER:
			hunt(stranger);
			break;
		case NeuralFactors.out.HUNT_FRIENDLER:
			hunt(friendler);
			break;
		default:
			break;
		}
	}
	private void actHarvestGrass() {
		vel.set(grassDir);
		move();
		harvestGrass();

	}
	private void actHarvestBlood() {
		vel.set(bloodDir);
		move();
		harvestBlood();
	}
	private void fleeFrom(Agent a) {
		if (a == null) return;
		turnAwayFrom(a);
		move();
	}
	private void hunt(Agent a) {
		if (a == null) return;
		turnTowards(a);
		move();
		attack(a);
	}
	@Override
	protected int think() {
//
		seekStranger();
		if (stranger != null) {
			brain.neural.z[0][NeuralFactors.in.STRANGER] = 1f / (1f + Vision.calculateCircularDistance(pos, stranger.pos));
		}
		else {
			brain.neural.z[0][NeuralFactors.in.STRANGER] = -1f;
		}
		
		seekFriendler();
		if (stranger != null) {
			brain.neural.z[0][NeuralFactors.in.FRIENDLER] = 1f / (1f + Vision.calculateCircularDistance(pos, friendler.pos));
		}
		else {
			brain.neural.z[0][NeuralFactors.in.FRIENDLER] = -1f;
		}
		
		brain.neural.z[0][NeuralFactors.in.TILE_GRASS] = seekGrass(grassDir);
		brain.neural.z[0][NeuralFactors.in.TILE_BLOOD] = seekBlood(bloodDir);
		brain.neural.z[0][NeuralFactors.in.TILE_FAT] = seekFat(fatDir);
		
		brain.neural.z[0][NeuralFactors.in.HUNGER] = stomach.getRelativeFullness();
		
		return brain.neural.neuralMagic();
	}



	private void seekFriendler() {
		stranger = null;
		for (Agent a : nearbyAgents) {
			if (a != null && isCloselyRelated(a)) {
				friendler = (Brainler)a;
				return;
			}
		}
	}
	private void seekStranger() {
		stranger = null;
		for (Agent a : nearbyAgents) {
			if (a != null && !isCloselyRelated(a)) {
				stranger = a;
				return;
			}
		}
	}

	@Override
	public void inherit(Agent a) {
		if (a == null) {
			this.brain.neural.initWeightsRandom();
			stomach.inherit(rand(), 0);
		}
		else if (!(a instanceof Brainler)) {
			System.err.println("Trying to inherit a non-animal.");
			return;
		}
		else {
			this.brain.inherit(((Brainler)a).brain);
			stomach.inherit(a.stomach.p, 0.1f);
		}
		inheritAppearanceFactors((Brainler)a);
	}

	public static float MUTATION = 0.04f;
	private void inheritAppearanceFactors(Brainler a) {
		if (a == null) {
			for (int i = 0; i < NUM_APPEARANCE_FACTORS; ++i) {
				appearanceFactors[i] = Constants.RANDOM.nextFloat();
			}
		}
		else {
			for (int i = 0; i < NUM_APPEARANCE_FACTORS; ++i) {
				appearanceFactors[i] = a.appearanceFactors[i] + rand()*MUTATION;
				if (appearanceFactors[i] > 1) {appearanceFactors[i] = 1;}
				if (appearanceFactors[i] < 0) {appearanceFactors[i] = 0;}
			}
		}
		for (int i = 0; i < 3; ++i) {
			color[i] = appearanceFactors[i];
			secondaryColor[i] = appearanceFactors[i+3];
		}
	}
	
	public boolean isCloselyRelated(Agent a) {
		if (a instanceof Brainler) {
			return findRelationTo((Brainler) a) < 0.005f;
		}
		return false;
	}
	
	public float findRelationTo(Brainler a) {
		float relation = 0;
		for (int i = 0; i < NUM_APPEARANCE_FACTORS; ++i) {
			float delta = appearanceFactors[i] - a.appearanceFactors[i];
			relation += (delta*delta);
		}
		return relation;
	}


	@Override
	protected float getSpeed() {
		float brainOutput = brain.neural.getOutput(NeuralFactors.out.SPEED);
		float minSpeed = Stomach.minSpeed;
		if (brainOutput < -1) {brainOutput = -1;}
		else if (brainOutput > 1) {brainOutput = 1;}
		float speed = (1-minSpeed)/2 * brainOutput + (minSpeed+1)/2;
//		if (printStuff) {
//			System.out.println("brainOutput=" + brainOutput + ", speed = " + speed);
//		}
		return speed;
	}


	@Override
	protected void interactWith(Agent agent) {
		// Not used.
		System.err.println("interactWith not implemented for Brainler. Should not be here.");
	}
	
	@Override
	protected float getFightSkill() {
		return 0.5f;
	}
	@Override
	protected void interact() {
		System.err.println("interact not implemented for Brainler. Should not be here.");
	}

}
