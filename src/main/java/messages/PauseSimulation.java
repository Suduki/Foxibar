package main.java.messages;

import main.java.simulation.Simulation;

public class PauseSimulation extends Message
{
	public String messageName() { return "PauseSimulation"; }
	
	public void evaluate(Simulation s) {
			System.out.println("Pausing simulation.");
			s.setPause(true);			
	}
}