package main.java.messages;

import main.java.display.DisplayHandler;
import main.java.simulation.Simulation;

public abstract class Message {
	public void evaluate(Simulation simulation)
	{
		System.err.println("Message \"" + messageName() + "\" not recognized by Simulation");
	}
	
	public void evaluate(DisplayHandler displayHandler)
	{
		System.err.println("Message \"" + messageName() + "\" not recognized by DisplayHandler");
	}
	
	public abstract String messageName();
}
