package agents;

import constants.Constants;
import world.World;

public class Randomling extends Agent {

	public Randomling(float health, World world, AgentManager<Agent> agentManager) {
		super(health, world, agentManager);
		color = Constants.Colors.BLACK;
		secondaryColor = Constants.Colors.WHITE;
	}

	@Override
	public void inherit(Agent a, int i) {
		stomach.inherit(-1);
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
		return 1f;
	}

	@Override
	protected int think() {
		return Constants.RANDOM.nextInt(4);
	}

	@Override
	protected float getFightSkill() {
		return 0f;
	}

	@Override
	protected float getHarvestRatio() {
		return 1;
	}

	@Override
	protected void interactWith(Agent agent) {
		agent.health -= getFightSkill();
		health -= agent.getFightSkill();
	}

}
