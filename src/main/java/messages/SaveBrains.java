package messages;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import agents.Animal;
import agents.NeuralNetwork;
import constants.Constants;
import world.World;

public class SaveBrains extends Message {

	@Override
	public String messageName() {
		return "SaveBrains";
	}

	@Override
	public void evaluate(simulation.Simulation pSimulation)
	{
		saveBrains();
		System.out.println(messageName() + " Message evaluated");
	}
	
	
	public static void saveBrains() {
		System.err.println("save Brains not implemented");
	}
}
