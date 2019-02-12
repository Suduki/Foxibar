package actions;

import agents.Animal;

public abstract class Flee extends Action {

	public Flee() {
		super();
	}
	
	protected abstract Animal getAnimalToFleeFrom(Animal a);

	@Override
	public boolean determineIfPossible(Animal a) {
		isPossible = (getAnimalToFleeFrom(a) != null);
		return isPossible;
	}

	@Override
	public void commit(Animal a) {
		numCommits++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());
		
		a.turnAwayFrom(getAnimalToFleeFrom(a));
		a.move();
	}
}
