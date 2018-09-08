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
	protected int think() {
		// Seek partner
		int dir;
//		if (isFertileAndNotHungry()) {
//			if (( dir = seekPartner() )!= Constants.Neighbours.INVALID_DIRECTION) {
//				return dir;
//			}
//		}
		
		// Seek Blood
		if (( dir = seekBlood() )!= Constants.Neighbours.INVALID_DIRECTION) {
			return dir;
		}
		
		// Seek prey
		if (( dir = seekPrey() )!= Constants.Neighbours.INVALID_DIRECTION) {
			return dir;
		}
		
		// Friendlers are unattractive
		if (( dir = seekFriend() )!= Constants.Neighbours.INVALID_DIRECTION) {
			return dir;
		}
		return Constants.RANDOM.nextInt(4);
	}
	
//	private int seekPartner() {
//		mate();
//		return Constants.RANDOM.nextInt(4);
//	}
	private int seekBlood() {
		int bestDir = Constants.Neighbours.INVALID_DIRECTION;
		float bestH = 0;
		for (int tile = 0; tile < 4; tile++) {
			float h = world.blood.height[World.neighbour[tile][pos]];
			if (h > 0 && h > bestH) {
				bestDir = tile;
				bestH = h;
			}
		}
		return bestDir;

	}
	private int seekPrey() {
		double bestDistance = 1000000;
		Agent bestAgent = null;
		for (Agent a : nearbyAgents) {
			if (a != null && !(a instanceof Bloodling)) {
				double d = Vision.calculateCircularDistance(pos, a.pos);
				if (d < bestDistance) {
					bestAgent = a;
					bestDistance = d;
				}
			}
		}
		if (bestAgent != null) {
			return Vision.getDirectionOf(pos, bestAgent.pos);
		}
		return Constants.Neighbours.INVALID_DIRECTION;
	}
	
	private int seekFriend() {
		double bestDistance = 1000000;
		Agent bestAgent = null;
		for (Agent a : nearbyAgents) {
			if (a != null && (a instanceof Bloodling)) {
				double d = Vision.calculateCircularDistance(pos, a.pos);
				if (d < bestDistance) {
					bestAgent = a;
					bestDistance = d;
				}
			}
		}
		if (bestAgent != null) {
			//NOTE: Flipped the order of pos <=> bestAgent.pos to get the opposite effect
			return Vision.getDirectionOf(bestAgent.pos, pos);
		}
		return Constants.Neighbours.INVALID_DIRECTION;
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
