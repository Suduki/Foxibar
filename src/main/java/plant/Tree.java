package plant;

import agents.Agent;
import vision.Vision;
import world.World;

public class Tree extends Agent {
	
	public float leafness;

	public Tree(World world) {
		super(world);
	}

	@Override
	public boolean stepAgent() {
		
		leafness = health * size;
		return false;
	}

	@Override
	protected void die() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateNearestNeighbours(Vision vision) {
		// Trees don't care about any nearby agents... Yet?
	}

	@Override
	protected void addToChildren(Agent mate) {
		// Trees don't care about children... Yet?
	}

	@Override
	protected void inherit(Agent parent) {
		
	}
}
