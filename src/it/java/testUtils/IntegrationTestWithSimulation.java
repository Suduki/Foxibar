package testUtils;

import actions.Action;
import constants.Constants;

public class IntegrationTestWithSimulation extends TestWithSimulation {

	public IntegrationTestWithSimulation() {
		super(Constants.WORLD_MULTIPLIER_INTEGRATION_TEST);
	}

	protected int[] maxNumType = new int[AGENT_TYPES_NAMES.length];

	public void printActions(int numCalls) {

		for (Action act : Action.acts) {
			float perc = ((float) act.numCommits * 100) / numCalls;
			System.out.printf("%18s: %.2f%s\n", act.getClass().getSimpleName(), perc, "%");
		}
	}

	public void testMultipleAgents(int[] type, int[] initNumAgents, boolean printStuff) {
		for (int i = 0; i < type.length; ++i) {
			simulation.spawnAgentsAtRandomPosition(type[i], initNumAgents[i]);
		}
		int t = 0;
		while (t < 1000) {
			++t;
			simulation.step();
			for (int i = 0; i < type.length; ++i) {
				if (maxNumType[i] < simulation.getNumAgents(type[i])) {
					maxNumType[i] = simulation.getNumAgents(type[i]);
				}
				if (simulation.getNumAgents(type[i]) < initNumAgents[i] * 0.05f) {
					if (printStuff) {
						System.out.println(AGENT_TYPES_NAMES[type[i]] + " are almost exterminated after " + t + " time steps.");
						for (int j = 0; j < type.length; ++j) {
							System.out.println(AGENT_TYPES_NAMES[type[i]] + ": " + simulation.getNumAgents(type[i]));
						}
					}
					return;
				}
			}
		}
		if (printStuff)
			System.out.println("Both survived.");
		for (int i = 0; i < type.length; ++i) {
			if (printStuff)
				System.out.println("Num " + AGENT_TYPES_NAMES[type[i]] + " alive = " + simulation.getNumAgents(type[i]));
		}
	}

	public void testSurvivability(int agentType, int simTime, int numInit, boolean continuousSpawn, boolean printStuff) {
		if (printStuff)
			System.out.println("Testing survivability of " + AGENT_TYPES_NAMES[agentType]);

		simulation.spawnAgentsAtRandomPosition(agentType, numInit);
		int t;
		for (t = 0; t < simTime; t++) {
			int numActiveAgents = simulation.getNumAgents(agentType);
			if (continuousSpawn && numActiveAgents < numInit) {
				simulation.spawnAgentsAtRandomPosition(agentType, numInit - numActiveAgents);
			}
			if (numActiveAgents == 0) {
				return;
			}
			simulation.step();
		}
		if (printStuff)
			System.out.println(AGENT_TYPES_NAMES[agentType] + " Survivability test completed after " + t
					+ " time steps, with " + simulation.animalManagers.get(agentType).numAnimals + " survivors");
	}

	public void testWorldPopulated(int agentType) {
		TestHelper.verifyWorldEmpty(simulation);
		simulation.spawnAgentsAtRandomPosition(agentType, 100);
		simulation.step();
		TestHelper.verifyWorldNotEmpty(simulation);
	}

}
