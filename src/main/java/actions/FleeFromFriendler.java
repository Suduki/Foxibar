package actions;

import agents.Agent;

public class FleeFromFriendler extends Action {

	public FleeFromFriendler() {
		super();
	}
	
	@Override
	public boolean determineIfPossible(Agent a) {
		isPossible = (a.friendler != null);
		return isPossible;
	}

	@Override
	public void commit(Agent a) {
		numCommits++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());
		
		a.turnAwayFrom(a.friendler);
		a.move();
	}
}
