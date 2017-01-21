package main;

import constants.Constants;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import agents.Animal;
import world.Vision;
import world.World;
import display.DisplayHandler;

public class Main {

	public static DisplayHandler displayHandler;
	public static World world;
	public static double simulationFps;
	public static boolean doPause;

	private static double frameStartTime;
	private static double frameEndTime;

	private static ConcurrentLinkedQueue<SimulationEvent> incomingEvents;
	public static Vision vision;


	public static void main(String[] args) throws Exception {

		incomingEvents = new ConcurrentLinkedQueue<SimulationEvent>();

		world = new World();
		vision = new Vision();
		World.regenerate();
		Animal.init();
		displayHandler = new DisplayHandler();
		Thread.sleep(1000);

		// Prime the frame timer.
		frameStartTime = System.currentTimeMillis();

		try {
			while (handleEvents() && displayHandler.renderThreadThread.isAlive()) {
				world.update();
				Animal.moveAll();
				vision.update();

				sleepUntilNextUpdate();
			}
		}
		catch ( IllegalStateException e) {
			e.printStackTrace();
		}
		finally {
			displayHandler.exit();
		}

		System.out.println("Simulation (main) thread finished.");
	}

	private static void sleepUntilNextUpdate() {
		frameEndTime = System.currentTimeMillis();
		double wantedFrameTimeMs = 1000d/Constants.WANTED_FPS;
		double actualFrameTimeMs = frameEndTime - frameStartTime;

		double sleepTimeMs = wantedFrameTimeMs - actualFrameTimeMs;

		if (sleepTimeMs > 0) {
			simulationFps = 1000d/sleepTimeMs;
			try {
				Thread.sleep(Math.round(sleepTimeMs));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		else {
			simulationFps = 1000d/(actualFrameTimeMs);
		}

		// TODO: Can we sleep while waiting for a barrier here instead?
		while (doPause) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		frameStartTime = System.currentTimeMillis();
	}

	public static void putEvent(SimulationEvent event) {
		incomingEvents.add(event);
	}

	private static boolean handleEvents() {
		int numEvents = incomingEvents.size();
		for (int i = 0; i < numEvents; ++i) {
			SimulationEvent event = incomingEvents.poll();
			event.evaluate();
		}

		return true;
	}

	public interface SimulationEvent {
		public void evaluate();
	}

	public static class PauseSimulationEvent implements SimulationEvent {
		CountDownLatch pauseLatch;

		public PauseSimulationEvent(CountDownLatch _pauseLatch)
		{
			System.out.println("Creating a PauseSimulationEvent in thread " + Thread.currentThread().getName());
			pauseLatch = _pauseLatch;
		}

		@Override
		public void evaluate() {
			try {
				System.out.println("Waiting in PauseSimulationEvent.evaluate in thread " + Thread.currentThread().getName());
				pauseLatch.await();
				System.out.println("Done waiting in PauseSimulationEvent.evaluate in thread " + Thread.currentThread().getName());
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
}
