package agents;

public abstract class Agent {
	
	public static final int MAX_AGE = 3000;

	public float age;
	public float maxAge;
	
	public float health;
	protected float healPower;
	protected float maxHealth;
	
	protected float size;
	protected float growth;
	protected float maxSize;
	
	public boolean isAlive;
	
	public Agent(float health) {
		age = 0;
		this.health = health;
		
		maxAge = MAX_AGE; //TODO: move these
		healPower = 0.01f;
		maxHealth = 100;
		
		size = 1;
		growth = 0.01f;
		maxSize = 1;
	}

	/**
	 * Called once per simulation round.
	 * Updates the agent. 
	 * @return 
	 */
	protected abstract boolean stepAgent();
		
	/**
	 * Gathers carbon from air. Gathers blood, fat and fiber from ground.
	 */
	protected abstract void harvest();
	
	/**
	 * Increases size.
	 * Costs energy.
	 */
	protected abstract void grow();
	
	/**
	 * Increases health.
	 * Costs energy.
	 */
	protected abstract void heal();
	
	/**
	 * Kills the agent. 
	 * Puts blood, fiber and fat on ground.
	 */
	protected abstract void die();
	
	/**
	 * Increases age. 
	 * Kills agent if too old.
	 */
	public boolean age() {
		age++;
		if (age > maxAge) {
			die();
			return false;
		}
		return true;
	}
}


