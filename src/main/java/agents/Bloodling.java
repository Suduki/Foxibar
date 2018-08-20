package agents;

import constants.Constants;
import world.World;

public class Bloodling extends Agent {

	public Bloodling(float health, World world, AgentManager agentManager) {
		super(health, world, agentManager);
		color = Constants.Colors.WHITE;
		secondaryColor = Constants.Colors.RED;
	}

	@Override
	public void inherit(Agent a, int speciesId) {
		stomach.inherit(1);
		
		if (a != null) {
		}
		else if (!(a instanceof Bloodling)) {
			System.err.println("inheriting non-Bloodling");
		}
		else {
			
		}
	}

	@Override
	protected float getSpeed() {
		return 1;
	}

	@Override
	protected int think() {
		for (Agent nearbyAnimalId : nearbyAgents) {
			if (!(nearbyAnimalId instanceof Bloodling)) {
				
			}
		}
		return 0;
	}

	@Override
	protected float getFightSkill() {
		return 0.3f;
	}

	@Override
	protected float getHarvestRatio() {
		return 0;
	}

	@Override
	protected void interactWith(Agent agent) {
		
	}

}
