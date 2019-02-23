package agents;

import actions.Action;
import constants.Constants;
import talents.Talents;
import world.World;

public class Bloodling extends PriorityAnimal {
	
	public Bloodling(World world) {
		super(world, new Action[] {Action.harvestBlood, Action.huntStranger, Action.fleeFromFriendler, Action.randomWalk});
		color = Constants.Colors.WHITE;
		secondaryColor = Constants.Colors.RED;
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
