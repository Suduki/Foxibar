package agents;

import actions.Action;
import constants.Constants;
import skills.SkillSet;
import vision.Vision;
import world.World;

public class Grassler extends Agent {

	public Grassler(float health, World world, AgentManager<Agent> agentManager) {
		super(health, world, agentManager);
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
		if (action.determineIfPossible(this)) {
			action.commit(this);
			return;
		}
		
		action = Action.harvestGrass;
		if (action.determineIfPossible(this)) {
			action.commit(this);
			return;
		}
		
		action = Action.seekGrass;
		if (action.determineIfPossible(this)) {
			action.commit(this);
			return;
		}
		
		action = Action.randomWalk;
		if (action.determineIfPossible(this)) {
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
