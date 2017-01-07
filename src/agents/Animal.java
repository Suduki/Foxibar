package agents;

import world.World;
import constants.Constants;
import static constants.Constants.Neighbours.*;

public class Animal {
	
	private int age = 0;
	private int sinceLastBaby = 0;
	private short id;
	public float size = 3f;
	public float[] color;
	private int pos;
	private boolean isAlive;
	private float hunger = 100;
	
	private float recover = 0f;
	
	
	//************ GENETIC STATS ************
	private class Skill {
		float grassHarvest;
		float grassDigestion;
		
		float bloodDigestion;
		float bloodHarvest;

		float speed;
		float fight;
		
		public Skill() {
			grassHarvest = Constants.RANDOM.nextFloat();
			grassDigestion = Constants.RANDOM.nextFloat();
			
			bloodDigestion = Constants.RANDOM.nextFloat();
			bloodHarvest = Constants.RANDOM.nextFloat();

			speed = Constants.RANDOM.nextFloat();
			fight = Constants.RANDOM.nextFloat();
		}
	}
	private Skill skill;
	private boolean isFertile;
	private int timeBetweenBabies = 10;
	
	
	//************ STATIC STUFF ************
	public static Animal[] pool = new Animal[Constants.MAX_NUM_ANIMALS];
	public static int numAnimals = 0;
	public static short[] containsAnimals;
	
	public static void createAnimals(int num) {
		for(int i = 0; i < num; ++i) {
			pool[resurrectAnimal(Constants.RANDOM.nextInt(Constants.WORLD_SIZE), 
					1f,	1f)].skill.speed = Constants.RANDOM.nextFloat(); 
		}
		pool[0].skill.speed = 1f;
		pool[0].color[0] = 1f;
		pool[0].color[1] = 0f;
		pool[0].color[2] = 1f;
	}
	public static void killAllAnimals() {
		for (Animal a : pool) {
			a.die();
		}
	}
	
	public static void moveAll() {
		for (Animal a : pool) {
			if (a.isAlive) {
				a.age++;
				a.sinceLastBaby++;
				a.recover += a.skill.speed;
				if (a.recover > 1f) {
					a.recover--;
					a.move();
				}
				if (!a.isFertile && a.sinceLastBaby > a.timeBetweenBabies) {
					a.isFertile = true;
				}
			}
		}
	}
	
	public static void init() {
		containsAnimals = new short[Constants.WORLD_SIZE];
		
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			containsAnimals[i] = -1;
		}
		
		for(int i = 0; i < Constants.MAX_NUM_ANIMALS; ++i) {
			pool[i] = new Animal();
		}
	}
	
	public static int resurrectAnimal(int pos, float g, float b) {
		short id = 0;
		while (pool[id].isAlive) {
			id++;
			if (id == Constants.MAX_NUM_ANIMALS) {
				return -1;
			}
		}
		pool[id].isAlive = true;
		
		pool[id].skill.fight= Constants.RANDOM.nextFloat();
		pool[id].skill.grassHarvest = 1f - pool[id].skill.fight;
		
		pool[id].color[0] = pool[id].skill.fight;
		pool[id].color[1] = pool[id].skill.grassHarvest;
		pool[id].color[2] = pool[id].skill.grassHarvest;
		
		pool[id].pos = pos;
		pool[id].id = id;
		pool[id].age = 0;
		pool[id].sinceLastBaby = 0;
		pool[id].recover = 0f;
		pool[id].hunger = 100;
		
		numAnimals++;
		containsAnimals[pool[id].pos] = id;
		
		return id;
	}
	
// ************ INSTANCE STUFF ************
	private Animal() {
		this.isAlive = false;
		this.color = new float[3];
		this.skill = new Skill();
	}
	private void move() {
		
		// Remove animal from the world temporarily.
		int oldPos = pos; 
		containsAnimals[pos] = -1;
		
		moveRandom();
		if (containsAnimals[pos] != -1) {
			this.interactWith(containsAnimals[pos]);
		}
		containsAnimals[pos] = id;
		
		hunger-=1f;
		if (hunger < 0) {
			die();
		}
		
	}
	private void interactWith(short id2) {
		if (isFertile && pool[id2].isFertile) {
			resurrectAnimal(pos, 0f, 0f);
			isFertile = false;
			pool[id2].isFertile = false;
		}
		else {
			if (pool[id2].skill.fight < skill.fight) {
				pool[id2].die();
			}
			else {
				die();
			}
		}
	}
	private void moveRandom() {
		pos = World.neighbour[Constants.RANDOM.nextInt(5)][pos];
	}
	
	private void die() {
		if (this.isAlive) {
			this.isAlive = false;
			numAnimals--;
			containsAnimals[pos] = -1;
		}
	}
}
