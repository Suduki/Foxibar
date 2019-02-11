package plant;

import agents.Agent;
import simulation.Simulation;

public class Plant extends Agent {
	
	// health is always maxHealth, unless harvested
	// size is a value from 0 to ground_growth, controlled by how the tree has grown
	
	public static final float MAX_AGE = 100;
	public static float WANTED_AVERAGE_AMOUNT_OF_PLANTS;

	public float leafness() {return health * size;}
	public float groundGrowth;

	public Plant() {
		super();
		maxAge = (int) MAX_AGE;
		maxHealth = 1;
		
		WANTED_AVERAGE_AMOUNT_OF_PLANTS = Simulation.WORLD_SIZE / 20;
	}

	@Override
	public boolean stepAgent() {
		age();
		return isAlive;
	}
	
	@Override
	public boolean age() {
		grow();
		return super.age();
	}
	
	private void grow() {
		if (health < maxHealth) {
			health = Float.min(health + 0.1f, maxHealth);
		}
		else if (size < groundGrowth) {
			size = Float.min(size + 0.1f, groundGrowth);
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
}
