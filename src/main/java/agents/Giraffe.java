package agents;

import actions.ActionManager;
import actions.ActionManager.Actions;
import constants.Constants;
import talents.Talents;
import world.World;

public class Giraffe extends PriorityAnimal {

	public Giraffe(World world, ActionManager aM) {
		super(world, aM, new Actions[] {Actions.HarvestTree, Actions.RandomWalk});
		this.secondaryColor = Constants.Colors.YELLOW;
	}

	private static final int[] presetSkills = { Talents.DIGEST_FIBER, Talents.LONG_NECK };

	@Override
	protected void inherit(Animal a) {
		super.inherit(a);

		if (a == null) {
			talents.improveSkill(presetSkills);
		}
	}
}
