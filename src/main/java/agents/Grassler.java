package agents;

import actions.Action;
import constants.Constants;
import vision.Vision;
import world.World;

public class Grassler extends Agent {

	public Grassler(float health, World world, AgentManager<Agent> agentManager) {
		super(health, world, agentManager);
		color = Constants.Colors.BLACK;
		secondaryColor = Constants.Colors.WHITE;
	}

	@Override
	public void inherit(Agent a) {
		stomach.inherit(1);
		if (a == null) {
		}
		else if (!(a instanceof Grassler)) {
			System.err.println("Trying to inherit a non-Randomling.");
			return;
		}
		else {
		}		
	}

	private float speed = 0.6f;
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
		Action action = Action.fleeFromStranger;
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
