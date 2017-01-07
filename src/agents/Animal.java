package agents;

import java.util.ArrayList;
import java.util.Collections;

import world.World;
import constants.Constants;
import static constants.Constants.Neighbours.*;

public class Animal {
	
	private int age = 0;
	private int sinceLastBaby = 0;
	private int id;
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
	}
	private Skill skill;
	private boolean isFertile;
	private int timeBetweenBabies = 10;
	
	
	//************ STATIC STUFF ************
	public static Animal[] pool = new Animal[Constants.MAX_NUM_ANIMALS];
	public static int numAnimals = 0;
	public static int[] containsAnimals;
	
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
		containsAnimals = new int[Constants.WORLD_SIZE];
		
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			containsAnimals[i] = -1;
		}
		
		for(int i = 0; i < Constants.MAX_NUM_ANIMALS; ++i) {
			pool[i] = new Animal();
		}
	}
	
	public static int resurrectAnimal(int pos, float g, float b) {
		int id = 0;
		while (pool[id].isAlive) {
			id++;
			if (id == Constants.MAX_NUM_ANIMALS) {
				return -1;
			}
		}
		pool[id].isAlive = true;
		
		
//		pool[id].skill.fight = Constants.RANDOM.nextFloat();
		pool[id].skill.grassHarvest = 1f;
		pool[id].skill.grassDigestion = 10f;
		pool[id].skill.bloodHarvest = 1f;
		pool[id].skill.bloodDigestion = 0f;
		pool[id].skill.fight = 0f;
		pool[id].skill.speed = 0.5f + 0.5f * Constants.RANDOM.nextFloat();
		
		
		pool[id].color[0] = pool[id].skill.fight;
		pool[id].color[1] = pool[id].skill.grassHarvest;
		pool[id].color[2] = pool[id].skill.grassHarvest;
		
		pool[id].pos = pos;
		pool[id].id = id;
		pool[id].age = 0;
		pool[id].sinceLastBaby = 0;
		pool[id].recover = 0f;
		pool[id].hunger = 3;
		
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
		containsAnimals[pos] = -1;
		
		// Calculate to where we want to move
		if (isHungry()) {
			short grassDir = searchForGrass();
			if (grassDir == INVALID_DIRECTION) {
				moveRandom();
			} else {
				moveTo(grassDir);
			}
			eatGrass();
		}
		else if (isFertile) {
			short direction = searchForFertile();
			if (direction == INVALID_DIRECTION) {
				moveRandom();
			} else {
				moveTo(direction);
			}
		}
		
		
		if (containsAnimals[pos] != -1) {
			this.interactWith(containsAnimals[pos]);
		}
		containsAnimals[pos] = id;
		
		
		hunger-=1f;
		if (hunger < 0) {
			die();
		}
		
	}
	private short searchForFertile() {
		for (short dir = 0; dir < 5; ++dir) {
			int posi;
			if ((posi = containsAnimals[World.neighbour[dir][pos]]) != -1) {
				if (pool[posi].isFertile) {
					return dir;
				}
			}
		}
		return INVALID_DIRECTION;
	}
	private boolean isHungry() {
		return hunger < 3;
	}
	private void eatGrass() {
		this.hunger += World.grass.cut(skill.grassHarvest, pos) * skill.grassDigestion;
	}
	private void moveTo(short to) {
		pos = World.neighbour[to][pos];
	}
	private void interactWith(int id2) {
		if (isFertile && pool[id2].isFertile) {
			resurrectAnimal(pos, 0f, 0f);
			isFertile = false;
			pool[id2].isFertile = false;
			sinceLastBaby = 0;
			pool[id2].sinceLastBaby = 0;
		}
		else {
//			if (pool[id2].skill.fight < skill.fight) {
//				pool[id2].die();
//			}
//			else {
//				die();
//			}
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
	
	public short oppositeDirection(short d) {
		if (d == EAST) {
			return WEST;
		}
		else if (d == WEST) {
			return EAST;
		}
		else if (d == NORTH) {
			return SOUTH;
		}
		else if (d == SOUTH) {
			return NORTH;
		}
		else {
			return NONE;
		}
	}
	
	public short searchForGrass() {
		
		double bestHeight = 0;
		short bestDirection = INVALID_DIRECTION;
		
		for (short i = 0; i < 5; ++i)
		{
			if (World.grass.height[World.neighbour[i][pos]] > bestHeight) {
				bestHeight = World.grass.height[World.neighbour[i][pos]];
				bestDirection = i;
			}
		}
		
		return bestDirection;
	}
}
