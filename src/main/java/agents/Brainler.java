package agents;


import org.joml.Vector2f;

import actions.Action;
import constants.Constants;
import talents.Talents;
import vision.Vision;
import world.World;

public class Brainler extends Agent {
	public Brain brain;
	
	public float[] appearanceFactors;
	public static final int NUM_APPEARANCE_FACTORS = 6;
	
	public Brainler(World world, AgentManager<Agent> agentManager) {
		super(world, agentManager);
		this.brain = new Brain(false);
		this.color = new float[3];
		this.secondaryColor = new float[3];
		this.appearanceFactors = new float[NUM_APPEARANCE_FACTORS];
	}
	
	@Override
	protected void actionUpdate() {
		int action = updateBrainInputs();
		Action.acts[action].commit(this);
	}
	
	private int updateBrainInputs() {
		
		float[] brainInputVector = brain.neural.z[0];
		
		if (stranger != null) {
			brainInputVector[NeuralFactors.in.STRANGER] = 1f - (1f / (1f + Vision.calculateCircularDistance(pos, stranger.pos)));
		}
		else {
			brainInputVector[NeuralFactors.in.STRANGER] = -1f;
		}
		
		if (friendler != null) {
			brainInputVector[NeuralFactors.in.FRIENDLER] = 1f - (1f / (1f + Vision.calculateCircularDistance(pos, friendler.pos)));
		}
		else {
			brainInputVector[NeuralFactors.in.FRIENDLER] = -1f;
		}
		
		brainInputVector[NeuralFactors.in.TILE_GRASS] = Action.harvestGrass.grassness;
		brainInputVector[NeuralFactors.in.TILE_BLOOD] = Action.harvestBlood.bloodness;
		brainInputVector[NeuralFactors.in.SEEK_GRASS] = Action.seekGrass.grassness;
		brainInputVector[NeuralFactors.in.SEEK_BLOOD] = Action.seekBlood.bloodness;
		
		brainInputVector[NeuralFactors.in.TILE_TERRAIN_HEIGHT] = world.terrain.height[(int) pos.x][(int) pos.y];
		
		brainInputVector[NeuralFactors.in.HUNGER] = stomach.getRelativeFullness();
		
		return brain.neural.neuralMagic(Action.acts);
	}
	
	public static Brainler brainlerCreatedByUser;

	@Override
	public void inherit(Agent a) {
		if (a == null) {
			this.brain.neural.initWeightsRandom();
			if (brainlerCreatedByUser != null) {
				a = brainlerCreatedByUser;
			}
		}
		else if (!(a instanceof Brainler)) {
			System.err.println("Trying to inherit a non-animal.");
			return;
		}
		else {
			this.brain.inherit(((Brainler)a).brain);
		}
		
		super.inherit(a);
		
		inheritAppearanceFactors((Brainler)a);
	}

	public float appearanceMutation;
	public void inheritAppearanceFactors(Brainler a) {
		if (a == null) {
			appearanceMutation = 0.04f;
			for (int i = 0; i < NUM_APPEARANCE_FACTORS; ++i) {
				appearanceFactors[i] = Constants.RANDOM.nextFloat();
			}
		}
		else {
			for (int i = 0; i < NUM_APPEARANCE_FACTORS; ++i) {
				appearanceMutation = a.appearanceMutation;
				appearanceFactors[i] = a.appearanceFactors[i] + rand()*appearanceMutation;
				if (appearanceFactors[i] > 1) {appearanceFactors[i] = 1;}
				if (appearanceFactors[i] < 0) {appearanceFactors[i] = 0;}
			}
		}
		updateColors();
	}
	
	public void updateColors() {
		for (int i = 0; i < 3; ++i) {
			color[i] = (float) Math.round(appearanceFactors[i]);
			secondaryColor[i] = (float) Math.round(appearanceFactors[i+3]);
		}
		secondaryColor[0] = talents.talentsRelative[Talents.DIGEST_BLOOD];
		secondaryColor[1] = talents.talentsRelative[Talents.DIGEST_GRASS];
		secondaryColor[2] = 0;
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
		float minSpeed = Constants.Talents.MIN_SPEED;
		float maxSpeed = talents.get(Talents.SPEED);
		if (brainOutput < -1) {brainOutput = -1;}
		else if (brainOutput > 1) {brainOutput = 1;}
		float speed = (maxSpeed-minSpeed)/2 * brainOutput + (minSpeed+maxSpeed)/2;
		return speed;
	}
}
