package agents;

import java.util.ArrayList;

import utils.FibonacciHeap;
import utils.FibonacciHeap.Entry;
import vision.Vision;
import constants.Constants;

public class AgentHandler {

	public static boolean killAll = false;
	public static boolean saveBrains = false;
	public static boolean loadBrains = false;
	public static int numBloodlings;
	public static int numGrasslers;
	
	
	/**
	 * All agents pre-allocated in pool.
	 * Their position in this pool is their "id".
	 */
	private Animal[] pool = new Animal[Constants.MAX_NUM_ANIMALS];

	int[] containsAgents;
	private ArrayList<Animal> dead = new ArrayList<>();
	/**
	 * Contains all currently living agents.
	 * The min entry contains the agent that is next in turn to be updated.
	 */
	private FibonacciHeap<Agent> alive = new FibonacciHeap<>();

	
	/**
	 * Steps through all Animals that should move until the given goal time. 
	 * @param goalWorldTime
	 */
	public void updateAll(float goalWorldTime) {
		
		handleInput();
		
		Entry<Agent> currentEntry = alive.dequeueMin();
		Agent currentAgent = currentEntry.getValue();
		float currentTime = currentAgent.time;
		
		while (currentTime < goalWorldTime) {
			
			// Remove agent from world temporarily
			containsAgents[currentAgent.pos] = -1;
			
			// Allow the agent to make its move and stuff...
			float stepTime = currentAgent.update(currentAgent.speed);
			
			// Check if it survived.
			if (currentAgent.isAlive) {
				// Add agent to world again
				containsAgents[currentAgent.pos] = currentAgent.id;
				
				// Add it to the alive list again.
				currentAgent.time = currentTime + stepTime;
				alive.enqueue(currentAgent, currentAgent.time);
			}
			
			// Get next agent to move...
			currentEntry = alive.dequeueMin();
			currentAgent = currentEntry.getValue();
			currentTime = currentAgent.time;
		}
	}
	
	private void handleInput() {
		if (killAll) {
			while (!alive.isEmpty()) {
				alive.dequeueMin().getValue().die(0f);
			}
			killAll = false;
		}
		if (saveBrains) {
			Animal2.saveBrains();
			saveBrains = false;
		}
		if (loadBrains) {
			Animal2.loadBrains();
			loadBrains = false;
		}
	}

	public void init() {
		numGrasslers = 0;
		numBloodlings = 0;
		containsAgents = new int[Constants.WORLD_SIZE];

		for (int pos = 0; pos < Constants.WORLD_SIZE; ++pos) {
			containsAgents[pos] = -1;
		}

		for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
			pool[id] = new Animal();
			dead.add(pool[id]);
		}
	}
	
	public void spawnAnimal(Animal mom, Animal dad) {
		if (!dead.isEmpty()) {
			Animal child = dead.get(0);
			dead.remove(0);
			child.init(mom, dad);
			Vision.addAnimalToZone(child);
		}
		else {
			System.err.println("dead is empty; probably means pool is too small");
		}
	}
	
	public void spawnRandomGrassler() {
		if (!dead.isEmpty()) {
			Animal child = dead.get(0);
			dead.remove(0);
			child.init(Constants.SpeciesId.BEST_GRASSLER, Constants.SpeciesId.BEST_GRASSLER);
			Vision.addAnimalToZone(child);
		}
		else {
			System.err.println("dead is empty; probably means pool is too small");
		}
	}
	
	public void spawnRandomBloodling() {
		if (!dead.isEmpty()) {
			Animal child = dead.get(0);
			dead.remove(0);
			child.init(Constants.SpeciesId.BEST_BLOODLING, Constants.SpeciesId.BEST_BLOODLING);
			Vision.addAnimalToZone(child);
		}
		else {
			System.err.println("dead is empty; probably means pool is too small");
		}
	}
}
