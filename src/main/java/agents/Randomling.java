package agents;

import skills.SkillSet;
import actions.Action;
import constants.Constants;
import world.World;

public class Randomling extends Agent {

	public Randomling(float health, World world, AgentManager<Agent> agentManager) {
		super(health, world, agentManager);
		color = Constants.Colors.BLACK;
		secondaryColor = Constants.Colors.WHITE;
	}
	

	@Override
	protected void inherit(Agent a) {
		super.inherit(a);
		skillSet.inherit(SkillSet.RANDOMLING_SKILL_SET);
	}

	@Override
	protected void actionUpdate() {
		
		Action action = Action.harvestGrass;
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
	}
	
	@Override
	public boolean isCloselyRelatedTo(Agent a) {
		return isSameClassAs(a);
	}

}
