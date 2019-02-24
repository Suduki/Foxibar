package agents;

import actions.Action;
import constants.Constants;
import talents.Talents;
import world.World;

public class Grassler extends PriorityAnimal {

	public Grassler(World world) {
		super(world, new Action[] { Action.fleeFromStranger, Action.harvestGrass, Action.randomWalk });
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
