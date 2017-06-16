package messages;

import agents.Animal;
import world.World;

public class SaveBrains extends Message {

	@Override
	public String messageName() {
		return "KillAllAnimals";
	}

	@Override
	public void evaluate(simulation.Simulation pSimulation)
	{
		Animal.saveBrains = true;
	}
}
