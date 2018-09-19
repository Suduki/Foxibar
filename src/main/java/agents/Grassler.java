package agents;

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
		stomach.inherit(1, 0);
		if (a == null) {
		}
		else if (!(a instanceof Grassler)) {
			System.err.println("Trying to inherit a non-Randomling.");
			return;
		}
		else {
		}		
	}

	private float speed = 1f;
	@Override
	protected float getSpeed() {
		return speed;
	}

	@Override
	protected int think() {
		if (seekPredator()) {
			speed = 0.7f;
			return 0;
		}
		if (seekGrass(vel) > 0.05f) {
			speed = stomach.minSpeed;
			return 0;
		}
		speed = stomach.minSpeed;
		randomWalk();
		return 0;
	}
	
	private boolean seekPredator() {
		for (Agent a : nearbyAgents) {
			if (a != null && !(a instanceof Grassler)) {
				turnAwayFrom(a);
				return true;
			}
		}
		return false;
	}

	@Override
	protected float getFightSkill() {
		return 0f;
	}

	@Override
	protected void actionUpdate() {
		think();
		move();
		harvestGrass();
	}

	protected void interactWith(Agent agent) {
		System.err.println("interactWith not implemented for Grassler");
	}
}
