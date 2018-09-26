package actions;

import agents.Agent;

public class HuntFriendler extends Action {

	@Override
	public boolean determineIfPossible(Agent a) {
		isPossible = (a.friendler != null);
		return isPossible;
	}

	@Override
	public void commit(Agent a) {
		numCalls++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());
		
		a.turnTowards(a.friendler);
		a.move();
		a.attack(a.friendler);
	}
}
