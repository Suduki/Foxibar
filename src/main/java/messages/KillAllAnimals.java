package messages;

import agents.Brainler;
import main.Main;
import simulation.Simulation;
import world.World;

public class KillAllAnimals extends Message {

	@Override
	public String messageName() {
		return "KillAllAnimals";
	}

	@Override
	public void evaluate(simulation.Simulation pSimulation)
	{
		Main.mSimulation.killAllAgents();
	}
}
