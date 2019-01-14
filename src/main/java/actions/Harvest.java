package actions;

import org.joml.Vector2f;

import world.TileElement;
import agents.Agent;

public class Harvest extends Action {
	public Vector2f dir;
	public float heightBelowAgent;
	public float heightNearby;
	
	private boolean harvestPossible;
	private boolean searchPossible;
	
	private TileElement stuffToHarvest;
	private final int DIGEST_SKILL;
	private final int STOMACH_ID;

	public Harvest(TileElement stuffToHarvest, final int DIGEST_SKILL, final int STOMACH_ID) {
		super();
		dir = new Vector2f();
		this.DIGEST_SKILL = DIGEST_SKILL;
		this.STOMACH_ID = STOMACH_ID;
		this.stuffToHarvest = stuffToHarvest;
	}

	@Override
	public boolean determineIfPossible(Agent a) {
		isPossible = false;
		if (a.talents.getRelative(DIGEST_SKILL) > 0.2f) {
			if (canHarvest(a)) {
				harvestPossible = true;
				isPossible = true;
			}
			if (canSearch(a)) {
				searchPossible = true;
				isPossible = true;
			}
		}
		return isPossible;
	}

	@Override
	public void commit(Agent a) {
		numCommits++;
		if (!isPossible) System.err.println("Trying to commit to impossible Action" + this.getClass().getSimpleName());
		
		float energyLeft = 1f;
		
		energyLeft -= harvest(a);
		if (searchPossible) {
			a.vel.set(dir);
			a.vel.mul(energyLeft);
			a.move();
		}
	}
	
	private boolean canHarvest(Agent a) {
		heightBelowAgent = stuffToHarvest.getHeight((int)a.pos.x, (int)a.pos.y);
		return heightBelowAgent > 0.05f;
	}
	
	private boolean canSearch(Agent a) {
		heightNearby = stuffToHarvest.seekHeight(dir, (int)a.pos.x, (int)a.pos.y);
		return heightNearby > 0.1f;
	}
	
	private float harvest(Agent a) {
		return a.stomach.add(STOMACH_ID, stuffToHarvest.harvest(a.harvestSkill, (int) a.pos.x, (int) a.pos.y)) / a.harvestSkill;
	}
}
