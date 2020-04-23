package plant;

import agents.Agent;
import constants.Constants;
import simulation.Simulation;

public class Plant extends Agent {

	// health is always maxHealth, unless harvested
	// size is a value from 0 to ground_growth, controlled by how the tree has grown

	public static final float MAX_AGE = 2000;
	public static final float FINAL_HOURS_TIME = 200;
	public static final float GROWTH = 0.005f;
	public static final float HEALING = 0.0005f;
	public static float WANTED_AVERAGE_AMOUNT_OF_PLANTS() {return Simulation.WORLD_SIZE / 25f;}

	public float leafness() {
		return health * size;
	}
	
	public float getHeightOfLowestLeaves() {
		return leavesStartHeight + (1f - leavesStartHeight) * size * (1f - health);
	}
	
	private float leavesStartHeight = 0f;

	public float groundGrowth;

	public float[] color;
	public float[] secondaryColor;
	public float[] springColor;
	public float[] secondarySpringColor;
	public float[] summerColor;
	public float[] secondarySummerColor;
	public float[] autumnColor;
	public float[] secondaryAutumnColor;

	public Plant() {
		super();

		maxAge = (int) MAX_AGE;
		maxHealth = 1;
	}

	@Override
	public boolean stepAgent() {
		grow();
		age();
		
		return isAlive;
	}

	private void grow() {
		if (age < MAX_AGE - FINAL_HOURS_TIME) {
			if (health < maxHealth) {
				health = Float.min(health + HEALING * groundGrowth, maxHealth);
			} else if (size < groundGrowth) {
				size = Float.min(size + GROWTH * groundGrowth, groundGrowth);
			}
		} else {
			size -= groundGrowth / FINAL_HOURS_TIME;
		}
		
		
		float[] toColor = null;
		float[] toSecColor= null;
		
		
		if (Simulation.simulationTime % 1000 > 750) {
			// WINTER
			float brightness = 1f;
			toColor = new float[] {brightness, brightness, brightness};
			toSecColor = secondarySummerColor;
		}
		else if (Simulation.simulationTime % 1000 > 500) {
			toColor = autumnColor;
			toSecColor = secondaryAutumnColor;
		}
		else if (Simulation.simulationTime % 1000 > 250) {
			toColor = summerColor;
			toSecColor = secondarySummerColor;
		}
		else {
			toColor = springColor;
			toSecColor = secondarySpringColor;
		}
		
		float[] speedOfLosingColor = { 0.99f, 0.99f, 0.99f };
		
		for (int i = 0; i < 3; ++i) {
			color[i] = color[i] * speedOfLosingColor[i] + toColor[i] * (1f - speedOfLosingColor[i]);
			secondaryColor[i] = secondaryColor[i] * speedOfLosingColor[i]
							+ toSecColor[i] * (1f - speedOfLosingColor[i]);
		}
	}

	public void setGroundGrowth(float groundGrowth) {
		this.groundGrowth = groundGrowth;
	}

	public float harvest(float amount) {
		float oldLeafness = leafness();
		health = Float.max(0, (oldLeafness - amount)/size);
		return oldLeafness - leafness();
	}

	@Override
	public void reset() {
		super.reset();
		this.health = maxHealth;
		randomizeColors();
	}

	private void randomizeColors() {
		summerColor = new float[] { 0.2f* Constants.RANDOM.nextFloat(), 0.3f + 0.5f * Constants.RANDOM.nextFloat(), 0 };
		secondarySummerColor = new float[] { 0.2f* Constants.RANDOM.nextFloat(), 0.3f + 0.5f * Constants.RANDOM.nextFloat(), 0 };
		springColor = new float[] { Constants.RANDOM.nextFloat(), Constants.RANDOM.nextFloat(), Constants.RANDOM.nextFloat() };
		secondarySpringColor = new float[] { Constants.RANDOM.nextFloat(), Constants.RANDOM.nextFloat(), Constants.RANDOM.nextFloat() };
		autumnColor = new float[] { 0.8f * Constants.RANDOM.nextFloat(), 0.5f * Constants.RANDOM.nextFloat(), 0 };
		secondaryAutumnColor = new float[] { 0.8f * Constants.RANDOM.nextFloat(), 0.5f * Constants.RANDOM.nextFloat(), 0 };
		color = new float[] {0,0,0};
		secondaryColor = new float[] {0,0,0};
	}
}
