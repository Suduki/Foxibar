package main;

import org.junit.Assert;

import simulation.Simulation;
import vision.Vision;
import actions.Action;

public class TestHelper {
	public static void cleanup(Simulation simulation, Integer timeStep) {
		Action.reset();
		
		simulation.killAllAgents();
		simulation.step(timeStep++);
		verifyWorldEmpty(simulation);
		simulation.mWorld.reset(true);
	}
	
	public static void verifyWorldEmpty(Simulation simulation) {
		Assert.assertTrue(simulation.getNumAgents() == 0);
		Assert.assertTrue(visionZoneSize(simulation) == 0);
	}
	
	public static void verifyWorldNotEmpty(Simulation simulation) {
		Assert.assertTrue(simulation.getNumAgents() > 0);
		Assert.assertTrue(visionZoneSize(simulation) > 1);
	}
	
	public static int visionZoneSize(Simulation simulation) {
		int num = 0;
		for (Vision.Zone[] zi : simulation.vision.zoneGrid) {
			for (Vision.Zone z : zi) {
				num += z.agentsInZone.size();
			}	
		}
		return num;
	}
}
