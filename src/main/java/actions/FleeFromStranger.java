package actions;

import agents.Animal;

public class FleeFromStranger extends Flee {

	public FleeFromStranger() {
		super();
	}

	@Override
	protected Animal getAnimalToFleeFrom(Animal a) {
		return a.stranger;
	}
}
