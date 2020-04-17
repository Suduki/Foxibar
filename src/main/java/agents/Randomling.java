package agents;

import actions.ActionManager;
import actions.ActionManager.Actions;
import constants.Constants;
import talents.Talents;
import world.World;

public class Randomling extends PriorityAnimal {

	public Randomling(World world, ActionManager aM) {
		super(world, aM, new Actions[] { Actions.HarvestGrass, Actions.RandomWalk });
		color = Constants.Colors.BLACK;
		secondaryColor = Constants.Colors.WHITE;
	}

	private static final int[] presetSkills = { Talents.DIGEST_GRASS, Talents.MATE_COST };

	@Override
	protected void inherit(Animal a) {
		super.inherit(a);

		if (a == null) {
			talents.improveSkill(presetSkills);
		}
	}

}
