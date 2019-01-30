package actions;

import agents.Animal;

public class HuntStranger extends Action {
	public HuntStranger() {
		super();
	}
	
	@Override
	public boolean determineIfPossible(Animal a) {
		isPossible = (a.stranger != null);
		return isPossible;
	}

	@Override
	public void commit(Animal a) {
		numCommits++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());

		a.turnTowards(a.stranger);
		a.move();
		a.attack(a.stranger);
	}
}
