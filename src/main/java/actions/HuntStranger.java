package actions;

import agents.Agent;

public class HuntStranger extends Action {

	@Override
	public boolean determineIfPossible(Agent a) {
		isPossible = (a.stranger != null);
		return isPossible;
	}

	@Override
	public void commit(Agent a) {
		numCalls++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());
		
		a.turnTowards(a.stranger);
		a.move();
		a.attack(a.stranger);
	}
}
