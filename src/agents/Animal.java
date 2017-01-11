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
	public float size = 1f;
	public float[] color;
	private int pos;
	private boolean isAlive;
	private float hunger;
	
	private float recover = 0f;
	
	private enum Activity {
		DO_NOTHING, HARVEST_GRASS, HARVEST_BLOOD;
		
		public final static int GRASS = 0;
		public final static int BLOOD = 1;
	};
	
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
	public static boolean killAll = false;
	
	public static void moveAll() {
		if (killAll) {
			for (Animal a : pool) {
				a.die();
				containsAnimals[a.pos] = -1;
			}
			System.out.println("Num animals alive after killing them all: " + numAnimals);
			numAnimals = 0;
			killAll = false;
		}
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
	
	public static int resurrectAnimal(int pos, float g, float b, float hunger) {
		int id = 0;
		while (pool[id].isAlive) {
			id++;
			if (id == Constants.MAX_NUM_ANIMALS) {
				return -1;
			}
		}
		pool[id].isAlive = true;
		
		
//		pool[id].skill.fight = Constants.RANDOM.nextFloat();
		pool[id].skill.grassHarvest = 0.2f;
		pool[id].skill.grassDigestion = 10*0.14f / pool[id].skill.grassHarvest;
		pool[id].skill.bloodHarvest = 0.2f;
		pool[id].skill.bloodDigestion = 0;//10*0.14f / pool[id].skill.bloodHarvest;
		pool[id].skill.fight = 0f;
		pool[id].skill.speed = 0.7f + 0.3f * Constants.RANDOM.nextFloat();
		
		
		pool[id].color[0] = 1;
		pool[id].color[1] = 1;
		pool[id].color[2] = 1;
		
		pool[id].pos = pos;
		pool[id].id = id;
		pool[id].age = 0;
		pool[id].sinceLastBaby = 0;
		pool[id].recover = 0f;
		pool[id].hunger = hunger;
		
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

		// Remove animal from the world temporarily :F
		containsAnimals[pos] = -1;
		
		// Calculate to where we want to move
		if (isHungry()) {
			short[] bestDir = new short[1];
			Activity[] bestChoice = new Activity[1];
			if (findBestDir(bestDir, bestChoice)) {
				moveTo(bestDir[0]);
				switch (bestChoice[0]) {
				case HARVEST_BLOOD:
					harvestBlood();
					break;
				case HARVEST_GRASS:
					harvestGrass();
					break;
				default:
					System.out.println("ERROR: invalid choice!");
					break;
				}
			}
			else {
				moveRandom();
			}
		}
		else if (isFertile) { // TODO, kan bakas in i switch-satsen
			short direction = searchForFertile();
			if (direction == INVALID_DIRECTION) {
				moveRandom();
			} else {
				moveTo(direction);
			}
			if (containsAnimals[pos] != -1) {
				this.interactWith(containsAnimals[pos]);
			}
		}
		
		hunger--;
		if (hunger < 0) {
			die();
		}
		
		// Add animal to the world again :)
		containsAnimals[pos] = id;
	}
	private boolean findBestDir(short[] bestDirOut, Activity[] bestChoice) {
		
		short[] bestDir = new short[2];
		float[] bestVal = new float[2];

		scanNeighboringEnvironment(bestDir, bestVal);
		
		if (bestDir[Activity.GRASS]*skill.grassDigestion > 
			bestDir[Activity.BLOOD]*skill.bloodDigestion) {
			bestDirOut[0] = bestDir[Activity.GRASS];
			bestChoice[0] = Activity.HARVEST_GRASS;
		}
		else {
			bestDirOut[0] = bestDir[Activity.BLOOD];
			bestChoice[0] = Activity.HARVEST_BLOOD;
		}

		if (bestDirOut[0] != INVALID_DIRECTION) {
			return true;
		}
		else {
			return false;
		}
	}


	private void scanNeighboringEnvironment(short[] bestDir, float[] bestVal) {
		bestDir[0] = INVALID_DIRECTION;
		bestDir[1] = INVALID_DIRECTION;
		bestVal[0] = 0;
		bestVal[1] = 0;
		for (short i = 0; i < 5; ++i)
		{
			if (World.grass.height[World.neighbour[i][pos]] > bestVal[Activity.GRASS]) {
				bestVal[Activity.GRASS] = World.grass.height[World.neighbour[i][pos]];
				bestDir[Activity.GRASS] = i;
			}
			if (World.blood.height[World.neighbour[i][pos]] > bestVal[Activity.BLOOD]) {
				bestVal[Activity.BLOOD] = World.grass.height[World.neighbour[i][pos]];
				bestDir[Activity.BLOOD] = i;
			}
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
		return hunger < 5;
	}
	private void harvestGrass() {
		this.hunger += World.grass.harvest(skill.grassHarvest, pos) * skill.grassDigestion;
	}
	private void harvestBlood() {
		this.hunger += World.blood.harvest(skill.bloodHarvest, pos) * skill.bloodDigestion;
	}
	
	private void moveTo(short to) {
		pos = World.neighbour[to][pos];
	}
	private void interactWith(int id2) {
		if (isFertile && pool[id2].isFertile) {
			resurrectAnimal(pos, 0f, 0f, 3);
			isFertile = false;
			hunger -= 2f;
			sinceLastBaby = 0;
			pool[id2].isFertile = false;
			pool[id2].hunger -= 2f;
			pool[id2].sinceLastBaby = 0;
		}
	}
	private void moveRandom() {
		pos = World.neighbour[Constants.RANDOM.nextInt(5)][pos];
	}
	
	private void die() {
		if (this.isAlive) {
			numAnimals--;
		}
		containsAnimals[pos] = -1;
		isAlive = false;
		World.blood.append(pos);
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
}
