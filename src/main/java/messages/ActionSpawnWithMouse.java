package messages;

import main.Main;

public class ActionSpawnWithMouse extends Message {

	@Override
	public String messageName() {
		return "ActionSpawnWithMouse";
	}
	
	public ActionSpawnWithMouse(int id) {
		Main.animalTypeToSpawn = id;
	}

	@Override
	public void evaluate(simulation.Simulation pSimulation)
	{
	}
}
