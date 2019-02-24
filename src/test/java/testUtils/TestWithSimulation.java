package testUtils;

import org.junit.Before;

import simulation.Simulation;
import agents.Animal;
import agents.Bloodling;
import agents.Brainler;
import agents.Giraffe;
import agents.Grassler;
import agents.Randomling;
import constants.Constants;

public class TestWithSimulation {

	protected static Simulation simulation;
	protected short worldMultiplier;

	protected static final int RANDOMLING = 0;
	protected static final int BLOODLING = 1;
	protected static final int BRAINLER = 2;
	protected static final int GRASSLER = 3;
	protected static final int GIRAFFE = 4;

	protected static final String[] AGENT_TYPES_NAMES = new String[] { "Randomling", "Bloodling", "Brainler",
			"Grassler", "Giraffe" };

	protected TestWithSimulation(short worldMultiplier) {
		this.worldMultiplier = worldMultiplier;
	}

	@Before
	public void betweenTestsResetSimulation() {
		simulation = new Simulation(worldMultiplier,
				new Class[] { Randomling.class, Bloodling.class, Brainler.class, Grassler.class, Giraffe.class });
		System.out.println("Before tests completed");
	}
}
