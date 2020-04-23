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
		isPossible = a.nearbyPlant != null
				&& a.talents.get(Talents.LONG_NECK) > (a.nearbyPlant.getHeightOfLowestLeaves())
				&& !a.stomach.isFull();

		return isPossible;
	}

	@Override
	public void commit(Animal a) {
		numCommits++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());
		
		if (Vision.calculateCircularDistance(a.pos, a.nearbyPlant.pos) < Animal.REACH) {
			a.stomach.addFiber(a.nearbyPlant.harvest(Animal.HARVEST_FIBER));
		} else {
			a.turnTowards(a.nearbyPlant);
			a.move();
		}
	}
}
