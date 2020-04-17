package actions;

import java.util.ArrayList;

import agents.Animal;
import world.World;

public class ActionManager {
	public ArrayList<Action> acts = new ArrayList<>();
	
	public enum Actions {
		HarvestGrass,
		HarvestBlood,
		HarvestTree,
		RandomWalk,
		FleeFromStranger,
		FleeFromFriendler,
		HuntStranger,
		HuntFriendler
	}

	public ActionManager(World world) {
		acts.add(new HarvestGrass(world));
		acts.add(new HarvestBlood(world));
		acts.add(new HarvestTree());
		acts.add(new RandomWalk());
		acts.add(new FleeFromStranger());
		acts.add(new FleeFromFriendler());
		acts.add(new HuntStranger());
		acts.add(new HuntFriendler());
	}

	public void determineIfPossibleAllActions(Animal a) {
		for (Action act : acts) {
			act.determineIfPossible(a);
			act.numPossible += act.isPossible ? 1 : 0;
		}
	}
	
	public void act(Animal a) {
		for (Action act : acts) {
			act.determineIfPossible(a);
			act.numPossible += act.isPossible ? 1 : 0;
		}
	}
	
	public void reset() {
		for (Action act : acts) {
			act.numCommits = 0;
			act.numPossible = 0;
		}
	}
	
	public float getTotCalls() {
		float tot = 0;
		for (Action act : acts) {
			tot += act.numCommits;
		}
		return tot;
	}

	public void commit(int action, Animal agent) {
		acts.get(action).commit(agent);
	}
	
	public static int getNumActions() {
		return Actions.values().length;
	}

	public Action getAction(Actions actionE) {
		int a = actionE.ordinal();
		return acts.get(a);
	}
}
