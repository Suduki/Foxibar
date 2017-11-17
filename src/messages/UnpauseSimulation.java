package messages;

import simulation.Simulation;

public class UnpauseSimulation extends Message
{
	public String messageName() { return "UnpauseSimulation"; }
	
	public void evaluate(Simulation s) {
			System.out.println("Unpausing simulation.");
			s.setPause(false);			
	}
}
