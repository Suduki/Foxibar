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
		if (a.talents.getRelative(Talents.DIGEST_FIBER) < 0.2f) {
			isPossible = false;
		}
		else {
			isPossible = a.nearbyPlant != null;
		}
		
		return isPossible;
	}

	@Override
	public void commit(Animal a) {
		if (Vision.calculateCircularDistance(a.pos, a.nearbyPlant.pos) < Animal.REACH) {
			a.stomach.addFiber(a.nearbyPlant.harvest(a.harvestSkill));
		}
		else {
			a.turnTowards(a.nearbyPlant);
			a.move();
		}
	}

}
