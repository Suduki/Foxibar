package agents;

import org.joml.Vector2f;

import world.World;

public class Agent {

	public World world;
	
	public float age;
	public float maxAge;
	public float health;
	protected float maxHealth;
	public boolean isAlive;
	
	public int incarnation = 0;
	public Vector2f pos;
	
	public Agent(World world) {
		this.world = world;

		pos = new Vector2f();
	}
	

}
