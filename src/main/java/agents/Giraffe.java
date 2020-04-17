package agents;

import actions.Action;
import actions.ActionManager;
import actions.ActionManager.Actions;
import constants.Constants;
import world.World;

public class Giraffe extends PriorityAnimal {

	public Giraffe(World world, ActionManager aM) {
		super(world, aM, new Actions[] {Actions.HarvestTree, Actions.FleeFromFriendler, Actions.RandomWalk});
		this.secondaryColor = Constants.Colors.YELLOW;
	}
}
