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
		
		float fullness = stomach.getRelativeFullness();
		if (fullness > 1) {
			fullness = 1; 
			System.out.println("brain.neural.z[tile][0][NeuralFactors.HUNGER] > 1:  " + fullness);
		}
		brain.neural.z[NeuralFactors.Y][0][NeuralFactors.HUNGER] = fullness;
		
		brain.neural.z[NeuralFactors.Y][0][NeuralFactors.AGE] = ((float)age)/maxAge;
		
		for (int tile = 0; tile < 4; ++tile) {
			int tilePos = World.neighbour[tile][pos];
			
			brain.neural.z[tile][0][NeuralFactors.HUNGER] = stomach.getRelativeFullness();
			if (brain.neural.z[tile][0][NeuralFactors.HUNGER] > 1) {
				brain.neural.z[tile][0][NeuralFactors.HUNGER] = 1;
				System.out.println("brain.neural.z[tile][0][NeuralFactors.HUNGER] > 1:  " + brain.neural.z[tile][0][NeuralFactors.HUNGER]);
			}
			
			if (isFertileAndNotHungry()) {
				brain.neural.z[tile][0][NeuralFactors.FERTILE] = 1;
			}
			else {
				brain.neural.z[tile][0][NeuralFactors.FERTILE] = -1;
			}
			
			brain.neural.z[tile][0][NeuralFactors.AGE] = ((float)age)/maxAge;
			
//			brain.neural.z[tile][0][NeuralFactors.TILE_FIBER] = World.grass.height[tilePos] + 
//					World.fiber.height[tilePos] - 
//					World.grass.height[pos] - World.fiber.height[pos];
			brain.neural.z[tile][0][NeuralFactors.TILE_FAT] = world.fat.height[tilePos] - world.fat.height[pos];
			brain.neural.z[tile][0][NeuralFactors.TILE_BLOOD] = world.blood.height[tilePos] - world.blood.height[pos];
			brain.neural.z[tile][0][NeuralFactors.TILE_TERRAIN_HEIGHT] = 
					world.terrain.height[tilePos] - world.terrain.height[pos];
			
			brain.neural.z[tile][0][NeuralFactors.TILE_DANGER] = 0;
			brain.neural.z[tile][0][NeuralFactors.TILE_HUNT] = 0;
			brain.neural.z[tile][0][NeuralFactors.TILE_FRIENDS] = 0;
			
			// Loop through the sighted animals to determine tile goodnesses
			for (Agent nearbyAnimalId : nearbyAgents) {
				
				if (nearbyAnimalId == null) {
					continue;
				}
				
				float distance = (float) Vision.calculateCircularDistance(tilePos, nearbyAnimalId.pos);
				if (distance > Constants.Vision.MAX_DISTANCE_AN_AGENT_CAN_SEE) {
					continue;
				}
				float distanceFactor = 1f-distance/Constants.Vision.MAX_DISTANCE_AN_AGENT_CAN_SEE;
				
				// 0 distanceF means the animal is too far away
				if (distanceFactor < 0  || distanceFactor > 1) {
					System.err.println("distanceFactor = " + distanceFactor);
				}
				
				if (looksDangerous(nearbyAnimalId)) {
//					brain.neural.z[tile][0][NeuralFactors.TILE_DANGER] = Math.max(distanceFactor, brain.neural.z[tile][0][NeuralFactors.TILE_DANGER]);
				}
				else {
					brain.neural.z[tile][0][NeuralFactors.TILE_HUNT] = Math.max(distanceFactor, brain.neural.z[tile][0][NeuralFactors.TILE_HUNT]);
					brain.neural.z[tile][0][NeuralFactors.TILE_FRIENDS] = Math.max(distanceFactor, brain.neural.z[tile][0][NeuralFactors.TILE_FRIENDS]);
				}
				
				
			}
		}
		return brain.neural.neuralMagic();
	}


	@Override
	public void inherit(Agent a, int speciesId) {
		if (a == null) {
			this.brain.neural.initWeightsRandom();
			this.species = Species.getSpeciesFromId(speciesId);
		}
		else if (a instanceof Animal) {
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
	}


	@Override
	protected float getHarvestRatio() {
		return brain.neural.getOutput(NeuralFactors.OUT_HARVEST);
	}


	@Override
	protected float getSpeed() {
		return 1f;
	}
	
	@Override
	protected void interactWith(Agent agent) {
		if (brain.neural.getOutput(NeuralFactors.OUT_AGGRESSIVE) > 0) {
			fightWith((Animal) agent);
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
