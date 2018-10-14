package agents;

import java.util.Random;

import actions.Action;
import constants.Constants;
import constants.Constants.Neighbours;
import vision.Vision;
import world.World;

public class Bloodling extends Agent {
	
	public Bloodling(float health, World world, AgentManager agentManager) {
		super(health, world, agentManager);
		color = Constants.Colors.WHITE;
		secondaryColor = Constants.Colors.RED;
	}

	@Override
	public void inherit(Agent a) {
		stomach.inherit(-1);
		if (a != null && !(a instanceof Bloodling)) {
			System.err.println("inheriting non-Bloodling");
		}
	}

	private float speed = 1f;
	@Override
	protected float getSpeed() {
		return speed;
	}

	@Override
	protected float getFightSkill() {
		return 0.5f;
	}
	
	@Override
	protected void actionUpdate() {
		Action action;
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
