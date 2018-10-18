package messages;

import messages.Message;
import simulation.Simulation;

public class PauseSimulation extends Message
{
	public String messageName() { return "PauseSimulation"; }
	
	public void evaluate(Simulation s) {
		if (!s.isPaused()) {
			System.out.println("Pausing simulation.");
			s.setPaused(true);
		}
		else {
			System.out.println("Unpausing simulation.");
			s.setPaused(false);
		}
	}
}