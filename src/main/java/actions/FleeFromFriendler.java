package actions;

import agents.Animal;

public class FleeFromFriendler extends Action {

	public FleeFromFriendler() {
		super();
	}
	
	@Override
	public boolean determineIfPossible(Animal a) {
		isPossible = (a.friendler != null);
		return isPossible;
	}

	@Override
	public void commit(Animal a) {
		numCommits++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());
		
		a.turnAwayFrom(a.friendler);
		a.move();
	}
}
