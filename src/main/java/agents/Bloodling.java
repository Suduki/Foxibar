package agents;

import actions.Action;
import constants.Constants;
import world.World;

public class Bloodling extends Animal {
	
	public Bloodling(World world) {
		super(world);
		color = Constants.Colors.WHITE;
		secondaryColor = Constants.Colors.RED;
	}

	@Override
	protected void inherit(Animal a) {
		super.inherit(a);
	}
	
	@Override
	protected void actionUpdate() {
		
		Action action;
		action = Action.harvestBlood;
		if (action.isPossible) {
			action.commit(this);
			return;
		}
		
		action = Action.huntStranger;
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
		return isSameClassAs(a);
	}

}
