package actions;

import agents.Animal;
import talents.Talents;
import vision.Vision;

public class HarvestTree extends Action {

	public HarvestTree() {
		super();
	}

	@Override
	public boolean determineIfPossible(Animal a) {
		isPossible = a.talents.getRelative(Talents.DIGEST_FIBER) > 0.2f
				&& a.nearbyPlant != null
				&& !a.stomach.isFull();

		return isPossible;
	}

	@Override
	public void commit(Animal a) {
		if (Vision.calculateCircularDistance(a.pos, a.nearbyPlant.pos) < Animal.REACH) {
			a.stomach.addFiber(a.nearbyPlant.harvest(a.harvestSkill
					* a.talents.getRelative(Talents.DIGEST_FIBER)));
		} else {
			a.turnTowards(a.nearbyPlant);
			a.move();
		}
	}

}
