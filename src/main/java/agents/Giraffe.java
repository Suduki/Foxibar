package agents;

import actions.Action;
import constants.Constants;
import world.World;

public class Giraffe extends Animal {

	public Giraffe(World world) {
		super(world);
		this.secondaryColor = Constants.Colors.YELLOW;
	}

	@Override
	protected void actionUpdate() {
		
		Action action = Action.harvestTree;
		if (action.isPossible) {
			action.commit(this);
			return;
		}
		
		action = Action.fleeFromFriendler;
		if (action.isPossible) {
			action.commit(this);
			return;
		}
		
		action = Action.randomWalk;
		if (action.isPossible) {
			action.commit(this);
			return;
		}
		System.err.println("Should always be able to commit to an action");
		return;
	}

	@Override
	public boolean isCloselyRelatedTo(Animal a) {
		return a.getClass() == this.getClass();
	}

}
