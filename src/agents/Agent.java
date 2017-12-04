package agents;

public abstract class Agent {

	protected Integer id;
	public int pos;
	public float time;
	protected float speed;
	
	public boolean isAlive;
	
	/**
	 * Called on update.
	 * 
	 * @return The time to sleep. Used to determine order of action for all agents.
	 */
	public abstract float update(float passedTime);
	
	public abstract void die(float bloodFactor);
}
