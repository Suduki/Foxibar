package actions;

import agents.Agent;

public class RandomWalk extends Action {

	@Override
	public boolean determineIfPossible(Agent a) {
		isPossible = true;
		return isPossible;
	}

	@Override
	public void commit(Agent a) {
		numCalls++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());
		a.randomWalk();
		a.move();
		a.harvestGrass();
		a.harvestFat();
	}

}