package agents;


import org.joml.Vector2f;

import actions.Action;
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
	
	@Override
	protected void actionUpdate() {
		int action = think();
		Action.acts[action].commit(this);
	}
	
	private int think() {
		for (int i = 0; i < Action.getNumActions(); ++i) {
			Action act = Action.acts[i];
			act.determineIfPossible(this);
		}
		
		if (stranger != null) {
			brain.neural.z[0][NeuralFactors.in.STRANGER] = 1f / (1f + Vision.calculateCircularDistance(pos, stranger.pos));
		}
		else {
			brain.neural.z[0][NeuralFactors.in.STRANGER] = -1f;
		}
		
		if (friendler != null) {
			brain.neural.z[0][NeuralFactors.in.FRIENDLER] = 1f / (1f + Vision.calculateCircularDistance(pos, friendler.pos));
		}
		else {
			brain.neural.z[0][NeuralFactors.in.FRIENDLER] = -1f;
		}
		
		brain.neural.z[0][NeuralFactors.in.TILE_GRASS] = Action.seekGrass.grassness;
		brain.neural.z[0][NeuralFactors.in.TILE_BLOOD] = Action.seekBlood.bloodness;
		
		brain.neural.z[0][NeuralFactors.in.TILE_TERRAIN_HEIGHT] = world.terrain.height[(int) pos.x][(int) pos.y];
		
		brain.neural.z[0][NeuralFactors.in.HUNGER] = stomach.getRelativeFullness();
		
		return brain.neural.neuralMagic(Action.acts);
	}

	@Override
	public void inherit(Agent a) {
		if (a == null) {
			this.brain.neural.initWeightsRandom();
			stomach.inherit(rand());
			inheritAppearanceFactors(null);
		}
		else if (!(a instanceof Brainler)) {
			System.err.println("Trying to inherit a non-animal.");
			return;
		}
		else {
			this.brain.inherit(((Brainler)a).brain);
			stomach.inherit(a.stomach.p);
			inheritAppearanceFactors((Brainler)a);
		}
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
			color[i] = (float) Math.round(appearanceFactors[i]);
			secondaryColor[i] = (float) Math.round(appearanceFactors[i+3]);
		}
		secondaryColor[0] = -stomach.p;
		secondaryColor[1] = stomach.p;
		
	}
	
	@Override
	public boolean isCloselyRelatedTo(Agent a) {
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
		float brainOutput = brain.neural.getSpeed();
		float minSpeed = Constants.SkillSet.MIN_SPEED;
		if (brainOutput < -1) {brainOutput = -1;}
		else if (brainOutput > 1) {brainOutput = 1;}
		float speed = (1-minSpeed)/2 * brainOutput + (minSpeed+1)/2;
//		if (printStuff) {
//			System.out.println("brainOutput=" + brainOutput + ", speed = " + speed);
//		}
		return speed;
	}
	
	@Override
	protected float getFightSkill() {
		return 0.5f;
	}
}
