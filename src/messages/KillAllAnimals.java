package messages;

import agents.Animal2;
import world.World;

public class KillAllAnimals extends Message {

	@Override
	public String messageName() {
		return "KillAllAnimals";
	}

	@Override
	public void evaluate(simulation.Simulation pSimulation)
	{
		Animal2.killAll = true;
		//World.grass.killAllGrass();
	}
}
