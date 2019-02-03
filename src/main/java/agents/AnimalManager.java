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
	public int numAnimals = 0;
	public boolean killAll = false;

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
			System.err.println("constructing unknown Animal?");
		}

		this.world = world;
	}

	public void moveAll() {
		if (killAll) {
			for (Animal a : alive) {
				a.die();
				someoneDied(a, false);
			}
			if (numAnimals != 0) {
				System.err.println("numAnimals = " + numAnimals + ", should be 0 after killing all");
			}
			numAnimals = 0;
			killAll = false;
		}
		else {
			for (Animal a : alive) {
				a.updateNearestNeighbours(vision);
				if (a.stepAgent()) {
					// All is well
					if (a.didMate) {
						a.addToChildren(mate(a));
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


	public void spawnAnimal(int x, int y) {
		Animal child = resurrectAnimal();
		child.inherit(null);
		child.resetPos(x,  y);
		
		vision.addAgentToZone(child);
	}
	
	public Animal mate(Animal animal) {
		Animal child = resurrectAnimal();
		
		child.inherit(animal);

		child.resetPos(animal.pos.x, animal.pos.y);
		child.addParent(animal);
		
		vision.addAgentToZone(child);

		return child;
	}

	public Animal resurrectAnimal() {
		Animal id = findFirstAvailablePoolSpot();

		if (id == null) {
			System.err.println("did not find pool spot.");
			return null;
		}

		id.reset();

		numAnimals++;

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

	public void someoneDied(Animal animal, boolean diedNaturally) {
		numAnimals--;
		toDie.add(animal);

		vision.removeAnimalFromZone(animal, false);
	}

	public int getNumAnimals() {
		return numAnimals;
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
			vision.updateAgentZone((Animal) a);
		}
		toLive.clear();
	}

}
