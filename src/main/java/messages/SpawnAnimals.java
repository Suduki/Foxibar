package messages;

import main.Main;

public class SpawnAnimals extends Message {

	@Override
	public String messageName() {
		return "SpawnAnimals";
	}

	@Override
	public void evaluate(simulation.Simulation pSimulation)
	{
		isActive = !isActive;
	}

	private static final int SPAWN_FREQ = 1000;
	private static int spawnI = 0;
	private static boolean isActive = true;
	public static void step() {
		if (!isActive) return;
		
		if (spawnI++ % SPAWN_FREQ == 0) {
			Main.simulation.spawnRandomAgents(0, 50);
		}
	}
}
