package agents;

import java.util.ArrayList;
import java.util.Iterator;

import vision.Vision;
import world.World;
import constants.Constants;
import display.RenderState;
import messages.LoadBrains;
import messages.SaveBrains;

public class AnimalManager {
	
	public Animal[] pool = new Animal[Constants.MAX_NUM_ANIMALS];
	public ArrayList<Animal> alive = new ArrayList<>();
	public ArrayList<Animal> dead = new ArrayList<>();
	public ArrayList<Animal> toDie = new ArrayList<>();
	public ArrayList<Animal> toLive = new ArrayList<>();
	public int numAnimals = 0;
	public Animal[] containsAnimals;
	public boolean killAll = false;
	public boolean saveBrains = false;
	public boolean loadBrains = false;
	public Species[] species;
	
	public AnimalManager() {
		int numSpecies = 2; //TODO: Make this part of species instead. Also spawn part.
		species = new Species[numSpecies];
		species[0] = new Species(Constants.Colors.BLACK, Constants.Colors.RED);
		Constants.Species.BLOODLING = species[0];
		species[1] = new Species(Constants.Colors.WHITE, Constants.Colors.BLUE);
		Constants.Species.GRASSLER = species[1]; //TODO: Should not be needed.
		
		
		containsAnimals = new Animal[Constants.WORLD_SIZE];
		
		for (int pos = 0; pos < Constants.WORLD_SIZE; ++pos) {
			containsAnimals[pos] = null;
		}
		
		for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
			pool[id] = new Animal(0, id, species[0]); //TODO behövs id?
			dead.add(pool[id]);
		}
	}
	
	public void moveAll() {
		if (killAll) {
			for (Animal a : alive) {
				a.die();
			}
			System.out.println("Num animals alive after killing them all: " + numAnimals);
			numAnimals = 0;
			killAll = false;
		}
		else {
			for (Animal a : alive) {
				containsAnimals[a.pos] = null;
				if (a.stepAgent()) {
					containsAnimals[a.pos] = a;
				}
			}
		}
		
		for (Animal a : toDie) {
			alive.remove(a);
			dead.add(a);
		}
		toDie.clear();
		for (Animal a : toLive) {
			alive.add(a);
			dead.remove(a);
		}
		toLive.clear();
	}
	

	public void spawn(int pos, Species species) {
		Animal child = resurrectAnimal();
		child.brain.neural.initWeightsRandom();
		child.species = species;
		child.species.someoneWasBorn(species);

		child.pos = pos;
		child.oldPos = pos;
		
//			if (LoadBrains.bestBloodling != null && id.species.speciesId == Constants.SpeciesId.BLOODLING) {
//				id.neuralNetwork.inherit(LoadBrains.bestBloodling, LoadBrains.bestBloodling);
//			}
//			else if (LoadBrains.bestGrassler != null && id.species.speciesId == Constants.SpeciesId.GRASSLER) {
//				id.neuralNetwork.inherit(LoadBrains.bestGrassler, LoadBrains.bestGrassler);
//			}
//			else {
	}
	public Animal mate(Animal a1, Animal a2) {
		Animal child = resurrectAnimal();
		child.brain.inherit(a1.brain, a2.brain);
		child.species = a1.species;
		child.pos = a1.pos;
		child.oldPos = a2.pos;
		
		return a2;
	}
	
	public Animal resurrectAnimal() {
		Animal id = findFirstAvailablePoolSpot();
		
		if (id == null) {
			System.err.println("did not find pool spot.");
			return null;
		}
		
		id.isAlive = true;
		id.age = 0;
		id.score = 0;
		id.sinceLastBaby = 0;
		id.recover = 0f;
		id.hunger = Species.BIRTH_HUNGER;
		id.health = 0.1f;
		
		numAnimals++;
		containsAnimals[id.pos] = id;
		
		Vision.addAnimalToZone(id);
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
	
	public void someoneDied(Animal animal) {
		numAnimals--;
		toDie.add(animal);
	}

	public Animal getAnimalAt(int position) {
		return containsAnimals[position];
	}

	public int getNumAnimals() {
		int sum = 0;
		for (Species s : species) {
			sum += s.numAlive;
		}
		return sum;
	}

}
