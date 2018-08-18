package agents;

import java.util.ArrayList;

import vision.Vision;
import world.World;
import constants.Constants;

public class AgentManager<T extends Agent> {

	public Agent[] pool = new Agent[Constants.MAX_NUM_ANIMALS];
	public ArrayList<Agent> alive = new ArrayList<>();
	public ArrayList<Agent> dead = new ArrayList<>();
	public ArrayList<Agent> toDie = new ArrayList<>();
	public ArrayList<Agent> toLive = new ArrayList<>();
	public int numAgents = 0;
	public boolean killAll = false;
	public boolean saveBrains = false;
	public boolean loadBrains = false;

	public Vision vision;
	private World world;

	private Class<T> clazz;

	public AgentManager(World world, Class<T> clazz) {
		vision = new Vision(Constants.Vision.WIDTH, Constants.Vision.HEIGHT);
		this.clazz = clazz;
		if (clazz == Randomling.class) {
			for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
				pool[id] = new Randomling(0, world, this); //TODO behövs id?
				dead.add(pool[id]);
			}
		}
		else if (clazz == Animal.class) {
			new Species(Constants.Colors.BLACK, Constants.Colors.RED);
			new Species(Constants.Colors.WHITE, Constants.Colors.BLUE);
			
			for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
				pool[id] = new Animal(0, world, (AgentManager<Animal>) this);
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
			for (Agent a : alive) {
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
			for (Agent a : alive) {
				vision.updateNearestNeighbours(a);
				if (a.stepAgent()) {
					vision.updateAgentZone(a);
					world.updateContainsAgents(a);
				}
				else {
					someoneDied(a, true);
				}
			}
		}

		// Remove all dead agents from loop
		for (Agent a : toDie) {
			alive.remove(a);
			dead.add(a);
		}
		toDie.clear();

		// Add all newborn agents to loop
		for (Agent a : toLive) {
			alive.add(a);
		}
		toLive.clear();
	}


	public void spawnAgent(int pos, int id) {
		Agent child = resurrectAgent();
		child.inherit(null, id);
		child.pos = pos;
		child.oldPos = pos;
		vision.addAgentToZone(child);
		if (id == 0) {
			child.stomach.inherit(-1);
		}
		else if (id == 1) {
			child.stomach.inherit(1);
		}
		else {
			System.err.println("Invalid id");
		}
	}
	public Agent mate(Agent agent) {
		Agent child = resurrectAgent();
		
		if (agent instanceof Animal) {
			child.inherit(agent, ((Animal)agent).species.speciesId);
		} 
		else {
			child.inherit(agent, 0);
		}

		child.pos = agent.pos;
		child.oldPos = agent.oldPos;
		child.parent = agent;
		vision.addAgentToZone(child);

		child.stomach.inherit(agent.stomach.p);

		return child;
	}

	public Agent resurrectAgent() {
		Agent id = findFirstAvailablePoolSpot();

		if (id == null) {
			System.err.println("did not find pool spot.");
			return null;
		}

		id.reset();

		numAgents++;

		return id;
	}

	private Agent findFirstAvailablePoolSpot() {
		if (dead.size() == 0) {
			System.err.println("Dead pool is empty");
			return null;
		}
		Agent next = dead.get(0);
		dead.remove(next);
		toLive.add(next);
		return next;
	}

	public void someoneDied(Agent agent, boolean diedNaturally) {
		numAgents--;
		toDie.add(agent);

		world.removeAgentFromContainsAgents(agent);
		vision.removeAgentFromZone(agent, diedNaturally);
	}

	public int getNumAgents() {
		return numAgents;
	}

}
