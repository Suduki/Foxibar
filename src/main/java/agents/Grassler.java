package agents;

import actions.Action;
import constants.Constants;
import talents.Talents;
import vision.Vision;
import world.World;

public class Grassler extends Agent {

	public Grassler(World world, AgentManager<Agent> agentManager) {
		super(world, agentManager);
		color = Constants.Colors.BLACK;
		secondaryColor = Constants.Colors.WHITE;
	}
	
	@Override
	protected void inherit(Agent a) {
		super.inherit(a);
	}

	@Override
	protected void actionUpdate() {
		
		Action action = Action.fleeFromStranger;
		if (action.isPossible) {
			action.commit(this);
			return;
		}
		
		action = Action.harvestGrass;
		if (action.isPossible) {
			action.commit(this);
			return;
		}
		
		action = Action.seekGrass;
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
	public boolean isCloselyRelatedTo(Agent a) {
		return isSameClassAs(a);
	}
}
