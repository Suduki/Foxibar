package agents;

import actions.Action;
import constants.Constants;
import world.World;

public class Randomling extends Agent {

	private float speed = 1f;
	
	public Randomling(float health, World world, AgentManager<Agent> agentManager) {
		super(health, world, agentManager);
		color = Constants.Colors.BLACK;
		secondaryColor = Constants.Colors.WHITE;
	}

	@Override
	public void inherit(Agent a) {
		stomach.inherit(1);
		if (a == null) {
		}
		else if (!(a instanceof Randomling)) {
			System.err.println("Trying to inherit a non-Randomling.");
			return;
		}
		else {
		}		
	}

	@Override
	protected float getSpeed() {
		return speed;
	}

	@Override
	protected float getFightSkill() {
		return 0f;
	}

	@Override
	protected void actionUpdate() {
		
		Action action = Action.randomWalk;
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
