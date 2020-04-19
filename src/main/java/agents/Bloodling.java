package agents;

import actions.ActionManager;
import actions.ActionManager.Actions;
import constants.Constants;
import talents.Talents;
import world.World;

public class Bloodling extends PriorityAnimal {

	public Bloodling(World world, ActionManager aM) {
		super(world, aM, new Actions[] { Actions.HarvestBlood, Actions.HuntStranger, Actions.FleeFromFriendler,
				Actions.RandomWalk });
		color = Constants.Colors.WHITE;
		secondaryColor = Constants.Colors.RED;
	}

	private static final int[] presetSkills = { Talents.DIGEST_BLOOD };

	@Override
	protected void inherit(Animal a) {
		super.inherit(a);

		if (a == null) {
			talents.improveSkill(presetSkills);
		}
	}

}
