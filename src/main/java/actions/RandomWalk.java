package actions;

import agents.Animal;

public class RandomWalk extends Action {
	
	public RandomWalk() {
		super();
	}

	@Override
	public boolean determineIfPossible(Animal a) {
		isPossible = true;
		return isPossible;
	}

	@Override
	public void commit(Animal a) {
		numCommits++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());
		a.randomWalk();
		a.move();
	}

}