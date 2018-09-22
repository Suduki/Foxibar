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
		System.err.println("think not implemented for grassler");
		return 0;
	}
	
	private Agent predator;
	private boolean seekPredator() { //TODO: Same code as stranger in Brainler
		for (Agent a : nearbyAgents) {
			if (a != null && !(a instanceof Grassler)) {
				predator = a;
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
		// Seek Predator
		seekPredator();
		if (predator != null) {
			speed = 0.6f;
			turnAwayFrom(predator);
			move();
			attack(predator);
			return;
		}
		
		// Seek Grass
		float grass = seekGrass(vel);
		if (grass > 0.1f) {
			speed = Stomach.minSpeed;
			move();
			harvestGrass();
			return;
		}
		
		speed = Stomach.minSpeed;
		randomWalk();
		move();
		return;
	}

	protected void interactWith(Agent agent) {
		System.err.println("interactWith not implemented for Grassler");
	}
}
