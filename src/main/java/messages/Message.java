package messages;

public abstract class Message {
	public void evaluate(simulation.Simulation simulation)
	{
		System.err.println("Message \"" + messageName() + "\" not recognized by Simulation");
	}
	
	public void evaluate(display.DisplayHandler displayHandler)
	{
		System.err.println("Message \"" + messageName() + "\" not recognized by DisplayHandler");
	}
	
	public abstract String messageName();
}
