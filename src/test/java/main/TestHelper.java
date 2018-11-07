package main;

import org.junit.Assert;

import simulation.Simulation;
import skills.SkillSet;
import vision.Vision;
import actions.Action;
import agents.Stomach;
import constants.Constants;

public class TestHelper {
	
	public static void cleanup(Simulation simulation, Integer timeStep) {
		Action.reset();
		
		simulation.killAllAgents();
		simulation.step(timeStep++);
		verifyWorldEmpty(simulation);
		simulation.mWorld.reset(true);
		
		SkillSet.changeSkillMax(SkillSet.DIGEST_BLOOD, Constants.SkillSet.MAX_DIGEST_BLOOD);
		SkillSet.changeSkillMax(SkillSet.DIGEST_GRASS, Constants.SkillSet.MAX_DIGEST_GRASS);
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
