package agents;

import java.util.ArrayList;

import messages.LoadBrains;
import messages.SaveBrains;
import simulation.Simulation;
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

	public Agent[] containsAgents;
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
		
		if (alive.isEmpty()) {
			return;
		}
		Entry<Agent> currentEntry = alive.min();
		Agent currentAgent = currentEntry.getValue();
		float currentTime = currentAgent.time;
		
		while (currentTime < goalWorldTime && !alive.isEmpty()) {
			// Get next agent to move...
			currentEntry = alive.dequeueMin();
			currentAgent = currentEntry.getValue();
			currentTime = currentAgent.time;
			
			// Remove agent from world temporarily
			containsAgents[currentAgent.pos] = null;
			
			// Allow the agent to make its move and stuff...
			float stepTime = currentAgent.update(currentAgent.speed);
			
			// Check if it survived.
			if (currentAgent.isAlive) {
				// Add agent to world again
				containsAgents[currentAgent.pos] = currentAgent;
				
				// Add it to the alive list again.
				currentAgent.time = currentTime + stepTime;
				alive.enqueue2(currentAgent, currentAgent.time);
			}
			
		}
	}
	
	private void handleInput() {
		if (killAll) {
			while (!alive.isEmpty()) {
				alive.dequeueMin().getValue().die(0f);
			}
			for (int i = 0 ; i < containsAgents.length; ++i) {
				containsAgents[i] = null;
			}
			killAll = false;
		}
		if (saveBrains) {
			SaveBrains.saveBrains();
			saveBrains = false;
		}
		if (loadBrains) {
			LoadBrains.loadBrains();
			loadBrains = false;
		}
	}

	public void init() {
		numGrasslers = 0;
		numBloodlings = 0;
		containsAgents = new Agent[Constants.WORLD_SIZE];

		for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
			pool[id] = new Animal();
			dead.add(pool[id]);
		}
	}
	
	
	public void spawnAnimal(Animal mom, Animal dad, int position, float time, int speciesId) {
		if (!dead.isEmpty()) {
			Animal child = dead.get(0);
			dead.remove(0);
			child.init(mom, dad, position, time, speciesId);
			Vision.addAnimalToZone(child);
			child.time = time;
			alive.enqueue2(child, time);
		}
		else {
			System.err.println("dead is empty; probably means pool is too small");
		}
	}
	
	public void spawnRandomGrassler() {
		if (!dead.isEmpty()) {
			Animal child = dead.get(0);
			dead.remove(0);
			child.init(Constants.SpeciesId.BEST_GRASSLER, Constants.SpeciesId.BEST_GRASSLER, 
					Constants.RANDOM.nextInt(Constants.WORLD_SIZE), Simulation.globalWorldTime,
					Constants.SpeciesId.GRASSLER);
			Vision.addAnimalToZone(child);
			child.time = Simulation.globalWorldTime+1;
			alive.enqueue2(child, Simulation.globalWorldTime+1);
		}
		else {
			System.err.println("dead is empty; probably means pool is too small");
		}
	}
	
	public void spawnRandomBloodling() {
		if (!dead.isEmpty()) {
			Animal child = dead.get(0);
			dead.remove(0);
			child.init(Constants.SpeciesId.BEST_BLOODLING, Constants.SpeciesId.BEST_BLOODLING,
					Constants.RANDOM.nextInt(Constants.WORLD_SIZE), Simulation.globalWorldTime,
					Constants.SpeciesId.BLOODLING);
			Vision.addAnimalToZone(child);
			child.time = Simulation.globalWorldTime+1;
			alive.enqueue2(child, Simulation.globalWorldTime+1);
		}
		else {
			System.err.println("dead is empty; probably means pool is too small");
		}
	}
}
