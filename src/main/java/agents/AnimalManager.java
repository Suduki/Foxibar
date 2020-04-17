package agents;

import java.util.ArrayList;

import actions.ActionManager;
import vision.Vision;
import world.World;

public class AnimalManager<AnimalClass extends Animal> {

	public AnimalClass[] pool;
	public ArrayList<AnimalClass> alive = new ArrayList<>();
	public ArrayList<AnimalClass> dead = new ArrayList<>();
	public ArrayList<AnimalClass> toDie = new ArrayList<>();
	public ArrayList<AnimalClass> toLive = new ArrayList<>();
	public int numAnimals = 0;
	public boolean killAll = false;

	public Vision vision;
	World world;

	// TODO: URK! Städa!
	public AnimalManager(World world, ActionManager actionManager, Class<AnimalClass> clazz, int maxNumAnimals,
			Vision vision) {
		this.vision = vision;
		pool = (AnimalClass[]) new Animal[maxNumAnimals];

		if (clazz == Randomling.class) {
			for (int id = 0; id < maxNumAnimals; ++id) {
				pool[id] = (AnimalClass) new Randomling(world, actionManager); // TODO behövs id?
				dead.add(pool[id]);
			}
		} else if (clazz == Brainler.class) {
			for (int id = 0; id < maxNumAnimals; ++id) {
				pool[id] = (AnimalClass) new Brainler(world, actionManager);
				dead.add(pool[id]);
			}
		} else if (clazz == Bloodling.class) {
			for (int id = 0; id < maxNumAnimals; ++id) {
				pool[id] = (AnimalClass) new Bloodling(world, actionManager);
				dead.add(pool[id]);
			}
		} else if (clazz == Grassler.class) {
			for (int id = 0; id < maxNumAnimals; ++id) {
				pool[id] = (AnimalClass) new Grassler(world, actionManager);
				dead.add(pool[id]);
			}
		} else if (clazz == Giraffe.class) {
			for (int id = 0; id < maxNumAnimals; ++id) {
				pool[id] = (AnimalClass) new Giraffe(world, actionManager);
				dead.add(pool[id]);
			}
		} else {
			System.err.println("constructing unknown Animal?");
		}

		this.world = world;
	}

	public void moveAll() {

		if (killAll) {
			for (AnimalClass a : alive) {
				a.die();
				someoneDied(a, false);
			}
			if (numAnimals != 0) {
				System.err.println("numAnimals = " + numAnimals + ", should be 0 after killing all");
			}
			numAnimals = 0;
			killAll = false;
		} else {
			for (AnimalClass a : alive) {
				vision.updateNearestNeighbours(a);

				if (a.stepAgent()) {
					// All is well
					if (a.didMate) {
						a.addToChildren(mate(a));
						a.didMate = false;
					}

					if (a.didMove) {
						for (Animal a2 : a.nearbyAgents) {
							if (a2 != null) {
								a.collide(a2);
							}
						}
						a.didMove = false;
					}
				} else {
					someoneDied(a, true);
				}
			}
		}
	}

	public Animal spawnAnimal(int x, int y) {
		AnimalClass child = resurrectAnimal();
		child.inherit(null);
		child.resetPos(x, y);

		return child;
	}

	private Animal mate(Animal animal) {
		Animal child = resurrectAnimal();

		child.inherit(animal);

		child.resetPos(animal.pos.x, animal.pos.y);
		child.addParent(animal);

		return child;
	}

	private AnimalClass resurrectAnimal() {
		AnimalClass id = findFirstAvailablePoolSpot();

		if (id == null) {
			System.err.println("did not find pool spot.");
			return null;
		}

		id.reset();

		numAnimals++;

		return id;
	}

	private AnimalClass findFirstAvailablePoolSpot() {
		if (dead.size() == 0) {
			System.err.println("Dead pool is empty");
			return null;
		}
		AnimalClass next = dead.get(0);
		dead.remove(next);
		toLive.add(next);
		return next;
	}

	private void someoneDied(AnimalClass animal, boolean diedNaturally) {
		numAnimals--;
		toDie.add(animal);
	}

	public int getNumAnimals() {
		return numAnimals;
	}

	public void synchAliveDead() {
		// Remove all dead agents from loop
		for (AnimalClass a : toDie) {
			alive.remove(a);
			dead.add(a);
		}
		toDie.clear();

		// Add all newborn agents to loop
		for (AnimalClass a : toLive) {
			alive.add(a);
		}
		toLive.clear();
	}

}
