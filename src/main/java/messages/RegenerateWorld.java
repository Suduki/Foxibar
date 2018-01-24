package main.java.messages;

import main.java.simulation.Simulation;
import main.java.world.World;

public class RegenerateWorld extends Message {

	@Override
	public String messageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void evaluate(Simulation pSimulation)
	{
		// TODO: Make good.
		World.regenerate();		
	}
}
