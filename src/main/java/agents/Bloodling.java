package agents;

import actions.Action;
import constants.Constants;
import talents.Talents;
import world.World;

public class Bloodling extends Animal {
	
	public Bloodling(World world) {
		super(world);
		color = Constants.Colors.WHITE;
		secondaryColor = Constants.Colors.RED;
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
	
	private static final int[] presetSkills = {Talents.DIGEST_BLOOD}; 
	@Override
	protected void inherit(Animal a) {
		super.inherit(a);
		
		if (a == null) {
			talents.improveSkill(presetSkills);
		}
	}

}
