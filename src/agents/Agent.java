package agents;

import utils.FibonacciHeap;

public abstract class Agent {

	protected Integer id;
	public int pos;
	public float time;
	protected float speed;
	
	public boolean isAlive;
	public FibonacciHeap.Entry<Agent> entry;
	
	/**
	 * Called on update.
	 * @param passedTime 
	 * 
	 * @return The time to sleep. Used to determine order of action for all agents.
	 */
	public abstract float update(float passedTime);
	
	public abstract void die(float bloodFactor);
}
