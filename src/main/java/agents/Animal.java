package agents;


import constants.Constants;
import vision.Vision;
import world.World;

public class Animal extends Agent {
	public Brain brain;
	
	public float[] appearanceFactors;
	public static final int NUM_APPEARANCE_FACTORS = 6;
	
	public Animal(float health, World world, AgentManager<Agent> agentManager) {
		super(health, world, agentManager);
		this.brain = new Brain(false);
		this.color = new float[3];
		this.secondaryColor = new float[3];
		this.appearanceFactors = new float[NUM_APPEARANCE_FACTORS];
	}

	private void fightWith(Agent agent) {
		agent.health -= getFightSkill();
	}

	@Override
	protected void think() {
//
//		int dirTowardsPrey = seekPrey();
//		int dirTowardsFriend = seekFriend();
//
//		float fullness = stomach.getRelativeFullness();
//		if (fullness > 1) {
//			fullness = 1; 
//			System.out.println("brain.neural.z[tile][0][NeuralFactors.HUNGER] > 1:  " + fullness);
//		}
//
//		for (int tile = 0; tile < 4; ++tile) {
//			brain.neural.z[tile][0][NeuralFactors.HUNGER] = fullness;
//			
//			if (tile == dirTowardsFriend) {
//				brain.neural.z[tile][0][NeuralFactors.TILE_FRIENDS] = 1;
//			}
//			else {
//				brain.neural.z[tile][0][NeuralFactors.TILE_FRIENDS] = -1;
//			}
//			if (tile == dirTowardsPrey) {
//				brain.neural.z[tile][0][NeuralFactors.TILE_PREY] = 1;
//			}
//			else {
//				brain.neural.z[tile][0][NeuralFactors.TILE_PREY] = -1;
//			}
//			
//			int tilePos = World.neighbour[tile][pos];
//
//			brain.neural.z[tile][0][NeuralFactors.TILE_FAT] = world.fat.height[tilePos] - world.fat.height[pos];
//			brain.neural.z[tile][0][NeuralFactors.TILE_BLOOD] = world.blood.height[tilePos] - world.blood.height[pos];
//			brain.neural.z[tile][0][NeuralFactors.TILE_FIBER] = world.grass.height[tilePos] - world.grass.height[pos];
//			brain.neural.z[tile][0][NeuralFactors.TILE_TERRAIN_HEIGHT] = 
//					world.terrain.height[tilePos] - world.terrain.height[pos];
//
//		}
//		return brain.neural.neuralMagic();
	}

	private Agent seekFriend() {
		double closestDistance = 1000000;
		Agent closestFriend = null;
		for (Agent a : nearbyAgents) {
			if (a != null && isCloselyRelated(a)) {
				// This is an Animal of my species.
				double d = Vision.calculateCircularDistance(pos, a.pos);
				if (d < closestDistance) {
					closestFriend = a;
					closestDistance = d;
				}
			}
		}
		return closestFriend;
	}
	private Agent seekPrey() {
		double closestDistance = 1000000;
		Agent closestAgent = null;
		for (Agent a : nearbyAgents) {
			if (a != null && !isCloselyRelated(a)) {
				// This is not an Animal of my species.
				double d = Vision.calculateCircularDistance(pos, a.pos);
				if (d < closestDistance) {
					closestAgent = a;
					closestDistance = d;
				}
			}
		}
		return closestAgent;
	}

	@Override
	public void inherit(Agent a) {
		if (a == null) {
			this.brain.neural.initWeightsRandom();
			stomach.inherit(rand(), 0);
		}
		else if (!(a instanceof Animal)) {
			System.err.println("Trying to inherit a non-animal.");
			return;
		}
		else {
			this.brain.inherit(((Animal)a).brain);
			stomach.inherit(a.stomach.p, 0.1f);
		}
		inheritAppearanceFactors((Animal)a);
	}

	public static float MUTATION = 0.04f;
	private void inheritAppearanceFactors(Animal a) {
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
		if (a instanceof Animal) {
			return findRelationTo((Animal) a) < 0.005f;
		}
		return false;
	}
	
	public float findRelationTo(Animal a) {
		float relation = 0;
		for (int i = 0; i < NUM_APPEARANCE_FACTORS; ++i) {
			float delta = appearanceFactors[i] - a.appearanceFactors[i];
			relation += (delta*delta);
		}
		return relation;
	}


	@Override
	protected float getSpeed() {
		float brainOutput = brain.neural.getOutput(NeuralFactors.OUT_SPEED);
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
//		if (agent instanceof Animal && ((Animal) agent).species == species) {
//			
//		}
		if (brain.neural.getOutput(NeuralFactors.OUT_AGGRESSIVE) > 0) {
			if (!isCloselyRelated(agent)) {
				fightWith(agent);
			}
		}
	}
	@Override
	protected float getFightSkill() {
		return 0.5f;
	}

	@Override
	protected boolean isFriendWith(Agent agent) {
		return this.getClass() == Animal.class && isCloselyRelated(agent);
	}

}
