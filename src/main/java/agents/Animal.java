package agents;

import java.util.ArrayList;

import constants.Constants;
import main.Main;
import simulation.Simulation;
import vision.Vision;
import world.World;

public class Animal extends Agent {
	public Brain brain;
	public Species species;

	public Animal(float health, World world, AgentManager<Agent> agentManager) {
		super(health, world, agentManager);
		this.brain = new Brain(false);
		this.species = Species.getSpeciesFromId(0);
		this.color = Constants.Colors.BLACK;
		this.secondaryColor = Constants.Colors.WHITE;
	}

	private void fightWith(Agent agent) {
		agent.health -= getFightSkill();
	}

	/**
	 * Decides where to go.
	 * @return 
	 */
	@Override
	protected int think() {

		int dirTowardsPrey = seekPrey();
		int dirTowardsFriend = seekFriend();

		float fullness = stomach.getRelativeFullness();
		if (fullness > 1) {
			fullness = 1; 
			System.out.println("brain.neural.z[tile][0][NeuralFactors.HUNGER] > 1:  " + fullness);
		}

		for (int tile = 0; tile < 4; ++tile) {
			brain.neural.z[tile][0][NeuralFactors.HUNGER] = fullness;
			brain.neural.z[tile][0][NeuralFactors.AGE] = ((float)age)/maxAge;
			
			if (tile == dirTowardsFriend) {
				brain.neural.z[tile][0][NeuralFactors.TILE_FRIENDS] = 1;
			}
			else {
				brain.neural.z[tile][0][NeuralFactors.TILE_FRIENDS] = -1;
			}
			if (tile == dirTowardsPrey) {
				brain.neural.z[tile][0][NeuralFactors.TILE_PREY] = 1;
			}
			else {
				brain.neural.z[tile][0][NeuralFactors.TILE_PREY] = -1;
			}
			
			int tilePos = World.neighbour[tile][pos];

			if (isFertileAndNotHungry()) {
				brain.neural.z[tile][0][NeuralFactors.FERTILE] = 1;
			}
			else {
				brain.neural.z[tile][0][NeuralFactors.FERTILE] = -1;
			}

			brain.neural.z[tile][0][NeuralFactors.AGE] = ((float)age)/maxAge;

			brain.neural.z[tile][0][NeuralFactors.TILE_FAT] = world.fat.height[tilePos] - world.fat.height[pos];
			brain.neural.z[tile][0][NeuralFactors.TILE_BLOOD] = world.blood.height[tilePos] - world.blood.height[pos];
			brain.neural.z[tile][0][NeuralFactors.TILE_TERRAIN_HEIGHT] = 
					world.terrain.height[tilePos] - world.terrain.height[pos];

		}
		return brain.neural.neuralMagic();
	}

	private int seekFriend() {
		double closestDistance = 1000000;
		Agent closestAgent = null;
		for (Agent a : nearbyAgents) {
			if (a != null) {
				if (((a instanceof Animal) && ((Animal) a).species == species)) {
					// This is an Animal of my species.
					double d = Vision.calculateCircularDistance(pos, a.pos);
					if (d < closestDistance) {
						closestAgent = a;
						closestDistance = d;
					}
				}
			}
		}
		if (closestAgent != null) {
			return Vision.getDirectionOf(pos, closestAgent.pos);
		}
		return Constants.Neighbours.INVALID_DIRECTION;
	}
	private int seekPrey() {
		double closestDistance = 1000000;
		Agent closestAgent = null;
		for (Agent a : nearbyAgents) {
			if (a != null) {
				if ((!(a instanceof Animal) || ((Animal) a).species != species)) {
					// This is not an Animal of my species.
					double d = Vision.calculateCircularDistance(pos, a.pos);
					if (d < closestDistance) {
						closestAgent = a;
						closestDistance = d;
					}
				}
			}
		}
		if (closestAgent != null) {
			return Vision.getDirectionOf(pos, closestAgent.pos);
		}
		return Constants.Neighbours.INVALID_DIRECTION;
	}

	@Override
	public void inherit(Agent a, int speciesId) {
		if (a == null) {
			this.brain.neural.initWeightsRandom();
			this.species = Species.getSpeciesFromId(speciesId);
		}
		else if (!(a instanceof Animal)) {
			System.err.println("Trying to inherit a non-animal.");
			return;
		}
		else {
			this.brain.inherit(((Animal)a).brain);
			this.species = ((Animal)a).species;
		}
		color = species.color;
		secondaryColor = species.secondaryColor;
		this.species.someoneWasBorn();
		stomach.inherit(1);
	}


	@Override
	protected float getHarvestRatio() {
		return brain.neural.getScaledOutput(NeuralFactors.OUT_HARVEST);
	}


	@Override
	protected float getSpeed() {
		return 1f;
	}

	@Override
	protected void interactWith(Agent agent) {
		if (agent instanceof Animal && ((Animal) agent).species == species)
		if (brain.neural.getOutput(NeuralFactors.OUT_AGGRESSIVE) > 0) {
			fightWith(agent);
		}
	}
	@Override
	protected float getFightSkill() {
		return species.fightSkill;
	}

	@Override
	protected boolean isFriendWith(Agent agent) {
		return this.getClass() == Animal.class && species.speciesId == ((Animal)agent).species.speciesId;
	}

}
