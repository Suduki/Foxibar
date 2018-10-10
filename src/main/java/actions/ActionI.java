package actions;

import agents.Agent;

public interface ActionI {
	public abstract boolean determineIfPossible(Agent a);
	public abstract void commit(Agent a);
}


