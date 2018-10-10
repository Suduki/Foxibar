package actions;

public abstract class Action implements ActionI {
	private static int numActions = 0;
	
	public int numCalls;
	
	public boolean isPossible;
	public static Action[] acts;
	
	public static SeekGrass 		seekGrass 			= new SeekGrass();
	public static SeekBlood 		seekBlood 			= new SeekBlood();
	public static SeekFat 			seekFat 			= new SeekFat();
	public static RandomWalk 		randomWalk 			= new RandomWalk();
	public static FleeFromStranger 	fleeFromStranger 	= new FleeFromStranger();
	public static FleeFromFriendler fleeFromFriendler 	= new FleeFromFriendler();
	public static HuntStranger 		huntStranger 		= new HuntStranger();
	public static HuntFriendler 	huntFriendler		= new HuntFriendler();
	
	
	public static void init() {
		if (isInitialized()) {
			return;
		}
		System.out.println("init Actions");
		acts = new Action[] {
				seekGrass,
				seekBlood,
				seekFat,
				randomWalk,
				fleeFromStranger,
				fleeFromFriendler,
				huntStranger,
				huntFriendler
			};
		numActions = acts.length;
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
			act.numCalls = 0;
		}		
	}

	public static float getTotCalls() {
		float tot = 0;
		for (Action act : Action.acts) {
			tot += act.numCalls;
		}
		return tot;
	}
}
