package actions;

import agents.Animal;

public abstract class Hunt extends Action {

	public Hunt() {
		super();
	}
	
	protected abstract Animal getAnimalToHunt(Animal a);

	@Override
	public boolean determineIfPossible(Animal a) {
		isPossible = (getAnimalToHunt(a) != null);
		return isPossible;
	}

	@Override
	public void commit(Animal a) {
		numCommits++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());

		Animal animalToHunt = getAnimalToHunt(a);
		
		a.turnTowards(animalToHunt);
		a.move();
		a.attack(animalToHunt);
	}
}
