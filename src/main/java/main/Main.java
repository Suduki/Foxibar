package main;

import messages.SpawnAnimals;
import actions.Action;
import agents.Bloodling;
import agents.Brainler;
import agents.Giraffe;
import agents.Grassler;
import agents.Randomling;
import constants.Constants;
import display.DisplayHandler;
import display.RenderState;
import simulation.Simulation;
import utils.FPSLimiter;

public class Main {
	public static final int RANDOMLING = 0;
	public static final int BLOODLING = 1;
	public static final int BRAINLER = 2;
	public static final int GRASSLER = 3;
	public static final int GIRAFFE = 4;

	public static int animalTypeToSpawn = GIRAFFE;
	public final static int plottingNumber = 5000;
	public static Simulation mSimulation;

	public static void main(String[] args) {

		mSimulation = new Simulation(Constants.WORLD_MULTIPLIER_MAIN,
				new Class[] { Randomling.class, Bloodling.class, Brainler.class, Grassler.class, Giraffe.class });
		DisplayHandler displayHandler = new DisplayHandler(mSimulation);
		FPSLimiter fpsLimiter = new FPSLimiter(Constants.WANTED_FPS);
		RenderState.activateState(RenderState.RENDER_WORLD_STILL);

		try {
			mSimulation.message(new SpawnAnimals());
			while (mSimulation.handleMessages() && displayHandler.renderThreadThread.isAlive()) {
				mSimulation.step();
				fpsLimiter.waitForNextFrame();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		dump();
		System.out.println("Simulation (main) thread finished.");
	}

	private static void dump() {
		for (Action act : Action.acts) {
			System.out.println(act.getClass().getSimpleName() + " " + act.numCommits);
		}
	}

}