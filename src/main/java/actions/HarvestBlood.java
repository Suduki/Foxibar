package actions;

import agents.Agent;

public class HarvestBlood extends Action {
	public float bloodness;

	public HarvestBlood() {
		super();
	}

	@Override
	public boolean determineIfPossible(Agent a) {
		isPossible = false;
		bloodness = a.world.blood.getHeight((int)a.pos.x, (int)a.pos.y);
		if (bloodness > 0.05f) {
			isPossible = true;
		}
		return isPossible;
	}

	@Override
	public void commit(Agent a) {
		numCommits++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());
		a.harvestBlood();
	}
}
