package agents;

import org.joml.Vector2f;

public abstract class Agent {
	public int age;
	public int trueAge;
	public int maxAge;
	public float health;
	protected float maxHealth;
	public boolean isAlive;
	public float size;
	
	public int incarnation = 0;
	public Vector2f pos;

	public boolean didMove;
	public boolean didMate;

	public Agent() {
		pos = new Vector2f();
	}


	/**
	 * Steps the agent one time step.
	 * @return whether the agent is alive or not.
	 */
	public abstract boolean stepAgent();


	public void die() {
		isAlive = false;
	}


	public void reset() {
		didMove = false;
		didMate = false;
		
		size = 0;
		
		isAlive = true;
		age = 0;
		trueAge = 0;
		health = 0.1f;
		incarnation++;		
	}

	public void resetPos(float x, float y) {
		pos.x = x;
		pos.y = y;		
	}
}
