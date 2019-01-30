package actions;

import agents.Animal;

public class HuntFriendler extends Action {
	public HuntFriendler() {
		super();
	}
	@Override
	public boolean determineIfPossible(Animal a) {
		isPossible = (a.friendler != null);
		return isPossible;
	}

	@Override
	public void commit(Animal a) {
		numCommits++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());

		a.turnTowards(a.friendler);
		a.move();
		a.attack(a.friendler);
	}
}
