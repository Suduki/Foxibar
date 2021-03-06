package simulation;

import vision.Vision;
import world.World;

import java.util.ArrayList;

import actions.Action;
import agents.Animal;
import agents.AnimalManager;
import constants.Constants;
import messages.MessageHandler;
import messages.Message;
import messages.SpawnAnimals;
import plant.PlantManager;
import talents.StomachRecommendation;
import talents.Talents;

public class Simulation extends MessageHandler {
	public World mWorld;
	private boolean mPaused = false;
	
	public static int simulationTime = 0;
	
	public static int WORLD_SIZE;
	public static int WORLD_SIZE_X;
	public static int WORLD_SIZE_Y;
	
	public boolean isPaused() {
		return mPaused;
	}

	public void setPaused(boolean mPaused) {
		this.mPaused = mPaused;
	}
	public Vision vision;
	
	public ArrayList<AnimalManager<? extends Animal>> animalManagers = new ArrayList<>();
	public PlantManager plantManager;
	
	public <T extends Animal> Simulation(short worldMultiplier, Class<T>... classes)
	{
		loadStomachRecommendation();
		WORLD_SIZE_X = (int) Math.pow(2, worldMultiplier);
		WORLD_SIZE_Y = (int) Math.pow(2, worldMultiplier);
		WORLD_SIZE = WORLD_SIZE_X * WORLD_SIZE_Y;
		Constants.MAX_NUM_ANIMALS = Integer.min(Constants.MAX_NUM_ANIMALS, WORLD_SIZE);
		vision = new Vision(Constants.Vision.WIDTH, Constants.Vision.HEIGHT);
		mWorld = new World(vision);
		Action.init(mWorld);
		Talents.init();
		for (Class<T> clazz : classes) {
			animalManagers.add(new AnimalManager<T>(mWorld, clazz, Constants.MAX_NUM_ANIMALS, vision));
		}
		plantManager = new PlantManager(vision, mWorld.terrain);
	}
	
	private void loadStomachRecommendation() {
		StomachRecommendation recommendation = StomachRecommendation.load(StomachRecommendation.bloodFile); 
		if (recommendation != null) {
			Constants.Talents.MAX_DIGEST_BLOOD = recommendation.highLimit;
			System.out.println("Found Blood file, will use that for the simulation");
		}
		
		recommendation = StomachRecommendation.load(StomachRecommendation.grassFile); 
		if (recommendation != null) {
			Constants.Talents.MAX_DIGEST_GRASS = recommendation.highLimit;
			System.out.println("Found Grass file, will use that for the simulation");
		}
		
		recommendation = StomachRecommendation.load(StomachRecommendation.fiberFile); 
		if (recommendation != null) {
			Constants.Talents.MAX_DIGEST_FIBER = recommendation.highLimit;
			System.out.println("Found Fiber file, will use that for the simulation");
		}
	}

	protected void evaluateMessage(Message pMessage)
	{
		pMessage.evaluate(this);
	}
	
	private int timeStep = 0;
	public void step()
	{
		simulationTime++;
		if (!mPaused)
		{
			mWorld.update(timeStep);
			SpawnAnimals.step();
			
			vision.clearAgents();
			for (AnimalManager<?> aM : animalManagers) {
				aM.synchAliveDead();
				for (Animal a : aM.alive) {
					vision.addAgentToZone(a);
				}
			}
			
			for (AnimalManager<?> aM : animalManagers) {
				aM.moveAll();
			}

			plantManager.spreadSeed();
			plantManager.synchAliveDead();
			plantManager.update();
		}
		timeStep++;
	}
	
	public World getWorld()
	{
		return mWorld;
	}
	
	public void killAllAgents() {
		for (AnimalManager<?> aM : animalManagers) {
			aM.killAll = true;
			aM.synchAliveDead();
			aM.moveAll();
			aM.synchAliveDead();
		}
	}
	
	public void spawnAgentsAtRandomPosition(int managerId, int num) {
		
		for (int i = 0; i < num; ++i) {
			int x = Constants.RANDOM.nextInt(WORLD_SIZE_X);
			int y = Constants.RANDOM.nextInt(WORLD_SIZE_Y);
			spawnAgent(x, y, managerId);
		}
	}

	public void resetWorld(boolean b) {
		mWorld.reset(b);
	}

	public Animal spawnAgent(int x, int y, int managerId) {
		Animal spawn = null;
		if (animalManagers.size() >= managerId) {
			spawn = animalManagers.get(managerId).spawnAnimal(x, y);
		}
		else {
			System.err.println("Trying to spawn agents in a non-existing manager?");
		}
		return spawn;
	}

	public int getNumAgents() {
		int numAgents = 0;
		for (AnimalManager<?> aM : animalManagers) {
			numAgents += aM.numAnimals;
		}
		return numAgents;
	}
	public int getNumAgents(int agentType) {
		AnimalManager<?> aM = animalManagers.get(agentType);
		return aM.numAnimals;
	}
}
