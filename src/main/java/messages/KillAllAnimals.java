package messages;

import agents.Animal;
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
		Simulation.animalManager.killAll = true;
		//World.grass.killAllGrass();
	}
}
