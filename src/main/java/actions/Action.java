package actions;

import agents.Agent;

public abstract class Action {
	private static int numActions = 0;
	
	public int numCommits;
	public int numPossible;
	
	public int id;
	public static int nextId = 0;
	
	public boolean isPossible;
	public static Action[] acts;
	
	
	public static SeekGrass 		seekGrass 			= new SeekGrass();
	public static SeekBlood 		seekBlood 			= new SeekBlood();
	public static HarvestGrass 		harvestGrass 		= new HarvestGrass();
	public static HarvestBlood 		harvestBlood 		= new HarvestBlood();
	public static RandomWalk 		randomWalk 			= new RandomWalk();
	public static FleeFromStranger 	fleeFromStranger 	= new FleeFromStranger();
	public static FleeFromFriendler fleeFromFriendler 	= new FleeFromFriendler();
	public static HuntStranger 		huntStranger 		= new HuntStranger();
	public static HuntFriendler 	huntFriendler		= new HuntFriendler();
	
	public abstract boolean determineIfPossible(Agent a);
	public abstract void commit(Agent a);
	
	public Action() {
		id = nextId++;
	}
	
	public static void init() {
		if (isInitialized()) {
			return;
		}
		System.out.println("init Actions");
		acts = new Action[] {
				seekGrass,
				seekBlood,
				harvestGrass,
				harvestBlood,
				randomWalk,
				fleeFromStranger,
				fleeFromFriendler,
				huntStranger,
				huntFriendler
			};
		numActions = acts.length;
	}
	
	public static void determineIfPossibleAllActions(Agent a) {
		for (int i = 0; i < Action.getNumActions(); ++i) {
			acts[i].determineIfPossible(a);
			acts[i].numPossible += acts[i].isPossible ? 1 : 0;
		}
	}

	public static int getNumActions() {
		init();
		return numActions;
	}
	
	private static boolean isInitialized() {
		return numActions != 0;
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
}
