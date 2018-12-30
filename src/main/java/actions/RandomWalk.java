package actions;

import agents.Agent;

public class RandomWalk extends Action {
	
	public RandomWalk() {
		super();
	}

	@Override
	public boolean determineIfPossible(Agent a) {
		isPossible = true;
		return isPossible;
	}

	@Override
	public void commit(Agent a) {
		numCommits++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());
		a.randomWalk();
		a.move();
	}

}