package world;

import constants.Constants;
import static constants.Constants.Neighbours.*;

import agents.Agent;
import agents.Agent;

public class World {

	public Terrain terrain;
	public Grass grass;
	public CarbonElement blood;
	public CarbonElement fiber;
	public CarbonElement fat;
	public Wind wind;
	public Agent[] containsAgents;
	
	public static int[] east;
	public static int[] north;
	public static int[] west;
	public static int[] south;
	public static int[] none;
	public static int[][] neighbour;
	

	public World() {
		terrain = new Terrain();
		grass = new Grass(terrain);
		blood = new CarbonElement(1, Constants.Colors.BLOOD, 1, Constants.Blood.DECAY_FACTOR);
		fiber = new CarbonElement(1, Constants.Colors.TREE, 1, Constants.Blood.DECAY_FACTOR);
		fat = new CarbonElement(1, Constants.Colors.WHITE, 1, Constants.Blood.DECAY_FACTOR);
		wind = new Wind();
		
		containsAgents = new Agent[Constants.WORLD_SIZE];
		
		calculateNeighbours();
		regenerate();
	}

	private void calculateNeighbours() {
		neighbour = new int[5][];
		east = new int[Constants.WORLD_SIZE];
		north = new int[Constants.WORLD_SIZE];
		west = new int[Constants.WORLD_SIZE];
		south = new int[Constants.WORLD_SIZE];
		none = new int[Constants.WORLD_SIZE];

		neighbour[EAST] = east;
		neighbour[NORTH] = north;
		neighbour[WEST] = west;
		neighbour[SOUTH] = south;
		neighbour[NONE] = none;

		// EAST
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			east[i] = i+1;
		}
		for (int i = 0; i < Constants.WORLD_SIZE_Y; ++i) {
			east[(i+1)*Constants.WORLD_SIZE_X - 1] = i*Constants.WORLD_SIZE_X;
		}

		// NORTH
		for (int i = 0; i < Constants.WORLD_SIZE_X; ++i) {
			north[i] = i + Constants.WORLD_SIZE - Constants.WORLD_SIZE_X;
		}
		for (int i = Constants.WORLD_SIZE_X; i < Constants.WORLD_SIZE; ++i) {
			north[i] = i - Constants.WORLD_SIZE_X;
		}

		// WEST
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			west[i] = i-1;
		}
		for (int i = 0; i < Constants.WORLD_SIZE_Y; ++i) {
			west[i*Constants.WORLD_SIZE_X] = (i+1)*Constants.WORLD_SIZE_X - 1;
		}

		// SOUTH
		for (int i = 0; i < Constants.WORLD_SIZE_X; ++i) {
			south[i + Constants.WORLD_SIZE - Constants.WORLD_SIZE_X] = i;
		}
		for (int i = 0; i < Constants.WORLD_SIZE - Constants.WORLD_SIZE_X; ++i) {
			south[i] = i + Constants.WORLD_SIZE_X;
		}

		// NONE
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			none[i] = i;
		}
	}

	final static int UPDATE_FREQUENCY = 8;
	public void update(int timeStep) {
		
		grass.grow(timeStep, UPDATE_FREQUENCY);
		blood.decay(timeStep, UPDATE_FREQUENCY);
		fiber.decay(timeStep, UPDATE_FREQUENCY);
		fat.decay(timeStep, UPDATE_FREQUENCY);
		wind.stepWind();

	}

	public void regenerate() {
		terrain.regenerate();
		grass.regenerate(false);
		wind.regenerate();
	}

	private static float[] tempColor = new float[3];
	
	public void updateColor(float[][] a, int pos) {
		float grassness, dirtness;

		a[pos][0] = 0f;
		a[pos][1] = 0f;
		a[pos][2] = 0f;

		grassness = grass.height[pos];
//		if (!grass.toBeUpdated[pos]) {
//			a[pos][2] += 0.7f;
//		}
		a[pos][0] += grassness*grass.color[0];
		a[pos][1] += grassness*grass.color[1];
		a[pos][2] += grassness*grass.color[2];

		terrain.getColor(pos, tempColor);
		dirtness = 1 - grassness;
		a[pos][0] += dirtness*tempColor[0];
		a[pos][1] += dirtness*tempColor[1];
		a[pos][2] += dirtness*tempColor[2];

		// Find the highest pile of fiber/fat/blood and use that color.
		blood.getColor(pos, tempColor);
		a[pos][0] += tempColor[0];
		a[pos][1] += tempColor[1];
		a[pos][2] += tempColor[2];
		fiber.getColor(pos, tempColor);
		a[pos][0] += tempColor[0];
		a[pos][1] += tempColor[1];
		a[pos][2] += tempColor[2];
		fat.getColor(pos, tempColor);
		a[pos][0] += tempColor[0];
		a[pos][1] += tempColor[1];
		a[pos][2] += tempColor[2];
	}

	public void updateContainsAgents(Agent a) {
		if (containsAgents[a.pos] == a) {
			// No movement, no change
			return;
		}
		else if (a.oldPos == a.pos) {
			// No movement
			if (containsAgents[a.pos] == null) {
				// but this agent was never the owner of the tile. (Should rarely happen)
				// Make the agent the owner of the tile.
				containsAgents[a.pos] = a;
			}
			return;
		}
		else {
			if (containsAgents[a.oldPos] == a) {
				containsAgents[a.oldPos] = null;
			}
			if (containsAgents[a.pos] != null) {
				System.err.println("trying to move to a populated tile.");
			}
			else {
				containsAgents[a.pos] = a;
			}
		}
	}

	public Agent getAgentAt(int tilePos) {
		return containsAgents[tilePos];
	}

	public void removeAgentFromContainsAgents(Agent agent) {
		if (containsAgents[agent.pos] == agent) {
			containsAgents[agent.pos] = null;
		}
		if (containsAgents[agent.oldPos] == agent) {
			containsAgents[agent.oldPos] = null;
		}
	}

	public void reset(boolean b) {
		grass.regenerate(b);
		blood.reset();
		fiber.reset();
		fat.reset();
	}
}
