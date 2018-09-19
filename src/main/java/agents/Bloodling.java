package agents;

import java.util.Random;

import constants.Constants;
import constants.Constants.Neighbours;
import vision.Vision;
import world.World;

public class Bloodling extends Agent {
	
	public Bloodling(float health, World world, AgentManager agentManager) {
		super(health, world, agentManager);
		color = Constants.Colors.WHITE;
		secondaryColor = Constants.Colors.RED;
	}

	@Override
	public void inherit(Agent a) {
		stomach.inherit(-1, 0);
		if (a != null && !(a instanceof Bloodling)) {
			System.err.println("inheriting non-Bloodling");
		}
	}

	private float speed = 1f;
	@Override
	protected float getSpeed() {
		return speed;
	}

	private Agent prey;
	@Override
	protected int think() {
		System.err.println("not implemented think for bloodling");
		return 0;
	}
	
	/**
	 * Updates vel accordingly
	 * @return whether we've found prey
	 */
	private boolean seekPrey() {
		prey = null;
		for (Agent a : nearbyAgents) {
			if (a != null && !(a instanceof Bloodling)) {
				prey = a;
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected float getFightSkill() {
		return 1f;
	}
	
	@Override
	protected void interactWith(Agent agent) {
		if (agent != null) {
			attack(agent);
		}
	}

	@Override
	protected void actionUpdate() {
		// Seek Blood
		float blood = seekBlood(vel);
		if (blood > 0.1f) {
			speed = 1;
			move();
			harvestBlood();
			return;
		}
		
		// Seek prey
		seekPrey();
		if (prey != null) {
			speed = 1f;
			turnTowards(prey);
			move();
			attack(prey);
			return;
		}
		
		speed = Stomach.minSpeed;
		randomWalk();
		move();
		return;
	}

}
