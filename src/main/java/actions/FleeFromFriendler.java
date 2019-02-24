package actions;

import agents.Animal;

public class FleeFromFriendler extends Flee {

	public FleeFromFriendler() {
		super();
	}
	
	@Override
	protected Animal getAnimalToFleeFrom(Animal a) {
		return a.friendler;
	}
}
