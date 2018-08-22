package messages;

import main.Main;
import simulation.Simulation;
import world.World;

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
		Main.simulation.mWorld.regenerate();		
	}
}
