package messages;

import agents.Animal;
import world.World;

public class KillAllAnimals extends Message {

	@Override
	public String messageName() {
		return "KillAllAnimals";
	}

	@Override
	public void evaluate(simulation.Simulation pSimulation)
	{
		Animal.killAll = true;
		//World.grass.killAllGrass();
	}
}
