package plant;

import agents.Agent;
import constants.Constants;
import simulation.Simulation;

public class Plant extends Agent {
	
	// health is always maxHealth, unless harvested
	// size is a value from 0 to ground_growth, controlled by how the tree has grown
	
	public static final float MAX_AGE = 1000;
	public static final float GROWTH = 0.005f;
	public static float WANTED_AVERAGE_AMOUNT_OF_PLANTS;

	public float leafness() {return health * size;}
	public float groundGrowth;
	
	public float[] color;
	public float[] secondaryColor;

	public Plant() {
		super();
		maxAge = (int) MAX_AGE;
		maxHealth = 1;
		
		color = new float[] {Constants.RANDOM.nextFloat(), Constants.RANDOM.nextFloat(), Constants.RANDOM.nextFloat()};
		secondaryColor = new float[] {Constants.RANDOM.nextFloat(), Constants.RANDOM.nextFloat(), Constants.RANDOM.nextFloat()};
		
		WANTED_AVERAGE_AMOUNT_OF_PLANTS = Simulation.WORLD_SIZE / 30;
	}

	@Override
	public boolean stepAgent() {
		age();
		if (health < maxHealth/2) {
			age += MAX_AGE / 1000;
		}
		return isAlive;
	}
	
	@Override
	public boolean age() {
		grow();
		return super.age();
	}
	
	private void grow() {
		if (health < maxHealth) {
			health = Float.min(health + GROWTH * groundGrowth, maxHealth);
		}
		else if (size < groundGrowth) {
			size = Float.min(size + GROWTH * groundGrowth, groundGrowth);
		}
	}

	public void setGroundGrowth(float groundGrowth) {
		this.groundGrowth = groundGrowth;
	}

	public float harvest(float amount) {
		float oldLeafness = leafness();
		health = Float.max(0, health - amount);
		
		return oldLeafness - leafness();
	}
	
	@Override
	public void reset() {
		super.reset();
		this.health = maxHealth;
	}
}
