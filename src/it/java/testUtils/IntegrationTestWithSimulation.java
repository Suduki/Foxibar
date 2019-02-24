package testUtils;

import actions.Action;
import constants.Constants;

public class IntegrationTestWithSimulation extends TestWithSimulation {

	public IntegrationTestWithSimulation() {
		super(Constants.WORLD_MULTIPLIER_INTEGRATION_TEST);
	}

	public void printActions(int numCalls) {

		for (Action act : Action.acts) {
			float perc = ((float) act.numCommits * 100) / numCalls;
			System.out.printf("%18s: %.2f%s\n", act.getClass().getSimpleName(), perc, "%");
		}
	}

	public float[] testMultipleAgents(int[] type, int[] initNumAgents, boolean printStuff) {
		
		float[] averages = new float[AGENT_TYPES_NAMES.length];
		
		for (int i = 0; i < type.length; ++i) {
			simulation.spawnAgentsAtRandomPosition(type[i], initNumAgents[i]);
		}
		int t = 0;
		int totalSimTime = 5000;
		while (t < totalSimTime) {
			++t;
			simulation.step();
			for (int i = 0; i < type.length; ++i) {
				averages[type[i]] += ((float)simulation.getNumAgents(type[i])) / totalSimTime;
				if (simulation.getNumAgents(type[i]) < initNumAgents[i] * 0.05f) {
					if (printStuff) {
						System.out.println(AGENT_TYPES_NAMES[type[i]] + " are almost exterminated after " + t + " time steps.");
						for (int j = 0; j < type.length; ++j) {
							System.out.println(AGENT_TYPES_NAMES[type[j]] + ": " + simulation.getNumAgents(type[j]));
						}
					}

					return averages;
				}
			}
		}
		if (printStuff)
			System.out.println("Both survived.");
		for (int i = 0; i < type.length; ++i) {
			if (printStuff)
				System.out.println("Num " + AGENT_TYPES_NAMES[type[i]] + " alive = " + simulation.getNumAgents(type[i]));
		}
		
		return averages;
	}

	public float testSurvivability(int agentType, int numInit, boolean continuousSpawn, boolean printStuff) {
		if (printStuff)
			System.out.println("Testing survivability of " + AGENT_TYPES_NAMES[agentType]);
		
		int simTime = 5000;
		
		float average = 0;

		simulation.spawnAgentsAtRandomPosition(agentType, numInit);
		int t;
		for (t = 0; t < simTime; t++) {
			int numActiveAgents = simulation.getNumAgents(agentType);
			average += ((float)numActiveAgents) / simTime;
			if (continuousSpawn && numActiveAgents < numInit) {
				simulation.spawnAgentsAtRandomPosition(agentType, numInit - numActiveAgents);
			}
			if (numActiveAgents == 0) {
				return average;
			}
			simulation.step();
		}
		if (printStuff)
			System.out.println(AGENT_TYPES_NAMES[agentType] + " Survivability test completed after " + t
					+ " time steps, with " + simulation.animalManagers.get(agentType).numAnimals + " survivors");
		
		return average;
	}

	public void testWorldPopulated(int agentType) {
		TestHelper.verifyWorldEmpty(simulation);
		simulation.spawnAgentsAtRandomPosition(agentType, 100);
		simulation.step();
		TestHelper.verifyWorldNotEmpty(simulation);
	}

}
