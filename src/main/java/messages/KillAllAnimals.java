package messages;

import agents.Animal;
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
		Main.simulation.randomlingManager.killAll = true;
		//World.grass.killAllGrass();
	}
}
