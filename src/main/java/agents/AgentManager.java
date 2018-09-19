package agents;

import java.util.ArrayList;

import vision.Vision;
import world.World;
import constants.Constants;

public class AgentManager<AgentClass extends Agent> {

	public AgentClass[] pool;
	public ArrayList<Agent> alive = new ArrayList<>();
	public ArrayList<Agent> dead = new ArrayList<>();
	public ArrayList<Agent> toDie = new ArrayList<>();
	public ArrayList<Agent> toLive = new ArrayList<>();
	public int numAgents = 0;
	public boolean killAll = false;
	public boolean saveBrains = false;
	public boolean loadBrains = false;

	public Vision vision;
	World world;

	public AgentManager(World world, Class<AgentClass> clazz, int maxNumAnimals, Vision vision) {
		this.vision = vision;
		pool = (AgentClass[]) new Agent[maxNumAnimals];
		
		if (clazz == Randomling.class) {
			for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
				pool[id] = (AgentClass) new Randomling(0, world, (AgentManager<Agent>) this); //TODO behövs id?
				dead.add(pool[id]);
			}
		}
		else if (clazz == Brainler.class) {
			for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
				pool[id] = (AgentClass) new Brainler(0, world, (AgentManager<Agent>) this);
				dead.add(pool[id]);
			}
		}
		else if (clazz == Bloodling.class) {
			for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
				pool[id] = (AgentClass) new Bloodling(0, world, (AgentManager<Agent>) this);
				dead.add(pool[id]);
			}
		}
		else if (clazz == Grassler.class) {
			for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
				pool[id] = (AgentClass) new Grassler(0, world, (AgentManager<Agent>) this);
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
			boolean printStuff = true;
			for (Agent a : alive) {
				a.printStuff = printStuff;
				printStuff = false;
				vision.updateNearestNeighbours(a);
				if (a.stepAgent()) {
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
			vision.updateAgentZone(a);
		}
		toLive.clear();
	}


	public void spawnAgent(int x, int y) {
		Agent child = resurrectAgent();
		child.inherit(null);
		child.pos.x = x;
		child.pos.y = y;
		child.old.x = x;
		child.old.y = y;
		vision.addAgentToZone(child);
	}
	public Agent mate(Agent agent) {
		Agent child = resurrectAgent();
		
		child.inherit(agent);

		child.pos.set(agent.old);
		child.old.set(agent.old);
		child.parent = agent;
		vision.addAgentToZone(child);

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

		vision.removeAgentFromZone(agent, false);
	}

	public int getNumAgents() {
		return numAgents;
	}

}
