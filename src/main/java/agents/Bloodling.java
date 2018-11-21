package agents;

import java.util.Random;

import actions.Action;
import constants.Constants;
import constants.Constants.Neighbours;
import talents.Talents;
import vision.Vision;
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
		if (action.determineIfPossible(this)) {
			action.commit(this);
			return;
		}
		
		action = Action.seekBlood;
		if (action.determineIfPossible(this)) {
			action.commit(this);
			return;
		}
		
		action = Action.huntStranger;
		if (action.determineIfPossible(this)) {
			action.commit(this);
			return;
		}
		
		action = Action.fleeFromFriendler;
		if (action.determineIfPossible(this)) {
			action.commit(this);
			return;
		}
		
		action = Action.randomWalk;
		if (action.determineIfPossible(this)) {
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
