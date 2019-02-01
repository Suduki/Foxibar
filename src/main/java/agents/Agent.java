package agents;

import org.joml.Vector2f;

import vision.Vision;
import world.World;

public abstract class Agent {

	public World world;
	
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

	public Agent(World world) {
		this.world = world;

		pos = new Vector2f();
	}


	/**
	 * Steps the agent one time step.
	 * @return whether the agent is alive or not.
	 */
	public abstract boolean stepAgent();


	protected abstract void die();


	protected abstract void updateNearestNeighbours(Vision vision);


	public void reset() {
		didMove = false;
		didMate = false;
		
		isAlive = true;
		age = 0;
		trueAge = 0;
		health = 0.1f;
		incarnation++;		
	}


	protected abstract void addToChildren(Agent child);

	protected abstract void inherit(Agent parent);


	public void resetPos(float x, float y) {
		pos.x = x;
		pos.y = y;		
	}


	protected abstract void addParent(Agent a);
}
