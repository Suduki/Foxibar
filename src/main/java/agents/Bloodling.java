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
		else {
			
		}
	}

	@Override
	protected float getSpeed() {
		return 1;
	}

	@Override
	protected void think() {
		// Seek partner
//		if (isFertileAndNotHungry()) {
//			if (seekPartner()) {
//				return;
//			}
//		}
		
		// Seek Blood
		if (seekBlood()) {
			return;
		}
		
		// Seek prey
		if (seekPrey()) {
			return;
		}
		
		// Friendlers are unattractive
		if (seekFriend()) {
			return;
		}
		
		randomWalk();
	}
	
	/**
	 * Updates vel accordingly
	 * @return whether we've found prey
	 */
	private boolean seekPrey() {
		for (Agent a : nearbyAgents) {
			if (a != null && !(a instanceof Bloodling)) {
				Vision.getDirectionOf(vel, pos, a.pos);
				return true;
			}
		}
		return false;
	}
	
	private boolean seekFriend() {
		for (Agent a : nearbyAgents) {
			if (a != null && (a instanceof Bloodling)) {
				//NOTE: Flipped the order of pos <=> bestAgent.pos to get the opposite effect
				Vision.getDirectionOf(vel, a.pos, pos);
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
		if (agent instanceof Bloodling) {
		}
		else {
			agent.health -= getFightSkill();
		}
	}

}
