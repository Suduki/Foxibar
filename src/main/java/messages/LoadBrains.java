package messages;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import agents.Brainler;
import agents.Brain;
import agents.NeuralNetwork;
import constants.Constants;
import world.World;

public class LoadBrains extends Message {

	@Override
	public String messageName() {
		return "LoadBrains";
	}

	@Override
	public void evaluate(simulation.Simulation pSimulation)
	{
//		Animal.loadBrains = true;
		System.err.println("Not implemented this.");
	}
	
	
	public static void loadBrains() {
		System.err.println("Not implemented this.");
	}
}
