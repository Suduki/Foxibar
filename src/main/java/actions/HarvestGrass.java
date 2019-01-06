package actions;

import talents.Talents;
import agents.Agent;

public class HarvestGrass extends Action {
	public float grassness;

	public HarvestGrass() {
		super();
	}

	@Override
	public boolean determineIfPossible(Agent a) {
		isPossible = false;
		if (a.talents.getRelative(Talents.DIGEST_GRASS) > 0.2f) {
			grassness = a.world.grass.getHeight((int)a.pos.x, (int)a.pos.y);
			if (grassness > 0.05f) {
				isPossible = true;
			}
		}
		return isPossible;
	}

	@Override
	public void commit(Agent a) {
		numCommits++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());
		a.harvestGrass();
	}
}
