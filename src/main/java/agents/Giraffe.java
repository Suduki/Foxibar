package agents;

import actions.Action;
import constants.Constants;
import world.World;

public class Giraffe extends PriorityAnimal {

	public Giraffe(World world) {
		super(world, new Action[] {Action.harvestTree, Action.fleeFromFriendler, Action.randomWalk});
		this.secondaryColor = Constants.Colors.YELLOW;
	}
}
