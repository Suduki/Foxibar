package agents;

import constants.Constants;
import world.World;

public class Randomling extends Agent {

	public Randomling(float health, World world, AgentManager agentManager) {
		super(health, world, agentManager);
	}

	public static int numRandomlings = 0;

	@Override
	public void inherit(Agent a, int i) {
		if (a == null) {
			numRandomlings ++;
		}
		else if (!(a instanceof Randomling)) {
			System.err.println("Trying to inherit a non-Randomling.");
			return;
		}
		else {
			numRandomlings ++;
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
		return 0;
	}

	@Override
	protected float getHarvestRatio() {
		return 1;
	}

	@Override
	protected void interactWith(Agent agent) {
		System.out.println("hi");
	}

}
