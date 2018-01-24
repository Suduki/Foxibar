package main.java.messages;

import main.java.agents.Animal;
import main.java.simulation.Simulation;

public class KillAllAnimals extends Message {

	@Override
	public String messageName() {
		return "KillAllAnimals";
	}

	@Override
	public void evaluate(Simulation pSimulation)
	{
		Animal.killAll = true;
		//World.grass.killAllGrass();
	}
}
