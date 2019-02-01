package agents;

import java.util.ArrayList;

import vision.Vision;
import world.World;
import constants.Constants;

public class AnimalManager<AgentClass extends Agent> {

	public AgentClass[] pool;
	public ArrayList<Agent> alive = new ArrayList<>();
	public ArrayList<Agent> dead = new ArrayList<>();
	public ArrayList<Agent> toDie = new ArrayList<>();
	public ArrayList<Agent> toLive = new ArrayList<>();
	public int numAgents = 0;
	public boolean killAll = false;

	public Vision vision;
	World world;

	public AnimalManager(World world, Class<AgentClass> clazz, int maxNumAgents, Vision vision) {
		this.vision = vision;
		pool = (AgentClass[]) new Agent[maxNumAgents];
		
		if (clazz == Randomling.class) {
			for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
				pool[id] = (AgentClass) new Randomling(world); //TODO behövs id?
				dead.add(pool[id]);
			}
		}
		else if (clazz == Brainler.class) {
			for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
				pool[id] = (AgentClass) new Brainler(world);
				dead.add(pool[id]);
			}
		}
		else if (clazz == Bloodling.class) {
			for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
				pool[id] = (AgentClass) new Bloodling(world);
				dead.add(pool[id]);
			}
		}
		else if (clazz == Grassler.class) {
			for(int id = 0; id < Constants.MAX_NUM_ANIMALS; ++id) {
				pool[id] = (AgentClass) new Grassler(world);
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
				a.updateNearestNeighbours(vision);
				if (a.stepAgent()) {
					// All is well
					if (a.didMate) {
						a.addToChildren(mate(a));
						a.didMate = false;
					}
					
					if (a instanceof Animal && a.didMove) {
						vision.updateAgentZone((Animal) a);
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
		Agent child = resurrectAgent();
		child.inherit(null);
		child.resetPos(x,  y);
		
		vision.addAgentToZone(child);
	}
	
	public Agent mate(Agent agent) {
		Agent child = resurrectAgent();
		
		child.inherit(agent);

		child.resetPos(agent.pos.x, agent.pos.y);
		child.addParent(agent);
		
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

	public void synchAliveDead() {
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

}
