package agents;

import actions.Action;
import constants.Constants;
import world.World;

public class Randomling extends Animal {

	private static Action[] KNOWN_ACTIONS;
	
	public Randomling(World world) {
		super(world);
		color = Constants.Colors.BLACK;
		secondaryColor = Constants.Colors.WHITE;
		
		if (KNOWN_ACTIONS == null) {
			KNOWN_ACTIONS = new Action[]{Action.harvestBlood, Action.harvestGrass, Action.randomWalk};
		}
	}
	
	
	@Override
	protected void actionUpdate() {
		int actionToTry = Constants.RANDOM.nextInt(KNOWN_ACTIONS.length);
		
		for (int i = 0; i < KNOWN_ACTIONS.length; ++i) {
			Action action = Action.acts.get(actionToTry);
			if (action.isPossible) {
				action.commit(this);
				return;
			}
			actionToTry = (actionToTry + 1) % KNOWN_ACTIONS.length;
		}
		
		System.err.println("Should always be able to commit to an action");
	}
	
	@Override
	public boolean isCloselyRelatedTo(Animal a) {
		return isSameClassAs(a);
	}

}
