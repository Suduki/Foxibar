package agents;

import actions.Action;
import constants.Constants;
import world.World;

public class Bloodling extends Agent {
	
	public Bloodling(World world, AgentManager agentManager) {
		super(world, agentManager);
		color = Constants.Colors.WHITE;
		secondaryColor = Constants.Colors.RED;
	}

	@Override
	protected void inherit(Agent a) {
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
	public boolean isCloselyRelatedTo(Agent a) {
		return isSameClassAs(a);
	}

}
