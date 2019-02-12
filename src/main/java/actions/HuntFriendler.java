package actions;

import agents.Animal;

public class HuntFriendler extends Hunt {
	
	public HuntFriendler() {
		super();
	}

	@Override
	protected Animal getAnimalToHunt(Animal a) {
		return a.friendler;
	}
	
}
