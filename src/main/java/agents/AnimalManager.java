package agents;

import java.util.ArrayList;

import vision.Vision;
import world.World;
import constants.Constants;

public class AnimalManager<AnimalClass extends Animal> {

	public AnimalClass[] pool;
	public ArrayList<Animal> alive = new ArrayList<>();
	public ArrayList<Animal> dead = new ArrayList<>();
	public ArrayList<Animal> toDie = new ArrayList<>();
	public ArrayList<Animal> toLive = new ArrayList<>();
	public int numAgents = 0;
	public boolean killAll = false;
	public boolean saveBrains = false;
	public boolean loadBrains = false;

	public Vision vision;
	World world;

	public AnimalManager(World world, Class<AnimalClass> clazz, int maxNumAnimals, Vision vision) {
		this.vision = vision;
		pool = (AnimalClass[]) new Animal[maxNumAnimals];
		
		if (clazz == Randomling.class) {
			for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
				pool[id] = (AnimalClass) new Randomling(world); //TODO behövs id?
				dead.add(pool[id]);
			}
		}
		else if (clazz == Brainler.class) {
			for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
				pool[id] = (AnimalClass) new Brainler(world);
				dead.add(pool[id]);
			}
		}
		else if (clazz == Bloodling.class) {
			for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
				pool[id] = (AnimalClass) new Bloodling(world);
				dead.add(pool[id]);
			}
		}
		else if (clazz == Grassler.class) {
			for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
				pool[id] = (AnimalClass) new Grassler(world);
				dead.add(pool[id]);
			}
		}
		else {
			System.err.println("constructing unknown agent?");
		}

		this.world = world;
	}

	public void moveAll() {
		if (killAll) {
			for (Animal a : alive) {
				a.die();
				someoneDied(a, false);
			}
			if (numAgents != 0) {
				System.err.println("numAgents = " + numAgents + ", should be 0 after killing all");
			}
			numAgents = 0;
			killAll = false;
		}
		else {
			boolean printStuff = true;
			for (Animal a : alive) {
				a.printStuff = printStuff;
				printStuff = false;
				vision.updateNearestNeighbours(a);
				if (a.stepAgent()) {
					// All is well
					if (a.didMate) {
						a.children.add(mate(a));
						a.didMate = false;
					}
					
					if (a.didMove) {
						vision.updateAgentZone(a);
						a.didMove = false;
					}
				}
				else {
					someoneDied(a, true);
				}
			}
		}

	}


	public void spawnAgent(int x, int y) {
		Animal child = resurrectAgent();
		child.inherit(null);
		child.pos.x = x;
		child.pos.y = y;
		child.oldPos.x = x;
		child.oldPos.y = y;
		vision.addAgentToZone(child);
	}
	public Animal mate(Animal agent) {
		Animal child = resurrectAgent();
		
		child.inherit(agent);

		child.pos.set(agent.oldPos);
		child.oldPos.set(agent.oldPos);
		child.parent = agent;
		vision.addAgentToZone(child);

		return child;
	}

	public Animal resurrectAgent() {
		Animal id = findFirstAvailablePoolSpot();

		if (id == null) {
			System.err.println("did not find pool spot.");
			return null;
		}

		id.reset();

		numAgents++;

		return id;
	}

	private Animal findFirstAvailablePoolSpot() {
		if (dead.size() == 0) {
			System.err.println("Dead pool is empty");
			return null;
		}
		Animal next = dead.get(0);
		dead.remove(next);
		toLive.add(next);
		return next;
	}

	public void someoneDied(Animal agent, boolean diedNaturally) {
		numAgents--;
		toDie.add(agent);

		vision.removeAgentFromZone(agent, false);
	}

	public int getNumAgents() {
		return numAgents;
	}

	public void synchAliveDead() {
		// Remove all dead agents from loop
		for (Animal a : toDie) {
			alive.remove(a);
			dead.add(a);
		}
		toDie.clear();
		
		// Add all newborn agents to loop
		for (Animal a : toLive) {
			alive.add(a);
			vision.updateAgentZone(a);
		}
		toLive.clear();
	}

}
