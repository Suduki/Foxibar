package main;

import constants.Constants;

import agents.Animal;
import world.World;
import display.DisplayHandler;

public class Main {

	public static DisplayHandler displayHandler;
	public static World world;
	public static double simulationFps;
	public static boolean doPause;

	public static void main(String[] args) throws Exception {

		world = new World();
		World.regenerate();
		Animal.init();
		displayHandler = new DisplayHandler();
		Thread.sleep(1000);

		double time = System.currentTimeMillis();
		double oldTime;

		try {
			//Mouse.create();
			while (displayHandler.renderThreadThread.isAlive()) {

				oldTime = System.currentTimeMillis();

				world.update();
				Animal.moveAll();
				
				time = System.currentTimeMillis();
				long sleepTime = Math.round(1000d/Constants.WANTED_FPS - (time - oldTime));
				
				if (sleepTime > 0) {
					simulationFps = 1000d/sleepTime;
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				else {
					simulationFps = 1000d/(time - oldTime);
				}
				
				while (doPause) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				oldTime = time;
			}
		}
		catch ( IllegalStateException e) {
			e.printStackTrace();
		}
		finally {
			displayHandler.exit();
		}
	}
}
