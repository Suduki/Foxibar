package testUtils;

import org.junit.Before;

import simulation.Simulation;
import agents.Bloodling;
import agents.Brainler;
import agents.Grassler;
import agents.Randomling;
import constants.Constants;

public class TestWithSimulation {

	protected static Simulation     simulation;
	
	@Before
	public void between() {
		simulation     = new Simulation(Constants.WORLD_MULTIPLIER_TEST, new Class[] {Randomling.class, Bloodling.class, Brainler.class, Grassler.class});
		System.out.println("Before tests completed");
	}
}
