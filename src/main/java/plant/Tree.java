package plant;

import agents.Agent;

public class Tree  extends Agent {
	
	public float leafness;

	public Tree() {
		super();
	}

	@Override
	public boolean stepAgent() {
		return isAlive;
	}
}
