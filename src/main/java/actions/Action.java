package actions;

import java.util.ArrayList;

import world.World;
import agents.Animal;

public abstract class Action {
	public static int numActions = 0;

	public int numCommits;
	public int numPossible;

	public int id;
	public static int nextId = 0;

	public boolean isPossible;
	public static ArrayList<Action> acts = new ArrayList<>();

	public static HarvestGrass 		harvestGrass;
	public static HarvestBlood 		harvestBlood;
	public static RandomWalk 		randomWalk;
	public static FleeFromStranger 	fleeFromStranger;
	public static FleeFromFriendler fleeFromFriendler;
	public static HuntStranger 		huntStranger;
	public static HuntFriendler 	huntFriendler;

	public abstract boolean determineIfPossible(Animal a);
	public abstract void commit(Animal a);

	public Action() {
		id = nextId++;
		acts.add(this);
	}

	public static void init(World world) {
		System.out.println("init Actions");
		harvestGrass 		= new HarvestGrass(world);
		harvestBlood 		= new HarvestBlood(world);
		randomWalk 			= new RandomWalk();
		fleeFromStranger 	= new FleeFromStranger();
		fleeFromFriendler 	= new FleeFromFriendler();
		huntStranger 		= new HuntStranger();
		huntFriendler		= new HuntFriendler();
		
		numActions = acts.size();
	}

	public static void determineIfPossibleAllActions(Animal a) {
		for (Action act : acts) {
			act.determineIfPossible(a);
			act.numPossible += act.isPossible ? 1 : 0;
		}
	}

	public static void reset() {
		for (Action act : Action.acts) {
			act.numCommits = 0;
			act.numPossible = 0;
		}
	}

	public static float getTotCalls() {
		float tot = 0;
		for (Action act : Action.acts) {
			tot += act.numCommits;
		}
		return tot;
	}
	
	public static void commit(int action, Animal agent) {
		acts.get(action).commit(agent);
	}
}
