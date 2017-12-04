package messages;

import agents.AgentHandler;

public class KillAllAnimals extends Message {

	@Override
	public String messageName() {
		return "KillAllAnimals";
	}

	@Override
	public void evaluate(simulation.Simulation pSimulation)
	{
		AgentHandler.killAll = true;
		//World.grass.killAllGrass();
	}
}
